package MultiplePiecesHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import OneNet3Cuboids.CuboidToFoldOn;
import OneNet3Cuboids.Utils;
import OneNet3Cuboids.Coord.Coord2D;
import OneNet3Cuboids.Coord.CoordWithRotationAndIndex;
import OneNet3Cuboids.Cuboid.SymmetryResolver.SymmetryResolver;
import OneNet3Cuboids.FancyTricks.ThreeBombHandler;
import OneNet3Cuboids.FoldingAlgoStartAnywhere.FoldResolveOrderedRegionsSkipSymmetries;
import OneNet3Cuboids.GraphUtils.PivotCellDescription;
import OneNet3Cuboids.Region.Region;
import OneNet3Cuboids.SolutionResovler.*;

//This needs to be worked on...

public class DFSPolyCubeComputeTaskGetter {


	public static long checkpointCounter = 0;
	public static long curNumPiecesCreated = 0;
	
	public static final int NUM_ROTATIONS = 4;
	public static final int NUM_NEIGHBOURS = NUM_ROTATIONS;
	
	
	
	public static void getComputeTask(CuboidToFoldOn cuboidToBuild, CuboidToFoldOn cuboidToBringAlong, boolean skipSymmetries,
			int maxDepth, int targetTaskIndex) {
		
		curNumPiecesCreated = 0;
		checkpointCounter = 0L;
		
		if(skipDirections == null) {
			initSkipDirectionsHashTable();
		}
		
		SolutionResolverIntersectInterface solutionResolver = null;
		

		if(maxDepth <= 0 || Utils.getTotalArea(cuboidToBuild.getDimensions())  <= maxDepth) {
			System.out.println("ERROR: invalid start depth of " + maxDepth);
			System.exit(1);
		}
		
		if(Utils.getTotalArea(cuboidToBuild.getDimensions()) != Utils.getTotalArea(cuboidToBringAlong.getDimensions())) {
			System.out.println("ERROR: The two cuboid to intersect don't have the same area.");
			System.exit(1);
		}
		
		// Set the solution resolver to different things depending on the size of the cuboid:
		solutionResolver = new StandardResolverForSmallIntersectSolution(cuboidToBuild);
		
		getComputeTaskInner(cuboidToBuild, cuboidToBringAlong, skipSymmetries, solutionResolver, maxDepth, targetTaskIndex);
	}

	public static void getComputeTaskInner(CuboidToFoldOn cuboidToBuild, CuboidToFoldOn cuboidToBringAlong, boolean skipSymmetries, SolutionResolverIntersectInterface solutionResolver,
			int maxDepth, int targetTaskIndex) {
		
		
		//cube.set start location 0 and rotation 0
		

		//TODO: LATER use hashes to help.. (record potential expansions, and no-nos...)
		Coord2D paperToDevelop[] = new Coord2D[Utils.getTotalArea(cuboidToBuild.getDimensions())];
		for(int i=0; i<paperToDevelop.length; i++) {
			paperToDevelop[i] = null;
		}
		
		int GRID_SIZE = 2*Utils.getTotalArea(cuboidToBuild.getDimensions());
	
		boolean paperUsed[][] = new boolean[GRID_SIZE][GRID_SIZE];
		int indexCuboidOnPaper[][] = new int[GRID_SIZE][GRID_SIZE];

		int indexCuboidOnPaper2ndCuboid[][] = new int[GRID_SIZE][GRID_SIZE];
		
		for(int i=0; i<paperUsed.length; i++) {
			for(int j=0; j<paperUsed[0].length; j++) {
				paperUsed[i][j] = false;
				indexCuboidOnPaper[i][j] = -1;
				indexCuboidOnPaper2ndCuboid[i][j] = -1;
			}
		}

		//Default start location GRID_SIZE / 2, GRID_SIZE / 2
		int START_I = GRID_SIZE/2;
		int START_J = GRID_SIZE/2;
		
		CuboidToFoldOn cuboid = new CuboidToFoldOn(cuboidToBuild);
		//Insert start cell:
		
		//Once this reaches the total area, we're done!
		int numCellsUsedDepth = 0;

		int START_INDEX = 0;
		int START_ROTATION = 0;
		paperUsed[START_I][START_J] = true;
		paperToDevelop[numCellsUsedDepth] = new Coord2D(START_I, START_J);
		
		cuboid.setCell(START_INDEX, START_ROTATION);
		indexCuboidOnPaper[START_I][START_J] = START_INDEX;
		numCellsUsedDepth += 1;
		
		Region regionsToHandleRevOrder[] = new Region[1];
		regionsToHandleRevOrder[0] = new Region(cuboid);
		
		ThreeBombHandler threeBombHandler = new ThreeBombHandler(cuboid);
		

		ArrayList<PivotCellDescription> startingPointsAndRotationsToCheck = PivotCellDescription.getUniqueRotationListsWithCellInfo(cuboidToBringAlong);
		
		System.out.println("Num starting points and rotations to check: " + startingPointsAndRotationsToCheck.size());
		
		for(int i=0; i<startingPointsAndRotationsToCheck.size() && (curNumPiecesCreated <= targetTaskIndex || targetTaskIndex < 0); i++) {
			
			int startIndex2ndCuboid =startingPointsAndRotationsToCheck.get(i).getCellIndex();
			int startRotation2ndCuboid = startingPointsAndRotationsToCheck.get(i).getRotationRelativeToCuboidMap();
			
			CuboidToFoldOn cuboidToBringAlongStartRot = new CuboidToFoldOn(cuboidToBringAlong);

			cuboidToBringAlongStartRot.setCell(startIndex2ndCuboid, startRotation2ndCuboid);
			indexCuboidOnPaper2ndCuboid[START_I][START_J] = startIndex2ndCuboid;
			
			int topBottombridgeUsedNx1x1[] = new int[Utils.getTotalArea(cuboidToBuild.getDimensions())];
			
			long debugIterations[] = new long[Utils.getTotalArea(cuboidToBuild.getDimensions())]; 
		
			getComputeTaskForStartingPointAndRotation(paperToDevelop, indexCuboidOnPaper, paperUsed, cuboid, numCellsUsedDepth, regionsToHandleRevOrder, -1L, skipSymmetries, solutionResolver, cuboidToBringAlongStartRot, indexCuboidOnPaper2ndCuboid, topBottombridgeUsedNx1x1, threeBombHandler, false, debugIterations, maxDepth, targetTaskIndex);
			

		}
		
		
		
	}
	
	public static void setSkipToHashMap(int numCellsUsedDepth, long prevCheckpointCounter, long prevNumPiecesCreated) {

		if(numCellsUsedDepth < DEPTH_TO_BOTHER_INDEXING && prevCheckpointCounter >= 0) {
			
			String key = prevCheckpointCounter + ", " + prevNumPiecesCreated;
			
			//System.out.println("Set Skip To hash Map!");
			//System.out.println("curNumPiecesCreated: " + curNumPiecesCreated);
			
			if(skipDirections[numCellsUsedDepth].containsKey(key)) {
				System.out.println("ERROR: Huh? This shouldn't happen?");
				System.out.println("AAH!");
				System.exit(0);
			}
			
			skipDirections[numCellsUsedDepth].put(key, checkpointCounter + "," + curNumPiecesCreated);
			
		}
	}
	
	public static int DEPTH_TO_BOTHER_INDEXING = 11;
	public static HashMap<String, String> skipDirections[] = null;
	public static int numInserts = 0;
	public static int curNumNoProgress[] = new int[DEPTH_TO_BOTHER_INDEXING];
	
	public static void initSkipDirectionsHashTable() {
		skipDirections = new HashMap[DEPTH_TO_BOTHER_INDEXING];
		
		for(int i=0; i<skipDirections.length; i++) {
			skipDirections[i] = new HashMap<String, String>();
		}
	}
	
	public static final int nugdeBasedOnRotation[][] = {{-1, 0, 1, 0}, {0, 1, 0 , -1}};
	
	public static void getComputeTaskForStartingPointAndRotation(Coord2D paperToDevelop[], int indexCuboidonPaper[][], boolean paperUsed[][], CuboidToFoldOn cuboid, int numCellsUsedDepth,
			Region regions[], long limitDupSolutions, boolean skipSymmetries, SolutionResolverIntersectInterface solutionResolver, CuboidToFoldOn cuboidToBringAlongStartRot, int indexCuboidOnPaper2ndCuboid[][],
			int topBottombridgeUsedNx1x1[],
			ThreeBombHandler threeBombHandler,
			boolean debugNope, long debugIterations[],
			int maxDepth, int targetTaskIndex
			) {

		
		if(cuboid.getNumCellsFilledUp() == maxDepth) {

			/*System.out.println();
			System.out.println("PIECE FOUND:");
			System.out.println("piece index: " + curNumPiecesCreated);
			Utils.printFold(paperUsed);
			Utils.printFoldWithIndex(indexCuboidonPaper);
			Utils.printFoldWithIndex(indexCuboidOnPaper2ndCuboid);
			
			System.out.println("Last cell inserted: " + indexCuboidonPaper[paperToDevelop[numCellsUsedDepth - 1].i][paperToDevelop[numCellsUsedDepth - 1].j]);
			*/
			if( targetTaskIndex == curNumPiecesCreated) {
				
				ComputeTaskMain.computeTask = new ComputeTaskDescription(paperToDevelop, indexCuboidonPaper, paperUsed, cuboid, numCellsUsedDepth,
						regions, limitDupSolutions, skipSymmetries, solutionResolver, cuboidToBringAlongStartRot, indexCuboidOnPaper2ndCuboid,
						topBottombridgeUsedNx1x1,
						threeBombHandler,
						debugNope, debugIterations);
				
				
				System.out.println();
				System.out.println("New task description:");
				System.out.println("Target task index: " + targetTaskIndex);
				System.out.println("start depth: " + numCellsUsedDepth);
				Utils.printFold(paperUsed);
				Utils.printFoldWithIndex(indexCuboidonPaper);
				Utils.printFoldWithIndex(indexCuboidOnPaper2ndCuboid);
				System.out.println();
				
			}
			
			curNumPiecesCreated++;
			return;
		}
		
		long prevCheckpointCounter = -1;
		long prevNumPiecesCreated = -1;
		
		//Logic for skipping ahead on the second pass:
		if(numCellsUsedDepth < DEPTH_TO_BOTHER_INDEXING) {
			
			prevCheckpointCounter = checkpointCounter;
			prevNumPiecesCreated = curNumPiecesCreated;
			
			if(skipDirections[numCellsUsedDepth].containsKey(checkpointCounter + ", " + curNumPiecesCreated)) {

				String tokens[] = skipDirections[numCellsUsedDepth].get(checkpointCounter + ", " + curNumPiecesCreated).split(",");
				
				if(Long.parseLong(tokens[1]) <= targetTaskIndex) {
					checkpointCounter = Long.parseLong(tokens[0]);
					curNumPiecesCreated = Long.parseLong(tokens[1]);
					
					//System.out.println("Going from " + prevCheckpointCounter + " to " + checkpointCounter);
					//System.out.println("Going from " + prevNumPiecesCreated + " pieces to " + curNumPiecesCreated);
					
					//System.exit(0);
					return;
				}
			}

			
		}
		checkpointCounter++;
		//End logic for skipping ahead on the second pass
		


		
		regions = FoldResolveOrderedRegionsSkipSymmetries.handleCompletedRegionIfApplicable(regions, limitDupSolutions, indexCuboidonPaper, paperUsed);
		
		if(regions == null) {
			
			setSkipToHashMap(numCellsUsedDepth, prevCheckpointCounter, prevNumPiecesCreated);
			return;
		}
		
		
		int regionIndex = regions.length - 1;
		long retDuplicateSolutions = 0L;
		
		
		//TODO: use a cache, and compare results.
		int maxOrderBasedOn3Bomb = threeBombHandler.getMaxOrderIndex(cuboid,
				paperToDevelop,
				indexCuboidonPaper,
				regions[regionIndex],
				curNumPiecesCreated,
				numCellsUsedDepth,
				topBottombridgeUsedNx1x1
			);
		
		//DEPTH-FIRST START:
		for(int i=regions[regionIndex].getMinOrderedCellCouldUsePerRegion(); i<=maxOrderBasedOn3Bomb && i<paperToDevelop.length && paperToDevelop[i] != null; i++) {
			
			int indexToUse = indexCuboidonPaper[paperToDevelop[i].i][paperToDevelop[i].j];
			
			
			if( ! regions[regionIndex].getCellIndexToOrderOfDev().containsKey(indexToUse)) {
				continue;

			} else if(SymmetryResolver.skipSearchBecauseOfASymmetryArgDontCareAboutRotation
					(cuboid, paperToDevelop, indexCuboidonPaper, i,indexToUse)
				&& skipSymmetries) {
				continue;

			} else	if( 2*numCellsUsedDepth < paperToDevelop.length && 
					SymmetryResolver.skipSearchBecauseCuboidCouldProvablyNotBeBuiltThisWay
					(cuboid, paperToDevelop, indexCuboidonPaper, i,indexToUse, regions[regionIndex], topBottombridgeUsedNx1x1) && skipSymmetries) {
				
				break;
				
			}

			CoordWithRotationAndIndex neighbours[] = cuboid.getNeighbours(indexToUse);
			
			int curRotation = cuboid.getRotationPaperRelativeToMap(indexToUse);
			
			int indexToUse2 = indexCuboidOnPaper2ndCuboid[paperToDevelop[i].i][paperToDevelop[i].j];
			int curRotationCuboid2 = cuboidToBringAlongStartRot.getRotationPaperRelativeToMap(indexToUse2);
			
			//Try to attach a cell onto indexToUse using all 4 rotations:
			for(int dirNewCellAdd=0; dirNewCellAdd<NUM_ROTATIONS; dirNewCellAdd++) {
				
				int neighbourArrayIndex = (dirNewCellAdd - curRotation + NUM_ROTATIONS) % NUM_ROTATIONS;
				
				if(cuboid.isCellIndexUsed(neighbours[neighbourArrayIndex].getIndex())) {
					
					//Don't reuse a used cell:
					continue;
					
				} else if(regions[regionIndex].getCellRegionsToHandleInRevOrder()[neighbours[neighbourArrayIndex].getIndex()] == false) {
					continue;
	
				} else if(regions[regionIndex].getCellIndexToOrderOfDev().get(indexToUse) == regions[regionIndex].getMinOrderedCellCouldUsePerRegion() 
						&& dirNewCellAdd <  regions[regionIndex].getMinCellRotationOfMinCellToDevPerRegion()) {
					continue;
				}

				int neighbourIndexCuboid2 = (dirNewCellAdd - curRotationCuboid2 + NUM_ROTATIONS) % NUM_ROTATIONS;
				
				int indexNewCell2 = cuboidToBringAlongStartRot.getNeighbours(indexToUse2)[neighbourIndexCuboid2].getIndex();
				
				if(cuboidToBringAlongStartRot.isCellIndexUsed(indexNewCell2)) {
					//no good!
					continue;
				}
				
				
				int new_i = paperToDevelop[i].i + nugdeBasedOnRotation[0][dirNewCellAdd];
				int new_j = paperToDevelop[i].j + nugdeBasedOnRotation[1][dirNewCellAdd];

				int indexNewCell = neighbours[neighbourArrayIndex].getIndex();
				
				
				if(paperUsed[new_i][new_j]) {
					//Cell we are considering to add is already there...
					continue;
				}
				
				
				int rotationNeighbourPaperRelativeToMap = (curRotation - neighbours[neighbourArrayIndex].getRot() + NUM_ROTATIONS) % NUM_ROTATIONS;
				int rotationNeighbourPaperRelativeToMap2 = (curRotationCuboid2 - cuboidToBringAlongStartRot.getNeighbours(indexToUse2)[neighbourIndexCuboid2].getRot() + NUM_ROTATIONS)  % NUM_ROTATIONS;
				
				if(SymmetryResolver.skipSearchBecauseOfASymmetryArg
						(cuboid, paperToDevelop, i, indexCuboidonPaper, dirNewCellAdd, curRotation, paperUsed, indexToUse, indexNewCell)
					&& skipSymmetries == true) {
					continue;
				}
				
				boolean cantAddCellBecauseOfOtherPaperNeighbours = FoldResolveOrderedRegionsSkipSymmetries.cantAddCellBecauseOfOtherPaperNeighbours(paperToDevelop, indexCuboidonPaper,
						paperUsed, cuboid, numCellsUsedDepth,
						regions, regionIndex, indexToUse,
						indexNewCell, new_i, new_j, i
					);
				
				
				Region regionsBeforePotentailRegionSplit[] = regions;
				int prevNewMinOrderedCellCouldUse = regions[regionIndex].getMinOrderedCellCouldUsePerRegion();
				int prevMinCellRotationOfMinCellToDev = regions[regionIndex].getMinCellRotationOfMinCellToDevPerRegion();
				
				if( !cantAddCellBecauseOfOtherPaperNeighbours) {

					//Split the regions if possible:
					regions = splitRegionsIfNewCellSplitsRegions(paperToDevelop, indexCuboidonPaper,
							paperUsed, cuboid, numCellsUsedDepth,
							regions,
							indexToUse, dirNewCellAdd, prevNewMinOrderedCellCouldUse, prevMinCellRotationOfMinCellToDev,
							new_i, new_j, indexNewCell, rotationNeighbourPaperRelativeToMap,
							skipSymmetries,
							cuboidToBringAlongStartRot, indexCuboidOnPaper2ndCuboid, indexNewCell2, rotationNeighbourPaperRelativeToMap2,
							topBottombridgeUsedNx1x1,
							threeBombHandler,
							false, debugIterations);
					
					if(regions == null) {
						cantAddCellBecauseOfOtherPaperNeighbours = true;
						regions = regionsBeforePotentailRegionSplit;
					}

				}
				
				if( ! cantAddCellBecauseOfOtherPaperNeighbours) {
					
					//Setup for adding new cell:
					cuboid.setCell(indexNewCell, rotationNeighbourPaperRelativeToMap);
					cuboidToBringAlongStartRot.setCell(indexNewCell2, rotationNeighbourPaperRelativeToMap2);
					
					paperUsed[new_i][new_j] = true;
					indexCuboidonPaper[new_i][new_j] = indexNewCell;
					paperToDevelop[numCellsUsedDepth] = new Coord2D(new_i, new_j);

					if(indexToUse == 0) {
						topBottombridgeUsedNx1x1[indexNewCell] = dirNewCellAdd;
					} else if(indexToUse == cuboid.getCellsUsed().length - 1) {
						topBottombridgeUsedNx1x1[indexNewCell] = NUM_ROTATIONS + dirNewCellAdd;
					} else {
						topBottombridgeUsedNx1x1[indexNewCell] = topBottombridgeUsedNx1x1[indexToUse];
					}

					indexCuboidOnPaper2ndCuboid[new_i][new_j] = indexNewCell2;

					//Add cell to new region(s):
					for(int r=regionsBeforePotentailRegionSplit.length - 1; r<regions.length; r++) {
						regions[r].addCellToRegion(indexNewCell, numCellsUsedDepth, indexToUse, dirNewCellAdd);						
					}

					
					numCellsUsedDepth += 1;
					//End setup

					long newLimitDupSolutions = limitDupSolutions;
					if(limitDupSolutions >= 0) {
						newLimitDupSolutions -= retDuplicateSolutions;
					}
					
					threeBombHandler.addCell(paperUsed, indexCuboidonPaper, cuboid,  regions[regions.length - 1],
							new_i, new_j, indexNewCell, rotationNeighbourPaperRelativeToMap, topBottombridgeUsedNx1x1, curNumPiecesCreated);
					
					
					getComputeTaskForStartingPointAndRotation(paperToDevelop, indexCuboidonPaper, paperUsed, cuboid, numCellsUsedDepth, regions, newLimitDupSolutions, skipSymmetries, solutionResolver, cuboidToBringAlongStartRot, indexCuboidOnPaper2ndCuboid, topBottombridgeUsedNx1x1, threeBombHandler, debugNope, debugIterations,
							maxDepth, targetTaskIndex);

					if(ComputeTaskMain.computeTask != null) {
						return;
					}
					if(numCellsUsedDepth < regions[0].getCellIndexToOrderOfDev().size()) {
						System.out.println("WHAT???");
						System.exit(1);
					}
					
					
					//Tear down (undo add of new cell)
					numCellsUsedDepth -= 1;

					regions = regionsBeforePotentailRegionSplit;
					

					//Remember to keep this after the regions update

					
					//Remove cell from last region(s):
					regions[regions.length - 1].removeCellFromRegion(indexNewCell, numCellsUsedDepth, prevNewMinOrderedCellCouldUse, prevMinCellRotationOfMinCellToDev);
	
					paperUsed[new_i][new_j] = false;
					indexCuboidonPaper[new_i][new_j] = -1;
					paperToDevelop[numCellsUsedDepth] = null;

					indexCuboidOnPaper2ndCuboid[new_i][new_j] = -1;
					
					cuboid.removeCell(indexNewCell);
					cuboidToBringAlongStartRot.removeCell(indexNewCell2);
					
					threeBombHandler.removeCell(paperToDevelop, paperUsed, indexCuboidonPaper, cuboid,  regions[regions.length - 1],
							new_i, new_j, indexNewCell, rotationNeighbourPaperRelativeToMap, topBottombridgeUsedNx1x1, curNumPiecesCreated);
					
					//End tear down


					if(limitDupSolutions >= 0 && retDuplicateSolutions > limitDupSolutions) {
						//Handling option to only find 1 or 2 solutions:
						//This has to be done after tear-down because these objects are soft-copied...

						setSkipToHashMap(numCellsUsedDepth, prevCheckpointCounter, prevNumPiecesCreated);
						return;
					}

					regionIndex = regions.length - 1;
					
				} // End recursive if cond
			} // End loop rotation
		} //End loop index

		setSkipToHashMap(numCellsUsedDepth, prevCheckpointCounter, prevNumPiecesCreated);
		return;
	}
	


 

	//j = rotation relativeCuboidMap
	public static Region[] splitRegionsIfNewCellSplitsRegions(Coord2D paperToDevelop[], int indexCuboidonPaper[][],
			boolean paperUsed[][], CuboidToFoldOn cuboid, int numCellsUsedDepth,
			Region regions[],
			int indexToUse, int newMinRotationToUse, int prevNewMinOrderedCellCouldUse, int prevMinCellRotationOfMinCellToDev,
			int new_i, int new_j, int indexNewCell, int rotationNeighbourPaperRelativeToMap,
			boolean skipSymmetries,
			CuboidToFoldOn cuboidToBringAlongStartRot, int indexCuboidOnPaper2ndCuboid[][], int indexNewCell2, int rotationNeighbourPaperRelativeToMap2,
			int topBottombridgeUsedNx1x1[],
			ThreeBombHandler threeBombHandler,
			boolean debugNope, long debugIterations[]) {
		
		boolean cantAddCellBecauseARegionDoesntHaveSolution = false;
		
		//Trying to divide and conquer here:
		
		CoordWithRotationAndIndex neighboursOfNewCell[] = cuboid.getNeighbours(indexNewCell);
		
		int regionIndex = regions.length - 1;
		
		//areCellsSepartedCuboid(CuboidToFoldOn cuboid, int startIndex, int goalIndex)
		
		//flag is redundant, but expressive!
		boolean foundABlankNeighbour = false;
		int firstIndex = -1;

		//Add potentially new cell just for test:
		cuboid.setCell(indexNewCell, rotationNeighbourPaperRelativeToMap);
				
		TRY_TO_DIVDE_REGIONS:
		for(int rotIndexToFill=0; rotIndexToFill<NUM_ROTATIONS; rotIndexToFill++) {
			
			

			int indexNeighbourOfNewCell = neighboursOfNewCell[rotIndexToFill].getIndex();
			
			if( ! cuboid.isCellIndexUsed(indexNeighbourOfNewCell)) {
				
				if( ! foundABlankNeighbour) {
					firstIndex = indexNeighbourOfNewCell;
					foundABlankNeighbour = true;
				} else {
					
					if(FoldResolveOrderedRegionsSkipSymmetries.areCellsSepartedCuboid(cuboid, firstIndex, indexNeighbourOfNewCell)) {
						
						//START DIVIDING THE REGION:
						
						//Look for a 3-three way:
						
						boolean found3Way = false;
						int indexNeighbourThirdWay = -1;
						for(int thirdRot=rotIndexToFill+1; thirdRot<NUM_ROTATIONS; thirdRot++) {
							
							int curThirdNeighbour = neighboursOfNewCell[thirdRot].getIndex();
							
							
							if(! cuboid.isCellIndexUsed(curThirdNeighbour)
									&& FoldResolveOrderedRegionsSkipSymmetries.areCellsSepartedCuboid(cuboid, curThirdNeighbour, firstIndex)
									&& FoldResolveOrderedRegionsSkipSymmetries.areCellsSepartedCuboid(cuboid, curThirdNeighbour, indexNeighbourOfNewCell)) {
								found3Way = true;
								indexNeighbourThirdWay = curThirdNeighbour;
							}
						}
						//End look for 3-three (cuboid separated into 3 regions)

						int numNewWays = 1;
						if(found3Way) {
							numNewWays = 2;
						}
							
						Region regionsSplit[] = new Region[regions.length + numNewWays];
						
						for(int k=0; k<regions.length - 1; k++) {
							regionsSplit[k] = regions[k];
						}
						

						for(int k=0; k<numNewWays + 1; k++) {
								
							int indexToAdd = regions.length - 1 + k;
							
							if(k==0) {
								regionsSplit[indexToAdd] = new Region(cuboid, regions[regionIndex], firstIndex, newMinRotationToUse);
							} else if(k == 1) {
								regionsSplit[indexToAdd] = new Region(cuboid, regions[regionIndex], indexNeighbourOfNewCell, newMinRotationToUse);	
							} else if(k == 2) {
								regionsSplit[indexToAdd] = new Region(cuboid, regions[regionIndex], indexNeighbourThirdWay, newMinRotationToUse);
								
							} else {
								System.out.println("ERROR: k is too high. k= " + k);
								System.exit(1);
							}
							
							
						}
						
						regionIndex = regions.length - 1;
						
						//Later:
						//TODO: check if region is the same as a hole in the net to go faster (Also useful for the 3Cuboid 1 net check)

						//reordering regions by whether or not they have a single solution and then by size:
						//TODO: afterwards, try incorporating algo that checks if there's less than or equal to N solutions? Probably bad...
						
						regions = regionsSplit;

						boolean regionHasOneSolution[] = new boolean[regions.length];
						for(int i2=regions.length - 1 - numNewWays; i2< regions.length; i2++) {
							
							paperUsed[new_i][new_j] = true;
							indexCuboidonPaper[new_i][new_j] = indexNewCell;
							paperToDevelop[numCellsUsedDepth] = new Coord2D(new_i, new_j);

							indexCuboidOnPaper2ndCuboid[new_i][new_j] = indexNewCell2;

							cuboidToBringAlongStartRot.setCell(indexNewCell2, rotationNeighbourPaperRelativeToMap2);
	
							regionsSplit[i2].addCellToRegion(indexNewCell, numCellsUsedDepth, indexToUse, newMinRotationToUse);
							
							if(indexToUse == 0) {
								topBottombridgeUsedNx1x1[indexNewCell] = newMinRotationToUse;
							} else if(indexToUse == cuboid.getCellsUsed().length - 1) {
								topBottombridgeUsedNx1x1[indexNewCell] = NUM_ROTATIONS + newMinRotationToUse;
							} else {
								topBottombridgeUsedNx1x1[indexNewCell] = topBottombridgeUsedNx1x1[indexToUse];
							}

							numCellsUsedDepth += 1;
							
							threeBombHandler.addCell(paperUsed, indexCuboidonPaper, cuboid,  regionsSplit[i2],
									new_i, new_j, indexNewCell, rotationNeighbourPaperRelativeToMap, topBottombridgeUsedNx1x1, curNumPiecesCreated);
							
							
							regionHasOneSolution[i2] = false;

							numCellsUsedDepth -= 1;
							
							
							regionsSplit[i2].removeCellFromRegion(indexNewCell, numCellsUsedDepth, prevNewMinOrderedCellCouldUse, prevMinCellRotationOfMinCellToDev);

							cuboidToBringAlongStartRot.removeCell(indexNewCell2);

							paperUsed[new_i][new_j] = false;
							indexCuboidonPaper[new_i][new_j] = -1;
							paperToDevelop[numCellsUsedDepth] = null;
							
							indexCuboidOnPaper2ndCuboid[new_i][new_j] = -1;
							
							threeBombHandler.removeCell(paperToDevelop, paperUsed, indexCuboidonPaper, cuboid,  regions[i2],
									new_i, new_j, indexNewCell, rotationNeighbourPaperRelativeToMap, topBottombridgeUsedNx1x1, curNumPiecesCreated);
							
						}
						
						//Sort new regions by whether it has only 1 solution, and then by size:
						for(int i2=regions.length - 1 - numNewWays; i2< regions.length; i2++) {
							
							int curBestIndex = i2;
							
							for(int j2=i2+1; j2<regions.length; j2++) {
								
								if(regionHasOneSolution[i2] && ! regionHasOneSolution[j2]) {
									curBestIndex = j2;

								} else if(regions[i2].getNumCellsInRegion() < regions[j2].getNumCellsInRegion()) {
									curBestIndex = j2;
								}
							}
							
							Region tmp = regions[i2];
							regions[i2] = regions[curBestIndex];
							regions[curBestIndex] = tmp;
						}
						//END sort
						
						 
						
						//End sort regions by size and if the region only has 1 solution
						
						break TRY_TO_DIVDE_REGIONS;
					}
				}
			}
		}//END LOOP
	
		
		//Remove potential new cell once test is done:
		cuboid.removeCell(indexNewCell);
		
		if(! cantAddCellBecauseARegionDoesntHaveSolution) {
			return regions;
		} else {
			return null;
		}
	}

	 
	
}
