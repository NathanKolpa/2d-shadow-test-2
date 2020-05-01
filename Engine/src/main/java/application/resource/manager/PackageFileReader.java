package application.resource.manager;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

public class PackageFileReader implements FileReader
{
	private Class packageClass;

	public PackageFileReader(Class packageClass)
	{
		this.packageClass = packageClass;
	}

	@Override
	public InputStream readFile(String path) throws FileNotFoundException
	{
		try
		{
			return packageClass.getResourceAsStream("/" + path);
		}
		catch (NullPointerException e)
		{
			throw new FileNotFoundException();
		}
	}
}
