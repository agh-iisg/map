package pl.edu.agh.iisg.map.gef.tool;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalEditPart;

import pl.edu.agh.iisg.map.gef.editpart.MapEditPart;
import pl.edu.agh.iisg.map.gef.editpart.MapScalableRootEditPart;
import pl.edu.agh.iisg.map.model.MapDiagram;
import pl.edu.agh.iisg.map.model.type.Coordinate;
import pl.edu.agh.iisg.map.tile.driver.IMapDriver;

/**
 * A tool that zooms rectangle map area.
 *
 */
public class MapRectangleZoomTool extends RubberbandSelectionTool {

    private MapDiagram getModel() {
        if (getCurrentViewer() != null) {
            return ((MapEditPart)getCurrentViewer().getContents()).getDiagram();
        }
        return null;
    }
    
    private IMapDriver getMapDriver() {
        if (getCurrentViewer() != null) {
            return ((MapScalableRootEditPart)getCurrentViewer().getRootEditPart()).getCurrentMapManager().getMapDriver();
        }
        return null;
    }

    /**
     * This method does nothing. It just overloads parent method to avoid exceptions.
     *
     * @see pl.edu.agh.cast.editor.graphical.tool.RubberbandSelectionTool#isFigureVisible(org.eclipse.draw2d.IFigure)
     */
    @Override
    protected boolean isFigureVisible(IFigure fig) {
        return true;
    }

    @Override
    protected void performMarqueeSelect() {

        Rectangle zoomRect = getMarqueeSelectionRectangle();

        if (zoomRect.width < 2 || zoomRect.height < 2) {
            return;
        }

        IMapDriver mapDriver = getMapDriver();

        int zoomLevel = getModel().getZoomLevel();

        Viewport editorViewport = (Viewport)((GraphicalEditPart)getCurrentViewer().getRootEditPart()).getFigure();

        int viewLocationX = editorViewport.getViewLocation().x;
        int viewLocationY = editorViewport.getViewLocation().y;
        int viewportWidth = editorViewport.getSize().width;
        int viewportHeight = editorViewport.getSize().height;

        // Selected area coordinates
        double selectedTopLatitude = mapDriver.yCoordinateToLatitude(viewLocationY + zoomRect.y(), zoomLevel);
        double selectedBottomLatitude = mapDriver.yCoordinateToLatitude(viewLocationY + zoomRect.bottom(), zoomLevel);
        double selectedLeftLongitude = mapDriver.xCoordinateToLongitue(viewLocationX + zoomRect.x(), zoomLevel);
        double selectedRightLongitude = mapDriver.xCoordinateToLongitue(viewLocationX + zoomRect.right(), zoomLevel);

        // Minimum target coordinates range for selected area on map
        double xSelectedCoordinatesRange = selectedLeftLongitude - selectedRightLongitude;
        xSelectedCoordinatesRange = xSelectedCoordinatesRange < 0 ? -xSelectedCoordinatesRange : xSelectedCoordinatesRange;

        double ySelectedCoordinatesRange = selectedTopLatitude - selectedBottomLatitude;
        ySelectedCoordinatesRange = ySelectedCoordinatesRange < 0 ? -ySelectedCoordinatesRange : ySelectedCoordinatesRange;

        double coverAreaX;
        double coverAreaY;

        zoomLevel = mapDriver.getMaxZoomLevel() + 1;

        do {
            zoomLevel--;
            coverAreaY = mapDriver.latitudeToYCoordinate(selectedTopLatitude, zoomLevel)
                    - mapDriver.latitudeToYCoordinate(selectedBottomLatitude, zoomLevel);
            coverAreaX = mapDriver.longitudeToXCoordinate(selectedRightLongitude, zoomLevel)
                    - mapDriver.longitudeToXCoordinate(selectedLeftLongitude, zoomLevel);
        } while ((coverAreaX > viewportWidth || coverAreaY > viewportHeight) && zoomLevel > mapDriver.getMinZoomLevel());

        getModel().setLongitude(new Coordinate((selectedLeftLongitude + selectedRightLongitude) / 2));
        getModel().setLatitude(new Coordinate((selectedTopLatitude + selectedBottomLatitude) / 2));
        getModel().setZoomLevel(zoomLevel);
    }

}
