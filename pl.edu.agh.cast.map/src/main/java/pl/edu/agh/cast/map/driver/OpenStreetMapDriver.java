package pl.edu.agh.cast.map.driver;

import org.eclipse.swt.graphics.ImageData;

import pl.edu.agh.cast.common.collections.Pair;
import pl.edu.agh.cast.map.storage.ITileStorageStrategy;
import pl.edu.agh.cast.map.storage.TileStorageStrategy;


// TODO: Auto-generated Javadoc
/**
 * The Class OpenStreetMapDriver.
 *
 * {@link IMapDriver} for OpenStreetMap maps.
 */
public class OpenStreetMapDriver implements IMapDriver {

    private static final String PNG_EXT = "png";
    
    /**
     * Dimension of map's tiles.
     */
    private static final Pair<Integer, Integer> TILE_DIMENSION = new Pair<>(256, 256);

    /**
     * Tile storage strategy.
     */
     private ITileStorageStrategy tileStorageStrategy;

    /** The converter. */
    private IMapCoordinateConverter converter = new MercatorProjectionCoordinateConverter(TILE_DIMENSION);

    /**
     * The Enum OpenStreetMapType.
     */
    public static enum OpenStreetMapType {
        /**
         * Specifies a standard roadmap image for tile, as is normally shown on the OpenStreetMap website.
         */
        ROADMAP("RoadMap");

        /**
         * Description of map type.
         */
        private String description;

        /**
         * Instantiates a new open street map type.
         *
         * @param description the description
         */
        OpenStreetMapType(String description) {
            this.description = description;
        }

        /**
         * {@inheritDoc}
         *
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return description;
        }
    }

    /**
     * Constructor.
     */
    public OpenStreetMapDriver() {
        tileStorageStrategy = new TileStorageStrategy(new String[] { "OpenStreetMapDriver" }, //$NON-NLS-1$
                PNG_EXT, null, null);

    }

    // BEGIN API Methods

    /**
     * {@inheritDoc}
     *
     * @see pl.edu.agh.cast.map.editor.driver.IMapDriver#getTileDimension()
     */
    @Override
    public Pair<Integer, Integer> getTileDimension() {
        return TILE_DIMENSION;
    }

    /**
     * {@inheritDoc}
     *
     * @see pl.edu.agh.cast.map.editor.driver.IMapDriver#getXRange(int)
     */
    @Override
    public int getXRange(int zoom) {
        return (1 << zoom) * TILE_DIMENSION.getFirst();
    }

    /**
     * {@inheritDoc}
     *
     * @see pl.edu.agh.cast.map.editor.driver.IMapDriver#getYRange(int)
     */
    @Override
    public int getYRange(int zoom) {
        return (1 << zoom) * TILE_DIMENSION.getSecond();
    }

    /**
     * {@inheritDoc}
     *
     * @see pl.edu.agh.cast.common.IDisposable#dispose()
     */
    @Override
    public void dispose() {
    }

    /**
     * {@inheritDoc}
     *
     * @see pl.edu.agh.cast.map.editor.driver.IMapDriver#getMaxZoomLevel()
     */
    @Override
    public int getMaxZoomLevel() {
        return 18;
    }

    /**
     * {@inheritDoc}
     *
     * @see pl.edu.agh.cast.map.editor.driver.IMapDriver#getMinZoomLevel()
     */
    @Override
    public int getMinZoomLevel() {
        return 2;
    }

    /**
     * {@inheritDoc}
     *
     * @see pl.edu.agh.cast.map.editor.driver.IMapDriver#getTileStorageStrategy()
     */
    @Override
    public ITileStorageStrategy getTileStorageStrategy() {
        return tileStorageStrategy;
    }

    /**
     * {@inheritDoc}
     *
     * @see pl.edu.agh.cast.map.editor.driver.IMapDriver#getNoTile()
     */
    @Override
    public ImageData getNoTile() {
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @see pl.edu.agh.cast.map.editor.driver.IMapDriver#getLoadTile()
     */
    @Override
    public ImageData getLoadTile() {
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @see pl.edu.agh.cast.map.editor.driver.IMapDriver#getDefaultZoomLevel()
     */
    @Override
    public int getDefaultZoomLevel() {
        return 6;
    }

    /**
     * {@inheritDoc}
     *
     * @see pl.edu.agh.cast.map.editor.driver.IMapCoordinateConverter#longitudeToXCoordinate(double, int)
     */
    @Override
    public int longitudeToXCoordinate(double longitude, int zoom) {
        return converter.longitudeToXCoordinate(longitude, zoom);
    }

    /**
     * {@inheritDoc}
     *
     * @see pl.edu.agh.cast.map.editor.driver.IMapCoordinateConverter#latitudeToYCoordinate(double, int)
     */
    @Override
    public int latitudeToYCoordinate(double latitude, int zoom) {
        return converter.latitudeToYCoordinate(latitude, zoom);
    }

    /**
     * {@inheritDoc}
     *
     * @see pl.edu.agh.cast.map.editor.driver.IMapCoordinateConverter#xCoordinateToLongitue(int, int)
     */
    @Override
    public double xCoordinateToLongitue(int xCoordinate, int zoom) {
        return converter.xCoordinateToLongitue(xCoordinate, zoom);
    }

    /**
     * {@inheritDoc}
     *
     * @see pl.edu.agh.cast.map.editor.driver.IMapCoordinateConverter#yCoordinateToLatitude(int, int)
     */
    @Override
    public double yCoordinateToLatitude(int yCoordinate, int zoom) {
        return converter.yCoordinateToLatitude(yCoordinate, zoom);
    }


    // END API Methods

}
