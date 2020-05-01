package application.resource.manager;

import infrastructure.Allocated;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class SingleFileResourceManager<T extends Allocated> extends ResourceManager<T>
{
	private FileReader reader;
	private ResourceLoader<T> resourceLoader;

	public SingleFileResourceManager(FileReader reader, ResourceLoader<T> resourceLoader)
	{
		this.reader = reader;
		this.resourceLoader = resourceLoader;
	}

	public T getResource(String path) throws FileNotFoundException, FileParsingException
	{
		if(isLoaded(path))
		{
			return getLoadedResource(path);
		}

		T resource = resourceLoader.load(reader.readFile(path));

		setLoadedResource(path, resource);

		return resource;
	}
}
