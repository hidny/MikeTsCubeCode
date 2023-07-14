package Utils;

import Coord.Coord3D;

public class Utils {

	public static final int NUM_SIDES = 6;
	
	public static int[] getBorders(Coord3D cuboidsInOrderToDevelop[]) {
		
		if(cuboidsInOrderToDevelop.length ==0) {
			return null;
		}
		
		int ret[] = new int[6];
		
		Coord3D tmp = cuboidsInOrderToDevelop[0];
		
		ret[0] = tmp.a;
		ret[1] = tmp.b;
		ret[2] = tmp.c;
		ret[3] = tmp.a;
		ret[4] = tmp.b;
		ret[5] = tmp.c;
		
		for(int i=1; cuboidsInOrderToDevelop[i] != null; i++) {
			
			tmp = cuboidsInOrderToDevelop[i];
			
			if(tmp == null) {
				break;
			}
 			if(tmp.a < ret[0]) {
 				ret[0] = tmp.a;
 			}
 			if(tmp.b < ret[1]) {
 				ret[1] = tmp.b;
 			}
 			if(tmp.c < ret[2]) {
 				ret[2] = tmp.c;
 			}
 			if(tmp.a > ret[3]) {
 				ret[3] = tmp.a;
 			}
 			if(tmp.b > ret[4]) {
 				ret[4] = tmp.b;
 			}
 			if(tmp.c > ret[5]) {
 				ret[5] = tmp.c;
 			}
		}
		return ret;
	}
	
	public static void printCubes(boolean cubesUsed[][][], Coord3D cuboidsInOrderToDevelop[]) {
		//Just be lazy and do 2D for now:
		int startKIndex = cubesUsed.length / 2;
		
		StringBuilder sb = new StringBuilder();
		
		int borders[] = getBorders(cuboidsInOrderToDevelop);
		
		for(int i=borders[0]; i<borders[3] + 1; i++) {
			for(int j=borders[1]; j<borders[4] + 1; j++) {
				if(cubesUsed[i][j][startKIndex]) {
					sb.append('#');
				} else {
					sb.append('.');
				}
			}
			sb.append("|" + System.lineSeparator());
		}
		System.out.println(sb.toString());
		
	}
	
	
}
