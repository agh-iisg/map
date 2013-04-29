package pl.edu.agh.iisg.map.e4.handler;

import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import pl.edu.agh.iisg.map.e4.part.MapEditorPart;
import pl.edu.agh.iisg.map.gef.tool.MapRectangleZoomTool;

public class ZoomToAreaHandler extends AbstractMapEditorPartHandler {

    
    @Execute
    public void execute(@Active MPart activePart) {
       MapEditorPart mapEditor = getMapEditor(activePart);
       mapEditor.getEditPartViewer().getEditDomain().setActiveTool(new MapRectangleZoomTool());
    }
    
}
