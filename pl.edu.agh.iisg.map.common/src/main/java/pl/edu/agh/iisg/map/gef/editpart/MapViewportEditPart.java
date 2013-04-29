package pl.edu.agh.iisg.map.gef.editpart;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.MouseWheelHelper;
import org.eclipse.gef.Request;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;

import pl.edu.agh.iisg.map.gef.figure.IMapViewportFigure;
import pl.edu.agh.iisg.map.gef.figure.MapViewportFigure;
import pl.edu.agh.iisg.map.gef.tool.MapPanningTool;
import pl.edu.agh.iisg.map.model.MapDiagram;
import pl.edu.agh.iisg.map.model.internal.MapViewport;
import pl.edu.agh.iisg.map.tile.manager.IMapManager;

/**
 * EditPart for MapViewport.
 * 
 */
public class MapViewportEditPart extends AbstractGraphicalEditPart implements PropertyChangeListener {

	// BEGIN Fields
	/** Editor's graphical viewer. */
	private GraphicalViewer graphicalViewer;

	/** Editor's viewport. */
	private Viewport editorViewport;

	/** Map's figure. */
	private IMapViewportFigure mapViewportFigure;

	private List<Object> children;

	private MapPanningTool panningTool;

	 /** Listener for editor's GraphicalViewer Control. Listens to resize event. */
    private ControlListener controlListener = new ControlAdapter() {
        private Dimension oldSize;

        @Override
        public void controlResized(ControlEvent e) {
            Dimension size = editorViewport.getSize();
            if (!size.equals(oldSize)) {
                refreshSize();
                mapViewportFigure.setLatitude(getModel().getDiagram().getLatitude());
                mapViewportFigure.setLongitude(getModel().getDiagram().getLongitude());
                refreshSizeAndLocation();
                oldSize = size;
            }
        }
    };
	
	// END Fields

	// BEGIN API Methods

	public MapViewportEditPart(MapViewport model) {
		setModel(model);
		panningTool = new MapPanningTool(false);
	}

	@Override
	public void removeNotify() {
		super.removeNotify();
		mapViewportFigure.dispose();
	};

	@Override
	public MapViewport getModel() {
		return (MapViewport) super.getModel();
	};

	@Override
	public void activate() {
		super.activate();
		getModel().getDiagram().addPropertyChangeListener(this);
		graphicalViewer = (GraphicalViewer) getViewer();
		graphicalViewer.getControl().addControlListener(controlListener);
		editorViewport = (Viewport) ((GraphicalEditPart) getRoot()).getFigure();

	}

	@Override
	public void deactivate() {
		graphicalViewer.getControl().removeControlListener(controlListener);
		getModel().getDiagram().removePropertyChangeListener(this);
		super.deactivate();
	}

	@Override
	public IFigure getContentPane() {
		// Children's figures should be placed on Map Tool Layer. Please read
		// more in MapScalableRootEditPart.

		// Obtain Layer Manager
		RootEditPart rootEditPart = getRoot();
		EditPartViewer editPartViewer = rootEditPart.getViewer();
		LayerManager layerManager = (LayerManager) editPartViewer
				.getEditPartRegistry().get(LayerManager.ID);
		IFigure contentPane = layerManager
				.getLayer(MapScalableRootEditPart.MAP_TOOL_LAYER);

		return contentPane;
	}

	@Override
	protected void addChild(EditPart child, int index) {
		super.addChild(child, index);
	}

	@Override
	protected void refreshChildren() {
		super.refreshChildren();
	}

	@Override
	protected List<Object> getModelChildren() {
		if (children == null) {
			children = new LinkedList<>();

		}
		return children;
	}

	@Override
	protected void refreshVisuals() {
		refreshPosition();
		refreshZoomLevel();
		refreshSizeAndLocation();
	}

	@Override
	protected IFigure createFigure() {
		mapViewportFigure = new MapViewportFigure();
		mapViewportFigure.setMapManager(getMapManager());
		mapViewportFigure.setLoadingTileImage(getMapManager().getLoadTile());
		// Viewport obtaining for print command
		if (editorViewport == null) {
			editorViewport = (Viewport) ((GraphicalEditPart) getRoot())
					.getFigure();
		}
		refreshSize();

		return mapViewportFigure;
	}

	@Override
	protected void createEditPolicies() {
		// installEditPolicy(MapStatusLineUpdatePolicy.MOUSE_MOVEMENT_ROLE, new
		// MapStatusLineUpdatePolicy());

		installEditPolicy(EditPolicy.COMPONENT_ROLE, null);

	}

	// END API Methods

	private IMapManager getMapManager() {
		// return MapManagers.getMapManager(getModel().getMapKey());
		return ((MapScalableRootEditPart)getRoot()).getCurrentMapManager();
	}

	private void refreshPosition() {
		mapViewportFigure.setLatitude(getModel().getDiagram().getLatitude());
		mapViewportFigure.setLongitude(getModel().getDiagram().getLongitude());
	}

	private void refreshZoomLevel() {
		mapViewportFigure.setPositionAndZoomLevel(getModel().getDiagram().getLongitude(),
				getModel().getDiagram().getLatitude(), getModel().getDiagram().getZoomLevel());
		refreshSize();
	}

	private void refreshSize() {
		Dimension newSize = editorViewport.getSize();
		if (mapViewportFigure.getTopLeftX() < 0) {
			newSize.width -= mapViewportFigure.getTopLeftX();
		}
		if (mapViewportFigure.getTopLeftY() < 0) {
			newSize.height -= mapViewportFigure.getTopLeftY();
		}
		if (!newSize.equals(mapViewportFigure.getSize())) {
			mapViewportFigure.setSize(newSize);
		}
	}

	private void refreshSizeAndLocation() {
		if (editorViewport == null) {
			return;
		}

		// hack in order to support small map zooms.
		refreshSize();

		// update figure constraints
		MapEditPart mapEditPart = (MapEditPart) getParent();
		Dimension size = mapViewportFigure.getSize();

		Point viewportLocation = new Point(mapViewportFigure.getTopLeftX(),
				mapViewportFigure.getTopLeftY());

		mapEditPart.setLayoutConstraint(this, mapViewportFigure,
				new Rectangle(viewportLocation.x, viewportLocation.y,
						size.width, size.height));

		editorViewport.validate();
		editorViewport.setViewLocation(viewportLocation.x, viewportLocation.y);
	}

	@Override
	public DragTracker getDragTracker(Request request) {
		return panningTool;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter) {
		if (adapter == MouseWheelHelper.class) {
			return panningTool;
		}
		return super.getAdapter(adapter);
	}

	// BEGIN Listeners methods

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (MapDiagram.Events.ZOOM_EVENT.equals(evt.getPropertyName())) {
            refreshZoomLevel();
			refreshSizeAndLocation();

        } else if (MapDiagram.Events.LATITUDE_EVENT.equals(evt.getPropertyName())
                || MapDiagram.Events.LONGITUDE_EVENT.equals(evt.getPropertyName())) {
            refreshPosition();
            refreshSizeAndLocation();
            editorViewport.getUpdateManager().performUpdate();
        } 
		
	}

	// END Listeners methods

}
