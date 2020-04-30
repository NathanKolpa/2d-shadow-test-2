package renderer;

import application.RenderFrame;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import renderer.buffers.StaticVertexBuffer;
import renderer.buffers.VertexBuffer;
import renderer.buffers.layout.BufferElement;
import renderer.buffers.layout.BufferLayout;

import java.util.Scanner;

import static org.lwjgl.opengl.GL11.glViewport;

public class Renderer2D
{
	public static Renderer2D loadRenderer(RenderFrame target)
	{
		float data[] = new float[]{
				1.0f, 1.0f, 0.0f, 1.0f, 1.0f,//
				1.0f, -1.0f, 0.0f, 1.0f, 0.0f,//
				-1.0f, -1.0f, 0.0f, 0.0f, 0.0f,//

				1.0f, 1.0f, 0.0f, 1.0f, 1.0f,//
				-1.0f, 1.0f, 0.0f, 0.0f, 1.0f,//
				-1.0f, -1.0f, 0.0f, 0.0f, 0.0f,//
		};

		VertexBuffer buffer = StaticVertexBuffer.fromLayout(data, new BufferLayout(new BufferElement[]{
				new BufferElement(3),//position
				new BufferElement(2),//texture
		}));

		Shader defaultShader = Shader.fromText(readFile("/shaders/Default.vert"), readFile("/shaders/Default.frag"));

		return new Renderer2D(target, buffer, defaultShader);
	}

	private static String readFile(String path)
	{
		return new Scanner(Renderer2D.class.getResourceAsStream(path), "UTF-8").useDelimiter("\\A").next();
	}

	private RenderFrame target;
	private Shader defaultShader;
	private Camera currentCamera = null;
	private VertexBuffer testBuffer;

	private Renderer2D(RenderFrame target, VertexBuffer testBuffer, Shader defaultShader)
	{
		this.testBuffer = testBuffer;
		this.defaultShader = defaultShader;
		this.target = target;
	}

	// begin render code

	private float getAspect()
	{
		return 1.7777777777777777f;
	}

	private Matrix4f getMvp(Transform model)
	{
		Vector2f cameraPos = currentCamera.getTransform().getPosition();
		float halfWidth = target.getPixelWidth() / 2f;
		float halfHeight = target.getPixelHeight() / 2f;

		return new Matrix4f().setOrtho(-halfWidth, halfWidth, -halfHeight, halfHeight, -1, 1)
				.translate(-cameraPos.x, -cameraPos.y, 0)
				.translate(model.getPosition().x, model.getPosition().y, 0)
				.scale(model.getScale().x, model.getScale().y, 0);
	}

	public void beginScene(Camera camera)
	{
		currentCamera = camera;

		glViewport(0, 0, (int) target.getPixelWidth(), (int) target.getPixelHeight());
		target.clear();
	}

	public void drawMesh(VertexBuffer buffer, Transform transform)
	{
		defaultShader.bind();
		defaultShader.setMat4("uni_mvp", getMvp(transform));

		buffer.draw();
	}

	public void endScene()
	{
		currentCamera = null;
	}

	public void clean()
	{
		defaultShader.clean();
		testBuffer.clean();
	}
}
