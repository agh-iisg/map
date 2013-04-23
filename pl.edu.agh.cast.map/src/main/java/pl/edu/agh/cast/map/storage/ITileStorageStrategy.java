package pl.edu.agh.cast.map.storage;

/**
 * Interface for strategies of map tiles storing.
 * 
 */
public interface ITileStorageStrategy {

    /**
     * Returns <b>part of</b> path to root directory. <b>Part</b> means that this method returns only those directories that should be
     * placed somewhere (on hard drive, optical drive, network etc.) in order to localize root path. Root path is directory which stores all
     * tiles or is parent directories for all sub-directories that stores all tiles.
     * 
     * @return Root path.
     */
    public String getRootPath();

    /**
     * Returns <b>part of</b> path to tile. <b>Part</b> means that this method returns only those directories and file that should be placed
     * somewhere (on hard drive, optical drive, network etc.) in order to localize specified tile. E.g. for input
     * <code>getTilePath(5, 2, 1)</code> method can return <em>"SomeDirectory/5/2/1-whatever.png"</em> which means that somewhere there
     * should be 3 directories: <em>"SomeDirectory"</em>, <em>"5"</em>, <em>"2"</em> and file <em>"1-whatever.png"</em> where tile should be
     * stored. Creating concrete file path should be done by external mechanism (e.g. {@link IDiscStorageStrategy}).
     * 
     * @param zoom
     *            Map's zoom level.
     * @param column
     *            Tile's column number.
     * @param row
     *            Tile's row number.
     * @return Path to tile.
     */
    public String getTilePath(int zoom, int column, int row);

}
