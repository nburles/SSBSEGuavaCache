package cs.crest.cachetune;

import energyProfiler.StreamGobbler;

public class RunFitness {
	static final Runtime rt = Runtime.getRuntime();
	
	public static void main(String[] args) {
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
}
