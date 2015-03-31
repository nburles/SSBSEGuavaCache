package energyProfiler;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import jeep.tuple.Tuple3;

/*
 * This is a bad implementation that times the entire execution of some code,
 * including starting up the JVM, etc... use SimpleJalen or a Bytecode option
 * if possible!
 */
public class EnergyProfilerSuperSimpleJalen extends EnergyProfilerJalen {
	/* CONSTRUCTORS */
	
	public EnergyProfilerSuperSimpleJalen(String runCode, String runPackageName, String runClassName, String[] runParameters, List<Tuple3<String, String, String>> testClassesParam) throws IOException, InterruptedException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		super(runCode, runPackageName, runClassName, runParameters, testClassesParam);
	}
	public EnergyProfilerSuperSimpleJalen(String runCode, String runPackageName, String runClassName, String[] runParameters, String testCode, String testPackageName, String testClassName) throws IOException, InterruptedException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		super(runCode, runPackageName, runClassName, runParameters, testCode, testPackageName, testClassName);
	}
	public EnergyProfilerSuperSimpleJalen() {
	}
	
	/* FITNESS METHODS */
	
	/*
	 * At the moment, assumes "code" is a valid program ready to write, compile, and run...
	 * may need to add certain bits such as a main method to wrap it
	 * 
	 * The code shouldn't be in a package, it just needs to be a standalone Java file (and only one!)
	 * 
	 * Will return the fitness as a double in terms of energy use, therefore lower is better
	 * Below zero indicates an error (maximum / worst fitness)
	 * 
	 * @return double fitness - time used in seconds
	 */
	public double fitness(String code, String packageName, String className, String[] params) throws IOException, InterruptedException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		// Write java
		EnergyProfiler.writeCode(code, packageName, className);
		
		// Compile java
		EnergyProfiler.compileCode(packageName, className);
		
		// Run java
		ThreadMXBean mxbean = ManagementFactory.getThreadMXBean();
		// Check CPU time measurement is supported by the JVM
		if (!mxbean.isCurrentThreadCpuTimeSupported()) {
			System.err.println("Current thread CPU time is not supported on this JVM");
			throw new UnsupportedOperationException("Current thread CPU time is not supported on this JVM"); // TODO: create custom exception
		}
		// Enable CPU time measurement if disabled
		if (!mxbean.isThreadCpuTimeEnabled()) {
			mxbean.setThreadCpuTimeEnabled(true);
		}
		Long cpuTimeStart = mxbean.getCurrentThreadCpuTime();
		if (null == runClass) { // if runClass is null, then the provided code contains the main method
			EnergyProfiler.runCode("java", null, packageName, className, params, null, null, null, null);
		} else { // otherwise run the main method provided to the constructor
			EnergyProfiler.runCode("java", null, runPackage, runClass, runParams, null, null, null, null);
		}
		Long cpuTimeEnd = mxbean.getCurrentThreadCpuTime();
		
		// Calculate fitness
		double fitnessValue = Long.valueOf(cpuTimeEnd - cpuTimeStart).doubleValue();
		
		// Cleanup
		EnergyProfiler.cleanup(packageName, className);
		if (null != runClass) { // cleanup main
			EnergyProfiler.cleanup(runPackage, runClass);
		}
		
		return nanosecondsToJoules(fitnessValue);
	}
	
	/* MAIN */
	
	public static void main(String[] args) {
		// Just to allow testing...
		try {
			List<String> runLines = Files.readAllLines(Paths.get("../Hanoi/src/hanoi/TowersOfHanoi.java"), Charset.defaultCharset());
			String runCode = String.join("\n", runLines);
			String[] runParams = {"25"};
			List<String> testLines = Files.readAllLines(Paths.get("../Hanoi/src/hanoi/TowersOfHanoiSubMinimal.java"), Charset.defaultCharset());
			String testCode = String.join("\n", testLines);
			List<String> lines = Files.readAllLines(Paths.get("../Hanoi/src/hanoi/TowersOfHanoiSub.java"), Charset.defaultCharset());
			String code = String.join("\n", lines);
			List<Tuple3<String, String, String>> testClasses = new ArrayList<Tuple3<String, String, String>>();
			testClasses.add(Tuple3.cons(testCode, "hanoi", "TowersOfHanoiSub"));
			EnergyProfiler energyProfiler = new EnergyProfilerSuperSimpleJalen(runCode, "hanoi", "TowersOfHanoi", runParams, testClasses);
			double fitnessVal = energyProfiler.fitness(code, new String[]{});
			System.out.println("FITNESS: " + fitnessVal);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
