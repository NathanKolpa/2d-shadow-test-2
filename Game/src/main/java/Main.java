import window.GameWindow;
import window.GlfwWindow;

public class Main
{
	public static void main(String[] args)
	{
		LifeTime.init();

		run(args);

		LifeTime.destroy();
	}

	public static void run(String[] args)
	{
		GlfwWindow window = new GlfwWindow(1280, 720, "yeet");

		window.clean();
	}
}
