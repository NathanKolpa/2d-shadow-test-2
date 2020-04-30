import renderer.Camera;
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
	public static void main(String[] args) throws Exception
	{
		LifeTime.init();

		run(args);

		LifeTime.destroy();
	}

	public static void run(String[] args)
	{
		GlfwWindow window = new GlfwWindow(1280, 720, "yeet");
		window.bindContext();

		//load
		Camera camera = new Camera();

		Renderer2D renderer2D = Renderer2D.loadRenderer(window);
		Transform transform = new Transform();
		transform.getPosition().y = 100;
		transform.getScale().x = 1000;
		transform.getScale().y = 1000;

		Transform occlusionTransform = new Transform();
		occlusionTransform.getScale().x = 10;
		occlusionTransform.getScale().y = 10;
		occlusionTransform.getPosition().x = 100;
		occlusionTransform.getPosition().y = 100;

		DynamicVertexBuffer buffer = DynamicVertexBuffer.fromLayout(new float[]{
				1.0f, 1.0f,
				1.0f, -1.0f,
				-1.0f, -1.0f,

				1.0f, 1.0f,
				-1.0f, 1.0f,
				-1, -1.0f,

		}, new BufferLayout(new BufferElement[]{
				new BufferElement(2),
		}));

		//run

		float elapsedTime = 0;

		while (window.shouldRun())
		{
			window.pollEvents();
			window.clear();

			elapsedTime += 0.001f;

			camera.getPosition().x = (float) Math.sin(elapsedTime) * 500f;
			camera.getPosition().y = (float) Math.cos(elapsedTime) * 500f;

//			transform.getScale().x += 1 * 0.5;
//			transform.getScale().y += 1 * 0.5;

			renderer2D.beginScene(camera);

			{
				renderer2D.beginLighting();
				renderer2D.addOccluder(buffer, occlusionTransform);

				renderer2D.addDynamicLight(transform);
				renderer2D.endLighting();
			}

//			renderer2D.drawMesh(buffer, transform);
			renderer2D.drawMesh(buffer, occlusionTransform);

			renderer2D.endScene();

			window.display();
		}




		buffer.clean();
		renderer2D.clean();

		window.unbindContext();
		window.clean();

	}
}
