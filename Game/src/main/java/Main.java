import infrastructure.LifeTime;
import infrastructure.opengl.buffers.StaticVertexBuffer;
import infrastructure.opengl.buffers.VertexBuffer;
import infrastructure.opengl.texture.Texture;
import infrastructure.renderer.Camera;
import infrastructure.renderer.Renderer2D;
import infrastructure.renderer.Transform;
import infrastructure.opengl.buffers.DynamicVertexBuffer;
import infrastructure.opengl.buffers.layout.BufferElement;
import infrastructure.opengl.buffers.layout.BufferLayout;
import infrastructure.resource.AssetManager;
import infrastructure.window.GlfwWindow;

public class Main
{
	public static void main(String[] args) throws Exception
	{
		LifeTime.init();

		run(args);

		LifeTime.destroy();
	}

	public static void run(String[] args) throws Exception
	{
		GlfwWindow window = new GlfwWindow(1280, 720, "yeet");
		AssetManager assetManager = new AssetManager();
		window.bindContext();

		Texture texture = assetManager.getTextures().getResource("textures/fallback.png");

		//load
		Camera camera = new Camera();

		Renderer2D renderer2D = Renderer2D.loadRenderer(window, assetManager);
		Transform transform = new Transform();
		transform.getPosition().y = 100;
		transform.getScale().x = 1000;
		transform.getScale().y = 1000;

		Transform occlusionTransform = new Transform();
		occlusionTransform.getScale().x = 50;
		occlusionTransform.getScale().y = 100;
		occlusionTransform.getPosition().x = 100;
		occlusionTransform.getPosition().y = 100;

		VertexBuffer buffer = StaticVertexBuffer.fromLayout(new float[]{
				1.0f, 1.0f, 0.0f, 1.0f, 1.0f,//
				1.0f, -1.0f, 0.0f, 1.0f, 0.0f,//
				-1.0f, -1.0f, 0.0f, 0.0f, 0.0f,//

				1.0f, 1.0f, 0.0f, 1.0f, 1.0f,//
				-1.0f, 1.0f, 0.0f, 0.0f, 1.0f,//
				-1.0f, -1.0f, 0.0f, 0.0f, 0.0f,//
		}, new BufferLayout(new BufferElement[]{
				new BufferElement(3),//position
				new BufferElement(2),//texture
		}));

		float time = 0;

		while (window.shouldRun())
		{
			window.pollEvents();
			window.clear();

			time += 0.01;

			occlusionTransform.getPosition().x = (float) Math.sin(time) * 150f;
			occlusionTransform.getPosition().y = (float) Math.cos(time) * 150f;

			renderer2D.beginScene(camera);

			{
				renderer2D.beginLighting();
				renderer2D.addOccluder(buffer, occlusionTransform);

				renderer2D.addDynamicLight(transform);
				renderer2D.endLighting();
			}

			renderer2D.drawMesh(buffer, occlusionTransform, texture);

			renderer2D.endScene();

			window.display();
		}




		buffer.clean();
		renderer2D.clean();
		assetManager.clean();

		window.unbindContext();
		window.clean();
	}
}
