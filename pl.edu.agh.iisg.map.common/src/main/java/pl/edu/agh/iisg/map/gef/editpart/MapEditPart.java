package pl.edu.agh.iisg.map.gef.editpart;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.ConnectionRouter;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;

import pl.edu.agh.iisg.map.model.MapDiagram;
import pl.edu.agh.iisg.map.model.internal.MapViewport;

/**
 * Contents edit part for map diagrams.
 * 
 */
public class MapEditPart extends AbstractGraphicalEditPart implements PropertyChangeListener /*, IMovableEditorEditPart */ {
    // BEGIN Fields

    /** Children's model. */
    private List<Object> modelChildren;

    private MapViewport mapViewport;

    private final MapDiagram diagram;

    private static Map<String, ConnectionRouter> routersMap = new HashMap<>();

    // private final IMapClusteringStrategy mapClusteringStrategy;

    private boolean zoomChanged = true;

    static {
        //TrapeziumRouter fanRouter = new TrapeziumRouter();
        //fanRouter.setNextRouter(new BendpointConnectionRouter());

        //routersMap.put("default", fanRouter); //$NON-NLS-1$

        routersMap.put("null", ConnectionRouter.NULL); //$NON-NLS-1$
    }

    // END Fields

    /**
     * Constructor.
     * 
     * @param diagram
     *            Diagram for which Map should be created.
     */
    public MapEditPart(MapDiagram diagram) {
        if (diagram == null) {
            throw new IllegalArgumentException("Cannot create edit part with null diagram"); //$NON-NLS-1$
        }
        setModel(diagram);
        this.diagram = diagram;

        // mapClusteringStrategy = new SimpleMapClusteringStrategy(diagram);
    }

    // BEGIN API Methods

    @Override
    public void addNotify() {
        initZoomAndPosition();
        //mapClusteringStrategy.init();
        super.addNotify();
    }

    @Override
    public void activate() {
        super.activate();
        //((MapDiagram)getModel()).getModel().addPropertyChangeListener(this);

        refreshChildrenVisuals();
    }

    @Override
    public void deactivate() {
        //((IMapDiagram)getModel()).getModel().removePropertyChangeListener(this);

        super.deactivate();
    }

    @Override
    public List<Object> getModelChildren() {
        MapDiagram model = (MapDiagram)getModel();
        if (mapViewport == null) {
            mapViewport = new MapViewport(model);
        }
        modelChildren = new LinkedList<>();
        modelChildren.add(mapViewport);

        if (zoomChanged) {
            zoomChanged = false;
            // mapClusteringStrategy.recluster();
        } else {
            // mapClusteringStrategy.refresh();
        }

        //modelChildren.addAll(mapClusteringStrategy.getClusters());
        //modelChildren.addAll(mapClusteringStrategy.getUnclusteredNodes());

        // modelChildren.addAll(model.getSpotSequences());

        return modelChildren;
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
//        String name = evt.getPropertyName();
//        if (IMapDataSet.Events.ZOOM_EVENT.equals(name)) {
//            zoomChanged = true;
//            refresh();
//            refreshChildrenVisuals();
//        } else if (IMapDataSet.Events.CONNECTION_ROUTER_EVENT.equals(name)) {
//            refreshRouter();
//        } else if (IMapDataSet.Events.REFRESH_MAP_VISUALS.equals(name)) {
//            refresh();
//            refreshChildrenVisuals();
//        } else if (IPresentationDataSet.Events.CHILD_EVENT.equals(name)) {
//            refresh();
//            refreshChildrenVisuals();
//        }
    }

    private void refreshRouter() {
//        String id = diagram.getConnectionRouterId();
//
//        ConnectionLayer connLayer = (ConnectionLayer)getLayer(LayerConstants.CONNECTION_LAYER);
//
//        ConnectionRouter router = routersMap.get(id);
//
//        if (router != null) {
//            connLayer.setConnectionRouter(router);
//        }
    }

    /** A method for refreshing children visuals. */
    public void refreshChildrenVisuals() {
        for (Object editPart : getChildren()) {
            ((AbstractGraphicalEditPart)editPart).refresh();
        }
    }

    /**
     * Gets the diagram.
     * 
     * @return the diagram
     */
    public MapDiagram getDiagram() {
        return diagram;
    }

    @Override
    protected void addChildVisual(EditPart childEditPart, int index) {
        super.addChildVisual(childEditPart, index);
        // TODO [mozgawa][CAST-1571] Is the refreshing really needed? Investigate!
    }

    @Override
    protected IFigure createFigure() {
        IFigure figure = new FreeformLayer();
        figure.setLayoutManager(new FreeformLayout());

        refreshRouter();

        return figure;
    }

    @Override
    protected void createEditPolicies() {
        /*
         * Policy with EditPolicy.COMPONENT_ROLE key has the task of preventing the root of the model from being deleted. It overrides the
         * createDeleteCommand method to return an unexecutable command.
         */
        installEditPolicy(EditPolicy.COMPONENT_ROLE, new RootComponentEditPolicy());

        /*
         * Policy registered with EditPolicy.LAYOUT_ROLE key, handles create and constraint change requests. The first request is posted
         * when a new shape is dropped into a diagram. The layout policy returns a command that adds a new shape to the diagram editor and
         * places it at the drop location. The constraint change request is posted whenever the user resizes or moves shapes already present
         * in the diagram.
         */
//        installEditPolicy(EditPolicy.LAYOUT_ROLE, new MapLayoutEditPolicy());

        //installEditPolicy(MapStatusLineUpdatePolicy.MOUSE_MOVEMENT_ROLE, new MapStatusLineUpdatePolicy());
    }

    // END API Methods

    /** Calculates appropriate zoom level and center point for map's elements. */
    private void initZoomAndPosition() {
        MapDiagram mapDiagram= (MapDiagram)getModel();
        // This used if we are printing or exporting as an image!
        if (mapDiagram.getZoomLevel() != null) {
            return;
        }

//        Display display = Display.getCurrent();
//        Rectangle bounds = display.getBounds();

        // Area that should be visible
//        int coverX = (int)(bounds.width * 0.7);
//        int coverY = (int)(bounds.height * 0.7);

        // Obtain map nodes (only nodes will be considered in calculating zoom level)
//        Collection<IMapNode> mapNodes = mapDataSet.getMapNodes();
//
//        MapKey mapKey = mapDataSet.getMapKey();
//        if (mapKey == null) {
//            mapKey = MapManagers.getMapKeys().iterator().next();
//            mapDataSet.setMapKey(mapKey);
//        }

//        IMapDriver mapDriver = MapDrivers.getMapDriver(mapKey);
//
//        if (mapNodes.size() > 0) {
//            double left = Double.MAX_VALUE;
//            double right = Double.MIN_VALUE;
//            double top = Double.MAX_VALUE;
//            double bottom = Double.MIN_VALUE;
//
//            for (IMapNode mapNode : mapNodes) {
//                double latitude = mapNode.getLatitude().getDecimalDegrees();
//                double longitude = mapNode.getLongitude().getDecimalDegrees();
//
//                if (latitude < top) {
//                    top = latitude;
//                }
//                if (latitude > bottom) {
//                    bottom = latitude;
//                }
//                if (longitude < left) {
//                    left = longitude;
//                }
//                if (longitude > right) {
//                    right = longitude;
//                }
//            }
//
//            double coverAreaX;
//            double coverAreaY;
//            int zoomLevel = mapDriver.getMaxZoomLevel() + 1;
//
//            do {
//                zoomLevel--;
//                coverAreaY = mapDriver.latitudeToYCoordinate(top, zoomLevel) - mapDriver.latitudeToYCoordinate(bottom, zoomLevel);
//                coverAreaX = mapDriver.longitudeToXCoordinate(right, zoomLevel) - mapDriver.longitudeToXCoordinate(left, zoomLevel);
//            } while ((coverAreaX > coverX || coverAreaY > coverY) && zoomLevel > mapDriver.getMinZoomLevel());
//
//            // Set calculated zoom level and center point
//
//            mapDataSet.setZoomLevel(zoomLevel);
//            mapDataSet.setLatitude(new Coordinate(bottom + (top - bottom) / 2.0));
//            mapDataSet.setLongitude(new Coordinate(left + (right - left) / 2.0));
//        } else {
//            mapDataSet.setZoomLevel(mapDriver.getDefaultZoomLevel());
//        }
    }

//    @Override
//    public Command getMoveCommand(Point direction) {
//        double latitude = mapViewport.getLatitude().getDecimalDegrees();
//        double longitude = mapViewport.getLongitude().getDecimalDegrees();
//        int zoomLevel = mapViewport.getZoomLevel();
//        final IMapDataSet mapDataSet = mapViewport.getRelatedDataSet();
//
//        IMapDriver mapDriver = MapDrivers.getMapDriver(mapDataSet.getMapKey());
//
//        int x = mapDriver.longitudeToXCoordinate(longitude, zoomLevel) + direction.x;
//        int y = mapDriver.latitudeToYCoordinate(latitude, zoomLevel) + direction.y;
//
//        final double newLatitude = mapDriver.yCoordinateToLatitude(y, zoomLevel);
//        final double newLongitude = mapDriver.xCoordinateToLongitue(x, zoomLevel);
//
//        Command command;
//
//        // check if the coordinates are good
//        if (newLatitude >= -90 && newLatitude <= 90 && newLongitude >= -180 && newLatitude <= 180) {
//            command = new Command() {
//                @Override
//                public void execute() {
//                    mapDataSet.setLatitude(new Coordinate(newLatitude));
//                    mapDataSet.setLongitude(new Coordinate(newLongitude));
//                }
//            };
//        } else {
//            command = new Command() {};
//        }
//        return command;
//    }

    /**
     * Gets the set of unclustered nodes.
     * 
     * @return the unclustered nodes
     */
//    public Set<IMapNode> getUnclusteredNodes() {
//        return mapClusteringStrategy.getUnclusteredNodes();
//    }

}
