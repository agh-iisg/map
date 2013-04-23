package pl.edu.agh.iisg.map.tile.storage;

import java.io.File;

import pl.edu.agh.cast.common.StringConstants;

/**
 * Simple implementation of {@link ITileStorageStrategy} for CAST application. Strategy of tile storing:
 * {directory-1}/.../{directory-N}/{zoom}/{column}/{prefix}{row}{suffix}.{extension}.
 * 
 */
public class TileStorageStrategy implements ITileStorageStrategy {

    /**
     * Top directories before directories of zoom and column. Concatenated to one string.
     */
    private String directories;

    /**
     * Extension of tile file.
     */
    private final String extension;

    /**
     * Prefix of tile file.
     */
    private final String filePrefix;

    /**
     * Suffix of tile file. Will be placed before extension.
     */
    private final String fileSuffix;

    /**
     * Constructor. Please see API of this class to learn about tile storing strategy of this class.
     * 
     * @param directories
     *            Top directories before directories of zoom and column. May be <code>null</code>.
     * @param extension
     *            Extension of tile file.
     * @param filePrefix
     *            Prefix of tile file. May be <code>null</code>
     * @param fileSuffix
     *            Suffix of tile file. Will be placed before extension. May be <code>null</code>.
     */
    public TileStorageStrategy(String[] directories, String extension, String filePrefix, String fileSuffix) {
        if (extension == null) {
            throw new IllegalArgumentException("Extension need to be specified."); //$NON-NLS-1$
        }

        if (directories != null) {
            StringBuilder builder = new StringBuilder();
            for (String directory : directories) {
                builder.append(String.format("%s%s", directory, File.separator)); //$NON-NLS-1$
            }
            this.directories = builder.toString();
        }

        this.extension = extension;
        this.filePrefix = filePrefix != null ? filePrefix : StringConstants.EMPTY;
        this.fileSuffix = fileSuffix != null ? fileSuffix : StringConstants.EMPTY;
    }

    @Override
    public String getRootPath() {
        return directories;
    }

    @Override
    public String getTilePath(int zoom, int column, int row) {
        return String.format("%s%d%s%d%s%s%d%s.%s", getRootPath(), zoom, File.separator, column, //$NON-NLS-1$
                File.separator, filePrefix, row, fileSuffix, extension);
    }

}
