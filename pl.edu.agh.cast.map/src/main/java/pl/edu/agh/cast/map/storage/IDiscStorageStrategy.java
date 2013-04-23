package pl.edu.agh.cast.map.storage;

import java.io.File;

/**
 * Interface for storing map tiles on disk strategies.
 * 
 */
public interface IDiscStorageStrategy {

    /**
     * Returns path to root directory where all tiles are stored (if tiles are stored in sub-directories than returned directory is its
     * parent directory).
     * 
     * @return Path to root directory where all tiles are stored (if tiles are stored in sub-directories than returned directory is its
     *         parent directory).
     */
    public File getPathToRoot();

    /**
     * Returns path to tile.
     * 
     * @param zoom
     *            Map's zoom level.
     * @param column
     *            Tile's column number.
     * @param row
     *            Tile's row number.
     * 
     * @return Path to tile.
     */
    public File getPathToTile(int zoom, int column, int row);
}
