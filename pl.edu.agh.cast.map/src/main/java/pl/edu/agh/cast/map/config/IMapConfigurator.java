package pl.edu.agh.cast.map.config;

import pl.edu.agh.cast.map.provider.IMapProvider;

public interface IMapConfigurator extends IMapConfigurationProvider {

	// Disc cache

	void enableDiscCache(boolean enabled);

	void enableDiscCacheLimit(boolean enabled);

	/**
	 * 
	 * @param limit
	 *            in MB
	 */
	void setDiscCacheLimit(int limit);

	// Memory cache

	void enableMemoryCache(boolean enabled);

	/**
	 * 
	 * @param limit
	 *            in MB
	 */
	void setMemoryCacheLimit(int limit);

	// Map providers
	
	void addMapProvider(IMapProvider mapProvider);
	
	void removeMapProvider(IMapProvider mapProvider);
	
}
