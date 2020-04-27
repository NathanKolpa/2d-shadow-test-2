package window;

public interface GameWindow
{
	void setTitle(String title);
	void setWidth(int width);
	void setHeight(int height);

	String getTitle();
	int getWidth();
	int getHeight();
	boolean shouldRun();

	void display();
	void pollEvents();
}
