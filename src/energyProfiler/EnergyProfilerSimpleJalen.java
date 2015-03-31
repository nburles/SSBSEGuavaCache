package energyProfiler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import jeep.tuple.Tuple2;
import jeep.tuple.Tuple3;

/*
 * This is a better implementation that injects Jalen into the beginning and end of a
 * main method of some code, and so only times the actual execution, not the JVM startup/etc...
 * but it's still using timing, so use one of the Bytecode options if possible!
 */
public class EnergyProfilerSimpleJalen extends EnergyProfilerJalen {
	private static final String jalenImport = "\n" +
			"// JALEN\n" +
			"import java.lang.management.ManagementFactory;\n" +
			"import java.lang.management.ThreadMXBean;\n" +
			"// JALEN\n";
	private static final String jalenStart = "\n" + 
			"\t\t// JALEN\n" +
			"\t\tThreadMXBean simplejalen_mxbean = ManagementFactory.getThreadMXBean();\n" +
			"\t\t// Check CPU time measurement is supported by the JVM\n" +
			"\t\tif (!simplejalen_mxbean.isCurrentThreadCpuTimeSupported()) {\n" +
			"\t\t\tSystem.err.println(\"Current thread CPU time is not supported on this JVM\");\n" +
			"\t\t\tSystem.exit(1);\n" +
			"\t\t}\n" +
			"\t\t// Enable CPU time measurement if disabled\n" +
			"\t\tif (!simplejalen_mxbean.isThreadCpuTimeEnabled()) {\n" +
			"\t\t\tsimplejalen_mxbean.setThreadCpuTimeEnabled(true);\n" +
			"\t\t}\n" +
			"\t\tLong simplejalen_cpuTimeStart = simplejalen_mxbean.getCurrentThreadCpuTime();\n" +
			"\t\t// JALEN\n";
	private static final String jalenEnd = "\n" +
			"\t\t// JALEN\n" +
			"\t\tLong simplejalen_cpuTimeEnd = simplejalen_mxbean.getCurrentThreadCpuTime();\n" +
			"\t\t// Calculate fitness\n" +
			"\t\tdouble simplejalen_fitnessValue = Long.valueOf(simplejalen_cpuTimeEnd - simplejalen_cpuTimeStart).doubleValue();\n" +
			"\t\tSystem.out.println(\"JALEN:\" + simplejalen_fitnessValue);\n" +
			"\t\t// JALEN\n";

	/* CONSTRUCTORS */
		
	public EnergyProfilerSimpleJalen(String runCode, String runPackageName, String runClassName, String[] runParameters, List<Tuple3<String, String, String>> testClassesParam) throws IOException, InterruptedException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		// TODO: Why can't I use super() anywhere other than at the top?!
		if (null == runCode || null == runPackageName || null == runClassName || null == runParameters || null == testClassesParam) {
			throw new NullPointerException("Cannot provide null as parameters, please just provide empty strings / string array");
		}
		
		testClasses = new ArrayList<Tuple2<String, String>>();
		
		// Inject Jalen
		runCode = injectJalen(runCode);
		
		// Write java
		EnergyProfiler.writeCode(runCode, runPackageName, runClassName);
		for (Tuple3<String, String, String> testClass : testClassesParam) {
			EnergyProfiler.writeCode(testClass.getFirst(), testClass.getSecond(), testClass.getThird());
			testClasses.add(Tuple2.cons(testClass.getSecond(), testClass.getThird()));
		}
		
		// Compile java
		EnergyProfiler.compileCode(runPackageName, runClassName);
		
		// Set command to run
		runPackage = runPackageName;
		runClass = runClassName;
		runParams = runParameters;
	}
	public EnergyProfilerSimpleJalen(String runCode, String runPackageName, String runClassName, String[] runParameters, String testCode, String testPackageName, String testClassName) throws IOException, InterruptedException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		// TODO: Why can't I use super() anywhere other than at the top?!
		if (null == runCode || null == runPackageName || null == runClassName || null == runParameters || null == testCode || null == testPackageName || null == testClassName) {
			throw new NullPointerException("Cannot provide null as parameters, please just provide empty strings / string array");
		}
		
		testClasses = new ArrayList<Tuple2<String, String>>();
		
		// Inject Jalen
		runCode = injectJalen(runCode);
		
		// Write java
		EnergyProfiler.writeCode(runCode, runPackageName, runClassName);
		EnergyProfiler.writeCode(testCode, testPackageName, testClassName);
		testClasses.add(Tuple2.cons(testPackageName, testClassName));
		
		// Compile java
		EnergyProfiler.compileCode(runPackageName, runClassName);
		
		// Set command to run
		runPackage = runPackageName;
		runClass = runClassName;
		runParams = runParameters;
	}
	public EnergyProfilerSimpleJalen() {
	}
		
	/* OTHER METHODS */
	
	// Injects Jalen's timing around the main method
	// Using simple string matching...
	// TODO: Parse it properly using http://javaparser.github.io/javaparser/
	private static String injectJalen(String code) {
		if (code.contains("JALEN")) {
			return code;
		}
		
		boolean imported = false;
		boolean started = false;

		// Don't need to import after "package xxx", as it's not in a package
		if (!code.contains("package ")) {
			imported = true;
			code = jalenImport + code;
		}
		String[] lines = code.split("\n");
		
		int braces = 0;
		for (int lineCounter = 0; lineCounter < lines.length; ++lineCounter) {
			// Find "package xxx", so imports can be injected after this
			if (!imported && lines[lineCounter].contains("package ")) {
				lines[lineCounter] += jalenImport;
			}
			
			// Find main method (and end thereof) to inject jalen itself
			if (lines[lineCounter].toLowerCase().contains("public static void main(")) {
				// For now, assume the code is formatted nicely with just
				// public static void main(String[] arguments) [throws Exception] {
				// on this line!
				braces = 1;
				started = true;
				// Inject start into current line
				lines[lineCounter] = lines[lineCounter] + jalenStart;
			} else if (started && braces > 0) {
				for (int charCounter = 0; charCounter < lines[lineCounter].length(); ++charCounter) {
					switch (lines[lineCounter].charAt(charCounter)) {
					case '{':
						braces++;
						break;
					case '}':
						braces--;
						// Inject end at current position
						if (0 == braces) {
							lines[lineCounter] = lines[lineCounter].substring(0, charCounter) + jalenEnd + lines[lineCounter].substring(charCounter, lines[lineCounter].length());
							return String.join("\n", lines);
						}
						break;
					}
				}
			}
		}
		
		if (!started) {
			throw new StringIndexOutOfBoundsException("Didn't find \"public static void main(\"");
		} else {
			throw new StringIndexOutOfBoundsException("Didn't find matching brace for main()");
		}
	}
	
	private static double extractJalenFitness(String[] output) {
		for (String line : output) {
			if (line.contains("JALEN:")) {
				return Double.parseDouble(line.replaceAll("JALEN:", ""));
			}
		}
		System.out.println("Didn't find Jalen measurement");
		return returnFail;
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
	@Override
	public double fitness(String code, String packageName, String className, String[] params) throws IOException, InterruptedException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (null == runClass) { // if runClass is null, then the provided code contains the main method
			// Inject Jalen
			code = injectJalen(code);
		}
		
		// Write java
		EnergyProfiler.writeCode(code, packageName, className);
		
		// Compile java
		EnergyProfiler.compileCode(packageName, className);
		
		// Run java
		ByteArrayOutputStream outBAOS = new ByteArrayOutputStream();
		PrintStream outPS = new PrintStream(outBAOS);
		if (null == runClass) { // if runClass is null, then the provided code contains the main method
			EnergyProfiler.runCode("java", null, packageName, className, params, null, outPS, null, null);
		} else { // otherwise run the main method provided to the constructor
			EnergyProfiler.runCode("java", null, runPackage, runClass, runParams, null, outPS, null, null);
		}
		String[] outputLines = outBAOS.toString().split("\n");
		
		// Calculate fitness
		double fitnessValue = extractJalenFitness(outputLines);
		
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
			EnergyProfiler energyProfiler = new EnergyProfilerSimpleJalen(runCode, "hanoi", "TowersOfHanoi", runParams, testClasses);
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
