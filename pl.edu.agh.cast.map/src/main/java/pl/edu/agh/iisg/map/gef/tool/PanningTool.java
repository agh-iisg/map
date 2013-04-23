package pl.edu.agh.iisg.map.gef.tool;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.tools.SelectionTool;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import pl.edu.agh.iisg.map.Activator;

/**
 * A subclass of the SelectionTool that allows panning by holding down the space bar.
 *
 */
public class PanningTool extends SelectionTool implements DragTracker {

    private Point viewLocation;

    /**
     * Image descriptor for open hand icon.
     */
    public static final ImageDescriptor GRAB_OPEN_16X16 = AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
            "images/cursor/hand_open.gif"); //$NON-NLS-1$

    /**
     * Image descriptor for close hand icon.
     */
    public static final ImageDescriptor GRAB_CLOSED_16X16 = AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
            "images/cursor/hand_closed.gif"); //$NON-NLS-1$

    /**
     * Open grab cursor.
     */
    protected static final Cursor GRAB_OPEN_CURSOR = new Cursor(Display.getDefault(), GRAB_OPEN_16X16.getImageData(), 0, 0);

    /**
     * Closed grab cursor.
     */
    protected static final Cursor GRAB_CLOSED_CURSOR = new Cursor(Display.getDefault(), GRAB_CLOSED_16X16.getImageData(), 0, 0);

    /**
     * Constructor.
     */
    public PanningTool() {
        super();
    }

    @Override
    public Cursor getDefaultCursor() {
        return GRAB_OPEN_CURSOR;
    }

    /**
     * @see org.eclipse.gef.tools.AbstractTool#getDebugName()
     */
    @Override
    protected String getDebugName() {
        return "Panning Tool"; //$NON-NLS-1$
    }

    /**
     * @see org.eclipse.gef.tools.SelectionTool#handleButtonDown(int)
     */
    @Override
    protected boolean handleButtonDown(int which) {
        viewLocation = ((FigureCanvas)getCurrentViewer().getControl()).getViewport().getViewLocation();
        setCursor(GRAB_CLOSED_CURSOR);
        return true;
    }

    /**
     * @see org.eclipse.gef.tools.SelectionTool#handleButtonUp(int)
     */
    @Override
    protected boolean handleButtonUp(int which) {
        setCursor(GRAB_OPEN_CURSOR);
        return true;
    }

    /**
     * @see org.eclipse.gef.tools.AbstractTool#handleDrag()
     */
    @Override
    protected boolean handleDrag() {
        FigureCanvas canvas = (FigureCanvas)getCurrentViewer().getControl();
        canvas.scrollTo(viewLocation.x - getDragMoveDelta().width, viewLocation.y - getDragMoveDelta().height);
        return true;
    }

    /**
     * @see org.eclipse.gef.tools.SelectionTool#handleFocusLost()
     */
    protected boolean handleFocusLost() {
        return true;
    }

}
