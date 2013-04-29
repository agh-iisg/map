package pl.edu.agh.iisg.map.gef.palette;

import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteToolbar;
import org.eclipse.gef.palette.ToolEntry;

public class MapEditorPaletteFactory {

    public static PaletteRoot create() {
        PaletteRoot root = new PaletteRoot();

        root.add(createToolsEntry(root));

        return root;
    }
    
    private static PaletteEntry createToolsEntry(PaletteRoot palette) {
        PaletteToolbar toolbar = new PaletteToolbar("Tools");
        
        ToolEntry entry = new MapPanningToolEntry("Panning tool");
        toolbar.add(entry);
        palette.setDefaultEntry(entry);
        
        toolbar.add(new MapRectangleZoomToolEntry("Zoom to rectangle", "Zoom to rectangle"));
        
        return toolbar;
        
    }
}
