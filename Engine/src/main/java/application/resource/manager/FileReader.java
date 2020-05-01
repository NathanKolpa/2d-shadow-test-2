package application.resource.manager;

import java.io.FileNotFoundException;
import java.io.InputStream;

public interface FileReader
{
	InputStream readFile(String path) throws FileNotFoundException;
}
