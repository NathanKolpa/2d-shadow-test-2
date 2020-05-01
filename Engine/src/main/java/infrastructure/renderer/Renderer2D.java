package infrastructure.renderer;

import application.RenderFrame;
import infrastructure.Allocated;
import infrastructure.opengl.Shader;
import infrastructure.opengl.buffers.StaticVertexBuffer;
import infrastructure.opengl.buffers.VertexBuffer;
import infrastructure.opengl.buffers.layout.BufferElement;
import infrastructure.opengl.buffers.layout.BufferLayout;
import infrastructure.opengl.exceptions.ShaderCompileException;
import infrastructure.opengl.exceptions.ShaderLinkException;
import infrastructure.opengl.texture.FrameBuffer;
import infrastructure.opengl.texture.Texture;
import infrastructure.resource.AssetManager;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import java.io.FileNotFoundException;

import static org.lwjgl.opengl.GL11.*;

public class Renderer2D implements Allocated
{
	public static Renderer2D loadRenderer(RenderFrame target, AssetManager assetManager)
			throws FileNotFoundException, ShaderLinkException, ShaderCompileException
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

		Shader defaultShader = assetManager.getShaders().getShader("shaders/Default.vert", "shaders/Default.frag");
		Shader defaultTexturedShader = assetManager.getShaders().getShader("shaders/Default.vert", "shaders/DefaultTextured.frag");
		Shader testShader = assetManager.getShaders().getShader("shaders/TestShader.vert", "shaders/TestShader.frag");
		Shader occlusionTransform = assetManager.getShaders().getShader("shaders/Dynamic2DLightPipeline/OcclusionTransform.vert", "shaders/Dynamic2DLightPipeline/OcclusionTransform.frag");
		Shader dynamicLightShader = assetManager.getShaders().getShader("shaders/Dynamic2DLightPipeline/DynamicLight2D.vert", "shaders/Dynamic2DLightPipeline/DynamicLight2D.frag");
		Shader occlusionShader = assetManager.getShaders().getShader("shaders/Dynamic2DLightPipeline/Occlusion.vert", "shaders/Dynamic2DLightPipeline/Occlusion.frag");
		Shader lightSamplerShader = assetManager.getShaders().getShader("shaders/Dynamic2DLightPipeline/LightSampler2D.vert", "shaders/Dynamic2DLightPipeline/LightSampler2D.frag");

		FrameBuffer localOcclusionMap = FrameBuffer.createFrameBuffer(1024, 1024);
		FrameBuffer occlusionMap = FrameBuffer.createFrameBuffer(1024, 1024);
		FrameBuffer dynamicLightLookup = FrameBuffer.createFrameBuffer(1024, 1);

		return new Renderer2D(target, occlusionMap, dynamicLightLookup, buffer, defaultShader, occlusionTransform, localOcclusionMap, occlusionShader, dynamicLightShader, lightSamplerShader, testShader, defaultTexturedShader);
	}

	private final RenderFrame target;
	private final FrameBuffer localOcclusionMap;
	private final FrameBuffer occlusionMap;
	private final FrameBuffer dynamicLightLookup;

	private Shader defaultShader;
	private Shader occlusionShader;
	private Shader occlusionTransform;
	private Shader dynamicLightShader;
	private Shader lightSamplerShader;
	private Shader testShader;
	private Shader defaultTexturedShader;

	private Camera currentCamera = null;
	private final VertexBuffer testBuffer;


	private Renderer2D(RenderFrame target, FrameBuffer occlusionMap, FrameBuffer dynamicLightLookup, VertexBuffer testBuffer, Shader defaultShader, Shader occlusionTransform, FrameBuffer localOcclusionMap, Shader occlusionShader, Shader dynamicLightShader, Shader lightSamplerShader, Shader testShader, Shader defaultTexturedShader)
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
		this.defaultTexturedShader = defaultTexturedShader;
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
		float halfWidth = target.getFrameWidth() / 2f;
		float halfHeight = target.getFrameHeight() / 2f;
		return new Matrix4f().setOrtho(-halfWidth, halfWidth, -halfHeight, halfHeight, -1, 1);
	}

	private Matrix4f getMvp(Transform model)
	{
		Vector2f cameraPos = currentCamera.getPosition();
		float halfWidth = target.getFrameWidth() / 2f;
		float halfHeight = target.getFrameHeight() / 2f;

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
		float res = 500;

		//transform the occlusion map
		{
			float halfWidth = transform.getScale().x / target.getFrameWidth() * 2f;
			float halfHeight = transform.getScale().y / target.getFrameHeight() * 2f;
			Matrix4f localTransform = new Matrix4f().setOrtho(-halfWidth, halfWidth, -halfHeight, halfHeight, -1, 1)
					.translate(-((transform.getPosition().x - cameraPos.x) / target.getFrameWidth() * 2f), -((transform.getPosition().y - cameraPos.y) / target
							.getFrameHeight() * 2f), 0);

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
			dynamicLightShader.setVec2("uni_resolution", res, res);
			dynamicLightShader.setFloat("uni_size", 1f);

			testBuffer.draw();

			dynamicLightShader.unBind();
			dynamicLightLookup.unbindContext();
		}

		target.setViewport();

		//draw to light map
		{
			float halfWidth = target.getFrameWidth() / 2f;
			float halfHeight = target.getFrameHeight() / 2f;

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

	public void drawMesh(VertexBuffer buffer, Transform transform, Texture texture)
	{
		texture.bind(0);

		defaultTexturedShader.bind();
		defaultTexturedShader.setMat4("uni_mvp", getMvp(transform));
		defaultTexturedShader.setInt("uni_texture", 0);


		buffer.draw();
	}


	@Override
	public void clean()
	{
		testBuffer.clean();

		localOcclusionMap.clean();
		occlusionMap.clean();
		dynamicLightLookup.clean();
	}
}
