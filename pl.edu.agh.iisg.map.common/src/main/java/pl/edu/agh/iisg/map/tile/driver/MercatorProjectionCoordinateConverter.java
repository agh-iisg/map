package pl.edu.agh.iisg.map.tile.driver;

import pl.edu.agh.cast.common.collections.Pair;

/**
 * Coordinates converter (geographical latitude/longitude <=> bitmap pixels x/y) due to Mercator projection project:
 * http://en.wikipedia.org/wiki/Mercator_projection.
 *
 */
public class MercatorProjectionCoordinateConverter implements IMapCoordinateConverter {

    /**
     * Dimension of particular tile.
     */
    private final Pair<Integer, Integer> tileDimension;

    /**
     * Constructor.
     *
     * @param tileDimension
     *            Dimension of particular tile.
     */
    public MercatorProjectionCoordinateConverter(Pair<Integer, Integer> tileDimension) {
        this.tileDimension = tileDimension;
    }

    /**
     * Constructor.
     *
     * @param width
     *            Width dimension of particular tile.
     * @param height
     *            Height dimension of particular tile.
     */
    public MercatorProjectionCoordinateConverter(Integer width, Integer height) {
        tileDimension = new Pair<>(width, height);
    }

    // BEGIN API Methods

    /**
     * Converts longitude to X coordinate at specified zoom level. Range of X coordinates depends on zoom level. <br>
     * <b>CAUTION:</b> This method may require a lot of mathematical computation so use it wisely.
     *
     * @param longitude
     *            Longitude to convert.
     * @param zoom
     *            Specified zoom level.
     * @param width
     *            Width dimension of particular tile.
     * @return Converted longitude to X coordinate at specified zoom level.
     */
    public static int longitudeToXCoordinate(double longitude, int zoom, Integer width) {
        return (int)((longitude + 180.0) / 360.0 * (1 << zoom) * width);
    }

    /**
     * Converts latitude to Y coordinate at specified zoom level. Range of Y coordinates depends on zoom level. <br>
     * <b>CAUTION:</b> This method may require a lot of mathematical computation so use it wisely.
     *
     * @param latitude
     *            Latitude to convert.
     * @param zoom
     *            Specified zoom level.
     * @param height
     *            Height dimension of particular tile.
     * @return Converted latitude to Y coordinate at specified zoom level.
     */
    public static int latitudeToYCoordinate(double latitude, int zoom, Integer height) {
        return (int)((1 - Math.log(Math.tan(Math.toRadians(latitude)) + 1 / Math.cos(Math.toRadians(latitude))) / Math.PI) / 2
                * (1 << zoom) * height);
    }

    /**
     * Converts X coordinate to longitude at specified zoom level. <br>
     * <b>CAUTION:</b> This method may require a lot of mathematical computation so use it wisely.
     *
     * @param xCoordinate
     *            X coordinate to convert. Range of X coordinates depends on zoom level.
     * @param zoom
     *            Specified zoom level.
     * @param width
     *            Width dimension of particular tile.
     * @return Converted X coordinate to longitude at specified zoom level.
     */
    public static double xCoordinateToLongitued(int xCoordinate, int zoom, Integer width) {
        return (double)xCoordinate / (double)width / (1 << zoom) * 360.0 - 180.0;
    }

    /**
     * Converts Y coordinate to latitude at specified zoom level. <br>
     * <b>CAUTION:</b> This method may require a lot of mathematical computation so use it wisely.
     *
     * @param yCoordinate
     *            Y coordinate to convert. Range of Y coordinates depends on zoom level.
     * @param zoom
     *            Specified zoom level.
     * @param height
     *            Height dimension of particular tile.
     * @return Converted Y coordinate to latitude at specified zoom level.
     */
    public static double yCoordinateToLatitude(int yCoordinate, int zoom, Integer height) {
        return Math.atan(Math.sinh(Math.PI * (1 - 2 * yCoordinate / (double)height / (1 << zoom)))) * 180.0 / Math.PI;
    }

    /**
     * {@inheritDoc}
     *
     * @see pl.edu.agh.iisg.map.tile.driver.editor.driver.IMapCoordinateConverter#longitudeToXCoordinate(double, int)
     */
    @Override
    public int longitudeToXCoordinate(double longitude, int zoom) {
        return longitudeToXCoordinate(longitude, zoom, tileDimension.getFirst());
    }

    /**
     * {@inheritDoc}
     *
     * @see pl.edu.agh.iisg.map.tile.driver.editor.driver.IMapCoordinateConverter#latitudeToYCoordinate(double, int)
     */
    @Override
    public int latitudeToYCoordinate(double latitude, int zoom) {
        return latitudeToYCoordinate(latitude, zoom, tileDimension.getSecond());
    }

    /**
     * {@inheritDoc}
     *
     * @see pl.edu.agh.iisg.map.tile.driver.editor.driver.IMapCoordinateConverter#xCoordinateToLongitue(int, int)
     */
    @Override
    public double xCoordinateToLongitue(int xCoordinate, int zoom) {
        return xCoordinateToLongitued(xCoordinate, zoom, tileDimension.getFirst());
    }

    /**
     * {@inheritDoc}
     *
     * @see pl.edu.agh.iisg.map.tile.driver.editor.driver.IMapCoordinateConverter#yCoordinateToLatitude(int, int)
     */
    @Override
    public double yCoordinateToLatitude(int yCoordinate, int zoom) {
        return yCoordinateToLatitude(yCoordinate, zoom, tileDimension.getSecond());
    }

    // END API Methods
}
