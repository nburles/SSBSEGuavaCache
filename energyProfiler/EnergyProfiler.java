package energyProfiler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import jeep.lang.Diag;
import jeep.tuple.Tuple2;
import jeep.tuple.Tuple3;

/*
 * An abstract class for classes which will profile energy usage using an
 * external JVM
 */
public abstract class EnergyProfiler {
	static final double returnFail = Double.POSITIVE_INFINITY;
	static final Runtime rt = Runtime.getRuntime();

	protected String runPackage = null;
	protected String runClass = null;
	protected String[] runParams = null;
	protected List<Tuple2<String, String>> testClasses = null; // <testPackage, testClass>

	/* CONSTRUCTORS */
	
	/*
	 * Constructor for the class which compiles some code containing a main method ready to
	 * run each time the fitness method is called (the fitness method is then changing /
	 * compiling different code that is expected to be called by the code provided here!)
	 * 
	 * You must also provide a minimal compilable example of the code which will be called by
	 * this main method (to allow initial compilation!)
	 * 
	 * testClasses => String testCode, String testPackageName, String testClassName
	 */
	protected EnergyProfiler(String runCode, String runPackageName, String runClassName, String[] runParameters, List<Tuple3<String, String, String>> testClassesParam) throws IOException, InterruptedException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, FailedToCompileException {
		if (null == runCode || null == runPackageName || null == runClassName || null == runParameters || null == testClassesParam) {
			throw new NullPointerException("Cannot provide null as parameters, please just provide empty strings / string array");
		}

		testClasses = new ArrayList<Tuple2<String, String>>();
		
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
	
	/*
	 * Constructor for the class which compiles some code containing a main method ready to
	 * run each time the fitness method is called (the fitness method is then changing /
	 * compiling different code that is expected to be called by the code provided here!)
	 * 
	 * You must also provide a minimal compilable example of the code which will be called by
	 * this main method (to allow initial compilation!)
	 * 
	 * testClasses => String testCode, String testPackageName, String testClassName
	 * 
	 * TODO: Why the **** can't I call another constructor in Java without it being "the first statement"?!
	 */
	protected EnergyProfiler(String runCode, String runPackageName, String runClassName, String[] runParameters, String testCode, String testPackageName, String testClassName) throws IOException, InterruptedException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, FailedToCompileException {
		if (null == runCode || null == runPackageName || null == runClassName || null == runParameters || null == testCode || null == testPackageName || null == testClassName) {
			throw new NullPointerException("Cannot provide null as parameters, please just provide empty strings / string array");
		}

		testClasses = new ArrayList<Tuple2<String, String>>();
		
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

	
	/*
	 * Default constructor for the class - if this is used, then the code provided to the fitness
	 * method is assumed to contain the runnable main method
	 */
	protected EnergyProfiler() {
	}

	/* USEFUL METHODS */
	
	/*
	 * Runs an external process and returns its exit code
	 */
	protected static int runExternal(String command, String[] envp, String outType, PrintStream outPS, String errType, PrintStream errPS) throws IOException, InterruptedException {
		Process proc;
		if (null == envp) {
			proc = rt.exec(command);
		} else {
			proc = rt.exec(command, envp);
		}
		StreamGobbler outGobbler = new StreamGobbler(proc.getInputStream(), outType, outPS);
		StreamGobbler errGobbler = new StreamGobbler(proc.getErrorStream(), errType, errPS);
		outGobbler.start();
		errGobbler.start();
		int retVal = proc.waitFor();
		outGobbler.join();
		errGobbler.join();
		return retVal;
	}
	
	/*
	 * Combines list of Strings into single String with leading space if params is not empty
	 */
	protected static String getParamString(String[] params) {
		String paramString = " " + String.join(" ", params);
		if (paramString == " ") {
			paramString = "";
		}
		return paramString;
	}

	/*
	 * Package name -> package path
	 * e.g. java.io -> ./java/io
	 */
	protected static String getPackagePath(String packageName) {
		return packageName.replace(".", "/");
	}
	
	/*
	 * Package name + class name -> class path
	 * e.g. java.io + IOException -> ./java/io/IOException
	 */
	protected static String getPathToClass(String packageName, String className) {
		String packagePath = getPackagePath(packageName);
		if ("" == packagePath) {
			return className;
		}
		return String.join("/", new String[]{getPackagePath(packageName), className});
	}

	/*
	 * Writes the code to the correct location, given package and class names
	 */
	protected static void writeCode(String code, String packageName, String className) throws IOException {
		Files.createDirectories(Paths.get("srcMod/" + getPackagePath(packageName)));
		Files.write(Paths.get("srcMod/" + getPathToClass(packageName, className) + ".java"), code.getBytes());
	}

	/*
	 * Compiles the code, given package and class names
	 */
	protected static void compileCode(String packageName, String className) throws IOException, InterruptedException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, FailedToCompileException {
		int retVal = EnergyProfiler.runExternal("javac -J-Xss10m -cp " + System.getProperty("java.class.path") + " -sourcepath " + System.getProperty("user.dir") + ":" + System.getProperty("user.dir") + "/srcMod srcMod/" + getPathToClass(packageName, className) + ".java", null, null, null, null, null);
		if (0 != retVal) {
			Diag.println("javac -J-Xss10m -cp " + System.getProperty("java.class.path") + " -sourcepath " + System.getProperty("user.dir") + ":" + System.getProperty("user.dir") + "/srcMod srcMod/" + getPathToClass(packageName, className) + ".java");
			//Diag.println(String.join("\n", Files.readAllLines(Paths.get("srcMod/" + getPathToClass(packageName, className) + ".java"))));
			throw new FailedToCompileException("Failed to compile: " + retVal);
		}
		// TODO: use reflective compilation... currently doesn't compile required classes!
        /*DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

        // This sets up the class path that the compiler will use.
        // I've added the .jar file that contains the DoStuff interface within in it...
        List<String> optionList = new ArrayList<String>();
        optionList.add("-classpath");
        optionList.add(System.getProperty("java.class.path") + ";" + System.getProperty("user.dir"));
        
        Iterable<? extends JavaFileObject> compilationUnit = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(new File("src/" + getPathToClass(packageName, className) + ".java")));
        JavaCompiler.CompilationTask task = compiler.getTask(
            null, 
            fileManager, 
            diagnostics, 
            optionList, 
            null, 
            compilationUnit);
        fileManager.close();
        if (!task.call()) {
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                System.out.format("Error on line %d in %s%n",
                        diagnostic.getLineNumber(),
                        diagnostic.getSource().toUri());
            }
            throw new UnsupportedOperationException("Failed to compile");
        }*/
	}
	
	/*
	 * Runs the code, given command, package and class names, streams, and params
	 */
	protected static void runCode(String command, String[] envp, String packageName, String className, String[] params, String outType, PrintStream outPS, String errorType, PrintStream errorPS) throws IOException, InterruptedException, FailedToRunException {
		ByteArrayOutputStream errBAOS = new ByteArrayOutputStream();
		if (null == errorPS) {
			errorPS = new PrintStream(errBAOS);
		}
		int retVal = EnergyProfiler.runExternal(command + " -cp " + System.getProperty("java.class.path") + " " + getPathToClass(packageName, className) + getParamString(params), envp, outType, outPS, errorType, errorPS);
		if (0 != retVal) {
			Diag.println(command + " -cp " + System.getProperty("java.class.path") + " " + getPathToClass(packageName, className) + getParamString(params));
			Diag.println(errBAOS.toString());
			throw new FailedToRunException("Failed to run: " + retVal);
		}
	}
	
	/*
	 * Cleans up the created files packageName/className.java and packageName/className.class
	 */
	protected static void cleanup(String packageName, String className) throws IOException {
		// Don't do any of this - not worth this risk within the "src/" directory!
		// TODO: Can redo this, as source is now in srcMod
/*		// Delete files
		Files.delete(Paths.get("src/" + getPathToClass(packageName, className) + ".java"));
		Files.delete(Paths.get("src/" + getPathToClass(packageName, className) + ".class"));
		// Delete paths if empty
		String packagePath = getPackagePath(packageName);
		while ("" != packagePath) {
			Path packPath = Paths.get(packagePath);
			try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(packPath)) {
				if (!dirStream.iterator().hasNext()) {
					Files.delete(packPath);
					String[] splitPath = packagePath.split("/");
					splitPath = Arrays.copyOf(splitPath, splitPath.length - 1);
					packagePath = String.join("/", splitPath);
				} else {
					break;
				}
			}
		}*/
	}
	
	/* FITNESS METHODS */
	
	/*
	 * At the moment, assumes "code" is a valid program ready to write, compile, and run...
	 * may need to add certain bits such as a main method to wrap it?
	 * 
	 * The code can be in a package, as long as it is a standalone Java file (and only one!)
	 * 
	 * Will return the fitness as a double in terms of "energy use" (for whatever definition
	 * the specific implementations use), therefore lower is better.
	 * POSITIVE_INFINITY indicates an error (maximum / worst fitness)
	 */
	abstract public double fitness(String code, String packageName, String className, String[] params) throws IOException, InterruptedException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, FailedToCompileException, FailedToRunException;
	/*
	 * This version should only work if a non-default constructor is used
	 */
	public double fitness(String code, String[] params) throws IOException, InterruptedException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, FailedToCompileException, FailedToRunException {
		if (null == testClasses || 1 != testClasses.size()) {
			throw new NullPointerException("Cannot use this version of fitness method with the default constructor");
		}
		return fitness(code, testClasses.get(0).getFirst(), testClasses.get(0).getSecond(), params);
	}
}
