package SolutionResolver;

import Coord.Coord3D;
import DupCheck.BasicUniqueCheckImproved2D;
import Utils.Utils;

public class StandardResolverForBig2DSolutions implements SolutionResolverInterface {

	private long numUniqueFound = 0;
	private long numFound = 0;
	
	public StandardResolverForBig2DSolutions() {
		
		
	}
	
	@Override
	public long resolveSolution(Coord3D[] cuboidsInOrderToDevelop, boolean[][][] cubesUsed) {

		System.out.println(numFound +
				" (num unique: " + numUniqueFound + ")");
		
		numFound++;
		

		if(BasicUniqueCheckImproved2D.isUnique(cuboidsInOrderToDevelop, cubesUsed)) {
			numUniqueFound++;

			System.out.println("----");
			System.out.println("Unique solution found:");
			Utils.printCubes(cubesUsed, cuboidsInOrderToDevelop);
			
			System.out.println("Solution code: " + BasicUniqueCheckImproved2D.debugLastScore);
			System.out.println("Num unique solutions found: " + 
					numUniqueFound);

			return 1L;
		} else {

			//System.out.println("Solution not found");
			return 0L;
		}
	}

	public long getNumUniqueFound() {
		return numUniqueFound;
	}



	
}
