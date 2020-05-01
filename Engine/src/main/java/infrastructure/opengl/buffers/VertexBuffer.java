package infrastructure.opengl.buffers;

import infrastructure.Allocated;

public interface VertexBuffer extends Allocated
{
	void  clean();
	void draw();
}
