package pl.edu.agh.iisg.map.config;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import pl.edu.agh.iisg.map.Activator;
import pl.edu.agh.iisg.map.tile.provider.IMapProvider;

/**
 * Map configuration holder which keeps all data in Eclipse preference store.
 * 
 */
public class PreferencesMapConfigurator implements IMapConfigurator {

    private final IPreferenceStore store;

    public PreferencesMapConfigurator() {
        store = Activator.getDefault().getPreferenceStore();
    }

    public PreferencesMapConfigurator(boolean initDefaultsManually) {
        this();
        if (initDefaultsManually) {
            new DefualtValuesInitializer().initializeDefaultPreferences();
        }
    }

    @Override
    public boolean discCacheEnabled() {
        return store.getBoolean(Keys.MAP_DISC_CACHE_ENABLED);
    }

    @Override
    public boolean discCacheLimitEnabled() {
        return store.getBoolean(Keys.MAP_DISC_CACHE_LIMIT_ENABLED);
    }

    @Override
    public int discCacheLimit() {
        return store.getInt(Keys.MAP_DISC_CACHE_LIMIT);
    }

    @Override
    public String discCacheLocation() {
        return store.getString(Keys.MAP_DISC_CACHE_LOCATION);
    }

    @Override
    public boolean memoryCacheEnabled() {
        return store.getBoolean(Keys.MAP_MEMORY_CACHE_ENABLED);
    }

    @Override
    public int memoryCacheLimit() {
        return store.getInt(Keys.MAP_MEMORY_CACHE_LIMIT);
    }

    @Override
    public List<IMapProvider> mapProviders() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void enableDiscCache(boolean enabled) {
        store.setValue(Keys.MAP_DISC_CACHE_ENABLED, enabled);

    }

    @Override
    public void enableDiscCacheLimit(boolean enabled) {
        store.setValue(Keys.MAP_DISC_CACHE_LIMIT_ENABLED, enabled);

    }

    @Override
    public void setDiscCacheLimit(int limit) {
        store.setValue(Keys.MAP_DISC_CACHE_LIMIT, limit);

    }

    @Override
    public void setDiscCacheLocation(String location) {
        store.setValue(Keys.MAP_DISC_CACHE_LOCATION, location);

    }

    @Override
    public void enableMemoryCache(boolean enabled) {
        store.setValue(Keys.MAP_MEMORY_CACHE_ENABLED, enabled);

    }

    @Override
    public void setMemoryCacheLimit(int limit) {
        store.setValue(Keys.MAP_MEMORY_CACHE_LIMIT, limit);

    }

    @Override
    public void addMapProvider(IMapProvider mapProvider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeMapProvider(IMapProvider mapProvider) {
        // TODO Auto-generated method stub

    }

    public static class DefualtValuesInitializer extends AbstractPreferenceInitializer {

        private static final int OPERATIONAL_MEMORY;
        static {
            OPERATIONAL_MEMORY = (int)(Runtime.getRuntime().maxMemory() / (1024 * 1024));
        }

        @Override
        public void initializeDefaultPreferences() {

            IPreferenceStore store = Activator.getDefault().getPreferenceStore();

            // Default map type
            // Iterator<IMapVendorDescriptor> typeIterator =
            // resolver.getMapVendorsDescriptors().iterator();
            // IMapVendorDescriptor typeDescriptor = typeIterator.hasNext() ?
            // typeIterator.next() : null;
            //
            // if (typeDescriptor == null) {
            //		            logger.error("There are no map types registered in application."); //$NON-NLS-1$
            // return;
            // } else {
            // store.setDefault(MapProvidersPreferencesPage.MAP_VENDOR_ID,
            // typeDescriptor.getId());
            // }

            // Default map driver
            // Iterator<IMapDriverDescriptor> driverIterator =
            // resolver.getMapDriversRelatedToVendor(typeDescriptor).iterator();
            // IMapDriverDescriptor driverDescriptor = driverIterator.hasNext()
            // ? driverIterator.next() : null;
            //
            // if (driverDescriptor == null) {
            //		            logger.error(String.format("There are no map driver registered in application for type %s."//$NON-NLS-1$
            // , typeDescriptor));
            // return;
            // } else {
            // store.setDefault(MapProvidersPreferencesPage.MAP_DRIVER_ID,
            // driverDescriptor.getId());
            // }

            // Default map driver configuration
            // store.setDefault(MapProvidersPreferencesPage.MAP_DRIVER_CONFIGURATION,
            // SerializerUtil.CONF_EMPTY);

            // Default flag that indicates whether memory cache is enabled
            store.setDefault(Keys.MAP_MEMORY_CACHE_ENABLED, true);

            // Default operational memory limit for map cache
            store.setDefault(Keys.MAP_MEMORY_CACHE_LIMIT, OPERATIONAL_MEMORY / 4);

            // Default flag that indicates whether disc cache is enabled
            store.setDefault(Keys.MAP_DISC_CACHE_ENABLED, true);

            // Default tile storage directory for disc cache

            // TODO: this code requires dependency to org.eclipse.core.resources; consider to remove this!
            String workspace = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
            //			String workspace = System.getProperty("workspace.location"); //$NON-NLS-1$
            workspace += File.separator + ".maps"; //$NON-NLS-1$

            File mapDirectory = new File(workspace);
            if (!mapDirectory.exists() && !mapDirectory.mkdirs()) {
                Logger.getLogger(getClass()).warn("Cannot create directory for map tile"); //$NON-NLS-1$
            }
            store.setDefault(Keys.MAP_DISC_CACHE_LOCATION, workspace);

            // Default flag that indicates whether disc cache limit is enabled
            store.setDefault(Keys.MAP_DISC_CACHE_LIMIT_ENABLED, false);

            // Default value of disc cache limit
            store.setDefault(Keys.MAP_DISC_CACHE_LIMIT, 0);

            // Default map providers
            store.setDefault(Keys.MAP_PROVIDERS,
                    "org.openstreetmap.driver.default|ROADMAP4|true0|100|32|com.google.maps.internetProvider54|com.google.maps|");

            // Determine value of discCacheStorage
            // loadDiscCacheStorage();

        }

    }

    public final class Keys {
        /**
         * <code>MAP_MEMORY_CACHE_ENABLED</code> preference name. This preference keeps flag that indicates whether memory cache is enabled.
         */
        public static final String MAP_MEMORY_CACHE_ENABLED = "MAP_MEMORY_CACHE_ENABLED"; //$NON-NLS-1$

        /**
         * <code>MAP_MEMORY_CACHE_LIMIT</code> preference name. This preference keeps information how many operational memory should be used
         * for storing map tiles by memory cache.
         */
        public static final String MAP_MEMORY_CACHE_LIMIT = "MAP_MEMORY_CACHE_LIMIT"; //$NON-NLS-1$

        /**
         * <code>MAP_DISC_CACHE_ENABLED</code> preference name. This preference keeps flag that indicates whether disc cache is enabled.
         */
        public static final String MAP_DISC_CACHE_ENABLED = "MAP_DISC_CACHE_ENABLED"; //$NON-NLS-1$

        /**
         * <code>MAP_DISC_CACHE_LOCATION</code> preference name. This preference keeps information where map tiles for disc cache should be
         * stored.
         */
        public static final String MAP_DISC_CACHE_LOCATION = "MAP_DISC_CACHE_LOCATION"; //$NON-NLS-1$

        /**
         * <code>MAP_DISC_CACHE_LIMIT_ENABLED</code> preference name. This preference keeps information whether storage limit for disc cache
         * is enabled.
         */
        public static final String MAP_DISC_CACHE_LIMIT_ENABLED = "MAP_DISC_CACHE_LIMIT_ENABLED"; //$NON-NLS-1$

        /**
         * <code>MAP_DISC_CACHE_LIMIT</code> preference name. This preference keeps information how many space should be used for storing
         * map tiles by disc cache.
         */
        public static final String MAP_DISC_CACHE_LIMIT = "MAP_DISC_CACHE_LIMIT"; //$NON-NLS-1$

        /**
         * <code>MAP_PROVIDERS</code> preference name. This preference keeps serialized to String collection of map providers that should be
         * used for tile obtaining. <b>CAUTION:</b> collection can contain providers incompatible with selected map driver. To solve this
         * issue there should be mechanism for filtering providers compatible with specified map driver.
         */
        public static final String MAP_PROVIDERS = "MAP_PROVIDERS"; //$NON-NLS-1$
    }
}
