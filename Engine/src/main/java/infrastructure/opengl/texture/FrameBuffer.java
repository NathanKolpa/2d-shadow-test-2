package infrastructure.opengl.texture;

import application.RenderFrame;

import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL30.*;

public class FrameBuffer implements RenderFrame
{
	private int bufferId;
	private Texture texture;

	public static FrameBuffer createFrameBuffer(int width, int height)
	{
		FrameBuffer frameBuffer = new FrameBuffer(glGenFramebuffersEXT(), Texture.createRgbaTexture(width, height));

		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, frameBuffer.bufferId);
		frameBuffer.texture.bind();

		glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, frameBuffer.texture.getTextureId(), 0);
		frameBuffer.check();

		frameBuffer.texture.unBind();
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);

		return frameBuffer;
	}

	private FrameBuffer(int bufferId, Texture texture)
	{
		this.bufferId = bufferId;
		this.texture = texture;
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
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
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
	public float getFrameWidth()
	{
		return texture.getWidth();
	}

	@Override
	public float getFrameHeight()
	{
		return texture.getHeight();
	}

	@Override
	public void setViewport()
	{
		glViewport(0, 0, texture.getWidth(), texture.getHeight());
	}

	public void clean()
	{
	}

	public Texture getTexture()
	{
		return texture;
	}

}
