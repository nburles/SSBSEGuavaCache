package energyProfiler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jeep.tuple.Tuple3;

/*
 * Uses a count of the number of times each of the bytecode opcodes is executed (provided
 * by a debug build of OpenJDK, patched to provide a full histogram when run with the option
 * "-XX:+PrintBytecodeHistogram", in combination with a model provided by eLens 
 * (http://www.cs.binghamton.edu/~millerti/cs680r/papers/EstimatingMobileApplicationEnergy.pdf) 
 * in order to calculate the energy used.  Not deterministic, thanks to the JVM doing other
 * things, but (unlike timing) should be largely unaffected by other services/etc running on
 * the test system.  Use BTrace if possible, as this is deterministic!
 */
public class EnergyProfilerBytecodeTrace extends EnergyProfiler {
	private static final String debugJava = "/home/nburles/OpenJDK8/build/linux-x86_64-normal-server-fastdebug/jdk/bin/java";
	// Prints a Bytecode Histogram after execution, disables JIT, increases initial heap size, increases initial Eden size
	private static final String debugOptions = " -XX:+PrintBytecodeHistogram -Djava.compiler=NONE -Xms100m -Xmn50m";

	/* CONSTRUCTORS */
	
	public EnergyProfilerBytecodeTrace(String runCode, String runPackageName, String runClassName, String[] runParameters, List<Tuple3<String, String, String>> testClassesParam) throws IOException, InterruptedException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, FailedToCompileException {
		super(runCode, runPackageName, runClassName, runParameters, testClassesParam);
	}
	public EnergyProfilerBytecodeTrace(String runCode, String runPackageName, String runClassName, String[] runParameters, String testCode, String testPackageName, String testClassName) throws IOException, InterruptedException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, FailedToCompileException {
		super(runCode, runPackageName, runClassName, runParameters, testCode, testPackageName, testClassName);
	}
	public EnergyProfilerBytecodeTrace() {
	}
	
	/* USEFUL METHODS */
	
	/*
	 * Extracts the bytecode counts from a stdout String[]
	 */
	private static Map<String, Integer> extractBytecodeCounts(String[] output) {
		Map<String, Integer> bytecodeCounts = new HashMap<String, Integer>();
		
		int running = 0;
		int bytecodeCount = 0;
		for (String line : output) {
			switch (running) {
			case 0: // Not found histogram
				if (line.contains("Histogram of ")) {
					running = 1;
				}
				break;
			case 1: // Found histogram
				if (line.contains("----------------------------------------------------------------------")) {
					running = 2;
				}
				break;
			case 2: // Found start of list
				if (line.contains("----------------------------------------------------------------------")) {
					running = 3;
				} else {
					String[] splitLine = line.trim().split("\\s+");
					int opcodeCount = Integer.parseInt(splitLine[0]);
					bytecodeCounts.put(splitLine[2], opcodeCount);
					bytecodeCount += opcodeCount;
				}
				break;
			case 3: // Found end of list
				if (line.contains("(cutoff = -1.00%)")) {
					running = 4;
					String[] splitLine = line.trim().split("\\s+");
					if (Integer.parseInt(splitLine[0]) != bytecodeCount) {
						System.out.println("Number of bytecode operations doesn't add up: " + bytecodeCount + " summed, " + splitLine[0] + " total");
					}
				}
				break;
			}
		}
		
		return bytecodeCounts;
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
	 * @return double fitness - energy used in joules
	 */
	public double fitness(String code, String packageName, String className, String[] params) throws IOException, InterruptedException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, FailedToCompileException, FailedToRunException {
		// Write java
		EnergyProfiler.writeCode(code, packageName, className);
		
		// Compile java
		EnergyProfiler.compileCode(packageName, className);
		
		// Run java
		ByteArrayOutputStream outBAOS = new ByteArrayOutputStream();
		PrintStream outPS = new PrintStream(outBAOS);
		if (null == runClass) { // if runClass is null, then the provided code contains the main method
			EnergyProfiler.runCode(debugJava + debugOptions, null, packageName, className, params, null, outPS, null, outPS);
		} else { // otherwise run the main method provided to the constructor
			EnergyProfiler.runCode(debugJava + debugOptions, null, runPackage, runClass, runParams, null, outPS, null, outPS);
		}
		String[] outputLines = outBAOS.toString().split("\n");
		
		// Calculate fitness
		Map<String, Integer> bytecodeCounts = extractBytecodeCounts(outputLines);
		double fitnessValue = CalculateBytecodeEnergy.calculate(bytecodeCounts);
		
		// Cleanup
		EnergyProfiler.cleanup(packageName, className);
		if (null != runClass) { // cleanup main
			EnergyProfiler.cleanup(runPackage, runClass);
		}
		
		return fitnessValue / 1000000000; // fitness is measured in nanojoules, return value in joules
	}
	
	public static void main(String[] args) {
		// Just to allow testing...
		try {
/*
			List<String> runLines = Files.readAllLines(Paths.get("../Hanoi/src/hanoi/TowersOfHanoi.java"), Charset.defaultCharset());
			String runCode = String.join("\n", runLines);
			String[] runParams = {};
			List<String> testLines = Files.readAllLines(Paths.get("../Hanoi/src/hanoi/TowersOfHanoiSubMinimal.java"), Charset.defaultCharset());
			String testCode = String.join("\n", testLines);
			List<Tuple3<String, String, String>> testClasses = new ArrayList<Tuple3<String, String, String>>();
			testClasses.add(Tuple3.cons(testCode, "hanoi", "TowersOfHanoiSub"));
			EnergyProfiler energyProfiler = new EnergyProfilerBytecodeTrace(runCode, "hanoi", "TowersOfHanoi", runParams, testClasses);
			List<String> lines = Files.readAllLines(Paths.get("../Hanoi/src/hanoi/TowersOfHanoiSub.java"), Charset.defaultCharset());
			String code = String.join("\n", lines);
			double fitnessVal = energyProfiler.fitness(code, new String[]{});
			System.out.println("FITNESS: " + fitnessVal);
*/
			// Single file version:
			List<String> lines = Files.readAllLines(Paths.get("../Hanoi/src/hanoi/MassiveLoop.java"), Charset.defaultCharset());
			String code = String.join("\n", lines);
			String[] params = {};
			EnergyProfiler energyProfiler = new EnergyProfilerBytecodeTrace();
			double fitnessVal = energyProfiler.fitness(code, "hanoi", "MassiveLoop", params);
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
		} catch (FailedToCompileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FailedToRunException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
