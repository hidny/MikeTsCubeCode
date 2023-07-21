package MultiplePiecesHandler;



public class SanityTestComputeSmallPolycubeShapes {

	public static void main(String args[]) {
		
		
		int maxDepth = 7;
		
		int N = 8;
		int NUM_PIECED_FOR_N_AND_MAX_DEPTH = -1;
		
		long numSolutionsFound = 0L;
		//TODO: num 
		//10671
		for(int i=0; i<NUM_PIECED_FOR_N_AND_MAX_DEPTH; i++) {
			
			System.out.println(i);
			numSolutionsFound += ComputeTaskMain.runSubtask(N, maxDepth, i);
			
			
		}
		
		System.out.println("After going through " + NUM_PIECED_FOR_N_AND_MAX_DEPTH 
				+ " slices of the search space, the number of solutions for N = " + N + " is: " + numSolutionsFound);
	}
}
