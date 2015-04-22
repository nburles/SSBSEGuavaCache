package cs.crest.cachetune;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import cs.crest.cachetune.CacheVectorIndividual.ExpiryType;
import cs.crest.cachetune.CacheVectorIndividual.ReferenceStrength;

public class CreateNewCache {
	private static final String[] template;
	static {
		template = new String[9];
		for (int i = 0; i < 9; ++i) {
			try {
				template[i] = new String(Files.readAllBytes( Paths.get( "src/cs/crest/cachetune/cacheTemplate" + Integer.toString(i) + ".java.txt" ) ) );
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static String createNewCache(long[] genome) {
		StringBuilder s = new StringBuilder();
		s.append(template[0]);
		s.append(genome[0]);
		s.append(template[1]);
		s.append(genome[1]);
		s.append(template[2]);
		s.append(genome[2]);
		s.append(template[3]);
		s.append(ReferenceStrength.values()[(int) genome[7]]);
		s.append(template[4]);
		s.append(ReferenceStrength.values()[(int) genome[8]]);
		s.append(template[5]);
		ExpiryType expType = ExpiryType.values()[(int) genome[3]];
		if (ExpiryType.ExpireAfterWrite == expType) {
			s.append(genome[4]);
		} else {
			s.append(-1);
		}
		s.append(template[6]);
		if (ExpiryType.ExpireAfterAccess == expType) {
			s.append(genome[5]);
		} else {
			s.append(-1);
		}
		s.append(template[7]);
		if (ExpiryType.RefreshAfterWrite == expType) {
			s.append(genome[6]);
		} else {
			s.append(-1);
		}
		s.append(template[8]);
		return s.toString();
	}
}
