package application.resource;

import infrastructure.Allocated;

public interface ResourceLoader<T extends Allocated>
{
	T load(String fileContent) throws FileParsingException;
}
