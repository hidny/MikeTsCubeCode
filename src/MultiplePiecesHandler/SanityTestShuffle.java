package MultiplePiecesHandler;

import java.math.BigInteger;

public class SanityTestShuffle {

	public static void main(String args[]) {
		/*
secret key: 61173
Exp: 17
Mod: 2083723
		 */
		BigInteger exp = new BigInteger("" + 17);
		BigInteger key = new BigInteger("" + 61173);
		int modInt= 2083723;

		
		sanityTestShuffle(exp, key, new BigInteger("" + modInt));
	}
	
	public static void sanityTestShuffle(BigInteger exp, BigInteger key, BigInteger modInt) {
		//BigInteger exp = new BigInteger("" + 1);
		//BigInteger key = new BigInteger("" + 13);
		//int modInt= 21;
		
		BigInteger mod = new BigInteger("" + modInt);
		
		for(int i=0; i<modInt.intValue(); i++) {
			
			BigInteger iBig = new BigInteger("" + i);
			BigInteger tmp = ComputeBatchMain.getAPowerPmodMOD(iBig, exp, mod);
			
			BigInteger maybeI = ComputeBatchMain.getAPowerPmodMOD(tmp, key, mod);
			
			if(iBig.compareTo(maybeI) != 0) {
				System.out.println("AHH for i = " + i);
				System.out.println(i);
				System.out.println(maybeI);
				
				System.exit(1);
			}
			
		}
		
		System.out.println("Done");
	}
}
