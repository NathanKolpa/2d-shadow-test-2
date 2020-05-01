package application;

public interface RenderFrame
{
	void bindContext();
	void unbindContext();
	float getFrameWidth();// TODO migrate to int
	float getFrameHeight();
	void setViewport();
	void clear();
}
