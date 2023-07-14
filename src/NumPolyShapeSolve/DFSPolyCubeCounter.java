package NumPolyShapeSolve;


import Coord.Coord3D;
import SolutionResolver.SolutionResolverInterface;
import SolutionResolver.StandardResolverForBig2DSolutions;
import SolutionResolver.StandardResolverForSmall2DSolutions;
import Utils.Utils;

public class DFSPolyCubeCounter {


	public static final int NUM_ROTATIONS_3D = 24;
	
	public static final int NUM_NEIGHBOURS_3D= 6;
	public static final int NUM_ROTATIONS_2D_CHEAT= 4;
	public static final int NUM_ROTATIONS_2D = 4;

	
	//TODO: switch it to 3D later:
	public static final int NUM_ROTATIONS = NUM_ROTATIONS_2D_CHEAT;
	
	
	public static final int NUM_NEIGHBOURS = NUM_ROTATIONS_2D_CHEAT;
	
	public static final int BORDER_PADDING = 2;
	
	public static final int NOT_INSERTED = -1;
	
	public static Coord3D Coord3DSharedMem[][][];
	
	public static void solveCuboidIntersections(int N) {
		

		generateAllTheNudges();
		
		SolutionResolverInterface solutionResolver = null;
		
		if(N > 6 ) {
			solutionResolver = new StandardResolverForSmall2DSolutions();
		} else {
			solutionResolver = new StandardResolverForBig2DSolutions();
		}

		//I decided to null terminate the arrays because I'm nostalgic towards my C programming days...
		Coord3D cubesToDevelop[] = new Coord3D[N + 1];
		cubesToDevelopInFirstFunction = new Coord3D[N + 1];

		for(int i=0; i<cubesToDevelop.length; i++) {
			cubesToDevelop[i] = null;
			cubesToDevelopInFirstFunction[i] = null;
		}
		
		int GRID_SIZE = 2*N+1 + 2*BORDER_PADDING;
	
		boolean cubesUsed[][][] = new boolean[GRID_SIZE][GRID_SIZE][GRID_SIZE];
		cubesUsedInFirstFunction = new boolean[GRID_SIZE][GRID_SIZE][GRID_SIZE];
		
		int cubesOrdering[][][] = new int[GRID_SIZE][GRID_SIZE][GRID_SIZE];
		Coord3DSharedMem = new Coord3D[GRID_SIZE][GRID_SIZE][GRID_SIZE];
		

		for(int i=0; i<cubesUsed.length; i++) {
			for(int j=0; j<cubesUsed[1].length; j++) {
				for(int k=0; k<cubesUsed[2].length; k++) {
					cubesUsed[i][j][k] = false;
					cubesOrdering[i][j][k] = NOT_INSERTED;
					Coord3DSharedMem[i][j][k] = new Coord3D(i, j, k);
					cubesUsedInFirstFunction[i][j][k] = false;
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
		cubesToDevelop[numCellsUsedDepth] = Coord3DSharedMem[START_I][START_J][START_K];
		
		numCellsUsedDepth += 1;
		
		long debugIterations[] = new long[N];
	
		doDepthFirstSearch(cubesToDevelop, cubesUsed, numCellsUsedDepth, solutionResolver,
				false, debugIterations, cubesOrdering, START_INDEX, START_ROTATION);
		
		
		System.out.println("Current UTC timestamp in milliseconds: " + System.currentTimeMillis());
			
		
		System.out.println("Final number of unique solutions: " + solutionResolver.getNumUniqueFound());
	}
	
	
	public static final int nudgeBasedOnRotation[][] = {{-1, 0,  1,  0,  0,  0},
														{0,  1,  0, -1,  0,  0},
														{0,  0,  0,  0,  1,  -1}};
	public static long numIterations = 0;
	
	public static long doDepthFirstSearch(Coord3D cubesToDevelop[], boolean cubesUsed[][][], int numCellsUsedDepth,
			SolutionResolverInterface solutionResolver,
			boolean debugNope, long debugIterations[],
			int cubesOrdering[][][], int minIndexToUse, int minRotationToUse) {

		if(numCellsUsedDepth == cubesToDevelop.length - 1) {

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
		for(int curOrderedIndexToUse=minIndexToUse; curOrderedIndexToUse<numCellsUsedDepth && cubesToDevelop[curOrderedIndexToUse] != null; curOrderedIndexToUse++) {
			

			//Try to attach a cell onto indexToUse using all 4 rotations:
			for(int dirNewCellAdd=0; dirNewCellAdd<NUM_ROTATIONS; dirNewCellAdd++) {
				
				if(curOrderedIndexToUse == minIndexToUse
						&& dirNewCellAdd <  minRotationToUse) {
					continue;
				}

				
				int new_i = cubesToDevelop[curOrderedIndexToUse].a + nudgeBasedOnRotation[0][dirNewCellAdd];
				int new_j = cubesToDevelop[curOrderedIndexToUse].b + nudgeBasedOnRotation[1][dirNewCellAdd];
				int new_k = cubesToDevelop[curOrderedIndexToUse].c + nudgeBasedOnRotation[2][dirNewCellAdd];

				
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
					cubesToDevelop[numCellsUsedDepth] = Coord3DSharedMem[new_i][new_j][new_k];
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
	
	
	//TODO: use these:
	public static boolean cubesUsedInFirstFunction[][][];
	public static Coord3D cubesToDevelopInFirstFunction[];
	
	public static void clearCubesUsedInFirstFunction(Coord3D cubesToDevelop[]) {
		for(int i=0; cubesToDevelop[i] != null && i<cubesToDevelop.length; i++) {
			cubesUsedInFirstFunction[cubesToDevelop[i].a][cubesToDevelop[i].b][cubesToDevelop[i].c] = false;
		}
	}
	
	//TODO: put in it's own Class
	//TOOD: reread and test
	//TODO: it's wrong!
	public static boolean isFirstSightOfShape(Coord3D cubesToDevelop[], boolean cubesUsed[][][], int numCellsUsedDepth) {


		//TODO: Don't recalc this every time: (just keep track of it dynamically)
		Coord3D cur;
		
		//TODO: You shouldn't use the keyword 'new' outside of the start of the algorithm.
		int arrayStandard[] = new int[numCellsUsedDepth - 1];

		int num = 0;

		for(int j=0; j<numCellsUsedDepth - 1; j++) {
			cur = cubesToDevelop[0];
			
			int minIndexToUse = 0;
			int minRotation = 0;

			NEXT_CELL_INSERT:
			for(int curOrderedIndexToUse=minIndexToUse; cubesToDevelop[curOrderedIndexToUse] != null; curOrderedIndexToUse++) {

				int dirStart = 0;

				if(curOrderedIndexToUse == minIndexToUse) {
					dirStart = minRotation + 1;
				}

				//Try to attach a cell onto indexToUse using all 4 rotations:
				for(int dirNewCellAdd=dirStart; dirNewCellAdd<NUM_ROTATIONS; dirNewCellAdd++) {
					
					
					int new_i = cubesToDevelop[curOrderedIndexToUse].a + nudgeBasedOnRotation[0][dirNewCellAdd];
					int new_j = cubesToDevelop[curOrderedIndexToUse].b + nudgeBasedOnRotation[1][dirNewCellAdd];
					int new_k = cubesToDevelop[curOrderedIndexToUse].c + nudgeBasedOnRotation[2][dirNewCellAdd];
					
					if(cubesUsed[new_i][new_j][new_k]) {
						arrayStandard[j] = num;
						
						minIndexToUse=curOrderedIndexToUse;
						minRotation=dirNewCellAdd;
						
						continue NEXT_CELL_INSERT;
					}
					num++;
				}
			}
		}
		
		//END TODO Don't recalc this every time
		
		for(int i=0; i<numCellsUsedDepth; i++) {

			NEXT_ROTATION:
			for(int r=0; r<NUM_ROTATIONS_2D_CHEAT; r++) {
				
				int startJ = 0;
				if(i==0 && r == 0) {
					continue;
				}
				cur = cubesToDevelop[i];
				int minIndexToUse = 0;
				int minRotation = 0;
				num = 0;

				
				for(int j=startJ; j<numCellsUsedDepth-1; j++) {
					
					NEXT_CELL_INSERT:
					for(int curOrderedIndexToUse=minIndexToUse; cubesToDevelop[curOrderedIndexToUse] != null; curOrderedIndexToUse++) {

						int dirStart = 0;

						if(curOrderedIndexToUse == minIndexToUse) {
							dirStart = minRotation + 1;
						}
						
						//Try to attach a cell onto indexToUse using all 24 symmetries:
						for(int dirNewCellAdd=dirStart; dirNewCellAdd<NUM_ROTATIONS_3D; dirNewCellAdd++) {
							
							
							//TODO: ahh! you need a new version of cubesToDevelop! This is all wrong!
							//TODO: make it able to switch from 2D to 3D without needing to change the variable:
							int new_i = cubesToDevelop[curOrderedIndexToUse].a + nugdeBasedOnRotationAllStartingSymmetries[r][0][dirNewCellAdd];
							int new_j = cubesToDevelop[curOrderedIndexToUse].b + nugdeBasedOnRotationAllStartingSymmetries[r][1][dirNewCellAdd];
							int new_k = cubesToDevelop[curOrderedIndexToUse].c + nugdeBasedOnRotationAllStartingSymmetries[r][2][dirNewCellAdd];
							
							if(cubesUsed[new_i][new_j][new_k]) {
								
								if(num < arrayStandard[j]) {
									return false;
								} else if(num > arrayStandard[j]) {
									continue NEXT_ROTATION;
								}
								
								minIndexToUse=curOrderedIndexToUse;
								minRotation=dirNewCellAdd;
								
								continue NEXT_CELL_INSERT;
							}
							num++;
						}
					}
					
				}
			}
		}
		
		
		return true;
	}
	
	public static int nugdeBasedOnRotationAllStartingSymmetries[][][];
	public static int NUM_DIMS_3D = 3;
	
	public static void generateAllTheNudges() {
		//For now, I'm doing 3D rotations but no 3D reflections.
		/* I'm basing all 24 rotations off of the original neighbourNudge vectors:
		 * public static final int nugdeBasedOnRotation[][] = {{-1, 0,  1,  0,  0,  0},
														        {0,  1,  0, -1,  0,  0},
														        {0,  0,  0,  0,  1,  -1}};
		 */
		
		nugdeBasedOnRotationAllStartingSymmetries = new int[NUM_ROTATIONS_3D][NUM_DIMS_3D][NUM_NEIGHBOURS_3D];
		
		for(int i=0; i<NUM_NEIGHBOURS_3D; i++) {
			for(int j=0; j<NUM_ROTATIONS_2D; j++) {
				
				int arrayToFill[][] = nugdeBasedOnRotationAllStartingSymmetries[NUM_ROTATIONS_2D * i + j];
				
				//Step 1:
				//1st column is the 1st column of nugdeBasedOnRotation for the first 6 indexes,
				// the 2nd column of nugdeBasedOnRotation for the next 6 indexes, and so on...
				for(int k=0; k<NUM_DIMS_3D; k++) {
					arrayToFill[k][0] = nudgeBasedOnRotation[k][i];
				}
				
				//Step 2:
				// 2nd column is jth next 90degree rotation from 1st column:
				int firstColumn[] = getjthColumnVector(arrayToFill, 0);

				int num90degreeVectorFoundBeforeCurrent = 0;
				for(int m=i+1; true; m++) {
					
					int otherVectorToUse[] = getjthColumnVector(nudgeBasedOnRotation, m % NUM_NEIGHBOURS_3D);
					
					if(! isZeroVector(crossProd3D(firstColumn, otherVectorToUse))) {
						
						if(num90degreeVectorFoundBeforeCurrent == j) {
							
							for(int k=0; k<NUM_DIMS_3D; k++) {
								arrayToFill[k][1] = otherVectorToUse[k];
							}
							
							break;
						}
						
						num90degreeVectorFoundBeforeCurrent++;
					}
				}
				
				
				//Step 3:
				// 3rd column is -(1st column)
				
				for(int k=0; k<NUM_DIMS_3D; k++) {
					arrayToFill[k][2] = 0 - arrayToFill[k][0];
				}
				
				//Step 4:
				// 4th column is -(2nd column)
				for(int k=0; k<NUM_DIMS_3D; k++) {
					arrayToFill[k][3] = 0 - arrayToFill[k][1];
				}
				
				//Step 5:
				// 5th column is (1st column) x (2nd column) (cross-product)
				// It turns out that I messed up and it's the negative of the cross-product..
				int secondColumn[] = getjthColumnVector(arrayToFill, 1);
				
				int product[] = crossProd3D(firstColumn, secondColumn);
				for(int k=0; k<NUM_DIMS_3D; k++) {
					arrayToFill[k][4] = 0 - product[k];
				}

				//Step 6:
				// 6th column is -(5th column) (cross-product)
				for(int k=0; k<NUM_DIMS_3D; k++) {
					arrayToFill[k][5] = 0 - nugdeBasedOnRotationAllStartingSymmetries[NUM_ROTATIONS_2D * i + j][k][4];
				}
			}
		}
		
		testPrintAllTheNudges();
		
		//Sanity test:
		for(int i=0; i<NUM_DIMS_3D; i++) {
			for(int j=0; j<NUM_NEIGHBOURS_3D; j++) {
				if(nugdeBasedOnRotationAllStartingSymmetries[0][i][j] != nudgeBasedOnRotation[i][j]) {
					System.out.println("ERROR: nugdeBasedOnRotationAllStartingRotations didn't get the 1st symmetry (the null one) right");
					System.exit(1);
				}
			}
		}
		
	}
	
	public static boolean isZeroVector(int a[]) {
		
		for(int i=0; i<a.length; i++) {
			if(a[i] != 0) {
				return false;
			}
		}
		
		return true;
	}
	
	public static int[] getjthColumnVector(int array[][], int j) {
		int ret[] = new int[array.length];
		
		for(int i=0; i<ret.length; i++) {
			ret[i] = array[i][j];
		}
		
		return ret;
	}
	
	public static int[] crossProd3D(int a[], int b[]) {
		
		int c[] = new int[NUM_DIMS_3D];
		
		
		
		for(int k=0; k<NUM_DIMS_3D; k++) {

			int elements[] = new int[4];
			int curIndex = 0;
			
			for(int i=0; i<NUM_DIMS_3D; i++) {
				if(i == k) {
					continue;
				}
				for(int j=0; j<2; j++) {
					if(j == 0) {
						elements[curIndex] = a[i];
					} else {
						elements[curIndex] = b[i];
					}
					curIndex++;
				}
			}

			c[k] = elements[0] * elements[3] - elements[1] * elements[2];
			
			if(k == 1) {
				c[k] *= -1; 
			}
		}
		
		return c;
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
	

	public static void testPrintAllTheNudges() {

		System.out.println("Printing all the nudges:");
		
		for(int i=0; i<NUM_ROTATIONS_3D; i++) {
			
			for(int j=0; j<NUM_DIMS_3D; j++) {
				for(int k=0; k<NUM_NEIGHBOURS_3D; k++) {
					if(nugdeBasedOnRotationAllStartingSymmetries[i][j][k] != -1) {
						System.out.print(" ");
					}
					System.out.print(nugdeBasedOnRotationAllStartingSymmetries[i][j][k] + " ");
				}
				System.out.println();
			}
			System.out.println();
		}
		
	}
	public static void testPrintVector(int a[]) {
		for(int i=0; i<a.length; i++) {
			System.out.println(a[i]);
		}
	}
	
	public static void testVectorProd() {
		int a[] = {1, 20, 3};
		int b[] = {1, 5, 7};
		
		int c[] = crossProd3D(a, b);
		testPrintVector(c);
	}

}
