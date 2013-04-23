package pl.edu.agh.iisg.map.tile.driver;

/**
 * Interface that defines methods for converting coordinates.
 *
 */
public interface IMapCoordinateConverter {

    /** Maximum absolute latitude value constant. */
    public static final double MAX_LATITUDE_VALUE = 90.0;

    /** Maximum absolute longitude value constant. */
    public static final double MAX_LONGITUDE_VALUE = 180.0;

    /**
     * Converts longitude to X coordinate at specified zoom level. Range of X coordinates depends on zoom level. <br>
     * <b>CAUTION:</b> This method may require a lot of mathematical computation so use it wisely.
     *
     * @param longitude
     *            Longitude to convert.
     * @param zoom
     *            Specified zoom level.
     * @return Converted longitude to X coordinate at specified zoom level.
     */
    public int longitudeToXCoordinate(double longitude, int zoom);

    /**
     * Converts latitude to Y coordinate at specified zoom level. Range of Y coordinates depends on zoom level. <br>
     * <b>CAUTION:</b> This method may require a lot of mathematical computation so use it wisely.
     *
     * @param latitude
     *            Latitude to convert.
     * @param zoom
     *            Specified zoom level.
     * @return Converted latitude to Y coordinate at specified zoom level.
     */
    public int latitudeToYCoordinate(double latitude, int zoom);

    /**
     * Converts X coordinate to longitude at specified zoom level. <br>
     * <b>CAUTION:</b> This method may require a lot of mathematical computation so use it wisely.
     *
     * @param xCoordinate
     *            X coordinate to convert. Range of X coordinates depends on zoom level.
     * @param zoom
     *            Specified zoom level.
     * @return Converted X coordinate to longitude at specified zoom level.
     */
    public double xCoordinateToLongitue(int xCoordinate, int zoom);

    /**
     * Converts Y coordinate to latitude at specified zoom level. <br>
     * <b>CAUTION:</b> This method may require a lot of mathematical computation so use it wisely.
     *
     * @param yCoordinate
     *            Y coordinate to convert. Range of Y coordinates depends on zoom level.
     * @param zoom
     *            Specified zoom level.
     * @return Converted Y coordinate to latitude at specified zoom level.
     */
    public double yCoordinateToLatitude(int yCoordinate, int zoom);

}
