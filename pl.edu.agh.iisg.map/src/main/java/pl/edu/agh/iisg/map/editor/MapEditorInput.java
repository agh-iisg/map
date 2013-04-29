package pl.edu.agh.iisg.map.editor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import pl.edu.agh.iisg.map.model.MapDiagram;

public class MapEditorInput implements IEditorInput {

    private static final String MAP_EDITOR_INPUT = "Map editor input";
    
    private MapDiagram diagram;

    public MapEditorInput(MapDiagram diagram) {
        setDiagram(diagram);
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(Class adapter) {
        return null;
    }

    @Override
    public boolean exists() {
        return false;
    }

    @Override
    public ImageDescriptor getImageDescriptor() {
        return null;
    }

    @Override
    public String getName() {
        return MAP_EDITOR_INPUT;
    }

    @Override
    public IPersistableElement getPersistable() {
        return null;
    }

    @Override
    public String getToolTipText() {
        return MAP_EDITOR_INPUT;
    }

    public MapDiagram getDiagram() {
        return diagram;
    }

    public void setDiagram(MapDiagram diagram) {
        this.diagram = diagram;
    }

    
}
