package pl.edu.agh.cast.map.cache;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;

import pl.edu.agh.cast.map.config.IMapConfigurationProvider;
import pl.edu.agh.cast.map.storage.DiscStorageStrategy;
import pl.edu.agh.cast.map.storage.IDiscStorageStrategy;
import pl.edu.agh.cast.map.storage.ITileStorageStrategy;
import pl.edu.agh.cast.util.FileUtil;

/**
 * Cache map's tiles on disc.
 * 
 */
public class DiscMapCache implements IMapCache {

    /**
     * Logger for this class.
     */
    private static Logger log = Logger.getLogger(DiscMapCache.class);

    /**
     * Limit of storage (In megabytes). If -1 than there is no storage limit.
     */
    private int storageLimit;

    /**
     * Strategy of storage map's tiles.
     */
    private IDiscStorageStrategy storageStrategy;

    /**
     * Keeps information of how many space is used by disc cache (in bytes). <b>CAUTION:</b> this value is obtained in asynchronous way, so
     * may not be available. If the value has not be establish yet than <code>-1</code> will be returned.
     */
    private long currentStorage = -1L;

    /**
     * Constructor.
     * 
     * @param storageStrategy
     *            Strategy of storing map tiles.
     * @param storageLimit
     *            Limit of storage (In MB). If -1 than there is no storage limit.
     */
    public DiscMapCache(IDiscStorageStrategy storageStrategy, int storageLimit) {
        this.storageStrategy = storageStrategy;
        this.storageLimit = storageLimit;
    }

    // BEGIN API Methods

    /**
     * Creates instance of {@link IDiscStorageStrategy}. Appropriate configuration is is obtained from application's settings (stored in
     * {@link IPreferenceStore}).
     * 
     * @param tileStorageStrategy
     *            Tiles storage strategy which is crucial for creating disc storage strategy.
     * @return Instance of {@link IDiscStorageStrategy}.
     */
    public static IDiscStorageStrategy createDiscStorageStrategy(ITileStorageStrategy tileStorageStrategy, IMapConfigurationProvider config) {
        String storageDirectory = config.discCacheLocation();
        IDiscStorageStrategy result = new DiscStorageStrategy(new File(storageDirectory), tileStorageStrategy);

        return result;
    }

    /**
     * Creates instance of {@link DiscyMapCache}. Appropriate disc cache's configuration is obtained from application's settings (stored in
     * {@link IPreferenceStore}). If disc cache is disabled in application's setting than <code>null</code> will be returned.
     * 
     * @param tileStorageStrategy
     *            Tile storage strategy (obtainable from {@link pl.edu.agh.cast.map.editor.driver.IMapDriver}).
     * 
     * @return Instance of {@link DiscMapCache} or <code>null</code> is memory cache is disabled in application's
     */
    public static DiscMapCache createDiscMapCache(ITileStorageStrategy tileStorageStrategy, IMapConfigurationProvider config) {

        DiscMapCache result = null;

        if (config.discCacheEnabled()) {
            IDiscStorageStrategy discStorageStrategy = createDiscStorageStrategy(tileStorageStrategy, config);
            boolean limitEnabled = config.discCacheLimitEnabled();
            int storageLimit = config.discCacheLimit();
            result = new DiscMapCache(discStorageStrategy, limitEnabled ? storageLimit : -1);
        }

        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @see pl.edu.agh.cast.map.editor.cache.IMapCache#getTile(int, int, int)
     */
    @Override
    public ImageData getTile(int zoom, int column, int row) {
        File tilePath = storageStrategy.getPathToTile(zoom, column, row);

        ImageData tile = null;
        if (tilePath.exists()) {
            byte[] data = null;
            try {
                FileInputStream fis = new FileInputStream(tilePath);
                data = FileUtil.streamToByteArray(fis);
                fis.close();
                tile = new ImageData(new ByteArrayInputStream(data));
            } catch (FileNotFoundException e) {
                log.error("Cannot find tile: " + tilePath.getAbsolutePath(), e); //$NON-NLS-1$
            } catch (IOException e) {
                log.error("Exception during obtaining tile: " + tilePath.getAbsolutePath(), e); //$NON-NLS-1$
            }
        }

        return tile;
    }

    /**
     * {@inheritDoc}
     * 
     * @see pl.edu.agh.cast.map.editor.cache.IMapCache#saveTile(int, int, int, org.eclipse.swt.graphics.ImageData)
     */
    @Override
    public void saveTile(int zoom, int column, int row, ImageData tile) {
        File tilePath = storageStrategy.getPathToTile(zoom, column, row);
        File tileDirectory = tilePath.getParentFile();

        if (!tileDirectory.exists()) {
            tileDirectory.mkdirs();
        }

        if (!tilePath.exists()) {
            try {
                long approximateSize = tile.width * tile.height * (tile.depth / 8);

                if (!isLimited() || (currentStorage + approximateSize) / 1048576.0 <= storageLimit) {
                    FileOutputStream fos = new FileOutputStream(tilePath);
                    ImageLoader imageLoader = new ImageLoader();
                    imageLoader.data = new ImageData[] { tile };
                    imageLoader.save(fos, SWT.IMAGE_PNG);
                    fos.close();

                    if (currentStorage != -1L) {
                        currentStorage += tilePath.length();
                    }
                } else {
                    log.info("Disc cache is full. Cannot save tile: " + tilePath.getAbsolutePath()); //$NON-NLS-1$
                }

            } catch (FileNotFoundException e) {
                log.error("Cannot save tile to: " + tilePath.getAbsolutePath(), e); //$NON-NLS-1$
            } catch (IOException e) {
                log.error("IOException during saving tile: " + tilePath.getAbsolutePath(), e); //$NON-NLS-1$
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Attempt of saving tile that already exists: " + tilePath.getAbsolutePath()); //$NON-NLS-1$
            }
        }

    }

    /**
     * {@inheritDoc}
     * 
     * @see pl.edu.agh.cast.common.IDisposable#dispose()
     */
    @Override
    public void dispose() {
    }

    // END API Methods

    /**
     * Return flag that indicates whether disc cache is limited.
     * 
     * @return Flag that indicates whether disc cache is limited.
     */
    private boolean isLimited() {
        return storageLimit != -1 && currentStorage != -1L;
    }

    // BEGIN Inner Classes

    /**
     * Runnable that is responsible for obtaining value of discCacheSize in asynchronous way.
     */
    private class DiscCacheSizeObtainer implements Runnable {
        /**
         * Tiles storage strategy.
         */
        private List<IDiscStorageStrategy> storageStrategies;

        /**
         * Constructor.
         * 
         * @param storageStrategy
         *            Tiles storage strategy.
         */
        public DiscCacheSizeObtainer(List<IDiscStorageStrategy> storageStrategies) {
            this.storageStrategies = storageStrategies;
        }

        /**
         * {@inheritDoc}
         * 
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            long storage = 0;
            LinkedList<File> dirToVisit = new LinkedList<>();
            for (IDiscStorageStrategy storageStrategy : storageStrategies) {
                dirToVisit.add(storageStrategy.getPathToRoot());
            }

            while (!dirToVisit.isEmpty()) {
                File[] listFiles = dirToVisit.removeFirst().listFiles();

                if (listFiles != null) {
                    for (File file : listFiles) {
                        if (Thread.currentThread().isInterrupted()) {
                            return;
                        }

                        if (file.isDirectory()) {
                            dirToVisit.add(file);
                        } else if (file.isFile()) {
                            storage += file.length();
                        }
                    }
                }
            }

            currentStorage = new Long(storage);
        }

    }

    // END Inner Classes

}
