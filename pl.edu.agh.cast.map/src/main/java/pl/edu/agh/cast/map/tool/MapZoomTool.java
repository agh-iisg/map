package pl.edu.agh.cast.map.tool;

import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.MouseWheelHelper;
import org.eclipse.gef.tools.TargetingTool;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;

import pl.edu.agh.cast.map.driver.IMapDriver;
import pl.edu.agh.cast.map.model.MapDiagram;
import pl.edu.agh.cast.map.model.type.Coordinate;

/**
 * Map zoom tool.
 * 
 */
public class MapZoomTool extends TargetingTool implements MouseWheelHelper {

	private IMapDriver mapDriver;

	private MapDiagram mapDiagram;

	
	public MapZoomTool(IMapDriver mapDriver, MapDiagram mapDiagram) {
		this.mapDriver = mapDriver;
		this.mapDiagram = mapDiagram;
	}
	
	private IMapDriver getMapDriver() {
		return mapDriver;
	}

	private MapDiagram getModel() {
		return mapDiagram;
	}

	@Override
	public void mouseWheelScrolled(Event event, EditPartViewer viewer) {
		if (getCurrentViewer() == null) {
			return;
		}
		Viewport editorViewport = (Viewport) ((GraphicalEditPart) getCurrentViewer()
				.getRootEditPart()).getFigure();

		IMapDriver driver = getMapDriver();
		Integer newZoomLevel;
		Integer zoomLevel = getModel().getZoomLevel();

		Dimension mapDimesion = editorViewport.getSize();

		// Moving area to the requested point.
		Point diff = new Point(event.x - mapDimesion.width / 2, event.y
				- mapDimesion.height / 2);

		double oldZoom = zoomLevel;

		Point oldPoint = new Point(driver.longitudeToXCoordinate(getModel()
				.getLongitude().getDecimalDegrees(), zoomLevel),
				driver.latitudeToYCoordinate(getModel().getLatitude()
						.getDecimalDegrees(), zoomLevel));

		Point diffedPoint = new Point(oldPoint.x + diff.x, oldPoint.y + diff.y);

		if (event.count > 0) {
			newZoomLevel = zoomLevel + 1;
		} else {
			newZoomLevel = zoomLevel - 1;
		}

		// Point after zooming
		double zoomChange = Math.pow(2.0, newZoomLevel - oldZoom);
		Point newPoint = new Point((int) ((diffedPoint.x) * zoomChange),
				(int) ((diffedPoint.y) * zoomChange));

		Point newViewLocation = new Point(newPoint.x - diff.x, newPoint.y
				- diff.y);

		// Checking if x is not greater than maximum allowed.
		int xRange = getMapDriver().getXRange(newZoomLevel);

		// Checking if x is not smaller than 0
		if (newViewLocation.x < 0) {
			newViewLocation.x = 0;
		}

		if (newViewLocation.x + mapDimesion.width / 2 > xRange) {
			newViewLocation.x = xRange - mapDimesion.width / 2;
		}

		// Checking if y is not smaller than 0
		if (newViewLocation.y < 0) {
			newViewLocation.y = 0;
		}

		// Checking if y is not greater than maximum allowed.
		int yRange = getMapDriver().getYRange(newZoomLevel);
		if (newViewLocation.y + mapDimesion.height / 2 > yRange) {
			newViewLocation.y = yRange - mapDimesion.height / 2;
		}

		if (newZoomLevel >= driver.getMinZoomLevel()
				&& newZoomLevel <= driver.getMaxZoomLevel()) {
			//getModel().setSuppressAllEvents(true);
			getModel().setLongitude(
					new Coordinate(driver.xCoordinateToLongitue(
							newViewLocation.x, newZoomLevel)));
			getModel().setLatitude(
					new Coordinate(driver.yCoordinateToLatitude(
							newViewLocation.y, newZoomLevel)));
			// getModel().setSuppressAllEvents(false);
			getModel().setZoomLevel(newZoomLevel);
		}
	}

	@Override
	public void handleMouseWheelScrolled(Event event) {
//		if (getCurrentViewer() == null) {
//			MapEditor mapEditor = (MapEditor) PlatformUI.getWorkbench()
//					.getActiveWorkbenchWindow().getActivePage()
//					.getActiveEditor();
//			if (mapEditor != null) {
//				setViewer(mapEditor.getGraphicalViewer());
//			}
//		}
		mouseWheelScrolled(event, getCurrentViewer());
	}

	@Override
	protected String getCommandName() {
		return null;
	}
}
