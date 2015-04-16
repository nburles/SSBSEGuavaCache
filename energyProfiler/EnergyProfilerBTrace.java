package energyProfiler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import jeep.tuple.Tuple3;

/*
 * Uses BTrace to deterministically trace the execution of some code, and builds an artificial
 * copy of this with unrolled loops / etc - this is then recompiled, and the bytecode
 * extracted, counted, and used in combination with a model provided by eLens 
 * (http://www.cs.binghamton.edu/~millerti/cs680r/papers/EstimatingMobileApplicationEnergy.pdf) 
 * in order to calculate the energy used.  Although it slightly simplifies the code "measured",
 * it does the same to all candidates and so should be acceptable for comparisons.
 */
public class EnergyProfilerBTrace extends EnergyProfiler {
	private static final String javaToolsPath = "/usr/lib/jvm/java-8-oracle/lib/tools.jar";
	
	private static final String bTraceBegin = "import com.sun.btrace.annotations.*;\n" +
			"import static com.sun.btrace.BTraceUtils.*;\n\n" +
			"@BTrace\n" +
			"public class BTraceProfiler {\n" +
			"  @OnMethod(\n" +
			"    clazz=\"";
	private static final String bTraceEnd = "\",\n" +
			"    location=@Location(value=Kind.LINE, line=-1)\n" +
			"  )\n" +
			"  public static void online(@ProbeClassName String pcn, @ProbeMethodName String pmn, int line) {\n" +
			"    //println(pcn + \".\" + pmn + \":\" + line);\n" +
			"    println(pmn + \":\" + line);\n" +
			"  }\n" +
			"}";

	/* CONSTRUCTORS */
	
	public EnergyProfilerBTrace(String runCode, String runPackageName, String runClassName, String[] runParameters, List<Tuple3<String, String, String>> testClassesParam) throws IOException, InterruptedException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		super(runCode, runPackageName, runClassName, runParameters, testClassesParam);
	}
	public EnergyProfilerBTrace(String runCode, String runPackageName, String runClassName, String[] runParameters, String testCode, String testPackageName, String testClassName) throws IOException, InterruptedException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		super(runCode, runPackageName, runClassName, runParameters, testCode, testPackageName, testClassName);
	}
	public EnergyProfilerBTrace() {
	}
	
	/*
	 * Writes and compiles the bTrace script
	 */
	private void bTraceWriteAndCompile(String bTraceCode) throws IOException, InterruptedException {
		EnergyProfiler.writeCode(bTraceCode, "", "BTraceProfiler");
		int retVal;
		if ((retVal = EnergyProfiler.runExternal("java -cp lib3rd/btrace-client.jar:" + javaToolsPath + " com.sun.btrace.compiler.Compiler BTraceProfiler.java", null, null, null, null, null)) != 0) {
			throw new UnsupportedOperationException("Failed to compile: " + retVal); // TODO: create custom exception
		}
		//if ((retVal = EnergyProfiler.runExternal(btracecPath + " BTraceProfiler.java", btraceEnvironment, null, null, null, null)) != 0) {
			//throw new UnsupportedOperationException("Failed to compile: " + retVal); // TODO: create custom exception
		//}
	}
	
	/* USEFUL METHODS */
	
	/*
	 * Adds the class onto the package, package + class -> package.class
	 * e.g. java.io + IOException -> java.io.IOException
	 */
	private String getBTraceClass(String packageName, String className) {
		return String.join(".", new String[]{packageName, className});
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
	 * TODO: amend stream gobbling so that it works on the stream while execution is still running?
	 * 
	 * @return double fitness - energy used in nJ
	 */
	public double fitness(String code, String packageName, String className, String[] params) throws IOException, InterruptedException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		// Write java
		EnergyProfiler.writeCode(code, packageName, className);

		// Compile java
		EnergyProfiler.compileCode(packageName, className);
		
		// Write and compile bTrace
		bTraceWriteAndCompile(bTraceBegin + getBTraceClass(packageName, className) + bTraceEnd);
		
		// Run java
		ByteArrayOutputStream outBAOS = new ByteArrayOutputStream();
		PrintStream outPS = new PrintStream(outBAOS);
		if (null == runClass) { // if runClass is null, then the provided code contains the main method
			EnergyProfiler.runCode("java -javaagent:lib3rd/btrace-agent.jar=stdout=true,script=BTraceProfiler.class", null, packageName, className, params, null, outPS, null, null);
		} else { // otherwise run the main method provided to the constructor
			EnergyProfiler.runCode("java -javaagent:lib3rd/btrace-agent.jar=stdout=true,script=BTraceProfiler.class", null, runPackage, runClass, runParams, null, outPS, null, null);
		}
		String[] outputLines = outBAOS.toString().split("\n");
		System.out.println(outBAOS.toString());
		
		// Calculate fitness - TODO
		//Map<String, Integer> bytecodeCounts = extractBytecodeCounts(outputLines);
		//double fitnessValue = CalculateBytecodeEnergy.calculate(bytecodeCounts);
		
		// Cleanup
		EnergyProfiler.cleanup(packageName, className);
		if (null != runClass) { // cleanup main
			EnergyProfiler.cleanup(runPackage, runClass);
		}
		Files.delete(Paths.get("BTraceProfiler.java"));
		Files.delete(Paths.get("BTraceProfiler.class"));
		
		return 0.0;//fitnessValue;
	}
	
	/* MAIN */
	
	public static void main(String[] args) {
		// Just to allow testing...
		try {
			List<String> lines = Files.readAllLines(Paths.get("../Hanoi/src/hanoi/MassiveLoop.java"), Charset.defaultCharset());
			String code = String.join("\n", lines);
			String[] params = {"5"};
			EnergyProfiler energyProfiler = new EnergyProfilerBTrace();
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
		}
	}
}
