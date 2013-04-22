package pl.edu.agh.cast.map.provider;

import org.eclipse.swt.graphics.ImageData;

/**
 * Interface for listeners that will be notified when requested tile (requested in asynchronously way) is available.
 * 
 */
public interface IAsynchronousTileResultListener {
    /**
     * Method is invoked by {@link pl.edu.agh.cast.map.editor.manager.IMapManager} when requested tile is available.
     * 
     * @param tile
     *            Requested tile. Result may be <code>null</code>.
     * @param zoom
     *            Zoom level of requested tile.
     * @param column
     *            Column number of requested tile.
     * @param row
     *            Row number of requested tile.
     */
    public void resultAvailable(ImageData tile, int zoom, int column, int row);
}
