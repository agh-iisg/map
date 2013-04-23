package pl.edu.agh.iisg.map.tile.driver;


/**
 * Utility class for {@link IMapCoordinateConverter}.
 *
 */
public class MapCoordinateConverters {

	private static IMapDriver osmDriver = new OpenStreetMapDriver();
	
    /**
     * Returns {@link IMapCoordinateConverter} actually used by {@link pl.edu.agh.iisg.map.tile.manager.editor.manager.IMapManager}. If
     * <tt>IMapManager</tt> is disposed or has not been initialized yet than <code>null</code> will be returned.
     *
     * @param mapKey
     *            the map key
     * @return {@link IMapCoordinateConverter} actually used by <tt>IMapManager</tt> or <code>null</code> if <tt>IMapManager</tt> id
     *         disposed or has not been initialized yet.
     */
    public static IMapCoordinateConverter getCoordinateConverter(MapKey mapKey) {
        return osmDriver;
    }

}
