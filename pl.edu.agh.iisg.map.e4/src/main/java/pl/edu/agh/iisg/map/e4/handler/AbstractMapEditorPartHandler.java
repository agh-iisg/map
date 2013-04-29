package pl.edu.agh.iisg.map.e4.handler;

import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import pl.edu.agh.iisg.map.e4.part.MapEditorPart;

public class AbstractMapEditorPartHandler {

    protected MapEditorPart getMapEditor(MPart part) {
        return (MapEditorPart)part.getObject();
    }
    
    public boolean canExecute(@Active MPart activePart) {
        return activePart.getObject() instanceof MapEditorPart;
    }
    
}
