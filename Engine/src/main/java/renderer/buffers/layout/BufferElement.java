package renderer.buffers.layout;

public class BufferElement
{
	private int size;

	public BufferElement(int size)
	{
		this.size = size;
	}

	/**
	 * The amount of 'elements' this element contains
	 * */
	public int getSize()
	{
		return size;
	}

	/**
	 * The size in bytes
	 * */
	public int getByteSize()
	{
		return size * 4;
	}
}
