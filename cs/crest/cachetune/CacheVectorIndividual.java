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

public class CacheVectorIndividual extends LongVectorIndividual {
	private static final long serialVersionUID = 1L;

	public enum ReferenceStrength {
    	STRONG, WEAK, SOFT;
    	public String toString() {
    		return name();
    	}
    }
    public enum ExpiryType {
    	ExpireAfterWrite, ExpireAfterAccess, RefreshAfterWrite;
    	public String toString() {
    		return name();
    	}
    }

    @Override
    public String genotypeToStringForHumans() {
        StringBuilder s = new StringBuilder();
        s.append("---------\n");
        s.append("initialCapacity: "+Long.toString(genome[0])+"\n");
        s.append("concurrencyLevel: "+Long.toString(genome[1])+"\n");
        s.append("maximumSize: "+Long.toString(genome[2])+"\n");
        ExpiryType expType = ExpiryType.values()[(int) genome[3]];
		s.append("expiryType: "+expType+"\n");
		switch (expType) {
		case ExpireAfterWrite:
			s.append("expireAfterWriteNanos: "+Long.toString(genome[4])+"ns\n");
			break;
		case ExpireAfterAccess:
			s.append("expireAfterAccessNanos: "+Long.toString(genome[5])+"ns\n");
			break;
		case RefreshAfterWrite:
			s.append("refreshAfterWriteNanos: "+Long.toString(genome[6])+"ns\n");
			break;
		}
		s.append("keyStrength: "+ReferenceStrength.values()[(int) genome[7]]+"\n");
		s.append("valueStrength: "+ReferenceStrength.values()[(int) genome[8]]+"\n");
        s.append("---------");
        return s.toString();
    }
}
