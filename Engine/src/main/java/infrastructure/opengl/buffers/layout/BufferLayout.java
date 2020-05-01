package infrastructure.opengl.buffers.layout;

import java.util.ArrayList;
import java.util.Arrays;

public class BufferLayout
{
	private ArrayList<BufferElement> elements;

	public BufferLayout()
	{
		elements = new ArrayList<>();
	}

	public BufferLayout(BufferElement[] elements)
	{
		this.elements = new ArrayList<>(Arrays.asList(elements));
	}

	public ArrayList<BufferElement> getElements()
	{
		return elements;
	}

	public int getVertexByteSize()
	{
		return elements.stream().mapToInt(BufferElement::getByteSize).sum();
	}

	public int getVertexSize()
	{
		return elements.stream().mapToInt(BufferElement::getSize).sum();
	}
}
