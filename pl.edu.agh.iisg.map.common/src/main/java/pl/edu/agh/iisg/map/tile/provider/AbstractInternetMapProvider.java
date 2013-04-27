package pl.edu.agh.iisg.map.tile.provider;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.ImageData;

/**
 * Abstract class that provides mechanism for tile obtaining from Internet.
 *
 */
public abstract class AbstractInternetMapProvider extends AbstractMapProvider {

    private static final String INTERNET_MAP_PROVIDER_RETRY_ATTEMPTS_AMOUNT = "InternetMap_retryAttemptsAmount"; //$NON-NLS-1$

    /** Default retry attempts amount. */
    private static final int DEFAULT_RETRY_ATTEMPTS_AMOUNT = 1;

    /** Minium retry attempts amount that can be set by user. */
    private static final int MIN_RETRY_ATTEMPTS_AMOUNT = 1;

    private int retryAttempts = 3;

    /**
     * Logger.
     */
    private static Logger log = Logger.getLogger(AbstractInternetMapProvider.class);

    /**
     * {@inheritDoc}
     *
     * @see pl.edu.agh.iisg.map.tile.provider.editor.provider.IMapProvider#getTile(int, int, int)
     */
    @Override
    public ImageData getTile(int zoom, int column, int row) {
        String urlString = getTileURL(zoom, column, row);

        ImageData tile = null;
        
        int currentRetryAttempts = this.retryAttempts;

        while (currentRetryAttempts > 0) {
            try {
                URL url = new URL(urlString);
                byte[] data = readDataFromURL(url);
                if (data != null) {
                    tile = new ImageData(new ByteArrayInputStream(data));
                    break; // Got tile, leaving the while loop.
                }
                currentRetryAttempts--;
            } catch (IOException e) {
                log.error("Error during obtaining tile from URL: " + urlString, e); //$NON-NLS-1$
                currentRetryAttempts--;
            }
        }
        return tile;
    };

    /**
     * {@inheritDoc}
     *
     * @see pl.edu.agh.cast.common.IDisposable#dispose()
     */
    @Override
    public void dispose() {
    }

    /**
     * Return URL where tile is located.
     *
     * @param zoom
     *            Tile zoom.
     * @param column
     *            Tile column.
     * @param row
     *            tile row
     * @return URL where tile is located.
     */
    protected abstract String getTileURL(int zoom, int column, int row);

    /**
     * Reads data from URL.
     *
     * @param url
     *            URL to site.
     * @return Data from URL.
     * @throws IOException
     * @throws IOException
     */
    private byte[] readDataFromURL(URL url) throws IOException  {
        URLConnection conn = url.openConnection();
        conn.setConnectTimeout(1000);

        if (conn.getContentLength() > 0) {
            byte[] data = new byte[conn.getContentLength()];

            int bytesRead = 0;
            int totalBytes = 0;
            while ((bytesRead = conn.getInputStream().read(data, totalBytes, data.length - totalBytes)) != -1) {
                totalBytes += bytesRead;
            }
            return data;
        }
        return null;
    }
}
