package infrastructure.resource;

import application.resource.manager.FileReader;
import application.resource.manager.PackageFileReader;
import application.resource.manager.SingleFileResourceManager;
import infrastructure.opengl.texture.Texture;
import infrastructure.resource.manager.ShaderResourceManager;
import infrastructure.resource.manager.TextureResourceLoader;

public class AssetManager
{
	private ShaderResourceManager shaders;
	private SingleFileResourceManager<Texture> textures;

	public AssetManager()
	{
		FileReader sharedFileReader = new PackageFileReader(getClass());

		shaders = new ShaderResourceManager(new PackageFileReader(getClass()));
		textures = new SingleFileResourceManager<>(sharedFileReader, new TextureResourceLoader());
	}

	public ShaderResourceManager getShaders()
	{
		return shaders;
	}

	public void clean()
	{
		shaders.cleanLoadedResources();
		textures.cleanLoadedResources();
	}

	public SingleFileResourceManager<Texture> getTextures()
	{
		return textures;
	}
}
