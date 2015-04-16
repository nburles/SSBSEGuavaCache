package energyProfiler;

import java.util.HashMap;
import java.util.Map;
 
public class CalculateBytecodeEnergy {
	private static final Map<String, Double> opcodeCosts = new HashMap<String, Double>();
	static {
		opcodeCosts.put("00", 136.53789216515506);
		opcodeCosts.put("01", 482.32630895392297);
		opcodeCosts.put("02", 380.05187728438004);
		opcodeCosts.put("03", 314.49362978076340);
		opcodeCosts.put("04", 370.84284130016570);
		opcodeCosts.put("05", 372.4552205365486);
		opcodeCosts.put("06", 384.41007759708785);
		opcodeCosts.put("07", 343.14065887639649);
		opcodeCosts.put("08", 350.34712907277684);
		opcodeCosts.put("09", 392.23030049943947);
		opcodeCosts.put("0a", 356.47613013017176);
		opcodeCosts.put("0b", 394.06662686679385);
		opcodeCosts.put("0c", 359.57117752058308);
		opcodeCosts.put("0d", 310.28595412304512);
		opcodeCosts.put("0e", 481.86640537493091);
		opcodeCosts.put("0f", 305.23204724144117);
		opcodeCosts.put("10", 358.24212106841487);
		opcodeCosts.put("11", 243.85067565964634);
		opcodeCosts.put("12", 405.55313416563666);
		opcodeCosts.put("13", 426.6380729676017);
		opcodeCosts.put("14", 447.97994774641477);
		opcodeCosts.put("15", 1779.9643513857787);
		opcodeCosts.put("16", 1691.74456554682894);
		opcodeCosts.put("17", 1640.4057650454747);
		opcodeCosts.put("18", 1976.8347371767966);
		opcodeCosts.put("19", 1062.35428298146746);
		opcodeCosts.put("1a", 1663.6022225977119);
		opcodeCosts.put("1b", 1643.8645981565531);
		opcodeCosts.put("1c", 1773.3897273585088);
		opcodeCosts.put("1d", 1754.7153086970235);
		opcodeCosts.put("1e", 1704.5361872990462);
		opcodeCosts.put("1f", 1869.3918807053013);
		opcodeCosts.put("20", 1897.6035751480962);
		opcodeCosts.put("21", 1416.458891962097);
		opcodeCosts.put("22", 1445.9276982997159);
		opcodeCosts.put("23", 1568.4090898413476);
		opcodeCosts.put("24", 1669.2757881147496);
		opcodeCosts.put("25", 1497.8890467020466);
		opcodeCosts.put("26", 1972.5604133970082);
		opcodeCosts.put("27", 1658.2480716216699);
		opcodeCosts.put("28", 1501.2894065056602);
		opcodeCosts.put("29", 1631.4187876329503);
		opcodeCosts.put("2a", 1072.46196935435784);
		opcodeCosts.put("2b", 1056.0357248778836);
		opcodeCosts.put("2c", 1149.1010346152157);
		opcodeCosts.put("2d", 1155.4497718858381);
		opcodeCosts.put("2e", 1610.6910441498539);
		opcodeCosts.put("2f", 1236.1743079036599);
		opcodeCosts.put("30", 1042.48471743134245);
		opcodeCosts.put("31", 2033.9744768679851);
		opcodeCosts.put("32", 996.9790590326433);
		opcodeCosts.put("33", 1094.9919524886442);
		opcodeCosts.put("34", 980.9277049562584);
		opcodeCosts.put("35", 506.16912475799601);
		opcodeCosts.put("36", 1093.2632032830274);
		opcodeCosts.put("37", 563.37891833212312);
		opcodeCosts.put("38", 1242.5240175201112);
		opcodeCosts.put("39", 711.07199076221865);
		opcodeCosts.put("3a", 968.8469461452937);
		opcodeCosts.put("3b", 1092.4717536822136);
		opcodeCosts.put("3c", 1147.1483747148223);
		opcodeCosts.put("3d", 1112.8093221048429);
		opcodeCosts.put("3e", 1072.07665838584774);
		opcodeCosts.put("3f", 646.8802037593194);
		opcodeCosts.put("40", 627.86496013655417);
		opcodeCosts.put("41", 569.80780003455531);
		opcodeCosts.put("42", 622.45377928773177);
		opcodeCosts.put("43", 1236.1365179541865);
		opcodeCosts.put("44", 1318.5091370064634);
		opcodeCosts.put("45", 1186.402829537879);
		opcodeCosts.put("46", 1183.4624172673411);
		opcodeCosts.put("47", 768.9008069491032);
		opcodeCosts.put("48", 668.61088516715429);
		opcodeCosts.put("49", 693.2059564304508);
		opcodeCosts.put("4a", 765.21652604855576);
		opcodeCosts.put("4b", 1052.3051750101984);
		opcodeCosts.put("4c", 1085.0026896896567);
		opcodeCosts.put("4d", 1074.4149774038156);
		opcodeCosts.put("4e", 893.31060065181263);
		opcodeCosts.put("4f", 2055.1407235431392);
		opcodeCosts.put("50", 1689.2357724419128);
		opcodeCosts.put("51", 1678.7476944881322);
		opcodeCosts.put("52", 2522.3934579216367);
		opcodeCosts.put("53", 2699.1314484069037);
		opcodeCosts.put("54", 2241.5393636908467);
		opcodeCosts.put("55", 2579.1340138948859);
		opcodeCosts.put("56", 1160.28068434848145);
		opcodeCosts.put("57", 250.4541110591975);
		opcodeCosts.put("58", 1005.62222940788696);
		opcodeCosts.put("59", 2167.5003648028720);
		opcodeCosts.put("5a", 3425.8006925384433);
		opcodeCosts.put("5b", 3762.656697668835);
		opcodeCosts.put("5c", 3298.5494756391923);
		opcodeCosts.put("5d", 4539.2139933506098);
		opcodeCosts.put("5e", 4234.4099607448256);
		opcodeCosts.put("5f", 2746.5966408747198);
		opcodeCosts.put("60", 1924.9675107106652);
		opcodeCosts.put("61", 1684.2014568575896);
		opcodeCosts.put("62", 1461.9723445613177);
		opcodeCosts.put("63", 1704.0157354270757);
		opcodeCosts.put("64", 1744.1423798144669);
		opcodeCosts.put("65", 1542.46533869973615);
		opcodeCosts.put("66", 1530.4857859235287);
		opcodeCosts.put("67", 1817.5471189031757);
		opcodeCosts.put("68", 1363.3178226124026);
		opcodeCosts.put("69", 1751.3578404275832);
		opcodeCosts.put("6a", 1628.0677640044741);
		opcodeCosts.put("6b", 1670.4129837364326);
		opcodeCosts.put("6c", 2903.4322150939608);
		opcodeCosts.put("6d", 3945.9257110820567);
		opcodeCosts.put("6e", 2344.699774000306);
		opcodeCosts.put("6f", 3330.1222797562697);
		opcodeCosts.put("70", 2814.6861677638973);
		opcodeCosts.put("71", 3819.0632939587568);
		opcodeCosts.put("72", 2404.5277106720337);
		opcodeCosts.put("73", 3029.542011263556);
		opcodeCosts.put("74", 672.94359286185253);
		opcodeCosts.put("75", 558.1188613202274);
		opcodeCosts.put("76", 669.16058560769759);
		opcodeCosts.put("77", 813.1888005101196);
		opcodeCosts.put("78", 1180.97565057233635);
		opcodeCosts.put("79", 895.62982609129806);
		opcodeCosts.put("7a", 1162.0661591879073);
		opcodeCosts.put("7b", 912.5277143509661);
		opcodeCosts.put("7c", 1277.4649279763465);
		opcodeCosts.put("7d", 1010.90907131364025);
		opcodeCosts.put("7e", 1339.7477750980488);
		opcodeCosts.put("7f", 1628.7642438707576);
		opcodeCosts.put("80", 1228.0281660496675);
		opcodeCosts.put("81", 1564.8675688760982);
		opcodeCosts.put("82", 1247.7212637163860);
		opcodeCosts.put("83", 1519.3611258644812);
		opcodeCosts.put("84", 1960.2582990721699);
		opcodeCosts.put("85", 735.3528423233255);
		opcodeCosts.put("86", 758.5382647323375);
		opcodeCosts.put("87", 567.9497240738392);
		opcodeCosts.put("88", 716.7968735953607);
		opcodeCosts.put("89", 759.87757649612685);
		opcodeCosts.put("8a", 893.25384860098284);
		opcodeCosts.put("8b", 1230.7978767091930);
		opcodeCosts.put("8c", 1297.02082588377264);
		opcodeCosts.put("8d", 736.0176647553688);
		opcodeCosts.put("8e", 1327.1283436448405);
		opcodeCosts.put("8f", 1351.4000078211108);
		opcodeCosts.put("90", 867.01803618558246);
		opcodeCosts.put("91", 679.87145583923681);
		opcodeCosts.put("92", 611.78989236988555);
		opcodeCosts.put("93", 672.75560580838715);
		opcodeCosts.put("94", 1507.03258200765845);
		opcodeCosts.put("95", 1593.7139814452635);
		opcodeCosts.put("96", 1631.8292192289974);
		opcodeCosts.put("97", 2022.8042795912463);
		opcodeCosts.put("98", 1957.484161872320);
		opcodeCosts.put("99", 738.3832664885940);
		opcodeCosts.put("9a", 641.14818054042656);
		opcodeCosts.put("9b", 682.74756799829790);
		opcodeCosts.put("9c", 666.03299386917602);
		opcodeCosts.put("9d", 601.42013061923543);
		opcodeCosts.put("9e", 712.2962938904472);
		opcodeCosts.put("9f", 1644.75094930681365);
		opcodeCosts.put("a0", 1749.5612414199371);
		opcodeCosts.put("a1", 1990.0025558479899);
		opcodeCosts.put("a2", 1571.5719314271815);
		opcodeCosts.put("a3", 1647.5826416839722);
		opcodeCosts.put("a4", 1488.4472581247469);
		opcodeCosts.put("a5", 1290.9221220321007);
		opcodeCosts.put("a6", 1099.5806870461193);
		opcodeCosts.put("a7", 254.60247861972862);
		opcodeCosts.put("a8", 139.31494116251238);
		opcodeCosts.put("a9", 69.30392769272829);
		opcodeCosts.put("aa", 200.10548598783660);
		opcodeCosts.put("ab", 0.0);
		opcodeCosts.put("ac", 117.85933071257232);
		opcodeCosts.put("ad", 77.57641008761712);
		opcodeCosts.put("ae", 183.51758524048708);
		opcodeCosts.put("af", 137.74820901121390);
		opcodeCosts.put("b0", 49.781666478258220);
		opcodeCosts.put("b1", 53.405809009278862);
		opcodeCosts.put("b2", 932.7798348090633);
		opcodeCosts.put("b3", 1048.5292334827400);
		opcodeCosts.put("b4", 1262.8816738851614);
		opcodeCosts.put("b5", 605.8824021574601);
		opcodeCosts.put("b6", 0.0);
		opcodeCosts.put("b7", 0.0);
		opcodeCosts.put("b8", 148.91331136495950);
		opcodeCosts.put("b9", 0.0);
		opcodeCosts.put("ba", 0.0);
		opcodeCosts.put("bb", 50.387323268502967);
		opcodeCosts.put("bc", 68.00199551253594);
		opcodeCosts.put("bd", 156.12684238864961);
		opcodeCosts.put("be", 1014.37721223049808);
		opcodeCosts.put("bf", 2.8256639768731051);
		opcodeCosts.put("c0", 529.51909057319604);
		opcodeCosts.put("c1", 1040.9719764381637);
		opcodeCosts.put("c2", 0.0);
		opcodeCosts.put("c3", 0.0);
		opcodeCosts.put("c5", 47.197926136165026);
		opcodeCosts.put("c6", 732.13847458139726);
		opcodeCosts.put("c7", 739.09208660153509);
		opcodeCosts.put("c8", 265.3990503022116);
		opcodeCosts.put("c9", 141.83997443313849);
		opcodeCosts.put("cb", 1262.8816738851614); // illegal: fast_agetfield
		opcodeCosts.put("cc", 1262.8816738851614); // illegal: fast_bgetfield
		opcodeCosts.put("cd", 1262.8816738851614); // illegal: fast_cgetfield
		opcodeCosts.put("ce", 1262.8816738851614); // illegal: fast_dgetfield
		opcodeCosts.put("cf", 1262.8816738851614); // illegal: fast_fgetfield
		opcodeCosts.put("d0", 1262.8816738851614); // illegal: fast_igetfield
		opcodeCosts.put("d1", 1262.8816738851614); // illegal: fast_lgetfield
		opcodeCosts.put("d2", 1262.8816738851614); // illegal: fast_sgetfield
		opcodeCosts.put("d3", 605.8824021574601); // illegal: fast_aputfield
		opcodeCosts.put("d4", 605.8824021574601); // illegal: fast_bputfield
		opcodeCosts.put("d5", 605.8824021574601); // illegal: fast_cputfield
		opcodeCosts.put("d6", 605.8824021574601); // illegal: fast_dputfield
		opcodeCosts.put("d7", 605.8824021574601); // illegal: fast_fputfield
		opcodeCosts.put("d8", 605.8824021574601); // illegal: fast_iputfield
		opcodeCosts.put("d9", 605.8824021574601); // illegal: fast_lputfield
		opcodeCosts.put("da", 605.8824021574601); // illegal: fast_sputfield
		opcodeCosts.put("db", 1072.46196935435784); // illegal: fast_aload_0
		opcodeCosts.put("dc", 1072.46196935435784); // illegal: fast_iaccess_0
		opcodeCosts.put("dd", 1072.46196935435784); // illegal: fast_aaccess_0
		opcodeCosts.put("de", 1072.46196935435784); // illegal: fast_faccess_0
		opcodeCosts.put("df", 1779.9643513857787); // illegal: fast_iload
		opcodeCosts.put("e0", 1779.9643513857787); // illegal: fast_iload2
		opcodeCosts.put("e1", 1779.9643513857787); // illegal: fast_icaload
		opcodeCosts.put("e2", 0.0); // illegal: fast_invokevfinal
		opcodeCosts.put("e3", 0.0); // illegal: fast_linearswitch
		opcodeCosts.put("e4", 0.0); // illegal: fast_binaryswitch
		opcodeCosts.put("e5", 405.55313416563666); // illegal: fast_aldc
		opcodeCosts.put("e6", 426.6380729676017); // illegal: fast_aldc_w
		opcodeCosts.put("e7", 53.405809009278862); // illegal: return_register_finalizer
		opcodeCosts.put("e8", 0.0); // illegal: invokehandle
		opcodeCosts.put("c4", 0.0); // ignore this, it executes the first operand as the next insturction
		opcodeCosts.put("ca", 0.0); // ignore this, it's "breakpoint" reserved
		opcodeCosts.put("e9", 0.0); // ignore this, it's "_shouldnotreachhere" illegal opcode
	}
	
	/*
	 * Sums the energy used, according to the model developed for eLens (http://www.cs.binghamton.edu/~millerti/cs680r/papers/EstimatingMobileApplicationEnergy.pdf)
	 */
	public static double calculate(Map<String, Integer> bytecodeCounts) {
		double energyUsed = 0.0;
		
		for (Map.Entry<String, Integer> opcode : bytecodeCounts.entrySet()) {
			energyUsed += opcodeCosts.get(opcode.getKey()) * opcode.getValue();
		}
		
		return energyUsed; // this value is the energy used by CPU+RAM in nJ
	}
}
