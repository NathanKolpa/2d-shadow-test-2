package application.resource;

import java.io.FileNotFoundException;

public interface FileReader
{
	String readFile(String path) throws FileNotFoundException;
}
