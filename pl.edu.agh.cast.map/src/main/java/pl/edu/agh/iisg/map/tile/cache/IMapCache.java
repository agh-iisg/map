package pl.edu.agh.iisg.map.tile.cache;

import org.eclipse.swt.graphics.ImageData;

import pl.edu.agh.cast.common.IDisposable;

/**
 * Map cache is one of the elements in map tile sources chain. Map cache has been distinguished from
 * {@link pl.edu.agh.iisg.map.tile.provider.editor.provider.IMapProvider} due to the fact that cache is operating on already loaded tiles (In fact
 * definition of 'already loaded tiles' can vary in different {@link IMapCache} implementation) to enhance performance of obtaining tiles
 * greatly.
 * 
 */
public interface IMapCache extends IDisposable {

    /**
     * Return tile described by 3 parameters - map's zoom level, tile's column number and tile's row number.<br/>
     * Tiles can be requested synchronously in UI Thread or asynchronously from other thread, so <b>synchronization if needed should be
     * considered</b>.
     * 
     * @param zoom
     *            Map's zoom level.
     * @param column
     *            Tile's column number.
     * @param row
     *            Tile's row number.
     * 
     * @return Image of requested tile or <code>null</code> otherwise.
     */
    ImageData getTile(int zoom, int column, int row);

    /**
     * Saves tile (described by 3 parameters - map's zoom level, tile's column number and tile's row number) in cache. Tiles can be provided
     * synchronously in UI Thread or asynchronously from other thread, so <b>synchronization if needed should be considered</b>.
     * 
     * @param zoom
     *            Map's zoom level.
     * @param column
     *            Tile's column number.
     * @param row
     *            Tile's row number.
     * @param tile
     *            Image of tile.
     */
    void saveTile(int zoom, int column, int row, ImageData tile);

}
