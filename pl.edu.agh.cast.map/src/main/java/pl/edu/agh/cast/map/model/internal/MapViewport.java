package pl.edu.agh.cast.map.model.internal;

import pl.edu.agh.cast.map.model.MapDiagram;
import pl.edu.agh.cast.map.model.type.Coordinate;

/**
 * Model for MapViewport.
 * 
 */
public class MapViewport {

    private final MapDiagram map;

   
    /**
     * Constructor.
     * 
     * @param mapDataSet
     *            map data set which this model is related to.
     */
    public MapViewport(MapDiagram mapDataSet) {
        this.map = mapDataSet;
       
    }

    public MapDiagram getDiagram() {
    	return map;
    }
}
