package NumPolyShapeSolve;


import Coord.Coord3D;
import SolutionResolver.SolutionResolverInterface;
import SolutionResolver.StandardResolverForSmall2DSolutions;
import Utils.Utils;

public class DFSPolyCubeCounter {

	
	public static final int NUM_ROTATIONS_2D_CHEAT= 4;
	public static final int NUM_ROTATIONS_3D = 6;

	//TODO: switch it to 3D later:
	public static final int NUM_ROTATIONS = NUM_ROTATIONS_2D_CHEAT;
	
	
	public static final int NUM_NEIGHBOURS = NUM_ROTATIONS;
	
	public static final int BORDER_PADDING = 2;
	
	public static final int NOT_INSERTED = -1;
	
	public static void solveCuboidIntersections(int N) {
		
		SolutionResolverInterface solutionResolver = new StandardResolverForSmall2DSolutions();

		Coord3D cubesToDevelop[] = new Coord3D[N];
		for(int i=0; i<cubesToDevelop.length; i++) {
			cubesToDevelop[i] = null;
		}
		
		int GRID_SIZE = 2*N+1 + 2*BORDER_PADDING;
	
		boolean cubesUsed[][][] = new boolean[GRID_SIZE][GRID_SIZE][GRID_SIZE];
		int cubesOrdering[][][] = new int[GRID_SIZE][GRID_SIZE][GRID_SIZE];;

		for(int i=0; i<cubesUsed.length; i++) {
			for(int j=0; j<cubesUsed[1].length; j++) {
				for(int k=0; k<cubesUsed[2].length; k++) {
					cubesUsed[i][j][k] = false;
					cubesOrdering[i][j][k] = NOT_INSERTED;
				}
			}
		}

		//Default start location GRID_SIZE / 2, GRID_SIZE / 2
		int START_I = GRID_SIZE/2;
		int START_J = GRID_SIZE/2;
		int START_K = GRID_SIZE/2;
		
		
		//Once this reaches the total area, we're done!
		int numCellsUsedDepth = 0;

		int START_INDEX = 0;
		int START_ROTATION = 0;
		
		cubesUsed[START_I][START_J][START_K] = true;
		cubesOrdering[START_I][START_J][START_K] = 0;
		cubesToDevelop[numCellsUsedDepth] = new Coord3D(START_I, START_J, START_K);
		
		numCellsUsedDepth += 1;
		
		long debugIterations[] = new long[N];
	
		doDepthFirstSearch(cubesToDevelop, cubesUsed, numCellsUsedDepth, solutionResolver,
				false, debugIterations, cubesOrdering, START_INDEX, START_ROTATION);
		
		
		System.out.println("Current UTC timestamp in milliseconds: " + System.currentTimeMillis());
			
		
		System.out.println("Final number of unique solutions: " + solutionResolver.getNumUniqueFound());
	}
	
	
	public static final int nugdeBasedOnRotation[][] = {{-1, 0,  1,  0,  0,  0},
														{0,  1,  0, -1,  0,  0},
														{0,  0,  0,  0,  1,  -1}};
	public static long numIterations = 0;
	
	public static long doDepthFirstSearch(Coord3D cubesToDevelop[], boolean cubesUsed[][][], int numCellsUsedDepth,
			SolutionResolverInterface solutionResolver,
			boolean debugNope, long debugIterations[],
			int cubesOrdering[][][], int minIndexToUse, int minRotationToUse) {

		if(numCellsUsedDepth == cubesToDevelop.length) {

			System.out.println("Found possibly duplicated solution!");
			long tmp = solutionResolver.resolveSolution(cubesToDevelop, cubesUsed);

			return tmp;
		}

		//System.out.println(numIterations);
		
		//Display debug/what's-going-on update
		numIterations++;
		
		if(numIterations % 100000000L == 0) {
			
			System.out.println("Num iterations: " + numIterations);
			Utils.printCubes(cubesUsed, cubesToDevelop);
			
			System.out.println("Solutions: " + solutionResolver.getNumUniqueFound());
			System.out.println();
			
			
		}
		//End display debug/what's-going-on update
		debugIterations[numCellsUsedDepth] = numIterations;
		
		long retDuplicateSolutions = 0L;
		
		//DEPTH-FIRST START:
		for(int curOrderedIndexToUse=minIndexToUse; curOrderedIndexToUse<numCellsUsedDepth && curOrderedIndexToUse<cubesToDevelop.length && cubesToDevelop[curOrderedIndexToUse] != null; curOrderedIndexToUse++) {
			

			//Try to attach a cell onto indexToUse using all 4 rotations:
			for(int dirNewCellAdd=0; dirNewCellAdd<NUM_ROTATIONS; dirNewCellAdd++) {
				
				if(curOrderedIndexToUse == minIndexToUse
						&& dirNewCellAdd <  minRotationToUse) {
					continue;
				}

				
				int new_i = cubesToDevelop[curOrderedIndexToUse].a + nugdeBasedOnRotation[0][dirNewCellAdd];
				int new_j = cubesToDevelop[curOrderedIndexToUse].b + nugdeBasedOnRotation[1][dirNewCellAdd];
				int new_k = cubesToDevelop[curOrderedIndexToUse].c + nugdeBasedOnRotation[2][dirNewCellAdd];

				
				if(cubesUsed[new_i][new_j][new_k]) {
					//Cell we are considering to add is already there...
					continue;
				}
				
				boolean cantAddCellBecauseOfOtherPaperNeighbours = cantAddCellBecauseOfOtherPaperNeighbours(
						cubesToDevelop, cubesUsed, numCellsUsedDepth,
						solutionResolver,
						debugNope, debugIterations,
						cubesOrdering, curOrderedIndexToUse, dirNewCellAdd,
						curOrderedIndexToUse,
						new_i, new_j, new_k
					);
				
				
				if( ! cantAddCellBecauseOfOtherPaperNeighbours) {


					//Setup for adding new cube:
					cubesUsed[new_i][new_j][new_k] = true;
					cubesToDevelop[numCellsUsedDepth] = new Coord3D(new_i, new_j, new_k);
					cubesOrdering[new_i][new_j][new_k] = numCellsUsedDepth;
					//End setup

					numCellsUsedDepth += 1;
					
					retDuplicateSolutions += doDepthFirstSearch(cubesToDevelop, cubesUsed, numCellsUsedDepth,
							solutionResolver,
							debugNope, debugIterations,
							cubesOrdering, curOrderedIndexToUse, dirNewCellAdd
						);
					
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
	
	public static boolean cantAddCellBecauseOfOtherPaperNeighbours(Coord3D cubesToDevelop[], boolean cubesUsed[][][], int numCellsUsedDepth,
			SolutionResolverInterface solutionResolver,
			boolean debugNope, long debugIterations[],
			int cubesOrdering[][][], int minIndexToUse, int minRotationToUse,
			int curOrderedIndexToUse,
			int new_i, int new_j, int new_k) {

		boolean cantAddCellBecauseOfOtherPaperNeighbours = false;
		
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
					cantAddCellBecauseOfOtherPaperNeighbours = true;
					break;
				}
				
			}
		}

		return cantAddCellBecauseOfOtherPaperNeighbours;
	}
	 

	public static void main(String args[]) {
		System.out.println("Polycube counter program:");
		System.out.println("Current UTC timestamp in milliseconds: " + System.currentTimeMillis());
		
		//Confirmed that the 2D version is this:
			//A000105		Number of free polyominoes (or square animals) with n cells.
			//(Formerly M1425 N0561)
		// I originally made it up to 4655.
		//1, 1, 1, 2, 5, 12, 35, 108, 369, 1285, 4655, 17073, 63600, 238591, 901971, 3426576, 13079255,
		solveCuboidIntersections(12);
		
		
		System.out.println("Current UTC timestamp in milliseconds: " + System.currentTimeMillis());
		
	}
	
}
