package pl.edu.agh.cast.map.driver;

import com.google.common.base.Objects;

/**
 * Provides unique key for map vendor, map driver and map type.
 */
public class MapKey {

    private final String mapVendorId;

    private final String mapDriverId;

    private final String mapTypeId;

    /**
     * Instantiates a new map key.
     *
     * @param mapVendorId - map vendor id (e.g. maps.google.com)
     * @param mapDriverId - map driver id
     * @param mapTypeId - map type id (e.g. SATELLITE)
     */
    public MapKey(String mapVendorId, String mapDriverId, String mapTypeId) {
        this.mapVendorId = mapVendorId;
        this.mapDriverId = mapDriverId;
        this.mapTypeId = mapTypeId;
    }

    /**
     * Gets the map driver id.
     *
     * @return the mapDriverId
     */
    public String getMapDriverId() {
        return mapDriverId;
    }

    /**
     * Gets the map type id.
     *
     * @return the mapTypeId
     */
    public String getMapTypeId() {
        return mapTypeId;
    }

    /**
     * Gets the map vendor id.
     *
     * @return the mapVendorId
     */
    public String getMapVendorId() {
        return mapVendorId;
    }

    @Override
    public String toString() {
        return String.format("%s %s: %s", mapVendorId, mapDriverId, mapTypeId); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof MapKey)) {
            return false;
        }
        MapKey that = (MapKey)obj;
        return (mapVendorId.equals(that.getMapVendorId()))
                && (mapDriverId.equals(that.getMapDriverId())) && (mapTypeId.equals(that.getMapTypeId()));
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(mapVendorId, mapDriverId, mapTypeId);
    }
}
