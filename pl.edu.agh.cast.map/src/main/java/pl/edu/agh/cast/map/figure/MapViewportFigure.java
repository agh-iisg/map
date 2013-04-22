package pl.edu.agh.cast.map.figure;

import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

import pl.edu.agh.cast.common.collections.Pair;
import pl.edu.agh.cast.map.manager.IMapManager;
import pl.edu.agh.cast.map.model.type.Coordinate;
import pl.edu.agh.cast.map.provider.IAsynchronousTileResultListener;

/**
 * Figure that is responsible for displaying map in background.
 *
 */
public class MapViewportFigure extends Figure implements IMapViewportFigure, IAsynchronousTileResultListener {

    /**
     * 'Loading tile' image data. It is required to check if all tiles were loaded.
     */
    // Hack due to exporting map to image.
    private ImageData loadingTileImage;

    /**
     * Image for tiles that does not exist really (Map does not provide this tile).
     */
    private ImageData emptyTile;

    /**
     * Actual zoom level.
     */
    private int zoomLevel = -1;
    
    /**
     * Manager of map's tiles that is responsible for obtaining specified tiles.
     */
    private IMapManager mapManager;
    
    

    /**
     * Longitude of point situated in the center of map.
     */
    private Coordinate longitude = null;

    /**
     * Latitude of point situated in the center of map.
     */
    private Coordinate latitude = null;

    /**
     * Offset in pixels from real x-coordinate for point longitude and x-coordinate of tile where point is located.
     */
    private int xPositionOffset;

    /**
     * Offset in pixels from real y-coordinate for point longitude and y-coordinate of tile where point is located.
     */
    private int yPositionOffset;

    /**
     * Dimension of map's tiles.
     */
    private Pair<Integer, Integer> tileDimension;

    /**
     * Concurrent map which is used in order to obtain information on how many tiles is still not loaded.
     */
    private final ConcurrentHashMap<Pair<Integer, Integer>, Boolean> notDownloaded = new ConcurrentHashMap<>();

    /**
     * Image of buffered map tiles with additionally tiles. Size of essential part of map is determined by: size of editor's window (size of
     * this figure) and size of single map's tile. Number of additional tiles are determined by two fields: xAdditionalTiles,
     * yAdditionalTiles.
     */
    private Image mapTiles;

    /**
     * X tiles to load are calculated due to the amount of tiles that can be displayed in editor + xOffestTiles on the left and right.
     */
    private int xTilesToLoad = 0;

    /**
     * Y tiles to load are calculated due to the amount of tiles that can be displayed in editor + yOffestTiles on the top and bottom.
     */
    private int yTilesToLoad = 0;

    /**
     * Determines how many additional tiles in x direction (on both sides) should be loaded and displayed on mapTiles.
     */
    private final int xAdditionalTiles = 2;

    /**
     * Determines how many additional tiles in y direction (on both sides) should be loaded and displayed on mapTiles.
     */
    private final int yAdditionalTiles = 1;

    /**
     * Number (x coordinate) of tile in top left corner of mapTiles.
     */
    private int xStartTile;

    /**
     * Number (y coordinate) of tile in top left corner of mapTiles.
     */
    private int yStartTile;

    /**
     * Number (x coordinate) of tile in bottom right corner of mapTiles.
     */
    private int xStopTile;

    /**
     * Number (y coordinate) of tile in bottom right corner of mapTiles.
     */
    private int yStopTile;

    /**
     * Keeps information about x position of top left point in map editor. It is position of pixel relative to whole provided map at actual
     * zoom level.
     */
    private int topLeftX;

    /**
     * Keeps information about y position of top left point in map editor. It is position of pixel relative to whole provided map at actual
     * zoom level.
     */
    private int topLeftY;

    /**
     * Default constructor. Sets layout.
     */
    public MapViewportFigure() {
        setLayoutManager(new XYLayout());
    }

    // BEGIN API Methods

    /**
     * {@inheritDoc}
     *
     * @see pl.edu.agh.cast.map.editor.figure.IMapViewportFigure#setPosition(double, double)
     */
    @Override
    public void setPosition(Coordinate lon, Coordinate lat) {
        setLongitude(lon);
        setLatitude(lat);
    }

    /**
     * {@inheritDoc}
     *
     * @see pl.edu.agh.cast.map.editor.figure.IMapViewportFigure#setMapManager(pl.edu.agh.cast.map.editor.manager.IMapManager)
     */
    @Override
    public void setMapManager(IMapManager mapManager) {
        this.mapManager = mapManager;

        tileDimension = mapManager.getMapDriver().getTileDimension();

        // Creates tile for situation when tile does not exists at all
        Image emptyTileImage = new Image(Display.getCurrent(), tileDimension.getFirst(), tileDimension.getSecond());
        GC gc = new GC(emptyTileImage);
        gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_BACKGROUND));
        gc.dispose();

        emptyTile = emptyTileImage.getImageData();
        emptyTileImage.dispose();

        setOpaque(true);

        initializeMapTiles();
    }

    /**
     * {@inheritDoc}
     *
     * @see pl.edu.agh.cast.map.editor.figure.IMapViewportFigure#setLongitude(double)
     */
    @Override
    public void setLongitude(Coordinate longitude) {
        this.longitude = longitude;

        int x = 0;
        if (getSafeSize().width != 0) {
            // Change xPositionOffset (offset between real longitude and corner of tile)
            x = mapManager.getMapDriver().longitudeToXCoordinate(longitude.getDecimalDegrees(), zoomLevel) - getSafeSize().width / 2;
            topLeftX = x;
        }

        // Check if map has been initialized
        if (xTilesToLoad != 0) {
            xPositionOffset = x % tileDimension.getFirst();

            int xNewStartTile = x / tileDimension.getFirst() - xAdditionalTiles;
            if (xNewStartTile != xStartTile) {
                moveMapTiles(xNewStartTile, yStartTile);
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see pl.edu.agh.cast.map.editor.figure.IMapViewportFigure#setLatitude(double)
     */
    @Override
    public void setLatitude(Coordinate latitude) {
        this.latitude = latitude;

        int y = 0;
        if (getSafeSize().height != 0) {
            // Change yPositionOffset (offset between real latitude and corner of tile)
            y = mapManager.getMapDriver().latitudeToYCoordinate(latitude.getDecimalDegrees(), zoomLevel) - getSafeSize().height / 2;
            topLeftY = y;
        }

        // Check if map has been initialized
        if (yTilesToLoad != 0) {
            yPositionOffset = y % tileDimension.getSecond();

            int yNewStartTile = y / tileDimension.getSecond() - yAdditionalTiles;
            if (yNewStartTile != yStartTile) {
                moveMapTiles(xStartTile, yNewStartTile);
            }

        }

    }

    /**
     * {@inheritDoc}
     *
     * @see pl.edu.agh.cast.map.editor.figure.IMapViewportFigure#setZoomLevel(int)
     */
    @Override
    public void setZoomLevel(int zoomLevel) {
        int oldZoomLevel = this.zoomLevel;
        this.zoomLevel = zoomLevel;

        if (latitude != null && longitude != null && oldZoomLevel != zoomLevel) {
            initializeMapTiles();
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see pl.edu.agh.cast.map.editor.figure.IMapViewportFigure#setPositionAndZoomLevel(double, double, int)
     */
    @Override
    public void setPositionAndZoomLevel(Coordinate lon, Coordinate lat, int zoom) {
        this.longitude = lon;
        this.latitude = lat;
        setZoomLevel(zoom);
    }

    /**
     * {@inheritDoc}
     *
     * @see pl.edu.agh.cast.map.editor.figure.IMapViewportFigure#getTopLeftX()
     */
    @Override
    public int getTopLeftX() {
        return topLeftX;
    }

    /**
     * {@inheritDoc}
     *
     * @see pl.edu.agh.cast.map.editor.figure.IMapViewportFigure#getTopLeftY()
     */
    @Override
    public int getTopLeftY() {
        return topLeftY;
    }

    /**
     * {@inheritDoc}
     *
     * @see pl.edu.agh.cast.common.IDisposable#dispose()
     */
    @Override
    public void dispose() {
    }

    @Override
    public void resultAvailable(final ImageData tile, final int zoom, final int column, final int row) {
//        ApplicationUtil.runInUIThread(
    	Display.getDefault().asyncExec(new Runnable() {

            @Override
            public void run() {
                putLoadedTile(tile, zoom, column, row, true);
                if (!loadingTileImage.equals(tile)) {
                    notDownloaded.remove(new Pair<>(column, row));
                }
            }
        });
//    	, ApplicationUtil.LOGGER_EXCEPTION_HANDLER);

    }

    private final Dimension getSafeSize() {
        Dimension figSize = getSize().getCopy();

        if (topLeftX < 0) {
            figSize.width += topLeftX;
        }
        if (topLeftY < 0) {
            figSize.height += topLeftY;
        }
        return figSize;
    }

    /**
     * {@inheritDoc}
     *
     * @see pl.edu.agh.cast.map.editor.figure.IMapViewportFigure#getMiddlePoint()
     */
    @Override
    public Point getMiddlePoint() {
        Dimension size = getSafeSize();
        return new Point(size.width / -2, size.height / -2);
    }

    // END API Methods

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.draw2d.Figure#paintFigure(org.eclipse.draw2d.Graphics)
     */
    @Override
    protected void paintFigure(Graphics graphics) {
        // Check if editor's field hasn't change.
        Pair<Integer, Integer> tilesToLoad = getTilesToLoad();
        if (tilesToLoad.getFirst() != xTilesToLoad || tilesToLoad.getSecond() != yTilesToLoad) {
            initializeMapTiles();
        }

        // Copy buffered mapTiles into editor.
        int x = xAdditionalTiles * tileDimension.getFirst() + xPositionOffset;
        int y = yAdditionalTiles * tileDimension.getSecond() + yPositionOffset;
        int w = getSize().width;
        int h = getSize().height;

        graphics.drawImage(mapTiles, x, y, w, h, topLeftX, topLeftY, w, h);
    }

    /**
     * Initialize mapTiles. Method is responsible for drawing fresh map. Drawing is processed asynchronously. There are only asynchronously
     * requests for map's tiles.
     */
    private void initializeMapTiles() {
    	Dimension figSize = getSafeSize();
    	// tilesToComplete.set(0);
        // Do not initialize tiles when figure size is not set.
        if (figSize.width == 0 || figSize.height == 0) {
            return;
        }

        Pair<Integer, Integer> tilesToLoad = getTilesToLoad();
        xTilesToLoad = tilesToLoad.getFirst();
        yTilesToLoad = tilesToLoad.getSecond();

        // Create new buffered image for map's tiles
        Image oldImage = mapTiles;
        mapTiles = new Image(Display.getCurrent(), xTilesToLoad * tileDimension.getFirst(), yTilesToLoad * tileDimension.getSecond());

        // Dispose old image
        if (oldImage != null && !oldImage.isDisposed()) {
            oldImage.dispose();
        }

        // Get x and y position for requested latitude and longitude
        int xPos = mapManager.getMapDriver().longitudeToXCoordinate(longitude.getDecimalDegrees(), zoomLevel) - figSize.width / 2;
        int yPos = mapManager.getMapDriver().latitudeToYCoordinate(latitude.getDecimalDegrees(), zoomLevel) - figSize.height / 2;
        topLeftX = xPos;
        topLeftY = yPos;

        xStartTile = xPos / tileDimension.getFirst() - xAdditionalTiles;
        yStartTile = yPos / tileDimension.getSecond() - yAdditionalTiles;

        xStopTile = xStartTile + xTilesToLoad - 1;
        yStopTile = yStartTile + yTilesToLoad - 1;

        xPositionOffset = xPos % tileDimension.getFirst();
        yPositionOffset = yPos % tileDimension.getSecond();

        // Load visible center
        for (int y = yStartTile + yAdditionalTiles; y <= yStopTile - yAdditionalTiles; y++) {
            for (int x = xStartTile + xAdditionalTiles; x <= xStopTile - xAdditionalTiles; x++) {
                loadTile(zoomLevel, x, y);
            }
        }

        // Load additional tiles
        for (int y = yStartTile; y < yStartTile + yAdditionalTiles; y++) {
            for (int x = xStartTile; x <= xStopTile; x++) {
                loadTile(zoomLevel, x, y);
            }
        }
        for (int y = yStopTile - yAdditionalTiles + 1; y <= yStopTile; y++) {
            for (int x = xStartTile; x <= xStopTile; x++) {
                loadTile(zoomLevel, x, y);
            }
        }
        for (int y = yStartTile + yAdditionalTiles; y <= yStopTile - yAdditionalTiles; y++) {
            for (int x = xStartTile; x < xStartTile + xAdditionalTiles; x++) {
                loadTile(zoomLevel, x, y);
            }
        }
        for (int y = yStartTile + yAdditionalTiles; y <= yStopTile - yAdditionalTiles; y++) {
            for (int x = xStopTile - xAdditionalTiles + 1; x <= xStopTile; x++) {
                loadTile(zoomLevel, x, y);
            }
        }

    }

    /**
     * Method puts provided tile on mapTiles.
     *
     * @param tile
     *            Tile to put.
     * @param zoom
     *            Zoom level of provided tile.
     * @param column
     *            X position of provided tile.
     * @param row
     *            Y position of provided tile.
     * @param repaint
     *            Flag that indicates whether repaint method should or should not be invoked at the end of this method.
     */
    private void putLoadedTile(ImageData tile, int zoom, int column, int row, boolean repaint) {
        if (tile != null && zoomLevel == zoom && column >= xStartTile && column <= xStopTile && row >= yStartTile && row <= yStopTile) {

            GC gc = new GC(mapTiles);
            int x = (column - xStartTile) * tileDimension.getFirst();
            int y = (row - yStartTile) * tileDimension.getSecond();

            Image tileImage = new Image(Display.getCurrent(), tile);
            gc.drawImage(tileImage, x, y);

            tileImage.dispose();
            gc.dispose();

            if (repaint) {
                repaint();
            }
        }

    }

    /**
     * Move buffered map and request for new tiles. This method has been implemented to avoid setting whole mapTiles from begin when map is
     * moved. Instead, old tiles that can bee used are moved and there is only request for tiles that hasn't been on mapTiles but are
     * required now.
     *
     * @param newXStartTile
     *            Number (x coordinate) of NEW tile in TOP-LEFT corner of mapTiles.
     * @param newYStartTile
     *            Number (y coordinate) of NEW tile in TOP-LEFT corner of mapTiles.
     */
    private void moveMapTiles(int newXStartTile, int newYStartTile) {
        // Count new values for X coordinate
        int xDiff = xStartTile - newXStartTile;
        xStartTile = newXStartTile;
        xStopTile -= xDiff;

        int xStart = 0;
        int absXDiff = Math.abs(xDiff);

        if (xDiff > 0) {
            // Request for tiles on right
            xStart = xStartTile;
        } else {
            // Request for tiles on left
            xStart = xStopTile - absXDiff;
        }

        // Count new values for Y coordinate
        int yDiff = yStartTile - newYStartTile;
        yStartTile = newYStartTile;
        yStopTile -= yDiff;

        int yStart = 0;
        int absYDiff = Math.abs(yDiff);

        if (yDiff > 0) {
            // Request for tiles on top
            yStart = yStartTile;
        } else {
            // Request for tiles on bottom
            yStart = yStopTile - absYDiff;
        }

        // Copy tiles that can be reused
        GC gc = new GC(mapTiles);
        gc.copyArea(0, 0, xTilesToLoad * tileDimension.getFirst(), yTilesToLoad * tileDimension.getSecond(),
                xDiff * tileDimension.getFirst(), yDiff * tileDimension.getSecond());
        gc.dispose();

        // Request for new X tiles
        if (xDiff != 0) {
            for (int y = yStartTile; y <= yStopTile; y++) {
                for (int x = xStart; x <= xStart + absXDiff; x++) {
                    loadTile(zoomLevel, x, y);
                }
            }
        }

        // Request for new Y tiles
        if (yDiff != 0) {
            for (int y = yStart; y <= yStart + absYDiff; y++) {
                for (int x = xStartTile; x <= xStopTile; x++) {
                    loadTile(zoomLevel, x, y);
                }
            }
        }

    }

    /**
     * Returns how many x/y tiles should be loaded. This values are determined by minimal tiles that should be loaded to cover whole editor
     * area (including actual positionOffset) and number of additional tiles determined by fields: xAdditionalTiles, yAdditionalTiles.
     *
     * @return How many x/y tiles should be loaded.
     */
    private Pair<Integer, Integer> getTilesToLoad() {
        int width = getSafeSize().width;
        int height = getSafeSize().height;

        int x = (width / tileDimension.getFirst()) + 2 * xAdditionalTiles + 2;
        int y = (height / tileDimension.getSecond()) + 2 * yAdditionalTiles + 2;
        return new Pair<>(x, y);
    }

    /**
     * Method is responsible for obtain tile from map provider. Two scenarios may happen. First: map provider can return tile immediately
     * (tile will be obtained from cache or fast running provider) than tile will be put on the mapTiles. Second: map provider will be
     * obtained in asynchronously way and {@link IMapManager} will inform when result is available. In that scenario there is need to wait
     * for the result and put tile on mapTile when it will show up.
     *
     * @param zoom
     *            Map's zoom level.
     * @param column
     *            Tile's column number.
     * @param row
     *            Tile's row number.
     */
    private void loadTile(int zoom, int column, int row) {
        if (column < 0 || row < 0 || column >= mapManager.getMapDriver().getXRange(zoomLevel) / tileDimension.getFirst()
                || row >= mapManager.getMapDriver().getYRange(zoomLevel) / tileDimension.getSecond()) {
            // If trying to obtain tile that map driver does not provide
            putLoadedTile(emptyTile, zoom, column, row, true);
        } else {
            // Try to obtain tile from map provider
            ImageData imageData = mapManager.getTile(zoom, column, row, this);
            if (loadingTileImage == imageData) {
                notDownloaded.put(new Pair<>(column, row), true);
            }
            putLoadedTile(imageData, zoom, column, row, true);
        }
    }

    @Override
    public int getCountOfTilesToLoad() {
        return notDownloaded.size();
    }

    @Override
    public ImageData getLoadingTileImage() {
        return loadingTileImage;
    }

    @Override
    public void setLoadingTileImage(ImageData loadingTileImage) {
        this.loadingTileImage = loadingTileImage;
    }

    @Override
    public void resetTilesToComplete() {
        notDownloaded.clear();
    }

}
