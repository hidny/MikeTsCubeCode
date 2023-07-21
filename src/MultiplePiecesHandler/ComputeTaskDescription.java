package MultiplePiecesHandler;

import Coord.Coord3D;

public class ComputeTaskDescription {
	public Coord3D cubesToDevelop[];
	public boolean cubesUsed[][][];
	public int numCellsUsedDepth;
	public boolean debugNope;
	public long debugIterations[];
	public int cubesOrdering[][][];
	public int minIndexToUse;
	public int minRotationToUse;
	public int curPathArray[];
	public int curNum;


	public ComputeTaskDescription(Coord3D[] cubesToDevelop, boolean[][][] cubesUsed, int numCellsUsedDepth,
			boolean debugNope, long[] debugIterations, int[][][] cubesOrdering, int minIndexToUse, int minRotationToUse,
			int[] curPathArray, int curNum) {
		super();
		this.cubesToDevelop = cubesToDevelop;
		this.cubesUsed = cubesUsed;
		this.numCellsUsedDepth = numCellsUsedDepth;
		this.debugNope = debugNope;
		this.debugIterations = debugIterations;
		this.cubesOrdering = cubesOrdering;
		this.minIndexToUse = minIndexToUse;
		this.minRotationToUse = minRotationToUse;
		this.curPathArray = curPathArray;
		this.curNum = curNum;
	}
	
	
}
