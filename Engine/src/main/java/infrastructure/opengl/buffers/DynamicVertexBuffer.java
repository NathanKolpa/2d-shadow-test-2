package infrastructure.opengl.buffers;

import org.lwjgl.BufferUtils;
import infrastructure.opengl.buffers.layout.BufferElement;
import infrastructure.opengl.buffers.layout.BufferLayout;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL45.*;

public class DynamicVertexBuffer implements VertexBuffer
{
	public static DynamicVertexBuffer fromLayout(float[] data, BufferLayout layout)
	{
		int vertexCount = data.length / layout.getVertexSize();

		int vaoId = glGenVertexArrays();
		int vboId = glGenBuffers();

		DynamicVertexBuffer vertexBuffer = new DynamicVertexBuffer(vaoId, vboId, vertexCount, layout.getElements()
				.size(), layout.getVertexSize(), layout.getVertexByteSize());

		vertexBuffer.setData(data);

		vertexBuffer.bind();

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
	private int vertexCount;
	private final int elementCount;
	private final int vertexSize;
	private final int stride;

	private DynamicVertexBuffer(int vaoId, int vboId, int vertexCount, int elementCount, int vertexSize, int stride)
	{
		this.vaoId = vaoId;
		this.vboId = vboId;
		this.vertexCount = vertexCount;
		this.elementCount = elementCount;
		this.vertexSize = vertexSize;
		this.stride = stride;
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
	}

	/**
	 * sets a single vertex
	 * @param data the data as a single vertex
	 * @param element the index
	 * */
	public void setVertex(int element, float[] data)
	{
		if(element >= vertexCount || element < 0 || data.length != vertexSize)
			throw new ArrayIndexOutOfBoundsException();

		bind();

		FloatBuffer vertexFloatBuffer = BufferUtils.createFloatBuffer(data.length);
		vertexFloatBuffer.put(data);
		vertexFloatBuffer.flip();

		glBufferSubData(GL_ARRAY_BUFFER, element * stride, vertexFloatBuffer);

		unBind();
	}

	/** Sets the data for this buffer.
	 * @implNote Expects the layout to be the same but the length can change.
	 * @param data the new data
	 * */
	public void setData(float[] data)
	{
		bind();

		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();

		glBufferData(GL_ARRAY_BUFFER, buffer, GL_DYNAMIC_DRAW);

		vertexCount = data.length / vertexSize;

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
