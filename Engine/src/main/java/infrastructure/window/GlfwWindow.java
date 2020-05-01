package infrastructure.window;

import application.GameWindow;
import application.RenderFrame;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.Callback;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;


public class GlfwWindow implements GameWindow, RenderFrame
{
	private long windowPtr;
	private GLCapabilities capabilities;
	Callback debugProc;

	private String title;
	private int width, height;

	public GlfwWindow(int width, int height, String title)
	{
		this.title = title;
		this.width = width;
		this.height = height;

		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_STENCIL_BITS, 4);
		glfwWindowHint(GLFW_SAMPLES, 4);

		windowPtr = glfwCreateWindow(width, height, title, NULL, NULL);

		if (windowPtr == NULL)
			throw new RuntimeException("Cannot create GLFW graphics.window");

		// set the graphics.window to the center
		try (MemoryStack stack = stackPush())
		{
			IntBuffer pWidth = stack.mallocInt(1);
			IntBuffer pHeight = stack.mallocInt(1);
			glfwGetWindowSize(windowPtr, pWidth, pHeight);

			GLFWVidMode mode = glfwGetVideoMode(glfwGetPrimaryMonitor());
			glfwSetWindowPos(windowPtr, (mode.width() - pWidth.get(0)) / 2, (mode.height() - pHeight.get(0)) / 2);
		}

		glfwSetWindowSizeCallback(windowPtr, (window, newWidth, newHeight) ->
		{
			this.width = newWidth;
			this.height = newHeight;
		});

		glfwShowWindow(windowPtr);
	}

	public void setTitle(String title)
	{
		this.title = title;
		glfwSetWindowTitle(windowPtr, title);
	}

	public void setWidth(int width)
	{

	}

	public void setHeight(int height)
	{

	}

	public String getTitle()
	{
		return title;
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public boolean shouldRun()
	{
		return !glfwWindowShouldClose(windowPtr);
	}

	public void display()
	{
		glfwSwapBuffers(windowPtr);
	}

	public void pollEvents()
	{
		glfwPollEvents();
	}

	public void clean()
	{
		glfwFreeCallbacks(windowPtr);
		glfwDestroyWindow(windowPtr);

		if (debugProc != null)
			debugProc.free();
	}

	@Override
	public void bindContext()
	{
		glfwMakeContextCurrent(windowPtr);

		if (capabilities == null)
			capabilities = GL.createCapabilities();

		debugProc = GLUtil.setupDebugMessageCallback();

		GL.setCapabilities(capabilities);
	}

	@Override
	public void unbindContext()
	{
		GL.setCapabilities(null);
		glfwMakeContextCurrent(NULL);
	}

	@Override
	public float getFrameWidth()
	{
		return getWidth();
	}

	@Override
	public float getFrameHeight()
	{
		return getHeight();
	}

	@Override
	public void setViewport()
	{
		glViewport(0, 0, getWidth(), getHeight());
	}

	@Override
	public void clear()
	{
		glClearColor(0.1f, 0.1f, 0.2f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
}
