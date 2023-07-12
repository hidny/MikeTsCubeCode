package SolutionResolver;

import Coord.Coord3D;

public interface SolutionResolverInterface {

	
	public long resolveSolution(Coord3D cuboidsInOrderToDevelop[], boolean paperUsed[][][]);
	
	
	public long getNumUniqueFound();
}
