package pl.edu.agh.cast.map.provider;


/**
 * Provides OpenStreetMap tiles from Internet (http://tile.openstreetmap.org/).
 *
 */
public class OpenStreetMapInternetProvider extends AbstractInternetMapProvider {

    /**
     * Web separator.
     */
    private static final String WS = "/";

    /**
     * URL base to Open Street Maps.
     */
    private static final String URL_BASE = "http://tile.openstreetmap.org/"; //$NON-NLS-1$

    /**
     * Format of image.
     */
    private static final String IMAGE_FORMAT = "png"; //$NON-NLS-1$

    // BEGIN API Methods

    /**
     * {@inheritDoc}
     *
     * @see pl.edu.agh.cast.map.editor.provider.AbstractInternetMapProvider#getTileURL(int, int, int)
     */
    @Override
    protected String getTileURL(int zoom, int column, int row) {
        StringBuffer buffer = new StringBuffer(URL_BASE);
        buffer.append(zoom);
        buffer.append(WS);
        buffer.append(column);
        buffer.append(WS);
        buffer.append(row);
        buffer.append('.');
        buffer.append(IMAGE_FORMAT);
        System.out.println(buffer.toString());
        return buffer.toString();
    }

    /**
     * {@inheritDoc}
     *
     * @see pl.edu.agh.cast.map.editor.provider.AbstractMapProvider#getDescription()
     */
    @Override
    public String getDescription() {
        return "OpenStreetMap (http://www.openstreetmap.org/)"; //$NON-NLS-1$
    }

    // END API Methods

}
