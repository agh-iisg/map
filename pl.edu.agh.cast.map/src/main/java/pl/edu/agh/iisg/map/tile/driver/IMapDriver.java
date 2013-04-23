package pl.edu.agh.iisg.map.tile.driver;

import org.eclipse.swt.graphics.ImageData;

import pl.edu.agh.cast.common.IDisposable;
import pl.edu.agh.cast.common.collections.Pair;
import pl.edu.agh.iisg.map.tile.storage.ITileStorageStrategy;

/**
 * Map driver is responsible for handling specified type of map (e.g. GoogleMap or OpenStreetMap). Map driver provides crucial information
 * about selected map type. Basically describes map that are provided by providers. Connection between {@link IMapDriver} and
 * {@link pl.edu.agh.iisg.map.tile.provider.editor.provider.IMapProvider} is done in settings page. One map driver can handle many map types (probably
 * needs some extra configuration in settings page). <b>IMPORTANT:</b> Map driver needs to have public non-argument public constructor.<br>
 * <b>CONFIGURATION:</b> In order to create configurable map driver interface
 * {@link pl.edu.agh.cast.ui.common.configuration.element.IConfigurableElement} should be implemented.
 * 
 * @see pl.edu.agh.cast.ui.common.configuration.element.IConfigurableElement
 */
public interface IMapDriver extends IDisposable, IMapCoordinateConverter {

    /** The Constant PARAMETER_MAP_TYPE. */
    public static final String PARAMETER_MAP_TYPE = "PARAMETER_MAP_TYPE"; //$NON-NLS-1$

    /**
     * Returns pair of Integers that represents dimension of tiles that this map driver can handle. Pair#first - represents width,
     * Pair#second - represents height.
     * 
     * @return Pair of Integers that represents dimension of tiles.
     */
    Pair<Integer, Integer> getTileDimension();

    /**
     * Returns x (in pixels) range of map at specified zoom level.
     * 
     * @param zoom
     *            Specified zoom level.
     * @return X (in pixels) range of map at specified zoom level.
     */
    int getXRange(int zoom);

    /**
     * Returns y (in pixels) range of map at specified zoom level.
     * 
     * @param zoom
     *            Specified zoom level.
     * @return Y (in pixels) range of map at specified zoom level.
     */
    int getYRange(int zoom);

    /**
     * Returns maximal zoom level.
     * 
     * @return Maximal zoom level.
     */
    int getMaxZoomLevel();

    /**
     * Returns minimal zoom level.
     * 
     * @return Minimal zoom level.
     */
    int getMinZoomLevel();

    /**
     * Returns default zoom level.
     * 
     * @return Default zoom level.
     */
    int getDefaultZoomLevel();

    /**
     * Returns tile storage strategy for driver. See the API of {@link ITileStorageStrategy} to learn more.
     * 
     * @return Tile storage strategy for driver.
     * @see ITileStorageStrategy
     */
    ITileStorageStrategy getTileStorageStrategy();

    /**
     * Returns image representing tile that is not available. If <code>null</code> than default image will be used.
     * 
     * @return Image representing tile that is not available.
     */
    ImageData getNoTile();

    /**
     * Returns image representing tile that will be asynchronously loaded. If <code>null</code> than default image will be used.
     * 
     * @return Image representing tile that will be asynchronously loaded.
     */
    ImageData getLoadTile();

}
