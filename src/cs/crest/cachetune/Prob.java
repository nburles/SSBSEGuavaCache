/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/

package cs.crest.cachetune;
import ec.*;
import ec.simple.*;
import ec.vector.*;
import java.io.InputStreamReader;
import java.io.BufferedReader;

public class Prob extends Problem implements SimpleProblemForm
    {
	/*package*/ static String convertToStrengthIndex(long value){
		if(value%3 == 0){
			return "WEAK";
		} else if((value - 1)%3 == 0) {
			return "STRONG";
		}

		return "SOFT";
	}

    public void evaluate(final EvolutionState state,
        final Individual ind,
        final int subpopulation,
        final int threadnum)
        {
        if (ind.evaluated) return;
	
	LongVectorIndividual ind2= (LongVectorIndividual)ind;

	String initialCapacity = Integer.toString((int) ind2.genome[0]);
	String concurrencyLevel = Integer.toString((int) ind2.genome[1]);
	String maximumSize = Integer.toString((int) ind2.genome[2]);
	String expireAfterWriteNanos = String.valueOf(ind2.genome[3]);
	String expireAfterAccessNanos = String.valueOf(ind2.genome[4]);
	String refreshNanos = String.valueOf(ind2.genome[5]);
	String keyStrength = convertToStrengthIndex(ind2.genome[6]);
	String valueStrength = convertToStrengthIndex(ind2.genome[7]);
	
	double fitness=0;
	try{
		Process proc = Runtime.getRuntime().exec("./evaluate "+initialCapacity+" "+concurrencyLevel+" "+maximumSize+" "+expireAfterWriteNanos+" "+expireAfterAccessNanos+" "+refreshNanos+" "+keyStrength+" "+valueStrength);

		BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		String s = null;
		while ((s = stdInput.readLine()) != null){

			fitness += Double.parseDouble(s);
		
		}
	} catch(Exception e){
		state.output.fatal("Could not read output from script");
	}	

	if(!(ind2.fitness instanceof SimpleFitness))
		state.output.fatal("Whoa! It's not a SimpleFitness!!!", null);
	((SimpleFitness)ind2.fitness).setFitness(state, fitness);
	ind2.evaluated = true;
        }
    }
