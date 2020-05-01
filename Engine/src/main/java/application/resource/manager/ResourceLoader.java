package application.resource.manager;

import infrastructure.Allocated;

import java.io.InputStream;

public interface ResourceLoader<T extends Allocated>
{
	T load(InputStream inputStream) throws FileParsingException;
}
