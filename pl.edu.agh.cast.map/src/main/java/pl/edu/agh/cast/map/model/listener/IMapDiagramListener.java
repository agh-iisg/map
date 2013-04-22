package pl.edu.agh.cast.map.model.listener;

import pl.edu.agh.cast.map.model.type.Coordinate;

public interface IMapDiagramListener {
	
	void notifyLatitudeChanged(Coordinate latitude);
	
	void notifyLongitudeChanged(Coordinate latitude);
	
	void notifyZoomLevelChanged(Integer zoomLevel);
}
