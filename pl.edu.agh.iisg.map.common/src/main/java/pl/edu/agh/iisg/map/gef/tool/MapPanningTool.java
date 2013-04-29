package pl.edu.agh.iisg.map.gef.tool;

import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.swt.widgets.Event;

import pl.edu.agh.iisg.map.gef.editpart.MapEditPart;
import pl.edu.agh.iisg.map.gef.editpart.MapScalableRootEditPart;
import pl.edu.agh.iisg.map.model.MapDiagram;
import pl.edu.agh.iisg.map.model.type.Coordinate;
import pl.edu.agh.iisg.map.tile.driver.IMapDriver;

/**
 * A subclass of the SelectionTool that allows panning by holding down the space bar.
 * 
 */
public class MapPanningTool extends PanningTool {

    /**
     * Point which indicates drag start location.
     */
    protected Point dragStartLocation;

    private MapZoomTool mapZoomTool;

    /** Indicates if the tool */
    private boolean isPrimary;

    public MapPanningTool() {
        this(true);
    }

    /**
     * Constructor. Allows to determine if is primary (works with any mouse button) or secondary (works only with middle mouse button or
     * CTRL pressed).
     * 
     * @param isPrimary
     *            flag indicating if the tool is primary
     */
    public MapPanningTool(boolean isPrimary) {
        super();
        this.isPrimary = isPrimary;
        mapZoomTool = new MapZoomTool();
    }

    
    @Override
    protected String getCommandName() {
        return null;
    }

    private IMapDriver getMapDriver() {
        if (getCurrentViewer() != null) {
            return ((MapScalableRootEditPart)getCurrentViewer().getRootEditPart()).getCurrentMapManager().getMapDriver();
        }
        return null;
    }

    private MapDiagram getModel() {
        if (getCurrentViewer() != null) {
            return ((MapEditPart)getCurrentViewer().getContents()).getDiagram();
        }
        return null;
    }

    @Override
    protected boolean handleButtonDown(int button) {
        if (isPrimary || button == 2 || getCurrentInput().isControlKeyDown()) {
            dragStartLocation = getLocation();
            setCursor(GRAB_CLOSED_CURSOR);
            return true;
        }
        dragStartLocation = null;
        return false;
    }

    @Override
    protected boolean handleDrag() {
        if (getCurrentViewer() == null) {
            return false;
        }
        Viewport editorViewport = (Viewport)((GraphicalEditPart)getCurrentViewer().getRootEditPart()).getFigure();
        if (dragStartLocation != null && !editorViewport.getViewLocation().equals(dragStartLocation)) {
            
            MapDiagram model = getModel();
            IMapDriver driver = getMapDriver();
            if (model == null || driver == null) {
                return false;
            }
            
            // Determine difference between last and actual position
            Point temp = getLocation();
            int newX = dragStartLocation.x - temp.x;
            int newY = dragStartLocation.y - temp.y;
            Integer zoomLevel = model.getZoomLevel();

            int x = driver.longitudeToXCoordinate(model.getLongitude().getDecimalDegrees(), zoomLevel);

            // Size of the viewport
            Dimension size = editorViewport.getSize();

            // Calculate position of MapViewportFigure's TOP-LEFT corner (x-coordinate)
            int topLeftX = x + newX - size.width / 2;

            // Check if newly set position (x-coordinate) is valid
            int xRange = driver.getXRange(zoomLevel);
            if ((topLeftX >= 0 || newX > 0) && topLeftX + size.width <= xRange) {
                x += newX;
            }
            double longitude = driver.xCoordinateToLongitue(x, zoomLevel);

            // Obtain y-coordinate of last position and transform y-coordinate to actual position
            int y = driver.latitudeToYCoordinate(model.getLatitude().getDecimalDegrees(), zoomLevel);

            // Calculate position of MapViewportFigure's TOP-LEFT corner (y-coordinate)
            int topLeftY = y + newY - size.height / 2;

            // Check if newly set position (y-coordinate) is valid
            int yRange = driver.getYRange(zoomLevel);
            if ((topLeftY >= 0 || newY > 0) && topLeftY + size.height <= yRange) {
                y += newY;
            }
            double latitude = driver.yCoordinateToLatitude(y, zoomLevel);

            Coordinate longitudeCoordinate = new Coordinate(longitude);
            Coordinate latitudeCoordinate = new Coordinate(latitude);

            if (!latitudeCoordinate.equals(model.getLatitude())) {
                // model.setSuppressAllEvents(true);
                model.setLongitude(longitudeCoordinate);
                // model.setSuppressAllEvents(false);
            } else {
                model.setLongitude(longitudeCoordinate);
            }

            model.setLatitude(latitudeCoordinate);

            dragStartLocation = temp;

        }
        return true;
    }

    @Override
    public void setViewer(EditPartViewer viewer) {
        super.setViewer(viewer);
        mapZoomTool.setViewer(viewer);
    }

    @Override
    public void mouseWheelScrolled(Event event, EditPartViewer viewer) {
        mapZoomTool.mouseWheelScrolled(event, viewer);
    }

}
