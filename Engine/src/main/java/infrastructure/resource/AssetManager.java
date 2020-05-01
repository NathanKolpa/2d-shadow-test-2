package infrastructure.resource;

import application.resource.PackageFileReader;
import infrastructure.resource.manager.ShaderResourceManager;

public class AssetManager
{
	private ShaderResourceManager shaders;

	public AssetManager()
	{
		shaders = new ShaderResourceManager(new PackageFileReader(getClass()));
	}

	public ShaderResourceManager getShaders()
	{
		return shaders;
	}

	public void clean()
	{
		shaders.cleanLoadedResources();
	}
}
