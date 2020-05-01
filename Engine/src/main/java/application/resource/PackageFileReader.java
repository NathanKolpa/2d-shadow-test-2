package application.resource;

import application.resource.FileReader;

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
	public String readFile(String path) throws FileNotFoundException
	{
		try
		{
			InputStream stream = packageClass.getResourceAsStream(path);
			return new Scanner(stream, "UTF-8").useDelimiter("\\A").next();
		}
		catch (NullPointerException e)
		{
			throw new FileNotFoundException();
		}
	}
}
