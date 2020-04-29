package renderer.texture;

import application.RenderFrame;

import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL30.*;

public class FrameBuffer implements RenderFrame
{
	private int bufferId;
	private Texture texture;

	public FrameBuffer(int width, int height)
	{
		bufferId = glGenFramebuffersEXT();
		texture = Texture.framebufferTexture(width, height);

		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, bufferId);
		texture.bind();

		glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, texture.getTextureId(), 0);
		check();

		texture.unBind();
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
	}

	private void check()
	{

		int framebuffer = glCheckFramebufferStatusEXT(GL_FRAMEBUFFER_EXT);
		switch (framebuffer)
		{
			case GL_FRAMEBUFFER_COMPLETE_EXT:
				break;
			case GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT:
				throw new RuntimeException("FrameBuffer has caused a GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT exception");
			case GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT:
				throw new RuntimeException("FrameBuffer has caused a GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT exception");
			case GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT:
				throw new RuntimeException("FrameBuffer has caused a GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT exception");
			case GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT:
				throw new RuntimeException("FrameBuffer has caused a GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT exception");
			case GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT:
				throw new RuntimeException("FrameBuffer has caused a GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT exception");
			case GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT:
				throw new RuntimeException("FrameBuffer has caused a GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT exception");
			default:
				throw new RuntimeException("Unexpected reply from glCheckFramebufferStatusEXT: " + framebuffer);
		}
	}

	public void clear()
	{
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glClear(GL_COLOR_BUFFER_BIT);
	}

	@Override
	public void bindContext()
	{
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, bufferId);
	}

	@Override
	public void unbindContext()
	{
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
	}

	@Override
	public float getPixelWidth()
	{
		return texture.getWidth();
	}

	@Override
	public float getPixelHeight()
	{
		return texture.getHeight();
	}

	public Texture getTexture()
	{
		return texture;
	}

}
