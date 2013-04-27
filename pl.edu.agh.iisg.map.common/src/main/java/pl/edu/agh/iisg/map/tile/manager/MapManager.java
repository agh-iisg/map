package pl.edu.agh.iisg.map.tile.manager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

import pl.edu.agh.cast.common.IDisposable;
import pl.edu.agh.cast.common.collections.Pair;
import pl.edu.agh.iisg.map.config.IMapConfigurationProvider;
import pl.edu.agh.iisg.map.config.PreferencesMapConfigurator;
import pl.edu.agh.iisg.map.tile.cache.DiscMapCache;
import pl.edu.agh.iisg.map.tile.cache.IMapCache;
import pl.edu.agh.iisg.map.tile.cache.MemoryMapCache;
import pl.edu.agh.iisg.map.tile.driver.IMapDriver;
import pl.edu.agh.iisg.map.tile.driver.OpenStreetMapDriver;
import pl.edu.agh.iisg.map.tile.provider.IAsynchronousTileResultListener;
import pl.edu.agh.iisg.map.tile.provider.IMapProvider;
import pl.edu.agh.iisg.map.tile.provider.OpenStreetMapInternetProvider;

/**
 * Default implementation of {@link IMapManager}. This implementation is using 3 different types of elements for tile managing:<br/>
 * <em>1. {@link IMapDriver}</em> - Map driver that can handle map type (one that is actually in use).<br/>
 * <em>2. {@link IMapCache}s</em> - Cache is responsible for providing already loaded tiles in more effective way. This implementation works
 * with {@link MemoryMapCache} and {@link DiscMapCache} implementations only.<br/>
 * <em>3. {@link IMapDriver}s</em> - Map providers are responsible for providing tiles from some source.<br/>
 * <br/>
 * This implementation obtains tiles from memory cache in UI Thread. Tiles stored by disc cache and stored by
 * {@link pl.edu.agh.iisg.map.tile.provider.editor.provider.IMapProvider}s will be obtained asynchronously in new thread.
 * 
 */
public final class MapManager implements IMapManager, IDisposable {

    // BEGIN Internal classes

    /**
     * Describes tile by 3 parameters - map's zoom level, tile's column number and tile's row number.
     */
    private class TileDescriptor {
        /**
         * Map's zoom level.
         */
        private int zoom;

        /**
         * Tile's column number.
         */
        private int column;

        /**
         * Tile's row number.
         */
        private int row;

        /**
         * Constructor.
         * 
         * @param zoom
         *            Map's zoom level.
         * @param column
         *            Tile's column number.
         * @param row
         *            Tile's row number.
         */
        public TileDescriptor(int zoom, int column, int row) {
            this.zoom = zoom;
            this.column = column;
            this.row = row;
        }

        /**
         * Returns map's zoom level.
         * 
         * @return Map's zoom level.
         */
        public int getZoom() {
            return zoom;
        }

        /**
         * Returns tile's column number.
         * 
         * @return Tile's column number.
         */
        public int getColumn() {
            return column;
        }

        /**
         * Return tile's row number.
         * 
         * @return Tile's row number.
         */
        public int getRow() {
            return row;
        }

        /**
         * {@inheritDoc}
         * 
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            if (obj instanceof TileDescriptor) {
                TileDescriptor desc = (TileDescriptor)obj;
                if (zoom == desc.zoom && column == desc.column && row == desc.row) {
                    return true;
                }
            }

            return false;
        }

        /**
         * {@inheritDoc}
         * 
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + zoom;
            result = prime * result + column;
            result = prime * result + row;

            return result;
        }

    }

    /**
     * Class that is responsible for downloading tiles in asynchronously way.
     */
    private class AsynchronousTileDownloader extends Thread {

        /**
         * Descriptor of tile that should be obtained in asynchronous way.
         */
        private TileDescriptor tileDescriptor;

        /**
         * Map's zoom level.
         */
        private int zoom;

        /**
         * Tile's column number.
         */
        private int column;

        /**
         * Tile's row number.
         */
        private int row;

        /**
         * Constructor.
         * 
         * @param tileDescriptor
         *            Descriptor of tile that should be obtained in asynchronous way. Cannot be <code>null</code>.
         */
        public AsynchronousTileDownloader(TileDescriptor tileDescriptor) {
            if (tileDescriptor == null) {
                throw new IllegalArgumentException("Parameter tileDescriptor cannot be null"); //$NON-NLS-1$
            }

            this.tileDescriptor = tileDescriptor;
            zoom = tileDescriptor.getZoom();
            column = tileDescriptor.getColumn();
            row = tileDescriptor.getRow();
        }

        /**
         * {@inheritDoc}
         * 
         * @see java.lang.Thread#run()
         */
        @Override
        public void run() {

            ImageData result = null;

            // Check in disc cache
            if (discCache != null) {
                result = discCache.getTile(zoom, column, row);
            }

            if (result == null) {
                // Try to find tile in map providers
                // Iterator<IMapProviderDescriptor> descriptorIterator = mapProviderDescriptors.iterator();
                // while (result == null && descriptorIterator.hasNext()) {
                // result = descriptorIterator.next().getMapProvider().getTile(zoom, column, row);
                // }
                result = mapProvider.getTile(zoom, column, row);

                if (result != null) {
                    // Learn disc cache
                    if (discCache != null && cachingEnabled) {
                        discCache.saveTile(zoom, column, row, result);
                    }
                }
            }

            if (result != null) {
                // Learn memory cache
                if (memoryCache != null && cachingEnabled) {
                    memoryCache.saveTile(zoom, column, row, result);
                }
            } else {
                // Obtain "No tile" tile.
                result = getNoTile();
            }

            // Inform asynchronous listeners
            synchronized (synchronizationMap) {
                if (!disposed) {
                    for (IAsynchronousTileResultListener asynchronousListener : synchronizationMap.get(tileDescriptor)) {
                        asynchronousListener.resultAvailable(result, zoom, column, row);
                    }

                    // Remove entry in synchronization map
                    synchronizationMap.remove(tileDescriptor);
                }
            }

        }
    }

    // END Internal classes

    /**
     * Logger.
     */
    private static Logger log = Logger.getLogger(MapManager.class);

    /**
     * The number of threads to keep in the pool, even if they are idle.
     */
    private int corePoolSize;

    /**
     * The maximum number of threads to allow in the pool.
     */
    private int maximumPoolSize;

    /**
     * When the number of threads is greater than the core, this is the maximum time that excess idle threads will wait for new tasks before
     * terminating.
     */
    private int keepAliveTime;

    /**
     * The time unit for the keepAliveTime field.
     */
    private TimeUnit unit;

    /**
     * Thread pool executor that limits count of thread that will be created.
     */
    private ThreadPoolExecutor pool;

    /**
     * Queue for threads.
     */
    private BlockingQueue<Runnable> threadsQueue;

    /**
     * Map driver that is responsible for handling currently selected map type.
     */
    private IMapDriver mapDriver;

    /**
     * Fast memory cache that is requested for tiles synchronously in UI thread.
     */
    private MemoryMapCache memoryCache;

    /**
     * Disc cache that is requested for tiles asynchronously in new thread.
     */
    private DiscMapCache discCache;

    /**
     * Ordered list of provider descriptors that keeps providers (and describing information) that are responsible for providing map's
     * tiles.
     */
    // private LinkedList<IMapProviderDescriptor> mapProviderDescriptors;

    private IMapProvider mapProvider;

    /**
     * Flag that indicates whether manager has or has not been disposed. Flag is used to prevent double disposing linked elements.
     */
    private boolean disposed = true;

    /**
     * Map that is used to prevent double asynchronous requesting for the same tile. First request should put result in cache. In the the
     * meaning time between first request and its result, all other requests to the same tiles should be satisfied by first request's
     * result. Map keeps {@link TileDescriptor} as a key (describes specific tile) and list of {@link IAsynchronousTileResultListener}s as
     * value (keeps list of listeners that should be informed when specific tile has been obtained asynchronously).
     */
    private Map<TileDescriptor, Set<IAsynchronousTileResultListener>> synchronizationMap;

    /**
     * Image for tiles that could not be obtained from map provider.
     */
    private ImageData noTile;

    /**
     * Image for tiles that are processed in asynchronously way.
     */
    private ImageData loadTile;

    private boolean cachingEnabled = true;

    /**
     * Constructor.
     * 
     * @param corePoolSize
     *            The number of threads to keep in the pool, even if they are idle.
     * @param maximumPoolSize
     *            The maximum number of threads to allow in the pool.
     * @param keepAliveTime
     *            When the number of threads is greater than the core, this is the maximum time that excess idle threads will wait for new
     *            tasks before terminating.
     * @param unit
     *            The time unit for the keepAliveTime argument.
     */
    public MapManager(int corePoolSize, int maximumPoolSize, int keepAliveTime, TimeUnit unit) {
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.keepAliveTime = keepAliveTime;
        this.unit = unit;
    }

    // BEGIN API Methods

    /**
     * {@inheritDoc}
     * 
     * @see pl.edu.agh.cast.common.IDisposable#dispose()
     */
    @Override
    public void dispose() {
        if (!disposed) {

            mapDriver.dispose();

            if (memoryCache != null) {
                memoryCache.dispose();
            }
            if (discCache != null) {
                discCache.dispose();
            }

            // for (IMapProviderDescriptor providerDescriptor : mapProviderDescriptors) {
            // providerDescriptor.getMapProvider().dispose();
            // }
            mapProvider.dispose();

            try {
                pool.shutdownNow();
            } catch (RuntimeException e) {
                log.info("Excpetion thrown during thread's pool shutdowning", e); //$NON-NLS-1$
            }

            threadsQueue.clear();
            synchronizationMap.clear();

            disposed = true;
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see pl.edu.agh.iisg.map.tile.manager.editor.manager.IMapManager#initialize(pl.edu.agh.iisg.map.tile.driver.editor.driver.IMapDriver, java.util.LinkedList,
     *      java.util.LinkedList)
     */
    @Override
    public void initialize(IMapDriver driver, IMapProvider provider, IMapConfigurationProvider config) {
        if (driver == null) {
            throw new IllegalArgumentException(String.format("One of input paramters is null: driver=[%s], caches=[], providers=[]", //$NON-NLS-1$
                    driver));
        }

        // Dispose if re-initializing (method will check if disposing is allowed)
        // TODO implement smart disposing (some resources can be not disposed in some cases)
        dispose();

        // Initialize thread pool executor
        threadsQueue = new LinkedBlockingDeque<>();
        pool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, threadsQueue);

        // Initialize synchronization map
        synchronizationMap = new HashMap<>();

        mapDriver = driver;
        mapProvider = provider;

        // Initialize caches
        this.memoryCache = MemoryMapCache.createMemoryMapCache(config);
        this.discCache = DiscMapCache.createDiscMapCache(driver.getTileStorageStrategy(), config);

        // mapProviderDescriptors = providerDescriptors;

        // Mark this manager as not disposed
        disposed = false;

        // Initialize "No tile" and "Load tile" tiles
        Pair<Integer, Integer> tileDimension = mapDriver.getTileDimension();

        noTile = mapDriver.getNoTile();
        if (noTile == null) {
            // Creates tile for situation when tile cannot be loaded.
            Image noTileImage = new Image(Display.getCurrent(), tileDimension.getFirst(), tileDimension.getSecond());
            GC gc = new GC(noTileImage);
            gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
            gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
            gc.fillRectangle(0, 0, tileDimension.getFirst(), tileDimension.getSecond());
            String drawText = "Cannot load tile"; //$NON-NLS-1$
            org.eclipse.swt.graphics.Point drawTextPoint = gc.textExtent(drawText);
            gc.drawText(drawText, (tileDimension.getFirst() - drawTextPoint.x) / 2, (tileDimension.getSecond() - drawTextPoint.y) / 2);
            gc.dispose();

            noTile = noTileImage.getImageData();
            noTileImage.dispose();
        }

        loadTile = mapDriver.getLoadTile();
        if (loadTile == null) {
            // Creates empty tile for situation when tile is loading
            Image loadTileImage = new Image(Display.getCurrent(), tileDimension.getFirst(), tileDimension.getSecond());
            GC gc = new GC(loadTileImage);
            gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
            gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
            gc.fillRectangle(0, 0, tileDimension.getFirst(), tileDimension.getSecond());
            String drawText = "Loading"; //$NON-NLS-1$
            org.eclipse.swt.graphics.Point drawTextPoint = gc.textExtent(drawText);
            gc.drawText(drawText, (tileDimension.getFirst() - drawTextPoint.x) / 2, (tileDimension.getSecond() - drawTextPoint.y) / 2);
            gc.dispose();

            loadTile = loadTileImage.getImageData();
            loadTileImage.dispose();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see pl.edu.agh.iisg.map.tile.manager.editor.manager.IMapManager#getTile(int, int, int,
     *      pl.edu.agh.iisg.map.tile.provider.editor.provider.IAsynchronousTileResultListener)
     */
    @Override
    public ImageData getTile(int zoom, int column, int row, IAsynchronousTileResultListener asyncResultListener)
        throws IllegalStateException {
        if (disposed) {
            throw new IllegalStateException("Map manager is disposed or has not been initialized yet."); //$NON-NLS-1$
        }

        ImageData result = null;

        // Try to obtain tile from map cache in UI thread.
        if (memoryCache != null) {
            result = memoryCache.getTile(zoom, column, row);
        }

        // Try to obtain tile from disc cache or any of map providers (attempt will be made in new thread).
        if (result == null) {
            // Check synchronization map in order to determine whether it is first asynchronous request for this tile.
            TileDescriptor tileDescriptor = new TileDescriptor(zoom, column, row);
            synchronized (synchronizationMap) {
                boolean asyncInvocation = false;
                Set<IAsynchronousTileResultListener> tileListeners = synchronizationMap.get(tileDescriptor);
                if (tileListeners == null) {
                    tileListeners = new HashSet<>();
                    synchronizationMap.put(tileDescriptor, tileListeners);

                    // First request for this tile
                    asyncInvocation = true;
                }
                tileListeners.add(asyncResultListener);

                // Run new thread to obtain tile asynchronously
                if (asyncInvocation) {
                    pool.execute(new AsynchronousTileDownloader(tileDescriptor));
                }
            }

            result = getLoadTile();
        }

        if (result == null) {
            // Tile is not available
            result = getNoTile();
        }

        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @see pl.edu.agh.iisg.map.tile.manager.editor.manager.IMapManager#isDisposed()
     */
    @Override
    public boolean isDisposed() {
        return disposed;
    }

    /**
     * {@inheritDoc}
     * 
     * @see pl.edu.agh.iisg.map.tile.manager.editor.manager.IMapManager#getMapDriver()
     */
    @Override
    public IMapDriver getMapDriver() {
        return mapDriver;
    }

    // END API Methods

    @Override
    public ImageData getLoadTile() {
        return loadTile;
    }

    /**
     * Returns image representing tile that is not available.
     * 
     * @return Image representing tile that is not available.
     */
    private ImageData getNoTile() {
        return noTile;
    }

    @Override
    public void setCaching(boolean value) {
        cachingEnabled = value;
    }

    public static final MapManager getInstance() {
        return SingletonHolder.manager;
    }

    private static class SingletonHolder {
        private static final MapManager manager = createInstance();

        private static MapManager createInstance() {
            // TODO: remove hard-coded initialization from here
            MapManager mapManager = new MapManager(10, 30, 50, TimeUnit.SECONDS);
            IMapDriver driver = new OpenStreetMapDriver();
            // TODO: consider to remove memory and disc cache from arguments (they could be initialized inside initialize method)
            mapManager.initialize(driver, new OpenStreetMapInternetProvider(), new PreferencesMapConfigurator());
                    
            return mapManager;
        }

    }
}
