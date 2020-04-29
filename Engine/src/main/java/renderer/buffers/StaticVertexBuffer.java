package renderer.buffers;

import org.lwjgl.BufferUtils;
import renderer.buffers.layout.BufferElement;
import renderer.buffers.layout.BufferLayout;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL45.*;

public class StaticVertexBuffer implements VertexBuffer
{
	public static StaticVertexBuffer fromLayout(float[] data, BufferLayout layout)
	{
		int vertexCount = data.length / layout.getVertexSize();

		//create and flit a FloatBuffer
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();

		int vaoId = glGenVertexArrays();
		int vboId = glGenBuffers();

		StaticVertexBuffer vertexBuffer = new StaticVertexBuffer(vaoId, vboId, vertexCount, layout.getElements().size());

		vertexBuffer.bind();

		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);

		int offset = 0;
		for (int i = 0; i < layout.getElements().size(); i++)
		{
			BufferElement bufferElement = layout.getElements().get(i);

			glVertexAttribPointer(i, bufferElement.getSize(), GL_FLOAT, false, layout.getVertexByteSize(), offset);

			offset += bufferElement.getByteSize();
		}

		vertexBuffer.unBind();

		return vertexBuffer;
	}

	private final int vaoId, vboId;
	private final int vertexCount;
	private final int elementCount;

	private StaticVertexBuffer(int vaoId, int vboId, int vertexCount, int elementCount)
	{
		this.vaoId = vaoId;
		this.vboId = vboId;
		this.vertexCount = vertexCount;
		this.elementCount = elementCount;
	}

	@Override
	public void clean()
	{
		unBind();

		glDeleteBuffers(vboId);
		glDeleteVertexArrays(vaoId);
	}

	@Override
	public void draw()
	{
		bind();
		glDrawArrays(GL_TRIANGLES, 0, vertexCount);
		unBind();
	}

	private void bind()
	{
		glBindVertexArray(vaoId);
		glBindBuffer(GL_ARRAY_BUFFER, vboId);

		for (int i = 0; i < elementCount; i++)
			glEnableVertexAttribArray(i);
	}

	private void unBind()
	{
		glBindVertexArray(0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		for (int i = 0; i < elementCount; i++)
			glDisableVertexAttribArray(i);
	}
}
