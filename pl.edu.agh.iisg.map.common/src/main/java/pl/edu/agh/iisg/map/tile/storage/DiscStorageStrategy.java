package pl.edu.agh.iisg.map.tile.storage;

import java.io.File;

/**
 * Default implementation of {@link IDiscStorageStrategy} based on {@link ITileStorageStrategy}.
 * 
 */
public class DiscStorageStrategy implements IDiscStorageStrategy {

    /**
     * Beginning of the path where tiles are stored. Points to concrete location for {@link ITileStorageStrategy}. This path will be added
     * before result of {@link ITileStorageStrategy#getTilePath(int, int, int)}.
     */
    private final String storageDirectory;

    /**
     * Tile storage strategy. Please see the API of {@link ITileStorageStrategy} to learn more.
     */
    private final ITileStorageStrategy tileStorageStrategy;

    /**
     * Constructor.
     * 
     * @param storageDirectory
     *            Beginning of the path where tiles are stored. Points to concrete location for {@link ITileStorageStrategy}. This path will
     *            be added before result of {@link ITileStorageStrategy#getTilePath(int, int, int)}.
     * @param tileStorageStrategy
     *            Tile storage strategy. Please see the API of {@link ITileStorageStrategy} to learn more.
     * 
     * @see ITileStorageStrategy.
     */
    public DiscStorageStrategy(File storageDirectory, ITileStorageStrategy tileStorageStrategy) {
        if (storageDirectory == null) {
            throw new IllegalArgumentException("Argument storageDirectory cannot be null"); //$NON-NLS-1$
        }
        if (tileStorageStrategy == null) {
            throw new IllegalArgumentException("Argument tileStorageStrategy cannot be null"); //$NON-NLS-1$
        }
        if (!storageDirectory.exists()) {
            throw new IllegalArgumentException(String.format("Directory \"%s\" does not exists.", //$NON-NLS-1$
                    storageDirectory.getAbsolutePath()));
        }

        this.storageDirectory = storageDirectory.getAbsolutePath() + File.separator;
        this.tileStorageStrategy = tileStorageStrategy;
    }

    /**
     * {@inheritDoc}
     * 
     * @see pl.edu.agh.iisg.map.tile.storage.editor.storage.IDiscStorageStrategy#getPathToRoot()
     */
    @Override
    public File getPathToRoot() {
        return new File(storageDirectory.concat(tileStorageStrategy.getRootPath()));
    }

    /**
     * {@inheritDoc}
     * 
     * @see pl.edu.agh.iisg.map.tile.storage.editor.storage.IDiscStorageStrategy#getPathToTile(int, int, int)
     */
    @Override
    public File getPathToTile(int zoom, int column, int row) {
        return new File(storageDirectory.concat(tileStorageStrategy.getTilePath(zoom, column, row)));
    }

}
