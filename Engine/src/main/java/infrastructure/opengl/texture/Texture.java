package infrastructure.opengl.texture;

import infrastructure.Allocated;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL13.*;

public class Texture implements Allocated
{
	private int textureId;
	private int width, height;

	public static final int FILTER_LINEAR = GL_LINEAR;
	public static final int FILTER_REPEAT = GL_REPEAT;
	public static final int FILTER_NEAREST = GL_NEAREST;

	public static final int WRAP_REPEAT = GL_REPEAT;

	public Texture(int textureId, int width, int height)
	{
		this.textureId = textureId;
		this.width = width;
		this.height = height;
	}

	public static Texture createRgbaTexture(int width, int height)
	{
		int textureId = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, textureId);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_INT, (ByteBuffer) null);
		glBindTexture(GL_TEXTURE_2D, 0);

		return new Texture(textureId, width, height);
	}

	public void bind(int slot)
	{
		glActiveTexture(GL_TEXTURE0 + slot);
		glBindTexture(GL_TEXTURE_2D, textureId);
	}

	public void unBind()
	{
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, 0);
	}

	public void setFilter(int minFilter, int magFilter)
	{
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, minFilter);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, magFilter);
	}

	public void setWrap(int s, int t)
	{
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, s);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, t);
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public int getTextureId()
	{
		return textureId;
	}

	@Override
	public void clean()
	{
		glDeleteTextures(textureId);
	}
}
