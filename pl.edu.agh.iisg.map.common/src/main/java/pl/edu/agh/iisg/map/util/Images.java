package pl.edu.agh.iisg.map.util;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import pl.edu.agh.iisg.map.Activator;

public class Images {
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
}
