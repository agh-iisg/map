package pl.edu.agh.cast.map.manager;

import org.eclipse.swt.graphics.ImageData;

import pl.edu.agh.cast.map.cache.DiscMapCache;
import pl.edu.agh.cast.map.cache.MemoryMapCache;
import pl.edu.agh.cast.map.config.IMapConfigurationProvider;
import pl.edu.agh.cast.map.driver.IMapDriver;
import pl.edu.agh.cast.map.provider.IAsynchronousTileResultListener;
import pl.edu.agh.cast.map.provider.IMapProvider;

/**
 * Implementation of this interface should be responsible for managing map cache, map providers and map drivers. Each of elements mentioned
 * before should be used to provide requested tiles for map editor. Map manager can also schedule and synchronize obtaining map tiles from
 * Internet asynchronous map providers.
 *
 */
public interface IMapManager  {

    /**
     * Initializes map manager with required elements. If you are reinitializing map manager, you do not have to worry about invoking
     * {@link IMapTileManager#dispose()} method it will be done for you.
     *
     * @param mapDriver
     *            Driver that is responsible for handling map type.
     * @param mapCaches TODO
     *            Ordered list of map cache that are responsible for enhancing performance of already loaded tiles.
     * @param mapProviderDescriptors
     *            Ordered list of provider descriptors that keeps providers (and describing information) that are responsible for providing
     *            map's tiles.
     */
    void initialize(IMapDriver mapDriver, IMapProvider mapProvider, IMapConfigurationProvider config);

    /**
     * Return tile described by 3 parameters - map's zoom level, tile's column number and tile's row number.<br/>
     * This method should return immediately after invocation, even if asynchronous {@link IMapDriver} may be in chain of map provider that
     * should be uses.<br/>
     * There are 3 different return states:<br/>
     * <em>1. Tile available</em> - As a result manager returns image with requested tile. This situation occurs when tile is available in
     * cache or can be obtained through synchronous {@link IMapDriver}<br>
     * <em>2. Loading tile</em> - As a result manager return special image (LOAD-TILE) that informs user that tile need to be asynchronously
     * downloaded. This situation occurs when tile is available but can be obtained only through asynchronous {@link IMapDriver}. LOAD-TILE
     * can be provided directly by manager or {@link IMapDriver} that is in use. In this situation <em>asyncResultListener</em> will be
     * informed asynchronously about tile downloading result (it is obvious that asynchronous downloading may fail).<br>
     * <em>3. No tile</em> - As a result manager return special image (NO-TILE) that informs user that tile is not available. This situation
     * occurs when tile is not available in cache and synchronous {@link IMapDriver}.<br/>
     *
     * @param zoom
     *            Map's zoom level.
     * @param column
     *            Tile's column number.
     * @param row
     *            Tile's row number.
     * @param asyncResultListener
     *            Listeners that will be notified about asynchronous downloading result.
     *
     * @throws IllegalStateException
     *             Exception will be thrown if method is invoked when <tt>IMapManager</tt> is disposed or has not been initialized yet.
     *
     * @return tile Image with tile (if available) or special image (LOAD-TILE or NO-TILE) otherwise.
     */
    ImageData getTile(int zoom, int column, int row, IAsynchronousTileResultListener asyncResultListener) throws IllegalStateException;

    /**
     * Returns flag that indicates whether <tt>IMapManager</tt> has or has not been disposed.<br>
     * <b>IMPORTANT:</b> Not initialized manager is also considered as disposed.
     *
     * @return flag that indicates whether <tt>IMapManager</tt> has or has not been disposed.
     */
    boolean isDisposed();

    /**
     * Returns {@link IMapDriver} that is linked with this map tile manager.
     *
     * @return {@link IMapDriver} that can handle actual (actually chosen) map type.
     */
    IMapDriver getMapDriver();

    /**
     * Sets the caching.
     *
     * @param value
     *            the value
     */
    void setCaching(boolean value);

    /**
     * Returns image representing tile that will be asynchronously loaded.
     *
     * @return Image representing tile that will be asynchronously loaded.
     */
    ImageData getLoadTile();

}
