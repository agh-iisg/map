package pl.edu.agh.iisg.map;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

public class MapApplication implements IApplication {

    private static Display display;
    
    @Override
    public Object start(IApplicationContext context) throws Exception {
        display = PlatformUI.createDisplay();

        try {
            int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
            if (returnCode == PlatformUI.RETURN_RESTART) {
                return IApplication.EXIT_RESTART;
            }
            return IApplication.EXIT_OK;
        } finally {
            if (!display.isDisposed()) {
                display.dispose();
            }
        }
    }

    @Override
    public void stop() {
     

    }

}
