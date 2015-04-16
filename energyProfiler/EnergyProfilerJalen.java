package energyProfiler;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import jeep.tuple.Tuple3;

/*
 * An abstract class for the Jalen's to extend...
 */
public abstract class EnergyProfilerJalen extends EnergyProfiler {
	/* CONSTRUCTORS */
	
	protected EnergyProfilerJalen(String runCode, String runPackageName, String runClassName, String[] runParameters, List<Tuple3<String, String, String>> testClassesParam) throws IOException, InterruptedException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		super(runCode, runPackageName, runClassName, runParameters, testClassesParam);
	}
	protected EnergyProfilerJalen(String runCode, String runPackageName, String runClassName, String[] runParameters, String testCode, String testPackageName, String testClassName) throws IOException, InterruptedException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		super(runCode, runPackageName, runClassName, runParameters, testCode, testPackageName, testClassName);
	}
	protected EnergyProfilerJalen() {
	}
	
	/* USEFUL METHODS */
	
	/*
	 * Converts a measured fitness (nanoseconds) into an energy use fitness (joules)
	 * The multiplier is somewhat dependent on the system used, so we just have a fixed value as an estimation
	 */
	protected static double nanosecondsToJoules(double nanoseconds) {
		final double TDPfactor = 0.7; // TDP factor for CMOS formula
		final double TDP = 125.0; // TDP of the CPU in use
		return (nanoseconds / 1000000000) * TDP * TDPfactor; // CMOS formula - time * power - where power is TDP * TDPfactor
	}
	
}
