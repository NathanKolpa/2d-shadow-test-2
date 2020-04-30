package renderer;

import org.joml.Vector2f;

public class Camera
{
	private Vector2f position = new Vector2f();
	private float rotation = 0;

	public Camera()
	{
	}

	public Vector2f getPosition()
	{
		return position;
	}

	public float getRotation()
	{
		return rotation;
	}

	public void setRotation(float rotation)
	{
		this.rotation = rotation;
	}
}
