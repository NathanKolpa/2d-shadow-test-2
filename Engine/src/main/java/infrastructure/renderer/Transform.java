package infrastructure.renderer;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class Transform
{
	private Vector2f position;
	private Vector3f rotation;
	private Vector2f scale;

	public Transform()
	{
		position = new Vector2f();
		rotation = new Vector3f();
		scale = new Vector2f(1, 1);
	}

	public Vector2f getPosition()
	{
		return position;
	}

	public Vector3f getRotation()
	{
		return rotation;
	}

	public Vector2f getScale()
	{
		return scale;
	}
}
