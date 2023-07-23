package NumPolyShapeSolveOptimized;


import java.util.ArrayList;

import Coord.Coord3D;
import Utils.Utils;

public class DFSPolyCubeCounterOptimized3 {


	public static boolean debugPrintEvery5Seconds = false;

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
	
	public static void enumerateNumberOfPolycubes(int N) {
		
		initializePrecomputedVars(N);

		//I decided to null terminate the arrays because I'm nostalgic towards my C programming days...
		Coord3D cubesToDevelop[] = new Coord3D[N + 1];
		cubesToDevelopInFirstFunction = new Coord3D[N + 1];

		for(int i=0; i<cubesToDevelop.length; i++) {
			cubesToDevelop[i] = null;
			cubesToDevelopInFirstFunction[i] = null;
		}
		
		int GRID_SIZE = 2*N+1 + 2*BORDER_PADDING;
	
		boolean cubesUsed[][][] = new boolean[GRID_SIZE][GRID_SIZE][GRID_SIZE];
		
		int cubesOrdering[][][] = new int[GRID_SIZE][GRID_SIZE][GRID_SIZE];
		

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
		
		
		//Once this reaches the value of N, we're done!
		int numCellsUsedDepth = 0;

		int START_INDEX = 0;
		int START_ROTATION = 0;
		
		cubesUsed[START_I][START_J][START_K] = true;
		cubesOrdering[START_I][START_J][START_K] = 0;
		cubesToDevelop[numCellsUsedDepth] = Coord3DSharedMem[START_I][START_J][START_K];
		
		numCellsUsedDepth += 1;
		
		long debugIterations[] = new long[N];
		
		int curPathArray[] = new int[N+1];
		int curNum = 0;
	
		long numSolutions = doDepthFirstSearch(cubesToDevelop, cubesUsed, numCellsUsedDepth,
				false, debugIterations, cubesOrdering, START_INDEX, START_ROTATION, curPathArray, curNum);
		
		System.out.println("-------------------");
		System.out.println("-------------------");
		System.out.println("-------------------");
		System.out.println("Current UTC timestamp in milliseconds: " + System.currentTimeMillis());
			
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println("Final number of unique solutions: " + numSolutions);
	}
	
	public static boolean intitalizedPrecomputedVars = false;

	public static void initializePrecomputedVars(int N) {

		if(intitalizedPrecomputedVars == false
				|| N != cubesToDevelopInFirstFunction.length - 1) {
			
			intitalizedPrecomputedVars = true;
			
			generateAllTheNudges();
			
			generateStartRotationsRuleOfThump3D();
			
			cubesToDevelopInFirstFunction = new Coord3D[N + 1];
	
			for(int i=0; i<cubesToDevelopInFirstFunction.length; i++) {
				cubesToDevelopInFirstFunction[i] = null;
			}
			
			int GRID_SIZE = 2*N+1 + 2*BORDER_PADDING;
		
			cubesUsedInFirstFunction = new boolean[GRID_SIZE][GRID_SIZE][GRID_SIZE];
			
			Coord3DSharedMem = new Coord3D[GRID_SIZE][GRID_SIZE][GRID_SIZE];
			
	
			for(int i=0; i<Coord3DSharedMem.length; i++) {
				for(int j=0; j<Coord3DSharedMem[1].length; j++) {
					for(int k=0; k<Coord3DSharedMem[2].length; k++) {
						Coord3DSharedMem[i][j][k] = new Coord3D(i, j, k);
						cubesUsedInFirstFunction[i][j][k] = false;
					}
				}
			}
			
		}
	}
	
	
	public static final int nudgeBasedOnRotation[][] = {{-1, 0,  1,  0,  0,  0},
														{0,  1,  0, -1,  0,  0},
														{0,  0,  0,  0,  1,  -1}};
	public static long numIterations = 0;
	public static long numSolutionsSoFarDebug = 0L;
	
	public static long doDepthFirstSearch(Coord3D cubesToDevelop[], boolean cubesUsed[][][], int numCellsUsedDepth,
			boolean debugNope, long debugIterations[],
			int cubesOrdering[][][], int minIndexToUse, int minRotationToUse,
			int curPathArray[], int curNum) {
		//System.out.println(numIterations);
		
		numIterations++;

		//Display debug/what's-going-on update:
		if((numIterations % 100000L == 0 && debugPrintEvery5Seconds)
				|| numIterations % 10000000L == 0) {
			
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

				curNum++;
				
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

					curPathArray[numCellsUsedDepth - 1] = curNum;
					
					numCellsUsedDepth += 1;

					if(isFirstSightOfShape(cubesToDevelop, cubesUsed, numCellsUsedDepth, curPathArray)) {
						
						if(numCellsUsedDepth == cubesToDevelop.length - 1) {
							
							numSolutionsSoFarDebug++;
							retDuplicateSolutions++;
							
						} else {
							retDuplicateSolutions += doDepthFirstSearch(cubesToDevelop, cubesUsed, numCellsUsedDepth,
									debugNope, debugIterations,
									cubesOrdering, curOrderedIndexToUse, dirNewCellAdd + 1,
									curPathArray, curNum
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
	
	public static boolean isFirstSightOfShape(Coord3D cubesToDevelop[], boolean cubesUsed[][][], int numCellsUsedDepth, int currentPolycubePerformance[]) {
		
		int minIndexToUse = 0;
		int minRotation = -1;
		int num = 0;
		
		int indexRootNeighbours = getNeighbourIndex(cubesToDevelop[0], cubesUsed);
		
		for(int i=0; i<numCellsUsedDepth; i++) {
			
			int listOfRotations[] = defaultListRotations;
			
			if(numCellsUsedDepth >= NUM_NEIGHBOURS_3D + 1) {
				int indexNodeNeighbours = getNeighbourIndex(cubesToDevelop[i], cubesUsed);
				
				listOfRotations = startRotationsToConsiderFor3D[indexRootNeighbours][indexNodeNeighbours];
				
				if(listOfRotations == null) {
					return false;
				}
				
				
			}

			NEXT_ROTATION:
			for(int ri=0; ri<listOfRotations.length; ri++) {
				
				int r = listOfRotations[ri];
				
				if(i==0 && r == 0) {
					continue;
				}
				
				
				minIndexToUse = 0;
				minRotation = -1;
				num = 0;

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
							
							if(num < currentPolycubePerformance[numCellsInserted - 1]) {
								
	                            
								//System.out.println("Not first sight!");
								//Utils.printCubesSingleDigitFirst10(cubesUsed, cubesToDevelop);
								//System.out.println("vs");
								//Utils.printCubesSingleDigitFirst10(cubesUsed, cubesToDevelopInFirstFunction);
								//System.exit(1);

								clearCubesUsedInFirstFunction(cubesToDevelopInFirstFunction);
								
								return false;

	                        } else if(num > currentPolycubePerformance[numCellsInserted - 1]) {
	                        	//System.out.println("Nope!");
	                        	clearCubesUsedInFirstFunction(cubesToDevelopInFirstFunction);
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
				
				clearCubesUsedInFirstFunction(cubesToDevelopInFirstFunction);
				
			} //END checking every symmetry
		} // END checking every cubes added
		


		//System.out.println("First sight!");
		//Utils.printCubesSingleDigitFirst10(cubesUsed, cubesToDevelop);
		
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

		//For debug:
		//testPrintAllTheNudges();
		
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
	

	//First index: 6 neighbours around root desc
	//Second index: 6 neighbours around node desc
	// This is only guaranteed to work when the number of polycubes is 7+ (or number of polycubes is greater than 2*d where d is the number of dimensions)
	// The real check is to see if the root node is still able to add more neighbours to itself, but that's a slightly more complicated check
	// that we don't need.
	public static int defaultListRotations[];
	public static int startRotationsToConsiderFor3D[][][];

	public static void generateStartRotationsRuleOfThump3D() {
		
		//Default list for when the polycube isn't big enough:
		defaultListRotations = new int[NUM_ROTATIONS_3D];
		for(int i=0; i<NUM_ROTATIONS_3D; i++) {
			defaultListRotations[i] = i;
		}
		//End default list
		ArrayList<Integer> validRotations;
		
		int numNeighbourConfigs = (int)Math.pow(2, NUM_NEIGHBOURS_3D);
		
		startRotationsToConsiderFor3D = new int[numNeighbourConfigs][numNeighbourConfigs][];
		
		int LENGTH_SIDES_FOR_EXERCISE = 3;
		int START_INDEX_EACH_DIM = LENGTH_SIDES_FOR_EXERCISE / 2;
		
		for(int i=0; i<numNeighbourConfigs; i++) {
			
			validRotations = new ArrayList<Integer>();
			
			NEXT_ARRAY_ELEMENT:
			for(int j=0; j<numNeighbourConfigs; j++) {
				
				//Coord3D cubesToDevelop1[] = new Coord3D[NUM_NEIGHBOURS_3D + 2];
				//Coord3D cubesToDevelop3[];
				boolean cubesUsed1[][][] = new boolean[LENGTH_SIDES_FOR_EXERCISE][LENGTH_SIDES_FOR_EXERCISE][LENGTH_SIDES_FOR_EXERCISE];
				boolean cubesUsed2[][][] = new boolean[LENGTH_SIDES_FOR_EXERCISE][LENGTH_SIDES_FOR_EXERCISE][LENGTH_SIDES_FOR_EXERCISE];
				
				for(int i2=0; i2<cubesUsed1.length; i2++) {
					for(int j2=0; j2<cubesUsed1[i2].length; j2++) {
						for(int k2=0; k2<cubesUsed1[j2].length; k2++) {
							cubesUsed1[i2][j2][k2] = false;
							cubesUsed2[i2][j2][k2] = false;
						}
					}
				}
				
				
				cubesUsed1[START_INDEX_EACH_DIM][START_INDEX_EACH_DIM][START_INDEX_EACH_DIM] = true;
				cubesUsed2[START_INDEX_EACH_DIM][START_INDEX_EACH_DIM][START_INDEX_EACH_DIM] = true;
				
				cubesUsed1 = setupNeighboursBasedOnNeighbourConfigIndex(cubesUsed1, i, START_INDEX_EACH_DIM);
				cubesUsed2 = setupNeighboursBasedOnNeighbourConfigIndex(cubesUsed2, j, START_INDEX_EACH_DIM);
				
				for(int r=0; r<NUM_ROTATIONS_3D; r++) {
					
					int indexForRotation = 0;
					
					for(int dirNewCellAdd=0; dirNewCellAdd<NUM_NEIGHBOURS_3D; dirNewCellAdd++) {

						
						int new_i = START_INDEX_EACH_DIM + nugdeBasedOnRotationAllStartingSymmetries[r][0][dirNewCellAdd];
						int new_j = START_INDEX_EACH_DIM + nugdeBasedOnRotationAllStartingSymmetries[r][1][dirNewCellAdd];
						int new_k = START_INDEX_EACH_DIM + nugdeBasedOnRotationAllStartingSymmetries[r][2][dirNewCellAdd];
						
						
						if(cubesUsed2[new_i][new_j][new_k]) {
							
							indexForRotation += (int)Math.pow(2, NUM_NEIGHBOURS_3D-1-dirNewCellAdd);
						}
						
					}
					
					if(indexForRotation > i) {
						
						//System.out.println(i +" vs " + j + " no good because of rotation: " + r);
						// Note that the logic changes if the 2nd cell is developed vs not developed.
						// I have a hacky work-around for this...
						startRotationsToConsiderFor3D[i][j] = null;
						continue NEXT_ARRAY_ELEMENT;
					} else if(indexForRotation < i) {
						//Not a contender
						//Keep going
					} else {
						//System.out.println("Add possible contender: " + i +", " + j + ": " + r);
						validRotations.add(r);
					}
				}
				
				//Put it in the array:
				
				startRotationsToConsiderFor3D[i][j] = toIntArray(validRotations);
				
			}
		}
		
	}
	
	public static int[] toIntArray(ArrayList<Integer> arrayListInts) {
		int ret[] = new int[arrayListInts.size()];
		
		for(int i=0; i<arrayListInts.size(); i++) {
			ret[i] = arrayListInts.get(i).intValue();
		}
		
		return ret;
	}
	
	public static int getNeighbourIndex(Coord3D point, boolean cubesUsed[][][]) {
		
		
		return  32 * (cubesUsed[point.a + nudgeBasedOnRotation[0][0]][point.b + nudgeBasedOnRotation[1][0]][point.c + nudgeBasedOnRotation[2][0]] ? 1 : 0)
			  + 16 * (cubesUsed[point.a + nudgeBasedOnRotation[0][1]][point.b + nudgeBasedOnRotation[1][1]][point.c + nudgeBasedOnRotation[2][1]] ? 1 : 0)
			  +  8 * (cubesUsed[point.a + nudgeBasedOnRotation[0][2]][point.b + nudgeBasedOnRotation[1][2]][point.c + nudgeBasedOnRotation[2][2]] ? 1 : 0)
			  +  4 * (cubesUsed[point.a + nudgeBasedOnRotation[0][3]][point.b + nudgeBasedOnRotation[1][3]][point.c + nudgeBasedOnRotation[2][3]] ? 1 : 0)
			  +  2 * (cubesUsed[point.a + nudgeBasedOnRotation[0][4]][point.b + nudgeBasedOnRotation[1][4]][point.c + nudgeBasedOnRotation[2][4]] ? 1 : 0)
			  +  1 * (cubesUsed[point.a + nudgeBasedOnRotation[0][5]][point.b + nudgeBasedOnRotation[1][5]][point.c + nudgeBasedOnRotation[2][5]] ? 1 : 0);
		
	}
	
	public static boolean[][][] setupNeighboursBasedOnNeighbourConfigIndex(boolean cubesUsed[][][], int index, int startIndexEachDim) {
	
		//int 
		//public static final int nudgeBasedOnRotation[][] = {{-1, 0,  1,  0,  0,  0},
		//		{0,  1,  0, -1,  0,  0},
		//		{0,  0,  0,  0,  1,  -1}};
		
		for(int i=0; i<nudgeBasedOnRotation[0].length; i++) {
			if( (index & (1 << (nudgeBasedOnRotation[0].length - 1 - i))) != 0) {
				
				cubesUsed[startIndexEachDim + nudgeBasedOnRotation[0][i]]
						 [startIndexEachDim + nudgeBasedOnRotation[1][i]]
						 [startIndexEachDim + nudgeBasedOnRotation[2][i]] = true;
			}
		}
		return cubesUsed;
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
		int N = 9;
		debugPrintEvery5Seconds = true;
		
		enumerateNumberOfPolycubes(N);
		
		//So far, I think I could get f(14) in 10 hours...
		//So, f(16) will probably take 2 months...
		// and f(17) 2 years... Not bad, but I think I can do better!
		
		//N=13 and N=14 started at 12:50 AM
		//N=13 ended at: 2:15:03 (85 minutes)
		//N=14 ended at: 12:32:03 PM (11 hours and 42 minutes) (Final number of unique solutions: 1039496297)
		
		//Update Optimized2 is over twice as fast!
		// N=12 took less than 3.5 minutes (It took over 10 minutes before)
		// N=13 started at 3:41:54 AM and ended at 4:09:49 AM (It took about 28 minutes vs the 85 minutes it took when I ran unoptimized)
		// It also got the right answer: "Final number of unique solutions: 138462649"
		
		//Update for Optimized3:
		// N=13: start: 3:58:50 PM end: 4:31:32 PM (It took less than 33 minutes) Worse than Optimized2?
		
		
		/* Start: July 17th 2023 11:35:48 AM EST
		 * Output:
		 * Final number of unique solutions: 59795121480
Done with N = 16
Current UTC timestamp in milliseconds: 1689973619436

END: 	Fri Jul 21 2023 17:06:59 GMT-0400 (Eastern Daylight Time)

4 days and 6 hours for the correct answer to N=16. N=17 is next!
*/
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
