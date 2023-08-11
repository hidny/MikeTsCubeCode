
# MikeTsCubeCode  

  
## Purpose of the Program 

Find the number of distinct 3D polycube shapes with the number of cubes equal to 17, and then take it further.
Challenge explanation: https://www.youtube.com/watch?v=g9n0a0644B4
Link to the currently known numbers related to this: http://kevingong.com/Polyominoes/Enumeration.html
(I'm mostly interested in the numbers that come after 59795121480 or 59,795,121,480)

So far, I have an algorithm that doesn't take that much memory and will do the job in about 2 months.
Thanks to Loïc Damien, the algorithm got ported to Rust, and got a speed boost. Loïc Damien's code: https://gitlab.com/dzamlo/polycubes  

I'm currently working on slicing it up into a bunch of pieces, so it could be run by multiple CPUs at the same time.

## How to Run the program
### How to Run on command line (Not recommended for large values of N)

#### Option 1: DFSPolyCubeCounterOptimized3

Go to NumPolyShapeSolveOptimized/DFSPolyCubeCounterOptimized3.java and hard-code the value of N to 12

Example:
You could set the number of cubes to be 12:
int N = 12;

Install Java version 8+

Example:
$ java -version
java version "1.8.0_73"
Java(TM) SE Runtime Environment (build 1.8.0_73-b02)
Java HotSpot(TM) 64-Bit Server VM (build 25.73-b02, mixed mode)

Go to the src folder and run the javac and java commands:
Michael@Felix MINGW64 ~/ProjectEuler2/PolycubeShapeCounter/src (main)
$ javac NumPolyShapeSolveOptimized/DFSPolyCubeCounterOptimized3.java

Michael@Felix MINGW64 ~/ProjectEuler2/PolycubeShapeCounter/src (main)
$ java NumPolyShapeSolveOptimized/DFSPolyCubeCounterOptimized3
Polycube counter program:
Current UTC timestamp in milliseconds: 1690171204839
Num iterations: 100000
.5#|
.1.|
402|
83.|

Solutions: 829726
(...)
Current UTC timestamp in milliseconds: 1690171423790



Final number of unique solutions: 18598427
Done with N = 12
Current UTC timestamp in milliseconds: 1690171423791

Michael@Felix MINGW64 ~/ProjectEuler2/PolycubeShapeCounter/src (main)



#### Option 2: the slower DFSPolyCubeCounter program


Go to NumPolyShapeSolve/DFSPolyCubeCounter.java and hard-code the value of N to 12

Michael@Felix MINGW64 ~/ProjectEuler2/PolycubeShapeCounter/src (main)
$ javac NumPolyShapeSolve/DFSPolyCubeCounter.java

Michael@Felix MINGW64 ~/ProjectEuler2/PolycubeShapeCounter/src (main)
$ java NumPolyShapeSolve/DFSPolyCubeCounter
Polycube counter program:
Current UTC timestamp in milliseconds: 1690172328372
Printing all the nudges:
-1  0  1  0  0  0
 0  1  0 -1  0  0
 0  0  0  0  1 -1

-1  0  1  0  0  0
(,,,)

Warning: it's slower!

The reason I kept it is because I think it's slightly simpler than the optimized code, and you have the option of comparing the two files.

#### Option 3: parallelized ComputeBatchMain

Setup the cube_search.properties file to the settings you want. I'll document the meaning of the
settings in the README file in cube_search_with_JAR later.

For now, I recommend trying these settings:
num_cubes=8
num_dimensions=3
search_start_depth=7
batch_size=20
batch_index_to_search=10000

Go to src, and run javac and java:

Michael@Felix MINGW64 ~/ProjectEuler2/PolycubeShapeCounter/src (main)
$ javac MultiplePiecesHandler/ComputeBatchMain.java

Michael@Felix MINGW64 ~/ProjectEuler2/PolycubeShapeCounter/src (main)
$ java MultiplePiecesHandler/ComputeBatchMain
Found properties file in parent directory.

The program will output to this path:
src\cube_count_output\cube_count_N_8_in_3D_SD_7_BS_20_IND_10.txt



At end of file output, you should see the following:
Total number of distinct shapes found for current batch: 18598427

18598427 matches the number that's found in:
http://kevingong.com/Polyominoes/Enumeration.html

### Suboption: From JAR

#### Through the command line

#### By just double-clicking it.

I didn't implement this :(


## Current Plan that already happened
  
* Finding the answer to N=17:
	* If everything goes well, I'll have the answer to N=16 by July 22nd 2023, and I'll hopefully confirm that it matches what Kevin Gong said it would be.  
(I'm currently 15 correct out of 15 trials)  
		* Update: N=16 was found and it agreed with Kevin Gong's answer.
	* Find N=17 by creating a program that slices up the problem into 1000ish digestible pieces, and running multiple CPUs on it at the same time.  
	* To see the template I'll be working off of, see: https://github.com/hidny/weirdMathStuff/blob/master/cuboidNetSearch/READ_ME.md  
		* I might bother to attach a GUI that has the option of stopping the program and explaining a bit about the program . (Update: I didn't bother...)
		* Once it's set up, I will run the program on 3 cores of my laptop for about 2 weeks and get the answer for N = 17.
			* (Update: this program is currently running, and will take under 3 weeks.)
  
* Once I find the answer for N=17:  
	* I'll announce it to everyone including Kevin Gong once the README is cleaned up  
		* Link to Kevin Gong: http://kevingong.com/Polyominoes/Enumeration.html   
		* (Update: I'll announce it more widely once I'm more comfortable with my documentation)
	* I could move on to finding the answer when we say that mirrored shapes are the same  
		* In the worst case, it will take twice as long. In the best case, there's a short-cut.
		* (Update: I'm actually going to try to find the # of rotational symmetries because I think that might lead to a faster way of getting the number of shapes)
		
  
	* If I feel like it, maybe I could find the answer for N=18, but optimistically, that will take 4 months on my machine. Maybe I could hope that someone else does it?
		(Update: I'm just going to hope... The rust program is really fast. I think the rust version will only takes 9 days on a strong machine)

(Update: Discussions of this project mostly happened in these two links: https://github.com/mikepound/cubes/issues/23 and https://github.com/mikepound/opencubes/issues/27 )

## Current Plan that hasn't happened yet
* I might explore an idea I had for an even faster algorithm:
	* I think that we could solve the 2D case pretty easily given that we have the answers to the number of fixed solutions, and that we can hopefully calculate the number of rotational symmetries.
	(I'm thinking about the 2D case because that's the one where it's known until N = 56, and it's a lot simpler than the 3D case.)

* I might explore solving this for 4D spaces, and then 5D, and so on...  
	* I just have to learn how rotations work in 4+D space.
	* Once the # of dimensions is high enough, I might need to conserve space by using hash sets instead of bool arrays. We'll see.
	
* Reread paper about this and make sure that I didn't reinvent the wheel in a worse way:
https://www.sciencedirect.com/science/article/pii/S0012365X0900082X
  
## High-level explanation of the algorithm  
  
### How a slightly more naïve version of it works:  
The key feature of this algorithm is that it doesn't store the already found polycubes.  
Instead, it develops the polycubes in a recursive way while making sure the order of the development respects a simple deterministic breadth-first-search algorithm. Simpler versions of this logic/algorithm can be found in:
* DFSPolyCubeCounterFixed.java, and
* DFSPolyCubeCounterFixed2D.java
TODO: I'll have to explain this in more detail.

Once it finds a polycube shape of the desired size, it runs a function that does a 'race' to figure out if the cube and rotation we're using as a starting point for the polycube results is the 'first' time the polycube would be explored by the simple breadth-first-search algorithm. That may sound impossible, but if you only develop the polycubes in a systematic order that covers all the shapes, and not randomly, there's 'only' 24*N different competitors every time you run the 'race'. (24 is the # of symmetries in 3D and N is the number of cubes that could act as a starting point.)  
  
The space usage is small because it doesn't hold previous configs. It's only O(n^3). It could even be lower if I really wanted it to be, but space isn't the issue.  
Unfortunately, the time taken is still exponential.
  
The time it takes is something like O( A(n) * n^3 * log(n) )  
where 'A(n)' is the number of answers as a function of n.
A(n) seems to be an exponential that multiplies by a number between 8 and 9 every time n is increased by 1.
See: https://oeis.org/A000162

  
#### Explanation of the time:  
  
If we generate all possible breadth-first-search paths and only count the 'race' winners:  
  
For every race winner, It took at most O(n) time to build the shape, there's up to N * 24 paths that will call the getRaceWinner() function and running the race takes N*log(N) time because there's O(n) competitors and the comparison between competitors takes O(log(n)) time.  
  
So that's why the time complexity seems to be:  
  
O( A(n) * N * (N*24) * (n * log(n)) )  
= O( A(n) * n^3 * log(n))  
where A(n) is the number of answers as a function of n. 
  
### Explanation of why it's good to throw away non-first polycube shapes  
  
An improvement that I found by accident is that if we don't delve deeper into breadth-first search paths  
that aren't first, for a complicated reason, we still get the right answer, but faster. The time complexity might still be the same, but we get to save a good amount of time. I haven't figured out if there's an improvement in the time complexity and It doesn't seem like an easy thing to work out. I'll look into this later. 
  
#### Rough Draft of 'proof' for why you could throw those away non-first polycube shapes (I'll need to fill in the details with more definitions and diagrams)  
  
  
Theorem:  
* The race winner for a polycube size N+1 implies the existence of a race winner in polycube size N with the same starting path for N>2.  
* Note that for the base-case of N=1 or 2, there's only 1 answer to work with anyways, so it's trivially true.
  
Follow these step:  
* Start with getting the race winner for a polycube size N+1  
* Remove the last cube (Cend) it used in the race from the polycube shape (and from the breadth-first-search path it took) 
  
  
** Notice that the new path pw is also the race winner for the smaller polycube shape because  
if we took another path from the N+1 polycube and removed the cube (Ce), it would either:  
1) Bog down the performance of the path because Ce was taken from somewhere between the  
beginning and the end, and based on the recursive function I used, that would slow it down.  
  
ex:  
1,4,5:  
to  
1,5  
  
  
2) Disappear because it started with the cube Ce  
3) Speed up slightly because cube Ce was the last cube.  
  
Because the new path pw is case 3, and if there were another path in case 3, (because of the way the race is conducted), pw would still be the race winner.  
  
Therefore, the theorem is true and you could throw away paths that lose the race for polycubes of size n without missing any solutions of size n+1.
  
  
## Lower level explanation of code:  
  
* Tried to reduce memory allocations by having pretty much all the memory declared at the start of the program.
* Decided to be 'wasteful' with space usage by using a bool array that is N^3 in size instead of using a hashset because that meant not constantly allocating memory...  
	* N^3 bits for N=40 is only 32000 bits...  
* Relevant code may be complicated, but it's only around 800 lines.  
* I tried to reduce branching by suggesting that the compiler do arithmetic instead of branching in the getNeighbourIndex() function, and I think the compiler actually understood!
* This probably could be rewritten in C, or assembly for better results. If I were interested in writing C or assembly for optimizations, this would be what I would work on.
* It got better results when it was ported to Rust. See Loïc Damien's code: https://gitlab.com/dzamlo/polycubes  , and also see the PR requests for an impressively fast version of the code.
* I didn't use a profiler, and I just used my intuition. I wouldn't be surprised if an expert can do much better.  
  
* Tricks to precompute a few things:  
	* Precomputed the 6 orthogonal directions to try in order for all 24 symmetries, so the program doesn't have to recalculate it every single time.
	* Precomputed a look-up table for the relevant contenders during the 'race'.
		* This look-up table only works if the root node for all contenders can't be filled up any more, so that's why the condition 'if(numCellsUsedDepth >= NUM_NEIGHBOURS_3D + 1) {' exists.

