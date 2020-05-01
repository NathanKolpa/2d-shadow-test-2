package infrastructure;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.system.Configuration;

import static org.lwjgl.glfw.GLFW.*;

public class LifeTime
{
	public static void init()
	{
		Configuration.DEBUG_MEMORY_ALLOCATOR.set(true);

		if (!glfwInit())
			throw new RuntimeException("Unable to initialize GLFW");

		GLFWErrorCallback.createPrint(System.err).set();
		glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);
	}

	public static void destroy()
	{
		glfwSetErrorCallback(null).free();
		glfwTerminate();
	}
}
