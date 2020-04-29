package renderer.buffers;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL45.*;

// TODO: make this a abstract class
public interface VertexBuffer
{
	void  clean();
	void draw();
}
