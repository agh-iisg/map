package pl.edu.agh.iisg.map.config;

import java.util.List;

import pl.edu.agh.iisg.map.tile.provider.IMapProvider;

public interface IMapConfigurationProvider {

	boolean discCacheEnabled();
	
	boolean discCacheLimitEnabled();
	
	int discCacheLimit();
	
	String discCacheLocation();
	
	boolean memoryCacheEnabled();
	
	int memoryCacheLimit();
	
	List<IMapProvider> mapProviders();
	
}
