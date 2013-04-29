package pl.edu.agh.iisg.map.gef.palette;

import org.eclipse.gef.palette.ToolEntry;

import pl.edu.agh.iisg.map.gef.tool.MapRectangleZoomTool;
import pl.edu.agh.iisg.map.util.Images;

/**
 * The Class MapRectangleZoomToolEntry.
 *
 */
public class MapRectangleZoomToolEntry extends ToolEntry {



    /**
     * Instantiates a new map rectangle zoom tool entry.
     *
     * @param label the label
     * @param description the description
     */
    public MapRectangleZoomToolEntry(String label, String description) {

        super(label, description, Images.ZOOM_ICON, Images.ZOOM_ICON, MapRectangleZoomTool.class);

    }

}
