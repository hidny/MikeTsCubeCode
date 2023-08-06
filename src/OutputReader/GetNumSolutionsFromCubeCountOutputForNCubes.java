package OutputReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


//Number of solutions for 17 cubes: 457409613979
//                                  457409613979.00


public class GetNumSolutionsFromCubeCountOutputForNCubes {
	
	//public static int START_DEPTH = 7;
	//public static int N =17;
	
	public static int START_DEPTH = 7;
	public static int N =17;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		//File folder = new File("D:/output13x1x1DoneSoFar");
		File folder = new File("./cube_count_output");
		
		File[] listOfFiles = folder.listFiles();
		
		long numOutputFromEachIndex[] = null;
		int numTimesIndexSeen[] = null;
		int numPieces = -1;
		
		long lastNumSolutions = -1L;

		long ret = 0L;
		
		String excelString = "";


		for (int i = 0; i < listOfFiles.length; i++) {

			  if (listOfFiles[i].isFile()) {
			    
			    if(! listOfFiles[i].getName().contains("_N_" + N + "_in_3D")) {
			    	continue;
			    } else if(! listOfFiles[i].getName().contains("_SD_" + START_DEPTH + "_")) {
			    	continue;
			    }
			    
			    System.out.println("File " + listOfFiles[i].getName());
			    
			    Scanner in = null;;
				try {
					in = new Scanner(new File(listOfFiles[i].getAbsolutePath()));
					
					boolean currentLineIsNumPieces = false;
					int currentPieceIndex = -1;
					boolean gotFinalNumber = false;
					
					
					while(in.hasNextLine()) {
						
						String tmp = in.nextLine();
						
						if(tmp.startsWith("Final num pieces:")) {
							currentLineIsNumPieces = true;
							continue;

						} else if(tmp.startsWith("Index pre-shuffle to post-shuffle:")) {
							currentPieceIndex = (int)getNumberAtEndOfLine(tmp);
							
						} else if(tmp.startsWith("Num solutions found after ")) {
							
							lastNumSolutions = getNumberAtEndOfLine(tmp);
							numTimesIndexSeen[currentPieceIndex]++;

							currentPieceIndex = -1;

							continue;
						} else if(tmp.startsWith("Total number of distinct shapes found for current batch:")) {
							
							if(gotFinalNumber) {
								System.out.println("ERROR: got final number before");
								System.exit(1);
							}
							
							long numSolutionsForFile = getNumberAtEndOfLine(tmp);
							
							if(	lastNumSolutions != numSolutionsForFile) {
								System.out.println("ERROR: num solutions in final line doesn't match num solutions in final batch");
								System.exit(1);
							}
							ret += numSolutionsForFile;
							
							excelString += numSolutionsForFile + "\n";

							gotFinalNumber = true;
						}
						
						if(currentLineIsNumPieces) {
							
							if(numPieces == -1) {
								numPieces = Integer.parseInt(tmp);
								
								numOutputFromEachIndex = new long[numPieces];
								numTimesIndexSeen = new int[numPieces];
								for(int j=0; j<numOutputFromEachIndex.length; j++) {
									numOutputFromEachIndex[j] = 0;
									numTimesIndexSeen[j] = 0;
								}
								
							} else {
								int newNumPieces = Integer.parseInt(tmp);
								
								if(newNumPieces != numPieces) {
									System.out.println("ERROR: num pieces not consistent!");
									System.exit(1);
								}
							}

							currentLineIsNumPieces = false;
						}
						
					}
					
					if(gotFinalNumber == false) {
						System.out.println("ERROR: Didn't get output from file.");
						System.exit(1);
					}
				
					in.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

				} finally {
					if(in != null) {
						in.close();
					}
				}
			  }
		}


		
		for(int i=0; i<numOutputFromEachIndex.length; i++) {
			if(numTimesIndexSeen[i] != 1) {
				System.out.println("ERROR: seen index " + i + " " + numTimesIndexSeen[i] + " times.");
				System.exit(1);
			}
			
		}
		
		System.out.println("Numbers per file to check on excel:");
		System.out.println(excelString);
		
		System.out.println();
		System.out.println();
		System.out.println("Number of solutions for " + N + " cubes: " + ret);
			
	}
	
	public static long getNumberAtEndOfLine(String s) {
		return Long
				.parseLong(s.split(" ")[s.split(" ").length - 1]);
	}
}
