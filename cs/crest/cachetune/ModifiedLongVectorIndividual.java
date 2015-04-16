/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package cs.crest.cachetune;

import ec.*;
import ec.util.*;
import ec.vector.*;
import java.io.*;
import cs.crest.cachetune.Prob;




public class ModifiedLongVectorIndividual extends LongVectorIndividual
    {
        
    public String genotypeToStringForHumans()
        {
        StringBuilder s = new StringBuilder();
	String initialCapacity = Integer.toString((int) genome[0]);
	String concurrencyLevel = Integer.toString((int) genome[1]);
	String maximumSize = Integer.toString((int) genome[2]);
	String expireAfterWriteNanos = String.valueOf(genome[3]);
	String expireAfterAccessNanos = String.valueOf(genome[4]);
	String refreshNanos = String.valueOf(genome[5]);
	String keyStrength = Prob.convertToStrengthIndex(genome[6]);
	String valueStrength = Prob.convertToStrengthIndex(genome[7]);
	
            s.append("---------\n");
	s.append("initialCapacity: "+initialCapacity+"\n");
	s.append("concurrencyLevel: "+concurrencyLevel+"\n");
	s.append("maximumSize: "+maximumSize+"\n");
	s.append("expireAfterWriteNanos: "+expireAfterWriteNanos+"\n");
	s.append("expireAfterAccessNanos: "+expireAfterAccessNanos+"\n");
	s.append("refreshNanos: "+refreshNanos+"\n");
	s.append("keyStrength: "+keyStrength+"\n");
	s.append("valueStrength: "+valueStrength+"\n");
            s.append("---------");
        return s.toString();
        }
}
