package pl.edu.agh.cast.map.e4.handler;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

import pl.edu.agh.cast.map.e4.MapEditorPart;
import pl.edu.agh.cast.map.model.MapDiagram;

public class OpenSampleMapHandler {

	private static final String EDITOR_ID = "pl.edu.agh.cast.map.e4.partdescriptor.editor";
	
	private static final String STACK_ID = "pl.edu.agh.cast.map.e4.partstack";
	
	@Inject
	private EPartService partService;

	@Inject
	private EModelService modelService;
	
	@Inject
	private MApplication application;
	
	@SuppressWarnings("restriction")
	@Execute
	public void execute() {
		MPart mapEditor = partService.createPart(EDITOR_ID);
		MPartStack stack = (MPartStack)modelService.find(STACK_ID, application);
		stack.getChildren().add(mapEditor);
		
		partService.showPart(mapEditor, PartState.ACTIVATE);
		
		((MapEditorPart)mapEditor.getObject()).setDiagram(new MapDiagram());
		

	}
}
