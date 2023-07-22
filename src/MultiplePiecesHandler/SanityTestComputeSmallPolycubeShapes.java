package MultiplePiecesHandler;



public class SanityTestComputeSmallPolycubeShapes {

	public static void main(String args[]) {
		
		
		int maxDepth = 7;
		
		int N = 10;
		int NUM_PIECED_FOR_N_AND_MAX_DEPTH = 600;
		
		long numSolutionsFound = 0L;
		
		int batchIndex = 1;

		for(int i=0; i<NUM_PIECED_FOR_N_AND_MAX_DEPTH; i++) {
			
			int indexToUse = batchIndex * NUM_PIECED_FOR_N_AND_MAX_DEPTH + i;
			System.out.println(i);
			numSolutionsFound += ComputeTaskMain.runSubtask(N, maxDepth, indexToUse);
			// N=10 test:
			// 292722
			//+ 53821
			// -------
			// 346543 (good!)
			
			
		}
		
		System.out.println("After going through " + NUM_PIECED_FOR_N_AND_MAX_DEPTH 
				+ " slices of the search space, the number of solutions for N = " + N + " is: " + numSolutionsFound);
	}
}
