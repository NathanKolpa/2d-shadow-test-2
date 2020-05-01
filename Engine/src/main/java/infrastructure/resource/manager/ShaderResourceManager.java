package infrastructure.resource.manager;

import infrastructure.opengl.Shader;
import infrastructure.opengl.exceptions.ShaderCompileException;
import infrastructure.opengl.exceptions.ShaderLinkException;
import application.resource.FileReader;
import application.resource.ResourceManager;

import java.io.FileNotFoundException;

public class ShaderResourceManager extends ResourceManager<Shader>
{

	private FileReader fileReader;

	public ShaderResourceManager(FileReader fileReader)
	{
		this.fileReader = fileReader;
	}

	public Shader getShader(String vertexPath, String fragmentPath)
			throws FileNotFoundException, ShaderCompileException, ShaderLinkException
	{
		String[] files = new String[]{vertexPath, fragmentPath};
		String key = getKey(files);

		if (isLoaded(key))
			return getLoadedResource(key);

		String vertexSource = fileReader.readFile(vertexPath);
		String fragmentSource = fileReader.readFile(fragmentPath);

		return Shader.fromText(vertexSource, fragmentSource);
	}

	private String getKey(String[] files)
	{
		StringBuilder keyBuilder = new StringBuilder();

		for (String file : files)
		{
			keyBuilder.append(file).append("|");
		}

		return keyBuilder.toString();
	}
}
