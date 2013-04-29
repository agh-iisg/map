package pl.edu.agh.iisg.map.gef.palette;

import org.eclipse.gef.palette.ToolEntry;

import pl.edu.agh.iisg.map.gef.tool.MapPanningTool;
import pl.edu.agh.iisg.map.util.Images;

public class MapPanningToolEntry extends ToolEntry {

    public MapPanningToolEntry(String label) {
        this(label, null);
    }
    
    public MapPanningToolEntry(String label, String shortDesc) {
        super(label, shortDesc, Images.GRAB_OPEN_16X16, Images.GRAB_OPEN_16X16, MapPanningTool.class);
    }

}
