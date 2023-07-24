package MultiplePiecesHandler;

import java.math.BigInteger;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

public class ComputeBatchMain {

	/*
	 * 
13x1x1 and 3x3x3 where max depth is 13:
Num pieces found: 2083716
Final num pieces:
2083716

secret key: 122572
Exp: 17
Mod: 2083723
	 */
	
	//How to test it:
	//Test Works:
	//public static int BATCH_SIZE = 20000;
	//public static int START_DEPTH = 6;
	
	//Test Works:
	//public static int BATCH_SIZE = 29270;
	//public static int START_DEPTH = 8;
	
	//What to run:
	//public static int BATCH_SIZE = 4000;
	//public static int START_DEPTH = 13;
	
	public static int BATCH_SIZE = -1;
	public static int START_DEPTH = -1;
	public static int BATCH_INDEX_TO_USE = -1;
	public static int NUM_CUBES = -1;
	public static int NUM_DIMENSIONS = -1;
	
	public static boolean RELECTIONS_ARE_SAME = false;
	
	public static int GET_ALL_PIECES_INDEX = -1;
	
	public static String PROPERTIES_FILE_NAME = "cube_search.properties";
	
	public static final boolean IS_FIRST_TRIAL = true;
	
	public static void main(String[] args) {
		
		boolean foundPropFile = parseConfigFileAndSetOutputFile(PROPERTIES_FILE_NAME, IS_FIRST_TRIAL);
		
		if( ! foundPropFile ) {
			//Try 1 level up, and then give up.
			parseConfigFileAndSetOutputFile(".." + File.separator + PROPERTIES_FILE_NAME, ! IS_FIRST_TRIAL);
		}
		
		System.out.println("Get num pieces: " + NUM_CUBES + "," + START_DEPTH);
		long numPieces = getNumPieces(NUM_CUBES, START_DEPTH);

		System.out.println("Final num pieces:");
		System.out.println(numPieces);

		System.out.println();
		System.out.println("Setup shuffle for pseudo-random sampling of tasks:");
		BigInteger shuffleParams[] = getShuffleNumbers(numPieces);
		
		BigInteger exp = shuffleParams[0];
		BigInteger mod = shuffleParams[1];

		System.out.println("Exp: " + exp);
		System.out.println("Mod: " + mod);
		System.out.println();

		long ret = 0L;
		for(int i=0; i<BATCH_SIZE; i++) {
			
			//TODO: Don't shuffle! Keep it simple!
			long indexBeforeTranslation = BATCH_INDEX_TO_USE * BATCH_SIZE + i;
			
			if(indexBeforeTranslation >= mod.longValue()) {
				System.out.println("Ending batch early because there's no more pieces left to process.");
				break;
			}
			
			int indexAfterTranslation = getAPowerPmodMOD(new BigInteger("" + indexBeforeTranslation), exp, mod).intValue();
			
			if(indexAfterTranslation >= numPieces) {
				continue;
			}
			
			long cur = ComputeTaskMain.runSubtask(NUM_CUBES, START_DEPTH, indexAfterTranslation);

			ret += cur;
			
			System.out.println("Index pre-shuffle to post-shuffle: " + indexBeforeTranslation + " to " + indexAfterTranslation);
			System.out.println("Done piece index: " + indexAfterTranslation);
			System.out.println("Num solutions found after " + (i+1) + " iteration(s): " + ret);
			System.out.println("-----");
			System.out.println("");
			System.out.println("");
			

		}
		
		System.out.println("Total number of distinct shapes found for current batch: " + ret);
		
	}
	
	public static boolean parseConfigFileAndSetOutputFile(String propertiesFileName, boolean firstTrial) {
		
		boolean retFound =false;
		
		 try (InputStream input = new FileInputStream(propertiesFileName)) {
			 
			 	retFound = true;
			 	

				 if(! firstTrial) {
					 System.out.println("Found properties file in parent directory.");
					 System.out.println();
				 } else {
					 System.out.println("Found properties file in current directory.");
					 System.out.println();
				 }
			 	
		        Properties prop = new Properties();
		
		        // load a properties file
		        prop.load(input);
		
		        String numCubesString = prop.getProperty("num_cubes");
		        String numDimensionsString = prop.getProperty("num_dimensions");
	
		        String depthString = prop.getProperty("search_start_depth");
		        String batchSizeString = prop.getProperty("batch_size");
		        String indexToUseString = prop.getProperty("batch_index_to_search");
		        

		        if(! isNumber(numCubesString)|| ! isNumber(numDimensionsString)) {
		        	System.out.println("ERROR: one of the settings is not a number: numCubesString, and/or numDimensionsString");
		        }
		        
		        if(Integer.parseInt(numDimensionsString) != 3) {
		        	System.out.println("ERROR: The number of dimensions in not set to 3, and that's not ready yet.");
		        }
		        
		        NUM_CUBES = Integer.parseInt(numCubesString);
		        NUM_DIMENSIONS = Integer.parseInt(numDimensionsString);
		        
		        if(! isNumber(batchSizeString)|| ! isNumber(indexToUseString) || ! isNumber(depthString)) {
		        	System.out.println("ERROR: one of the settings is not a number: batch_size, batch_index_to_search, and/or search_start_depth");
		        }
		        START_DEPTH = Integer.parseInt(depthString);
		        BATCH_SIZE = Integer.parseInt(batchSizeString);
		        BATCH_INDEX_TO_USE = Integer.parseInt(indexToUseString);
		        
		        
		        PrintStream o;
		        
		        String filenameString = "cube_count_N_" + numCubesString + "_in_" + numDimensionsString + "D_SD_" + START_DEPTH + "_BS_" + batchSizeString + "_IND_"+ indexToUseString +".txt";
		        String path = filenameString;
		        
		        if(prop.getProperty("output_folder") == null) {
		        	
		        	String defaultDirName = "cube_count_output";
		        	path = defaultDirName + File.separator + filenameString;
		        	File directory = new File(defaultDirName);
		        	if( ! directory.exists()) {
		        		directory.mkdir();
		        	}
		        	
		        	o = new PrintStream(new File(path));
			        
		        } else {
		        	String prefix = prop.getProperty("output_folder");
		        	
		        	while(prefix.startsWith("\"") && prefix.endsWith("\"")) {
		        		prefix = prefix.substring(1, prefix.length() - 1);
		        	}
		        	
		        	if(! prefix.endsWith(File.separator)) {
		        		prefix = prefix + File.separator;
		        	}
		        	
		        	path = prefix + filenameString;
		        	o = new PrintStream(new File(path));
		
		        }
		        
	        	System.out.println("The program will output to this path:\n" + path);
		        System.setOut(o);
		        
		        System.out.println("Counting shapes in " + numDimensionsString + "D. As of now, 2 shapes that are rotationally symmetric in " + numDimensionsString + "D are considered the same. (i.e. no reflections in 3D)");

		 
		    } catch (IOException ex) {
		    	
		    	if(firstTrial) {
		    		//Try again.
		    	} else {
		    		
		    		System.out.println("ERROR: could not find properties file in parent directory. Relative path: " + propertiesFileName);
			        ex.printStackTrace();
		    	}
		    }

		 return retFound;
	}
	
	
	//Shuffling the indexes based on RSA encryption:
	// This allows me to sample the search space in a way that isn't random, but can probably 
	// be treated as random enough for me to extrapolate the time it will take and how many solutions there are.

	public static BigInteger[] getShuffleNumbers(long numPieces) {
		
		long mod = numPieces;
		
		int defaultExp = 17;

		int carMichaelTotient = -1;
		
		for(; true; mod++) {
			
			if(mod % 2 == 1
					&& getPrimeDivisors(mod).length == 2) {
				

				long primes[] = getPrimeDivisors(mod);
				
				if(primes[0] * primes[1] < mod) {
					continue;
				}
				
				carMichaelTotient = (int)getLCM(primes[0] - 1, primes[1] - 1);
				
				if(carMichaelTotient % defaultExp != 0) {
					break;
				}
			}
		}
		
		
		int d = 1;
		//Compute the inverse the slow way:
		while( (d * defaultExp) % carMichaelTotient != 1) {
			d++;
		}
		
		System.out.println(defaultExp);
		System.out.println(mod);
		System.out.println("secret key: " + d);
		
		sanityTestShuffle(new BigInteger("" + defaultExp), new BigInteger("" + d), new BigInteger("" + mod));
		
		return new BigInteger[] {new BigInteger("" + defaultExp), new BigInteger("" + mod)};
	}
	

	public static void sanityTestShuffle(BigInteger exp, BigInteger key, BigInteger mod) {
		//BigInteger exp = new BigInteger("" + 1);
		//BigInteger key = new BigInteger("" + 13);
		//int modInt= 21;
		
		System.out.println("Sanity testing RSA shuffle:");

		int modInt = mod.intValue();
		
		for(int i=0; i<modInt; i++) {
			
			BigInteger iBig = new BigInteger("" + i);
			BigInteger tmp = ComputeBatchMain.getAPowerPmodMOD(iBig, exp, mod);
			
			BigInteger maybeI = ComputeBatchMain.getAPowerPmodMOD(tmp, key, mod);
			
			if(iBig.compareTo(maybeI) != 0) {
				System.out.println("Error: There's a problem with the RSA shuffle for i = " + i);
				System.out.println(i + " encrypted and decrypted to " + maybeI);
				
				System.exit(1);
			}
			
		}
		
		System.out.println("Done sanity test for e = " + exp + ", d = " + key + ", and n = " + modInt);
	}
	
	public static long getLCM(long a, long b) {
		return (a*b)/getGCD(a, b);
	}
	
	public static long getGCD(long a, long b) {
		if(b>a) {
			return getGCD(b, a);
		}
		
		long ret = a % b;
		
		if(ret == 0) {
			return b;
		} else{
			return getGCD(b, ret);
		}
	}
	
	//TODO: make sure the caller gets it in the right order:
	public static long getNumPieces(int n, int startDepth) {
		
		DFSPolyCubeCounterOptimized3StartDepthCutOff.getComputeTask(n, startDepth, -1);
		
		return DFSPolyCubeCounterOptimized3StartDepthCutOff.curNumPiecesCreated;
	}
	
	
	public static BigInteger getAPowerPmodMOD(BigInteger a, BigInteger pow, BigInteger MOD) {
		//base case for power:
		if(pow.compareTo(BigInteger.ZERO) == 0) {
			if(a.compareTo(BigInteger.ZERO) == 0 ) {
				System.out.println("0^0 is I don't know!!!");
				System.exit(1);
			} else if(pow.compareTo(BigInteger.ZERO) < 0 ) {
				System.out.println("No negative powers!" +  a + " to the power of " + pow + "?" );
				System.exit(1);
			}
			return BigInteger.ONE;
		} else if(a.compareTo(BigInteger.ZERO) == 0) {
			return BigInteger.ZERO;
		}
		
		//System.out.println(a + " to the power of " + pow);
		
		int lengthPowTable = 0;
		BigInteger current = BigInteger.ONE;
		while(current.compareTo(pow) <= 0) {
			lengthPowTable++;
			current = current.multiply(new BigInteger("2"));
		}
		
		//System.out.println("Length: " + lengthPowTable);
		
		//Setup the power of 2 table
		BigInteger pow2Table[] = new BigInteger[lengthPowTable];
		pow2Table[0] = a;
		
		
		for(int i=1; i<lengthPowTable; i++) {
			pow2Table[i] = (pow2Table[i-1].multiply(pow2Table[i-1])).mod(MOD);
		}
		//End setup the power of 2 table.
	
		current = pow;
		BigInteger answer = BigInteger.ONE;
		
		
		for(int i=0; i<lengthPowTable && current.compareTo(BigInteger.ZERO) > 0; i++) {
			if(current.mod(new BigInteger("2")).compareTo(BigInteger.ONE) == 0) {
				answer = (answer.multiply(pow2Table[i])).mod(MOD);
				current = current.subtract(BigInteger.ONE);
			}
			current = current.divide(new BigInteger("2"));
		}
		
		return answer;
	}
	


	public static long[] getPrimeDivisors(long n) {
		long nTemp = n;
		long LIMIT = (long)Math.sqrt(n);
		
		int numPrimeDivisors = 0;
		for(long i=2; i<=LIMIT; i++) {
			if(nTemp % i == 0 && isPrime(i)) {
				numPrimeDivisors++;
				while(nTemp % i == 0) {
					nTemp /= i;
				}
			}
		}
		if(nTemp > 1) {
			numPrimeDivisors++;
		}
		
		long divisors[] = new long[numPrimeDivisors];
		
		nTemp = n;
		int currentIndex = 0;
		for(long i=1; i<=LIMIT; i++) {
			if(nTemp % i == 0 && isPrime(i)) {
				divisors[currentIndex] = i;
				currentIndex++;
				while(nTemp % i == 0) {
					nTemp /= i;
				}
			}
		}
		if(nTemp > 1) {
			divisors[currentIndex] = nTemp;
			currentIndex++;
		}
		
		
		return divisors;
	}
	
	public static boolean isPrime(long num) {
		if(num<=1) {
			return false;
		}
		
		int sqrt = (int)Math.sqrt(num);
		for(int i=2; i<=sqrt ; i++) {
			if(num%i == 0) {
				return false;
			}
		}
		
		return true;
	}
	
	public static boolean isNumber(String val) {
		try {
			int a = Integer.parseInt(val);
			return true;
		} catch(Exception e) {
			return false;
		}
	}
	public static boolean isLong(String val) {
		try {
			long a = Long.parseLong(val);
			return true;
		} catch(Exception e) {
			return false;
		}
	}
	
	
}
