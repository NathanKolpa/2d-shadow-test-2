import renderer.Renderer2D;
import renderer.Shader;
import renderer.Transform;
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

		//load
		Renderer2D renderer2D = Renderer2D.loadRenderer(window);

		DynamicVertexBuffer buffer = DynamicVertexBuffer.fromLayout(new float[]{
				10.0f, 10.0f,
				10.0f, -10.0f,
				-10.0f, -10.0f,

				10.0f, 10.0f,
				-10.0f, 10.0f,
				-10, -10.0f,

		}, new BufferLayout(new BufferElement[]{
				new BufferElement(2),
		}));
		renderer2D.beginScene(null);

		//run
		renderer2D.drawMesh(buffer, new Transform());

		renderer2D.endScene();
		window.display();

		TimeUnit.SECONDS.sleep(1);

		buffer.clean();
		renderer2D.clean();

		window.unbindContext();
		window.clean();

	}
}
