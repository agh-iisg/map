package pl.edu.agh.iisg.map.gef.editpart;

import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Layer;
import org.eclipse.draw2d.LayeredPane;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.MouseWheelHelper;
import org.eclipse.gef.editparts.ScalableRootEditPart;

import pl.edu.agh.iisg.map.gef.tool.MapZoomTool;
import pl.edu.agh.iisg.map.model.MapDiagram;
import pl.edu.agh.iisg.map.tile.manager.MapManager;

/**
 * Implementation of {@link org.eclipse.gef.RootEditPart}.
 * <code>MapScalableRootEditPart</code> provides additional
 * <code>Printable Layer</code> named as <code>Map Tool Layer</code> which
 * stores figures of {@link MapViewportEditPart} . This layer has been
 * introduced due to the fact that {@link MapViewportEditPart}'s figures should
 * be drawn on the top layer to cover nodes and connections.
 * 
 * <P>
 * Actual structure of <code>Printable layers</code>:
 * <table cellspacing="0" cellpadding="0">
 * <tr>
 * <td colspan="2">Printable Layers</td>
 * </tr>
 * <tr>
 * <td></td>
 * <td>&#9500; Connection Layer</td>
 * </tr>
 * <tr>
 * <td></td>
 * <td>&#9500; Primary Layer</td>
 * </tr>
 * <tr>
 * <td></td>
 * <td>&#9492; Map Tool Layer</td>
 * </tr>
 * </table>
 * 
 */

public class MapScalableRootEditPart extends ScalableRootEditPart {

	/**
	 * Name of additional <code>Map Tool Layer</code> that keeps
	 * {@link MapViewportEditPart}'s figures's.
	 */
	public static final String MAP_TOOL_LAYER = "Map Tool Layer"; //$NON-NLS-1$

	/**
	 * Name of the additional <code>Label layer</code> that keeps labels for
	 * distance tool.
	 */
	public static final String LABEL_LAYER = "Label layer"; //$NON-NLS-1$

	/**
	 * Label layer. Used by distance tool.
	 * 
	 */
	final class LabelLayer extends Layer {
		private LabelLayer() {
			setEnabled(false);
		}

		@Override
		public Dimension getPreferredSize(int wHint, int hHint) {
			Rectangle rect = new Rectangle();
			for (int i = 0; i < getChildren().size(); i++) {
				rect.union(((IFigure) getChildren().get(i)).getBounds());
			}
			return rect.getSize();
		}
	}

	@Override
	protected LayeredPane createPrintableLayers() {
		LayeredPane printableLayer = super.createPrintableLayers();
		Layer layer = new Layer();
		layer.setLayoutManager(new FreeformLayout());
		printableLayer.add(layer, MAP_TOOL_LAYER);

		LabelLayer labelLayer = new LabelLayer();
		printableLayer.add(labelLayer, LABEL_LAYER);

		return printableLayer;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter) {
		if (adapter == MouseWheelHelper.class) {
			return new MapZoomTool(MapManager.getInstance().getMapDriver(),
					(MapDiagram) getModel());
		}
		return super.getAdapter(adapter);
	}

}
