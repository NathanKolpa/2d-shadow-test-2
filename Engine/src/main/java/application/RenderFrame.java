package application;

public interface RenderFrame
{
	void bindContext();
	void unbindContext();
	float getPixelWidth();
	float getPixelHeight();
	void clear();
}
