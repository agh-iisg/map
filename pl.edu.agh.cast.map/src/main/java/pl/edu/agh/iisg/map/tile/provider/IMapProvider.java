package pl.edu.agh.iisg.map.tile.provider;

import org.eclipse.swt.graphics.ImageData;

import pl.edu.agh.cast.common.IDisposable;

/**
 * Map provider is responsible for providing tiles of some map type. Provider can be configurable (via settings page, needs to extend
 * {@link pl.edu.agh.cast.ui.common.configuration.element.IConfigurableElement} IConfigurableElement) and provides tiles of many map types
 * or provides only one type of map. Connection between {@link pl.edu.agh.iisg.map.tile.provider.editor.manager.IMapProvider IMapProvider} and
 * {@link pl.edu.agh.iisg.map.tile.driver.editor.driver.IMapDriver} is done via extension points. <b>IMPORTANT:</b> Map provider needs to have public
 * non-argument public constructor.
 *
 */
public interface IMapProvider extends IDisposable {

    /**
     * Return tile described by 3 parameters - map's zoom level, tile's column number and tile's row number. Logic of obtaining tiles should
     * <b>NOT</b> be invoked in new thread even if can last long. {@link pl.edu.agh.iisg.map.tile.manager.editor.manager.IMapManager IMapManager} is
     * obligated to run UI-thread blocking providers in new thread (appropriate information should be provided during {@link IMapProvider}
     * registration).
     *
     * @param zoom
     *            Map's zoom level.
     * @param column
     *            Tile's column number.
     * @param row
     *            Tile's row number.
     *
     * @return Image of requested tile or <code>null</code> otherwise.
     */
    ImageData getTile(int zoom, int column, int row);

    /**
     * Returns string description of provider that should be visible in application's preference page that concerns map providers. Return
     * <code>null</code> if provider's name should be used instead.
     *
     * @return String description of provider that should be visible in application's preference page that concerns map providers. Return
     *         <code>null</code> if provider's name should be used instead.
     */
    String getDescription();

}
