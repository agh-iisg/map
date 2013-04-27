package pl.edu.agh.iisg.map.tile.cache;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.ImageData;

import pl.edu.agh.iisg.map.config.IMapConfigurationProvider;

/**
 * Caches map's tiles in memory.
 * 
 */
public class MemoryMapCache implements IMapCache {

	/**
	 * Logger.
	 */
	private static Logger log = Logger.getLogger(MemoryMapCache.class);

	/**
	 * Limit of operational memory (In megabytes).
	 */
	private int memoryLimit;

	/**
	 * Size of allocated memory (in bytes).
	 */
	private long allocatedMemory;

	/**
	 * Map of cached tiles.
	 */
	private Map<String, ImageData> cachedTiles = Collections
			.synchronizedMap(new LinkedHashMap<String, ImageData>());

	/**
	 * Constructor.
	 * 
	 * @param memoryLimit
	 *            Limit of memory that can be used for tiles storing (in
	 *            megabytes).
	 */
	public MemoryMapCache(int memoryLimit) {
		this.memoryLimit = memoryLimit;
		allocatedMemory = 0;
	}

	// BEGIN API Methods

	/**
	 * Creates instance of {@link MemoryMapCache}. Appropriate memory cache's
	 * configuration is obtained from given {@link IMapConfigurationProvider}.
	 * If memory cache is disabled in application's setting than
	 * <code>null</code> will be returned.
	 * 
	 * @return Instance of {@link MemoryMapCache} or <code>null</code> is memory
	 *         cache is disabled
	 */
	public static MemoryMapCache createMemoryMapCache(
			IMapConfigurationProvider config) {
		MemoryMapCache result = null;

		if (config.memoryCacheEnabled()) {
			result = new MemoryMapCache(config.memoryCacheLimit());
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see pl.edu.agh.iisg.map.tile.cache.editor.cache.IMapCache#getTile(int, int, int)
	 */
	@Override
	public ImageData getTile(int zoom, int column, int row) {
		return cachedTiles.get(getKey(zoom, column, row));
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see pl.edu.agh.iisg.map.tile.cache.editor.cache.IMapCache#saveTile(int, int, int,
	 *      org.eclipse.swt.graphics.ImageData)
	 */
	@Override
	public void saveTile(int zoom, int column, int row, ImageData tile) {
		String key = getKey(zoom, column, row);
		ImageData old = cachedTiles.put(key, tile);
		if (old != null) {
			if (log.isDebugEnabled()) {
				log.debug(String.format(
						"Tile under key %s has been overriden.", key)); //$NON-NLS-1$
			}
		}

		long newSize = sizeOfImage(tile);
		long oldSize = sizeOfImage(old);
		allocatedMemory += newSize - oldSize;

		while ((allocatedMemory / 1048576.0) > memoryLimit) {
			synchronized (cachedTiles) {
				Iterator<String> iterator = cachedTiles.keySet().iterator();
				if (iterator.hasNext()) {
					allocatedMemory -= sizeOfImage(cachedTiles.get(iterator
							.next()));
					iterator.remove();
				}
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
		cachedTiles.clear();
		allocatedMemory = 0;
	}

	// END API Methods

	/**
	 * Returns key for specified attributes.
	 * 
	 * @param zoom
	 *            Tile zoom level.
	 * @param column
	 *            Tile column.
	 * @param row
	 *            Tile row.
	 * @return Key for specified attributes.
	 */
	private String getKey(int zoom, int column, int row) {
		String separator = "/"; //$NON-NLS-1$
		return zoom + separator + column + separator + row;
	}

	/**
	 * Returns size of image in memory.
	 * 
	 * @param image
	 *            Image to investigate.
	 * @return Size of image in memory.
	 */
	private long sizeOfImage(ImageData image) {
		if (image == null) {
			return 0;
		}

		return image.width * image.height * (image.depth / 8);
	}

}
