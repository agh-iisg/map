package pl.edu.agh.iisg.map.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import pl.edu.agh.iisg.map.editor.MapEditor;
import pl.edu.agh.iisg.map.editor.MapEditorInput;
import pl.edu.agh.iisg.map.model.MapDiagram;

public class CreateSampleMapDiagram extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
        IWorkbenchPage page = window.getActivePage();

        
        try {
            page.openEditor(new MapEditorInput(new MapDiagram()), MapEditor.ID);
        } catch (PartInitException e) {
            e.printStackTrace();
        }
        return null;
    } 

    

}
