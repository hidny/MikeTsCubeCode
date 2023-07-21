package MultiplePiecesHandler;

import Coord.Coord3D;
import NumPolyShapeSolveOptimized.DFSPolyCubeCounterOptimized3;

public class ComputeTaskMain {

	// Length of depth 7 is 5396...
	
	public static int START_DEPTH = 13;
	public static int TARGET_TASK_INDEX = 1000000;
	
	
	//TODO: why have this be a static variable instead of a returned value?
	public static ComputeTaskDescription computeTask = null;
	
	public static void main(String[] args) {
		
		if(TARGET_TASK_INDEX < 0) {
			System.out.println("ERROR: please specify the TARGET_TASK_INDEX. (It's currently less than 0)");
		}
		
		long ret = runSubtask(START_DEPTH, TARGET_TASK_INDEX, 17);
		
		System.out.println("Final number of unique solutions for Task: " + ret);

		
	}
	
	public static long runSubtask(int start_depth, int targetIndex, int numCubes) {
		

		//System.out.println("DEBUG Target Index: " + targetIndex);
		updateComputeTask(start_depth, targetIndex, numCubes);
		
		if(computeTask == null) {
			System.out.println("Target index too high.");
			System.out.println("Num pieces found: " + DFSPolyCubeComputeTaskGetter.curNumPiecesCreated);
			System.exit(0);
		}
		
		ComputeTaskDescription taskDescriptionToUse = computeTask;
		
		
		System.out.println("Run compute task for index " + targetIndex + ": (Please wait)");
		
		
		DFSPolyCubeCounterOptimized3.initializePrecomputedVars(numCubes);

		long ret = DFSPolyCubeCounterOptimized3.doDepthFirstSearch(
				taskDescriptionToUse.cubesToDevelop,
				taskDescriptionToUse.cubesUsed,
				taskDescriptionToUse.numCellsUsedDepth,
				taskDescriptionToUse.debugNope,
				taskDescriptionToUse.debugIterations,
				taskDescriptionToUse.cubesOrdering,
				taskDescriptionToUse.minIndexToUse,
				taskDescriptionToUse.minRotationToUse,
				taskDescriptionToUse.curPathArray,
				taskDescriptionToUse.curNum
		);
		
		
		System.out.println("Finished task.");
		
		
		computeTask = null;
		taskDescriptionToUse = null;

		return ret;
	}
	
	public static void updateComputeTask(int startDepth, int targetIndex, int numCubes) {
		
		
		System.out.println("Fold Resolver Ordered Regions intersection skip symmetries Nx1x1:");

		computeTask = null;
		
		DFSPolyCubeComputeTaskGetter.getComputeTask(
				numCubes,
				true,//TODO: will I need the true label?
				startDepth,
				targetIndex
		);
	}

}
