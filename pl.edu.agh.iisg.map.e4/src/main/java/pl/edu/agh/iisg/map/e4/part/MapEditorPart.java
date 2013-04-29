package pl.edu.agh.iisg.map.e4.part;

import javax.annotation.PostConstruct;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.parts.GraphicalViewerImpl;
import org.eclipse.swt.widgets.Composite;

import pl.edu.agh.iisg.map.gef.editpart.MapEditPartsFactory;
import pl.edu.agh.iisg.map.gef.editpart.MapScalableRootEditPart;
import pl.edu.agh.iisg.map.gef.tool.MapPanningTool;
import pl.edu.agh.iisg.map.model.MapDiagram;

public class MapEditorPart {

    private EditPartViewer viewer;

    @PostConstruct
    protected void createControls(Composite parent) {
        createGraphicalViewer(parent);
    }

    protected void createGraphicalViewer(Composite parent) {
        viewer = new GraphicalViewerImpl();
        viewer.createControl(parent);
        viewer.setRootEditPart(new MapScalableRootEditPart());
        viewer.setEditPartFactory(new MapEditPartsFactory());
        // cannot use here DefaultEditDomain as it is dependent on IEditorPart which is defined in the compatibility layer
        viewer.setEditDomain(new EditDomain());
        viewer.getControl().setBackground(ColorConstants.white);
    }

    public void setDiagram(MapDiagram diagram) {
        getEditPartViewer().setContents(diagram);
        getEditPartViewer().getEditDomain().setActiveTool(new MapPanningTool());

    }

    public EditPartViewer getEditPartViewer() {
        return viewer;
    }

    @Focus
    public void onFocus() {
        getEditPartViewer().setFocus(getEditPartViewer().getRootEditPart());
    }
}
