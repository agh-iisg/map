package pl.edu.agh.iisg.map.gef.figure;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.graphics.ImageData;

import pl.edu.agh.cast.common.IDisposable;
import pl.edu.agh.iisg.map.model.type.Coordinate;
import pl.edu.agh.iisg.map.tile.manager.IMapManager;

/**
 * Interface for figure that represents map's viewport. This figure should contains tiles with map.
 *
 */
public interface IMapViewportFigure extends IFigure, IDisposable {

    /**
     * Sets geographical position (longitude and latitude) of point situated in the center of the map. <br>
     * <b>CAUTION:</b> This method do not repaints figure. Developer is responsible for repaint forcing.
     *
     * @param lon
     *            Longitude of point situated in the center of the map.
     * @param lat
     *            Latitude of point situated in in the center of the map.
     */
    public void setPosition(Coordinate lon, Coordinate lat);

    /**
     * Sets longitude of point situated in the center of the map. <br>
     * <b>CAUTION:</b> This method do not repaints figure. Developer is responsible for repaint forcing.
     *
     * @param longitude
     *            Longitude of point situated in the center of the map.
     */
    public void setLongitude(Coordinate longitude);

    /**
     * Sets latitude of point situated in the center of the map. <br>
     * <b>CAUTION:</b> This method do not repaints figure. Developer is responsible for repaint forcing.
     *
     * @param latitude
     *            Latitude of point situated in in the center of the map.
     */
    public void setLatitude(Coordinate latitude);

    /**
     * Sets actual zoom level.<br>
     * <b>CAUTION:</b> This method do not repaints figure. Developer is responsible for repaint forcing.
     *
     * @param zoomLevel
     *            Zoom level to set.
     */
    public void setZoomLevel(int zoomLevel);

    /**
     * Sets longitude and latitude at specified zoomLevel and reinitializeMapTiles if necessary.
     *
     * @param lon
     *            Longitude to set.
     * @param lat
     *            Latitude to set.
     * @param zoom
     *            ZoomLevel to set.
     */
    public void setPositionAndZoomLevel(Coordinate lon, Coordinate lat, int zoom);

    /**
     * Returns x-coordinate of top-left corner.
     *
     * @return X-coordinate of top-left corner.
     */
    public int getTopLeftX();

    /**
     * Returns y-coordinate of top-left corner.
     *
     * @return Y-coordinate of top-left corner.
     */
    public int getTopLeftY();

    /**
     * This method allows to get middle point of this figure.
     *
     * @return middle point
     */
    public Point getMiddlePoint();

    /**
     * This method returns count of tiles to complete loading an area.
     *
     * @return count of tiles to load
     */
    // This is a hack for image exporting. In future version of implementation there should be possibility to load tiles synchronously!
    public int getCountOfTilesToLoad();

    /**
     * Resets tiles to complete.
     */
    public void resetTilesToComplete();

    /**
     * Gets the loading tile image.
     *
     * @return the loading tile image
     */
    public ImageData getLoadingTileImage();

    /**
     * Sets the loading tile image.
     *
     * @param loadingTileImage
     *            the new loading tile image
     */
    void setLoadingTileImage(ImageData loadingTileImage);

    /**
     * Sets the map manager.
     *
     * @param mapManager the mapManager to set
     */
    public void setMapManager(IMapManager mapManager);

}
