package infrastructure.opengl.exceptions;

public class ShaderCompileException extends Exception
{
	public ShaderCompileException(String message, String shaderType)
	{
		super("Error while compiling a " + shaderType + " type shader: " + message);
	}
}
