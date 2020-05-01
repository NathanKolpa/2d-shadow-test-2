package infrastructure.opengl;

import infrastructure.Allocated;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import infrastructure.opengl.exceptions.ShaderCompileException;
import infrastructure.opengl.exceptions.ShaderLinkException;

import java.nio.FloatBuffer;
import java.util.HashMap;

import static java.lang.Character.getName;
import static org.lwjgl.opengl.GL20.*;

public class Shader implements Allocated
{
	public static Shader fromText(String vertexSource, String fragmentSource)
			throws ShaderCompileException, ShaderLinkException
	{
		int programId;
		int vertexShader = compileShader(vertexSource, GL_VERTEX_SHADER);
		int fragmentShader = compileShader(fragmentSource, GL_FRAGMENT_SHADER);

		programId = glCreateProgram();
		glAttachShader(programId, vertexShader);
		glAttachShader(programId, fragmentShader);
		glLinkProgram(programId);

		String infoLog = glGetProgramInfoLog(programId, glGetProgrami(programId, GL_INFO_LOG_LENGTH));

		if (glGetProgrami(programId, GL_LINK_STATUS) == GL_FALSE)
			throw new ShaderLinkException(infoLog);

		glDetachShader(programId, vertexShader);
		glDetachShader(programId, fragmentShader);
		glDeleteShader(vertexShader);
		glDeleteShader(fragmentShader);

		return new Shader(programId);
	}

	private static int compileShader(String source, int type) throws ShaderCompileException
	{
		int shader = glCreateShader(type);
		glShaderSource(shader, source);
		glCompileShader(shader);

		String infoLog = glGetShaderInfoLog(shader, glGetShaderi(shader, GL_INFO_LOG_LENGTH));

		if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE)
			throw new ShaderCompileException(infoLog, getName(type));

		return shader;
	}

	private int programId;
	private HashMap<String, Integer> locations = new HashMap<>();

	private Shader(int programId)
	{
		this.programId = programId;
	}

	public void bind()
	{
		glUseProgram(programId);
	}

	public void unBind()
	{
		glUseProgram(0);
	}

	@Override
	public void clean()
	{
		unBind();
		glDeleteProgram(programId);
	}

	// uniforms

	private int getLocation(String name)
	{
		if (locations.containsKey(name))
			return locations.get(name);

		int id = glGetUniformLocation(programId, name);
		if (id == -1)
			System.err.println("Location " + name + " not found");

		locations.put(name, id);
		return id;
	}

	private FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

	public void setMat4(String name, Matrix4f matrix)
	{
		matrix.get(matrixBuffer);
		glUniformMatrix4fv(getLocation(name), false, matrixBuffer);
	}

	public void setInt(String name, int value)
	{
		glUniform1i(getLocation(name), value);
	}

	public void setFloat(String name, float value)
	{
		glUniform1f(getLocation(name), value);
	}

	public void setVec2(String name, float x, float y)
	{
		glUniform2f(getLocation(name), x, y);
	}

	public void setVec2(String name, Vector2f value)
	{
		setVec2(name, value.x, value.y);
	}

	public void setVec3(String name, float x, float y, float z)
	{
		glUniform3f(getLocation(name), x, y, z);
	}

	public void setVec3(String name, Vector3f value)
	{
		setVec3(name, value.x, value.y, value.z);
	}

	public void setVec4(String name, float x, float y, float z, float w)
	{
		glUniform4f(getLocation(name), x, y, z, w);
	}

	public void setVec4(String name, Vector4f value)
	{
		setVec4(name, value.x, value.y, value.z, value.w);
	}
}
