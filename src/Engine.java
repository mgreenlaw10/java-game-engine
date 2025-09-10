package src;

import java.lang.reflect.InvocationTargetException;
import src.program.*;

public class Engine {

	/* Static instance
	*/
	static Engine staticInstance;

	/* Active program/window context
	*/
	private Program activeProgram;

	public static Engine getInstance() {
		return (staticInstance != null)? staticInstance : new Engine(); 
	}

	private Engine() {
		staticInstance = this;
	}

	/* Initialize with default menu program
	*/
	public void startLaunchMenu() {

		activeProgram = new MainMenu();
		startProgram();
	}

	/* Change the active program
	*/
	public <T extends Program> void switchProgram(Class<T> programClass) {

		activeProgram.destroy();
		try {
			activeProgram = programClass.getDeclaredConstructor().newInstance();
		} 
		catch (
			IllegalAccessException |
			IllegalArgumentException |
			InstantiationException | 
			NoSuchMethodException | 
			SecurityException | 
			InvocationTargetException e
		) {}
		startProgram();
	}

	/* Enter the active program's update loop in a separate thread
	*/
	public void startProgram() {
		activeProgram.start();
	}
}