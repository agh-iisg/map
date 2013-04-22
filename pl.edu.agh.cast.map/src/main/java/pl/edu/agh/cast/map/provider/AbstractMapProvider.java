package pl.edu.agh.cast.map.provider;


/**
 * Supply implementation of {@link IMapProvider} interface that forces returning provider's name in application's preference page. This
 * class implements {@link pl.edu.agh.cast.ui.common.configuration.element.IConfigurableElement} interface and has one parameter to be
 * configured: PARAMETER_DRIVER.
 *
 * @see AbstractMapProvider#PARAMETER_DRIVER
 */
public abstract class AbstractMapProvider implements IMapProvider {




    /**
     * {@inheritDoc}
     *
     * @see pl.edu.agh.cast.map.editor.provider.IMapProvider#getDescription()
     */
    @Override
    public String getDescription() {
        return null;
    }

 




}
