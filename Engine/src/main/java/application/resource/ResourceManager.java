package application.resource;

import infrastructure.Allocated;

import java.util.HashMap;
import java.util.Map;

public class ResourceManager<T extends Allocated>
{
	private HashMap<String, T> resources = new HashMap<>();

	public T getLoadedResource(String path)
	{
		return resources.get(path);
	}

	public boolean isLoaded(String path)
	{
		return resources.containsKey(path);
	}

	public void setLoadedResource(String path, T value)
	{
		resources.put(path, value);
	}

	public void cleanLoadedResources()
	{
		for(Map.Entry<String, T> allocated : resources.entrySet())
		{
			allocated.getValue().clean();
		}
	}
}
