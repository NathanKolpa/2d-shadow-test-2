package renderer;

import application.RenderFrame;
import org.joml.Matrix4f;
import renderer.buffers.VertexBuffer;

import java.util.Scanner;

import static org.lwjgl.opengl.GL11.glViewport;

public class Renderer2D
{
	public static Renderer2D loadRenderer(RenderFrame target)
	{
		Shader defaultShader = Shader.fromText(readFile("/shaders/Default.vert"), readFile("/shaders/Default.frag"));

		return new Renderer2D(target, defaultShader);
	}

	private static String readFile(String path)
	{
		return new Scanner(Renderer2D.class.getResourceAsStream(path), "UTF-8").useDelimiter("\\A").next();
	}

	private RenderFrame target;
	private Shader defaultShader;
	private Camera currentCamera = null;

	private Renderer2D(RenderFrame target, Shader defaultShader)
	{
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
		float halfWidth = target.getPixelWidth() / 2f;
		float halfHeight = target.getPixelHeight() / 2f;
		return new Matrix4f().setOrtho(-halfWidth, halfWidth, -halfHeight, halfHeight, -1, 1);
	}

	public void beginScene(Camera camera)
	{
		currentCamera = camera;

		glViewport(0, 0, (int)target.getPixelWidth(), (int)target.getPixelHeight());
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
	}
}
