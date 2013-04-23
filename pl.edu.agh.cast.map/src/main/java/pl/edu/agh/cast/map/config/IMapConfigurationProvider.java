package pl.edu.agh.cast.map.config;

import java.util.List;

import pl.edu.agh.cast.map.provider.IMapProvider;

public interface IMapConfigurationProvider {

	boolean discCacheEnabled();
	
	boolean discCacheLimitEnabled();
	
	int discCacheLimit();
	
	boolean memoryCacheEnabled();
	
	int memoryCacheLimit();
	
	List<IMapProvider> mapProviders();
	
}
