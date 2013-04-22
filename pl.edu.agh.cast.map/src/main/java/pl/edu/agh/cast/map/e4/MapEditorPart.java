package pl.edu.agh.cast.map.e4;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.parts.GraphicalViewerImpl;
import org.eclipse.swt.widgets.Composite;

import pl.edu.agh.cast.map.driver.OpenStreetMapDriver;
import pl.edu.agh.cast.map.editpart.MapEditPartsFactory;
import pl.edu.agh.cast.map.editpart.MapScalableRootEditPart;
import pl.edu.agh.cast.map.manager.MapManager;
import pl.edu.agh.cast.map.model.MapDiagram;
import pl.edu.agh.cast.map.provider.OpenStreetMapInternetProvider;
import pl.edu.agh.cast.map.tool.MapPanningTool;

public class MapEditorPart {

	private EditPartViewer viewer;
	
	@PostConstruct
	protected void createControls(Composite parent) {
		viewer =  new GraphicalViewerImpl();
		viewer.createControl(parent);
		viewer.setRootEditPart(new MapScalableRootEditPart());
		viewer.setEditPartFactory(new MapEditPartsFactory());
		viewer.setEditDomain(new EditDomain());
		viewer.getControl().setBackground(ColorConstants.white);
		
	}
	
	public void setDiagram(MapDiagram diagram) {
		getEditPartViewer().setContents(diagram);
		/// TODO: remove from here mapManager
		MapManager mapManager = MapManager.getInstance();
		getEditPartViewer().getEditDomain().setActiveTool(new MapPanningTool(mapManager.getMapDriver(), diagram, true));
		
	}
	
	public EditPartViewer getEditPartViewer() {
		return viewer;
	}
	
	@Focus
	public void onFocus() {
		getEditPartViewer().setFocus(getEditPartViewer().getRootEditPart());
	}
}
