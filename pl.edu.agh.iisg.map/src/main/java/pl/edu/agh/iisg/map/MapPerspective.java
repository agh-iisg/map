package pl.edu.agh.iisg.map;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class MapPerspective implements IPerspectiveFactory {
	public static final String MAP_PERSPECTIVE = "pl.edu.agh.iisg.map.perspective"; //$NON-NLS-1$

	@Override
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(true);
	}

}
