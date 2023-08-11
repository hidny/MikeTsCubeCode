package Demo;


import Coord.Coord3D;
import Utils.Utils;

public class DFSPolyCubeCounterFixed {


	public static final int NUM_ROTATIONS_3D = 24;
	
	public static final int NUM_NEIGHBOURS_3D= 6;
	public static final int NUM_ROTATIONS_2D_CHEAT= 4;
	public static final int NUM_ROTATIONS_2D = 4;

	
	//TODO: make it so the user can choose between 2D and 3D (For now, just change the var here)
	//public static final int NUM_NEIGHBOURS = NUM_ROTATIONS_2D_CHEAT;
	public static final int NUM_NEIGHBOURS = NUM_NEIGHBOURS_3D;
	
	public static final int BORDER_PADDING = 2;
	
	public static final int NOT_INSERTED = -1;
	
	public static Coord3D Coord3DSharedMem[][][];
	
	public static void solveCuboidIntersections(int N) {
		

		//I decided to null terminate the arrays because I'm nostalgic towards my C programming days...
		Coord3D cubesToDevelop[] = new Coord3D[N + 1];

		for(int i=0; i<cubesToDevelop.length; i++) {
			cubesToDevelop[i] = null;
		}
		
		int GRID_SIZE = 2*N+1 + 2*BORDER_PADDING;
	
		boolean cubesUsed[][][] = new boolean[GRID_SIZE][GRID_SIZE][GRID_SIZE];
		
		int cubesOrdering[][][] = new int[GRID_SIZE][GRID_SIZE][GRID_SIZE];
		Coord3DSharedMem = new Coord3D[GRID_SIZE][GRID_SIZE][GRID_SIZE];
		

		for(int i=0; i<cubesUsed.length; i++) {
			for(int j=0; j<cubesUsed[0].length; j++) {
				for(int k=0; k<cubesUsed[0][0].length; k++) {
					cubesUsed[i][j][k] = false;
					cubesOrdering[i][j][k] = NOT_INSERTED;
					Coord3DSharedMem[i][j][k] = new Coord3D(i, j, k);
				}
			}
		}

		//Default start location GRID_SIZE / 2, GRID_SIZE / 2
		int START_I = GRID_SIZE/2;
		int START_J = GRID_SIZE/2;
		int START_K = GRID_SIZE/2;
		
		
		//Once this reaches the value of N, we're done!
		int numCellsUsedDepth = 0;

		int START_INDEX = 0;
		int START_ROTATION = 0;
		
		cubesUsed[START_I][START_J][START_K] = true;
		cubesOrdering[START_I][START_J][START_K] = 0;
		cubesToDevelop[numCellsUsedDepth] = Coord3DSharedMem[START_I][START_J][START_K];
		
		numCellsUsedDepth += 1;
	
		long numSolutions = 1;
		if(N <= 1) {
			//Nevermind...
		} else {
			numSolutions = doDepthFirstSearch(cubesToDevelop, cubesUsed, numCellsUsedDepth,
				cubesOrdering, START_INDEX, START_ROTATION);
		}
		System.out.println("-------------------");
		System.out.println("-------------------");
		System.out.println("-------------------");
		System.out.println("Current UTC timestamp in milliseconds: " + System.currentTimeMillis());
			
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println("Final number of unique solutions: " + numSolutions);
	}
	
	
	public static final int nudgeBasedOnRotation[][] = {{-1, 0,  1,  0,  0,  0},
														{0,  1,  0, -1,  0,  0},
														{0,  0,  0,  0,  1,  -1}};
	public static long numIterations = 0;
	public static long numSolutionsSoFarDebug = 0L;
	
	public static long doDepthFirstSearch(Coord3D cubesToDevelop[], boolean cubesUsed[][][], int numCellsUsedDepth,
			int cubesOrdering[][][], int minIndexToUse, int minRotationToUse) {

		//System.out.println(numIterations);
		
		numIterations++;

		//Display debug/what's-going-on update:
		if(numIterations % 10000000L == 0) {
			
			System.out.println("Num iterations: " + numIterations);
			Utils.printCubesSingleDigitFirst10(cubesUsed, cubesToDevelop);
			
			System.out.println("Solutions: " + numSolutionsSoFarDebug);
			System.out.println();
			
			
		}
		//End display debug/what's-going-on update
		
		long retDuplicateSolutions = 0L;
		
		//DEPTH-FIRST START:
		for(int curOrderedIndexToUse=minIndexToUse; curOrderedIndexToUse<numCellsUsedDepth && cubesToDevelop[curOrderedIndexToUse] != null; curOrderedIndexToUse++) {
			

			//Try to attach a cell onto indexToUse using all rotations:
			for(int dirNewCellAdd=0; dirNewCellAdd<NUM_NEIGHBOURS; dirNewCellAdd++) {
				
				if(curOrderedIndexToUse == minIndexToUse
						&& dirNewCellAdd <  minRotationToUse) {
					continue;
				}

				
				int new_i = cubesToDevelop[curOrderedIndexToUse].a + nudgeBasedOnRotation[0][dirNewCellAdd];
				int new_j = cubesToDevelop[curOrderedIndexToUse].b + nudgeBasedOnRotation[1][dirNewCellAdd];
				int new_k = cubesToDevelop[curOrderedIndexToUse].c + nudgeBasedOnRotation[2][dirNewCellAdd];

				if(new_i > cubesToDevelop[0].a 
						|| (new_i == cubesToDevelop[0].a && new_j < cubesToDevelop[0].b)
						|| (new_i == cubesToDevelop[0].a && new_j == cubesToDevelop[0].b && new_k < cubesToDevelop[0].c)
					) {
					continue;
				} else if(cubesUsed[new_i][new_j][new_k]) {
					//Cell we are considering to add is already there...
					continue;
				}
				
				boolean cantAddCellBecauseOfOtherNeighbours = cantAddCellBecauseOfOtherNeighbours(
						cubesToDevelop, cubesUsed, numCellsUsedDepth,
						cubesOrdering, curOrderedIndexToUse, dirNewCellAdd,
						curOrderedIndexToUse,
						new_i, new_j, new_k
					);
				
				
				if( ! cantAddCellBecauseOfOtherNeighbours) {


					//Setup for adding new cube:
					cubesUsed[new_i][new_j][new_k] = true;
					cubesToDevelop[numCellsUsedDepth] = Coord3DSharedMem[new_i][new_j][new_k];
					cubesOrdering[new_i][new_j][new_k] = numCellsUsedDepth;
					//End setup

					numCellsUsedDepth += 1;

					if(numCellsUsedDepth == cubesToDevelop.length - 1) {
						
						numSolutionsSoFarDebug++;
						retDuplicateSolutions++;
						
						//System.out.println("Solution:");
						//Utils.printCubesSingleDigitFirst10(cubesUsed, cubesToDevelop);
						
					} else {
						retDuplicateSolutions += doDepthFirstSearch(cubesToDevelop, cubesUsed, numCellsUsedDepth,
								cubesOrdering, curOrderedIndexToUse, dirNewCellAdd
							);
					}

					numCellsUsedDepth -= 1;

					//Tear down (undo add of new cell)
					cubesUsed[new_i][new_j][new_k] = false;
					cubesToDevelop[numCellsUsedDepth] = null;
					cubesOrdering[new_i][new_j][new_k] = NOT_INSERTED;
					//End tear down


				} // End recursive if cond
			} // End loop rotation
		} //End loop index

		return retDuplicateSolutions;
	}
	
	public static final int ONE_EIGHTY_ROTATION = 2;
	
	//I'm enforcing an artificial constraint where the polycube shape
	// has to develop in the same order as a breath-first-search.
	// This has a lot of advantages that I will need to explain in some docs.
	public static boolean cantAddCellBecauseOfOtherNeighbours(Coord3D cubesToDevelop[], boolean cubesUsed[][][], int numCellsUsedDepth,
			int cubesOrdering[][][], int minIndexToUse, int minRotationToUse,
			int curOrderedIndexToUse,
			int new_i, int new_j, int new_k) {

		boolean cantAddCellBecauseOfOtherNeighbours = false;
		
		int neighboursBasedOnRotation[][] = {{new_i-1,   new_j,     new_k},
											 {new_i,   new_j+1,     new_k},
											 {new_i+1,   new_j,     new_k},
											 {new_i, new_j - 1,     new_k},
											 {new_i,     new_j, new_k + 1},
											 {new_i,     new_j, new_k - 1},
											 };
		
		
		for(int rotReq=0; rotReq<neighboursBasedOnRotation.length; rotReq++) {
			
			int i1 = neighboursBasedOnRotation[rotReq][0];
			int j1 = neighboursBasedOnRotation[rotReq][1];
			int k1 = neighboursBasedOnRotation[rotReq][2];
		
			if(cubesToDevelop[curOrderedIndexToUse].a == i1 
				&& cubesToDevelop[curOrderedIndexToUse].b == j1
				&& cubesToDevelop[curOrderedIndexToUse].c == k1) {
				
				continue;
			}
			
			//System.out.println("Cube neighbour:" + i1 + ", " + j1 + ", " + k1);
			
			if(cubesUsed[i1][j1][k1]) {
				//System.out.println("Connected to another paper");
				
				int orderOtherCell = cubesOrdering[i1][j1][k1];
		
				if(orderOtherCell < curOrderedIndexToUse ) {
					cantAddCellBecauseOfOtherNeighbours = true;
					break;
				}
				
			}
		}

		return cantAddCellBecauseOfOtherNeighbours;
	}
	


	public static void main(String args[]) {
		
		
		System.out.println("Polycube counter program (Fixed):");
		System.out.println("Current UTC timestamp in milliseconds: " + System.currentTimeMillis());
		
		int N = 14;
		solveCuboidIntersections(N);

		//Fixed solutions for 3D:
		// https://oeis.org/A001931
		// 1, 3, 15, 86, 534
		// 3481, 23502, 120631, 1150122, 8276084
		// 60494549, 446205905, 3322769321
		
		
		System.out.println("Done with N = " + N);
		System.out.println("Current UTC timestamp in milliseconds: " + System.currentTimeMillis());
		
	}

	

}
