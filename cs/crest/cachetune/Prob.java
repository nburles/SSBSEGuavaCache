/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/

package cs.crest.cachetune;
import ec.*;
import ec.simple.*;
import ec.vector.*;
import energyProfiler.EnergyProfiler;
import energyProfiler.EnergyProfilerBytecodeTrace;
import energyProfiler.FailedToCompileException;
import energyProfiler.FailedToRunException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import jeep.lang.Diag;

public class Prob extends Problem implements SimpleProblemForm {
	private static final long serialVersionUID = 1L;
	private static EnergyProfiler energyProfiler;
	static {
		try {
			energyProfiler = new EnergyProfilerBytecodeTrace(
				"",//runCode,
				"",//runPackageName,
				"",//runClassName,
				new String[]{},
				new String(Files.readAllBytes( Paths.get( "com/google/common/cache/CacheBuilder.java" ) ) ),
				"com.google.common.cache",
				"CacheBuilder"
				);
		} catch (ClassNotFoundException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| IOException | InterruptedException | FailedToCompileException e) {
			e.printStackTrace();
		}
	}

    public void evaluate(final EvolutionState state, Individual ind, final int subpopulation, final int threadnum) {
        if (ind.evaluated) {
        	return;
        }
        
        long[] chromosome = (long[]) ((CacheVectorIndividual) ind).getGenome();

        Diag.println("Chromosome: [" + chromosome[0] + "," + chromosome[1] + "," + chromosome[2] + "," + chromosome[3] + "," + chromosome[4] + "," + chromosome[5] + "," + chromosome[6] + "," + chromosome[7] + "," + chromosome[8] + "]");

        String newCache = CreateNewCache.createNewCache(chromosome);
        double fitness = Double.POSITIVE_INFINITY;
        try {
			fitness = energyProfiler.fitness(newCache, new String[]{});
		} catch (ClassNotFoundException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| IOException | InterruptedException | FailedToCompileException
				| FailedToRunException e) {
			e.printStackTrace();
		}
        
        ((SimpleFitness) ind.fitness).setFitness(state, -1 * fitness, false); // how would we know if the individual was ideal - isn't that the point of the GA!?
        ind.evaluated = true;
    }
}
