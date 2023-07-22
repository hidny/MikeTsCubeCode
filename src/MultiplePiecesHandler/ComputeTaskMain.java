package MultiplePiecesHandler;

import NumPolyShapeSolveOptimized.DFSPolyCubeCounterOptimized3;

public class ComputeTaskMain {

	// Length of depth 7 is 5396...
	
	public static int START_DEPTH = 13;
	public static int TARGET_TASK_INDEX = 1000000;
	
	public static void main(String[] args) {
		
		if(TARGET_TASK_INDEX < 0) {
			System.out.println("ERROR: please specify the TARGET_TASK_INDEX. (It's currently less than 0)");
		}
		
		long ret = runSubtask(START_DEPTH, TARGET_TASK_INDEX, 17);
		
		System.out.println("Final number of unique solutions for Task: " + ret);

		
	}
	
	public static long runSubtask(int numCubes, int start_depth, int targetIndex) {

		//System.out.println("DEBUG Target Index: " + targetIndex);
		ComputeTaskDescription taskDescriptionToRun = DFSPolyCubeCounterOptimized3StartDepthCutOff.getComputeTask(numCubes, start_depth, targetIndex);
		
		if(taskDescriptionToRun == null) {
			System.out.println("WARNING: Target index too high.");
			System.out.println("Max target index: " + DFSPolyCubeCounterOptimized3StartDepthCutOff.curNumPiecesCreated);
			return 0;
		}
		
		
		
		System.out.println("Run compute task for index " + targetIndex + ": (Please wait)");
		
		
		DFSPolyCubeCounterOptimized3.initializePrecomputedVars(numCubes);

		long ret = DFSPolyCubeCounterOptimized3.doDepthFirstSearch(
				taskDescriptionToRun.cubesToDevelop,
				taskDescriptionToRun.cubesUsed,
				taskDescriptionToRun.numCellsUsedDepth,
				taskDescriptionToRun.debugNope,
				taskDescriptionToRun.debugIterations,
				taskDescriptionToRun.cubesOrdering,
				taskDescriptionToRun.minIndexToUse,
				taskDescriptionToRun.minRotationToUse,
				taskDescriptionToRun.curPathArray,
				taskDescriptionToRun.curNum
		);
		
		
		System.out.println("Finished task.");
		

		return ret;
	}
	

}
