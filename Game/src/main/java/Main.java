import renderer.Shader;
import renderer.buffers.DynamicVertexBuffer;
import renderer.buffers.layout.BufferElement;
import renderer.buffers.layout.BufferLayout;
import window.GlfwWindow;

import java.util.concurrent.TimeUnit;

public class Main
{
	public static void main(String[] args) throws InterruptedException
	{
		LifeTime.init();

		run(args);

		LifeTime.destroy();
	}

	public static void run(String[] args) throws InterruptedException
	{
		GlfwWindow window = new GlfwWindow(1280, 720, "yeet");
		window.bindContext();


		Shader shader = Shader.fromText("#version 330 core\n" + "layout (location = 0) in vec3 aPos; \n" + "  \n" + "\n" + "void main()\n" + "{\n" + "    gl_Position = vec4(aPos, 1.0); // see how we directly give a vec3 to vec4's constructor\n" + "}",
				"#version 330 core\n" + "out vec4 FragColor;\n" + "  \n" + "\n" + "void main()\n" + "{\n" + "    FragColor = vec4(0.5, 0.5, 1.0, 1.0);\n" + "} ");

		DynamicVertexBuffer buffer = DynamicVertexBuffer.fromLayout(new float[]{
				1.0f, 1.0f, 0.0f,
				1.0f, -1.0f, 0.0f,
				-1.0f, -1.0f, 0.0f,


		}, new BufferLayout(new BufferElement[]{
				new BufferElement(3),
		}));


		shader.bind();
		buffer.draw();

		window.display();

		TimeUnit.SECONDS.sleep(1);

		buffer.clean();
		shader.clean();

		window.unbindContext();
		window.clean();

	}
}
