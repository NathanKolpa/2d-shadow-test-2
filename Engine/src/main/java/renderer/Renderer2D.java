package renderer;

import application.RenderFrame;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import renderer.buffers.StaticVertexBuffer;
import renderer.buffers.VertexBuffer;
import renderer.buffers.layout.BufferElement;
import renderer.buffers.layout.BufferLayout;
import renderer.texture.FrameBuffer;

import java.util.Scanner;

import static org.lwjgl.opengl.GL11.*;

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
		Shader testShader = Shader.fromText(readFile("/shaders/TestShader.vert"), readFile("/shaders/TestShader.frag"));
		Shader occlusionTransform = Shader.fromText(readFile("/Dynamic2DLightPipeline/OcclusionTransform.vert"), readFile("/Dynamic2DLightPipeline/OcclusionTransform.frag"));
		Shader dynamicLightShader = Shader.fromText(readFile("/Dynamic2DLightPipeline/DynamicLight2D.vert"), readFile("/Dynamic2DLightPipeline/DynamicLight2D.frag"));
		Shader occlusionShader = Shader.fromText(readFile("/Dynamic2DLightPipeline/Occlusion.vert"), readFile("/Dynamic2DLightPipeline/Occlusion.frag"));
		Shader lightSamplerShader = Shader.fromText(readFile("/Dynamic2DLightPipeline/LightSampler2D.vert"), readFile("/Dynamic2DLightPipeline/LightSampler2D.frag"));

		FrameBuffer localOcclusionMap = FrameBuffer.createFrameBuffer(1024, 1024);
		FrameBuffer occlusionMap = FrameBuffer.createFrameBuffer(1024, 1024);
		FrameBuffer dynamicLightLookup = FrameBuffer.createFrameBuffer(1024, 1024);

		return new Renderer2D(target, occlusionMap, dynamicLightLookup, buffer, defaultShader, occlusionTransform, localOcclusionMap, occlusionShader, dynamicLightShader, lightSamplerShader, testShader);
	}

	private static String readFile(String path)
	{
		return new Scanner(Renderer2D.class.getResourceAsStream(path), "UTF-8").useDelimiter("\\A").next();
	}

	private RenderFrame target;
	private final FrameBuffer localOcclusionMap;
	private FrameBuffer occlusionMap;
	private FrameBuffer dynamicLightLookup;

	private Shader defaultShader;
	private Shader occlusionShader;
	private Shader occlusionTransform;
	private Shader dynamicLightShader;
	private Shader lightSamplerShader;
	private Shader testShader;

	private Camera currentCamera = null;
	private VertexBuffer testBuffer;


	private Renderer2D(RenderFrame target, FrameBuffer occlusionMap, FrameBuffer dynamicLightLookup, VertexBuffer testBuffer, Shader defaultShader, Shader occlusionTransform, FrameBuffer localOcclusionMap, Shader occlusionShader, Shader dynamicLightShader, Shader lightSamplerShader, Shader testShader)
	{
		this.occlusionMap = occlusionMap;
		this.dynamicLightLookup = dynamicLightLookup;
		this.testBuffer = testBuffer;
		this.occlusionTransform = occlusionTransform;
		this.defaultShader = defaultShader;
		this.target = target;
		this.localOcclusionMap = localOcclusionMap;
		this.occlusionShader = occlusionShader;
		this.dynamicLightShader = dynamicLightShader;
		this.lightSamplerShader = lightSamplerShader;
		this.testShader = testShader;
	}

	// begin render code

	private void drawTestBuffer(FrameBuffer buffer)
	{
		testShader.bind();
		testShader.setInt("uni_texture", 1);

		buffer.getTexture().bind(1);

		testBuffer.draw();

		testShader.unBind();
	}

	private Matrix4f getProjection()
	{
		float halfWidth = target.getPixelWidth() / 2f;
		float halfHeight = target.getPixelHeight() / 2f;
		return new Matrix4f().setOrtho(-halfWidth, halfWidth, -halfHeight, halfHeight, -1, 1);
	}

	private Matrix4f getMvp(Transform model)
	{
		Vector2f cameraPos = currentCamera.getPosition();
		float halfWidth = target.getPixelWidth() / 2f;
		float halfHeight = target.getPixelHeight() / 2f;

		return new Matrix4f().setOrtho(-halfWidth, halfWidth, -halfHeight, halfHeight, -1, 1)
				.translate(model.getPosition().x - cameraPos.x, model.getPosition().y - cameraPos.y, 0)
				.rotateZ((float) Math.toRadians(currentCamera.getRotation()))
				.scale(model.getScale().x, model.getScale().y, 1);
	}

	public void beginScene(Camera camera)
	{
		currentCamera = camera;

		target.setViewport();
		target.clear();
	}

	public void endScene()
	{
//		drawTestBuffer(localOcclusionMap);

		currentCamera = null;
	}

	public void beginLighting()
	{
		occlusionMap.bindContext();
		occlusionMap.clear();
		occlusionMap.unbindContext();
	}

	public void endLighting()
	{
	}

	public void addOccluder(VertexBuffer buffer, Transform transform)
	{
		occlusionMap.bindContext();
		occlusionMap.setViewport();
		occlusionShader.bind();

		occlusionShader.setMat4("uni_mvp", getMvp(transform));
		buffer.draw();

		occlusionShader.unBind();
		occlusionMap.unbindContext();
		target.setViewport();
	}

	public void addDynamicLight(Transform transform)
	{
		Vector2f cameraPos = currentCamera.getPosition();

		//transform the occlusion map
		{
			float halfWidth = transform.getScale().x / target.getPixelWidth() * 2f;
			float halfHeight = transform.getScale().y / target.getPixelHeight() * 2f;
			Matrix4f localTransform = new Matrix4f().setOrtho(-halfWidth, halfWidth, -halfHeight, halfHeight, -1, 1)
					.translate(-((transform.getPosition().x - cameraPos.x) / target.getPixelWidth() * 2f),
							-((transform.getPosition().y - cameraPos.y) / target.getPixelHeight() * 2f), 0);

			localOcclusionMap.bindContext();
			localOcclusionMap.setViewport();
			localOcclusionMap.clear();

			occlusionMap.getTexture().bind(1);

			occlusionTransform.bind();
			occlusionTransform.setInt("uni_texture", 1);
			occlusionTransform.setMat4("uni_mvp", localTransform);

			testBuffer.draw();

			occlusionTransform.unBind();
			localOcclusionMap.unbindContext();
		}

		//create lookup texture
		{
			dynamicLightLookup.bindContext();
			dynamicLightLookup.setViewport();
			dynamicLightLookup.clear();


			dynamicLightShader.bind();

			localOcclusionMap.getTexture().bind(1);
			dynamicLightShader.setInt("uni_texture", 1);
			dynamicLightShader.setVec2("uni_resolution", localOcclusionMap.getTexture()
					.getWidth(), localOcclusionMap.getTexture().getHeight());
			dynamicLightShader.setFloat("uni_size", 1f);

			testBuffer.draw();

			dynamicLightShader.unBind();
			dynamicLightLookup.unbindContext();
		}

		target.setViewport();

		//draw to light map
		{
			float halfWidth = target.getPixelWidth() / 2f;
			float halfHeight = target.getPixelHeight() / 2f;

			Matrix4f mvp = new Matrix4f().setOrtho(-halfWidth, halfWidth, -halfHeight, halfHeight, -1, 1)
					.translate(transform.getPosition().x - cameraPos.x, transform.getPosition().y - cameraPos.y, 0)
					.scale(transform.getScale().x, transform.getScale().y, 1);

			glEnable(GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE);

			lightSamplerShader.bind();

			dynamicLightLookup.getTexture().bind(1);
			lightSamplerShader.setInt("uni_texture", 1);
			lightSamplerShader.setMat4("uni_mvp", mvp);
			lightSamplerShader.setVec4("uni_color", 1f, 1f, 1f, 1.0f);
			lightSamplerShader.setVec2("uni_resolution", dynamicLightLookup.getTexture()
					.getWidth(), dynamicLightLookup.getTexture().getHeight());
			lightSamplerShader.setFloat("uni_size", 1f);
			lightSamplerShader.setFloat("uni_intensity", 0.5f);

			testBuffer.draw();

			lightSamplerShader.unBind();
			glDisable(GL_BLEND);
		}

	}

	public void drawMesh(VertexBuffer buffer, Transform transform)
	{
		defaultShader.bind();
		defaultShader.setMat4("uni_mvp", getMvp(transform));

		buffer.draw();
	}


	public void clean()
	{
		defaultShader.clean();
		occlusionTransform.clean();
		occlusionShader.clean();

		testBuffer.clean();

		localOcclusionMap.clean();
		occlusionMap.clean();
	}
}
