package NumPolyShapeSolve;


import Coord.Coord3D;
import Utils.Utils;

public class DFSPolyCubeCounter {


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
		

		generateAllTheNudges();
		

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
		
		
		//Once this reaches the value of N, we're done!
		int numCellsUsedDepth = 0;

		int START_INDEX = 0;
		int START_ROTATION = 0;
		
		cubesUsed[START_I][START_J][START_K] = true;
		cubesOrdering[START_I][START_J][START_K] = 0;
		cubesToDevelop[numCellsUsedDepth] = Coord3DSharedMem[START_I][START_J][START_K];
		
		numCellsUsedDepth += 1;
		
		long debugIterations[] = new long[N];
	
		long numSolutions = doDepthFirstSearch(cubesToDevelop, cubesUsed, numCellsUsedDepth,
				false, debugIterations, cubesOrdering, START_INDEX, START_ROTATION);
		
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
			boolean debugNope, long debugIterations[],
			int cubesOrdering[][][], int minIndexToUse, int minRotationToUse) {

		//System.out.println(numIterations);
		
		numIterations++;

		//Display debug/what's-going-on update:
		if(numIterations % 10000L == 0) {
			
			System.out.println("Num iterations: " + numIterations);
			Utils.printCubesSingleDigitFirst10(cubesUsed, cubesToDevelop);
			
			System.out.println("Solutions: " + numSolutionsSoFarDebug);
			System.out.println();
			
			
		}
		//End display debug/what's-going-on update
		debugIterations[numCellsUsedDepth] = numIterations;
		
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

				
				if(cubesUsed[new_i][new_j][new_k]) {
					//Cell we are considering to add is already there...
					continue;
				}
				
				boolean cantAddCellBecauseOfOtherNeighbours = cantAddCellBecauseOfOtherNeighbours(
						cubesToDevelop, cubesUsed, numCellsUsedDepth,
						debugNope, debugIterations,
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
					
					if(isFirstSightOfShape(cubesToDevelop, cubesUsed, numCellsUsedDepth)) {
						
						if(numCellsUsedDepth == cubesToDevelop.length - 1) {
							
							numSolutionsSoFarDebug++;
							retDuplicateSolutions++;
							
						} else {
							retDuplicateSolutions += doDepthFirstSearch(cubesToDevelop, cubesUsed, numCellsUsedDepth,
									debugNope, debugIterations,
									cubesOrdering, curOrderedIndexToUse, dirNewCellAdd
								);
						}
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
			boolean debugNope, long debugIterations[],
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
	
	
	public static boolean cubesUsedInFirstFunction[][][];
	public static Coord3D cubesToDevelopInFirstFunction[];
	
	public static void clearCubesUsedInFirstFunction(Coord3D cubesToDevelop[]) {
		for(int i=0; cubesToDevelop[i] != null && i<cubesToDevelop.length; i++) {
			cubesUsedInFirstFunction[cubesToDevelop[i].a][cubesToDevelop[i].b][cubesToDevelop[i].c] = false;
			cubesToDevelopInFirstFunction[i] = null;
		}
	}
	
	public static boolean isFirstSightOfShape(Coord3D cubesToDevelop[], boolean cubesUsed[][][], int numCellsUsedDepth) {

		//TODO: Don't recalc this every time: (just keep track of it dynamically)
		
		//TODO: You shouldn't use the keyword 'new' outside of the start of the algorithm.
		int arrayStandard[] = new int[numCellsUsedDepth];

		int num = 0;
		
		int minIndexToUse = 0;
		int minRotation = -1;

		cubesToDevelopInFirstFunction[0] = cubesToDevelop[0];
		cubesUsedInFirstFunction[cubesToDevelopInFirstFunction[0].a][cubesToDevelopInFirstFunction[0].b][cubesToDevelopInFirstFunction[0].c] = true;
		
		NEXT_CELL_INSERT:
		for(int j=0; j<numCellsUsedDepth - 1; j++) {
			
			for(int curOrderedIndexToUse=minIndexToUse; cubesToDevelop[curOrderedIndexToUse] != null; curOrderedIndexToUse++) {

				int dirStart = 0;

				if(curOrderedIndexToUse == minIndexToUse) {
					dirStart = minRotation + 1;
				}

				//Try to attach a cube onto a neighbouring cube:
				for(int dirNewCellAdd=dirStart; dirNewCellAdd<NUM_NEIGHBOURS; dirNewCellAdd++) {

					num++;
					
					int new_i = cubesToDevelop[curOrderedIndexToUse].a + nudgeBasedOnRotation[0][dirNewCellAdd];
					int new_j = cubesToDevelop[curOrderedIndexToUse].b + nudgeBasedOnRotation[1][dirNewCellAdd];
					int new_k = cubesToDevelop[curOrderedIndexToUse].c + nudgeBasedOnRotation[2][dirNewCellAdd];
					
					if(cubesUsed[new_i][new_j][new_k] && !cubesUsedInFirstFunction[new_i][new_j][new_k]) {
						arrayStandard[j] = num;
						
						minIndexToUse=curOrderedIndexToUse;
						minRotation=dirNewCellAdd;
						
						cubesUsedInFirstFunction[new_i][new_j][new_k] = true;
						
						//TEST
						cubesToDevelopInFirstFunction[j+1] = Coord3DSharedMem[new_i][new_j][new_k];
						
						/*
						//Sanity
						if(cubesToDevelop[j + 1].a != new_i || cubesToDevelop[j + 1].b != new_j || cubesToDevelop[j + 1].c != new_k) {
							
							System.out.println("j + 1 = " + (j+1));
							Utils.printCubesSingleDigitFirst10(cubesUsedInFirstFunction, cubesToDevelopInFirstFunction);
							System.out.println("vs");
							Utils.printCubesSingleDigitFirst10(cubesUsed, cubesToDevelop);
							System.out.println("DOH!");
							System.exit(1);
						} else {
							//System.out.println("ok");
						}
						*/
						//END TEST
						
						continue NEXT_CELL_INSERT;
					}
				}
			}
		}
		
		
		/*
		//Testing:
		System.out.println("Print order:");
		for(int i=0; i<arrayStandard.length; i++) {
			if(i + 1 < arrayStandard.length) {
				System.out.print(arrayStandard[i] + ", ");
			} else {
				System.out.print(arrayStandard[i]);
			}
		}
		System.out.println();
		
		Utils.printCubes(cubesUsed, cubesToDevelop);
		
		System.out.println();
		sanityComparePrevOrder(arrayStandard);
		
		*/
		
		
		//END TODO Don't recalc this every time
		
		
		for(int i=0; i<numCellsUsedDepth; i++) {

			NEXT_ROTATION:
			for(int r=0; r<NUM_ROTATIONS_3D; r++) {
				
				if(i==0 && r == 0) {
					continue;
				}
				
				clearCubesUsedInFirstFunction(cubesToDevelop);
				

				minIndexToUse = 0;
				minRotation = -1;
				num = 0;

				//TODO: Avoid var declaration within loop...
				Coord3D cur = cubesToDevelop[i];

				cubesToDevelopInFirstFunction[0] = cur;
				cubesUsedInFirstFunction[cur.a][cur.b][cur.c] = true;
				int numCellsInserted = 1;


				NEXT_CELL_INSERT:
				for(int curOrderedIndexToUse=minIndexToUse; cubesToDevelopInFirstFunction[curOrderedIndexToUse] != null; curOrderedIndexToUse++) {

					int dirStart = 0;

					if(curOrderedIndexToUse == minIndexToUse) {
						dirStart = minRotation + 1;
					}

					//Try to attach a cube onto a neighbouring cube:
					for(int dirNewCellAdd=dirStart; dirNewCellAdd<NUM_NEIGHBOURS; dirNewCellAdd++) {

						num++;

						int new_i = cubesToDevelopInFirstFunction[curOrderedIndexToUse].a + nugdeBasedOnRotationAllStartingSymmetries[r][0][dirNewCellAdd];
						int new_j = cubesToDevelopInFirstFunction[curOrderedIndexToUse].b + nugdeBasedOnRotationAllStartingSymmetries[r][1][dirNewCellAdd];
						int new_k = cubesToDevelopInFirstFunction[curOrderedIndexToUse].c + nugdeBasedOnRotationAllStartingSymmetries[r][2][dirNewCellAdd];
						
						if(cubesUsed[new_i][new_j][new_k] && !cubesUsedInFirstFunction[new_i][new_j][new_k]) {
							
							if(num < arrayStandard[numCellsInserted - 1]) {
								
	                            
								//System.out.println("Not first sight!");
								//Utils.printCubesSingleDigitFirst10(cubesUsed, cubesToDevelop);
								//System.out.println("vs");
								//Utils.printCubesSingleDigitFirst10(cubesUsed, cubesToDevelopInFirstFunction);
								//System.exit(1);

								clearCubesUsedInFirstFunction(cubesToDevelop);
								
								return false;

	                        } else if(num > arrayStandard[numCellsInserted - 1]) {
	                        	//System.out.println("Nope!");
	                            continue NEXT_ROTATION;
	                        }
	                        minIndexToUse=curOrderedIndexToUse;
	                        minRotation=dirNewCellAdd;
							cubesUsedInFirstFunction[new_i][new_j][new_k] = true;
							cubesToDevelopInFirstFunction[numCellsInserted] = Coord3DSharedMem[new_i][new_j][new_k];
							numCellsInserted++;
	
							//System.out.println("Next loop!");
							//Utils.printCubesSingleDigitFirst10(cubesUsed, cubesToDevelopInFirstFunction);
							
							curOrderedIndexToUse--;
							continue NEXT_CELL_INSERT;
						}
					}
				}
				
				
			} //END checking every symmetry
		} // END checking every cubes added
		


		//System.out.println("First sight!");
		//Utils.printCubesSingleDigitFirst10(cubesUsed, cubesToDevelop);
		
		clearCubesUsedInFirstFunction(cubesToDevelop);
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
		
		//Confirmed that this program works with the 3D version: (I got it on the 1st try!)
		//A000162		Number of 3-dimensional polyominoes (or polycubes) with n cells.
		//1, 1, 1, 2, 8, 29, 166, 1023, 6922, 48311, 346543, 2522522, 18598427, 138462649, 1039496297, 7859514470, 59795121480
		//(Formerly M1845 N0731)
		//TODO: handle N=0 and N=1 case...
		int N = 12;
		solveCuboidIntersections(N);
		
		//So far, I think I could get f(14) in 10 hours...
		//So, f(16) will probably take 2 months...
		// and f(17) 2 years... Not bad, but I think I can do better!
		
		//N=13 and N=14 started at 12:50 AM
		//N=13 ended at: 2:15:03 (85 minutes)
		//N=14 ended at: 12:32:03 PM (11 hours and 42 minutes) (Final number of unique solutions: 1039496297)
		//N=15 started at Jul 15, 2:40AM and ended at: Jul 19, 1:40 AM (About 4 days)
		// Thankfully, The optimized version of this code is much faster!
		
		//If you want to see faster code that's harder to understand, see the latest in the src/NumPolyShapeSolveOptimized folder.
		System.out.println("Done with N = " + N);
		System.out.println("Current UTC timestamp in milliseconds: " + System.currentTimeMillis());
		
	}

	
	public static int debugPrevOrder[] = new int[0];
	public static void sanityComparePrevOrder(int cur[]) {
		
		for(int i=0; i<Math.min(debugPrevOrder.length, cur.length); i++) {
			if(debugPrevOrder[i] > cur[i]) {
				System.out.println("ERROR: the order is wrong!");
				System.exit(1);
			} else if(debugPrevOrder[i] < cur[i]) {
				break;
			}
		}
		
		debugPrevOrder = cur;
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
		System.out.println("Done printing all the nudges.");
		
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
