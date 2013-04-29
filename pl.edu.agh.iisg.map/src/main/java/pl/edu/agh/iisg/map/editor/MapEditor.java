package pl.edu.agh.iisg.map.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.parts.GraphicalEditorWithPalette;
import org.eclipse.gef.ui.parts.GraphicalViewerImpl;
import org.eclipse.swt.widgets.Composite;

import pl.edu.agh.iisg.map.gef.editpart.MapEditPartsFactory;
import pl.edu.agh.iisg.map.gef.editpart.MapScalableRootEditPart;
import pl.edu.agh.iisg.map.gef.palette.MapEditorPaletteFactory;
import pl.edu.agh.iisg.map.gef.tool.MapPanningTool;
import pl.edu.agh.iisg.map.model.MapDiagram;

public class MapEditor extends GraphicalEditorWithPalette {

    public static final String ID = "pl.edu.agh.iisg.map.editor";
 
    
    public MapEditor() {
        setEditDomain(new DefaultEditDomain(this));
    }
    
    /**
     * Creates graphical viewer using {@link GraphicalViewerImpl} instead of default implementation with scrolling.
     */
    @Override
    protected void createGraphicalViewer(Composite parent) {
        GraphicalViewer viewer = new GraphicalViewerImpl();
        viewer.createControl(parent);
        setGraphicalViewer(viewer);
        configureGraphicalViewer();
        hookGraphicalViewer();
        initializeGraphicalViewer();
    }


    protected void configureGraphicalViewer() {
        GraphicalViewer viewer = getGraphicalViewer();
        viewer.getControl().setBackground(ColorConstants.white);
        viewer.setRootEditPart(new MapScalableRootEditPart());
        viewer.setEditPartFactory(new MapEditPartsFactory());
    }


    @Override
    protected void initializeGraphicalViewer() {
        MapDiagram diagram = ((MapEditorInput)getEditorInput()).getDiagram();
        setDiagram(diagram);       
    }
    
    
    @Override
    protected PaletteRoot getPaletteRoot() {
        return MapEditorPaletteFactory.create();
    }
  
    
  

    @Override
    public void doSave(IProgressMonitor monitor) {

    }
    
    public void setDiagram(MapDiagram map) {
        getGraphicalViewer().setContents(map);
        getGraphicalViewer().getEditDomain().setActiveTool(new MapPanningTool());
    }

   
 
}
