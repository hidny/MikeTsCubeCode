
# MikeTsCubeCode  
  
# ROUGH DRAFT  
  
## Purpose of program  
  
* Find the number of distinct polycube shapes with the number of cubes equal to 17, and then take it further.
  
## Plan:  
  
* Finding the answer to N=17:
	* If everything goes well, I'll have the answer to N=16 by July 22nd 2023, and I'll hopefully confirm that it matches what Kevin Gong said it would be.  
(I'm currently 15 correct out of 15 trials)  
	* Find N=17 by creating a program that slices up the problem into 1000ish digestible pieces, and running multiple CPUs on it at the same time.  
	* To see the template I'll be working off of, see: https://github.com/hidny/weirdMathStuff/blob/master/cuboidNetSearch/READ_ME.md  
		* I might bother to attach a GUI that has the option of stopping the program and explaining a bit about the program  
		* Once it's set up, I will run the program on 3 cores of my laptop for about 2 weeks and get the answer for N = 17. (I hope)  
  
* Once I find the answer for N=17:  
	* I'll announce it to everyone including Kevin Gong once the README is cleaned up  
		* Link to Kevin Gong: http://kevingong.com/Polyominoes/Enumeration.html  
	* I could move on to finding the answer when we say that mirrored shapes are the same  
		* In the worst case, it will take twice as long. In the best case, there's a short-cut.  
  
	* If I feel like it, maybe I could find the answer for N=18, but optimistically, that will take 4 months on my machine.  
  
* I might explore solving this for 4D spaces, and then 5D, and so on...  
	* I just have to learn how rotations work in 4+D space.  
	* Once the # of dimensions is high enough, I might need to conserve space by using hash sets instead of bool arrays. We'll see.  
  
## High-level explanation of the algorithm  
  
### How a slightly more naïve version of it works:  
The key feature of this algorithm is that it doesn't store the already found polycubes.  
Instead, it develops the polycubes in a recursive way while making sure the order of the development respects a simple deterministic breadth-first-search algorithm.  
Once it finds a polycube shape of the desired size, it runs a function that does a 'race' to figure out if the cube and rotation we're using as a starting point for the polycube results is the 'first' time the polycube would be explored by the simple breadth-first-search algorithm. That may sound impossible, but if you only develop the polycubes in a systematic order that covers all the shapes, and not randomly, there's 'only' 24*N different competitors every time you run the 'race'. (24 is the # of symmetries in 3D and N is the number of cubes that could act as a starting point.)  
  
The space usage is small because it doesn't hold previous configs. It's only O(n^3). It could even be lower if I really wanted it to be, but space isn't the issue.  
Unfortunately, the time taken is still exponential, and it's not even a 'nice' exponential (see Appendix A).  
  
The time it takes is something like O( A(n) * n^2 * log(n) )  
where 'A(n)' is the number of answers as a function of n  
  
#### Explanation of the time:  
  
If we generate all possible breadth-first-search paths and only count the 'race' winners:  
  
For every race winner, there's up to N*24 paths that will call the getRaceWinner() function and running the race takes N*log(N) time because there O(n) competitors and the comparison between competitors takes O(log(n)) time  
  
So that's why the time complexity seems to be:  
  
O( A(n) * (N*24) * (n * log(n)) )  
= O( A(n) * n^2 * log(n))  
where A(n) is the number of answers as a function of n. 
  
### Explanation of why it's good to throw away non-first polycube shapes  
  
An improvement that I found by accident is that if we don't delve deeper into breadth-first search paths  
that aren't first, for a complicated reason, we still get the right answer, but faster. The time complexity is still the same, but we get to save a good amount of time.  
  
#### Proof for why you could throw those away non-first polycube shapes  
  
Rough sketch of the proof: (I'll need to fill in the details with more definitions and diagrams)  
  
Theorem:  
* The race winner for a polycube size N+1 implies the existence of a race winner in polycube size N with the same starting path for N>2.  
* Note that for the base-case of N=1 or 2, there's only 1 answer to work with anyways.  
  
Follow these step:  
* Start with any race winner for for a polycube size N+1  
* Remove the last cube (Cend) it used from the polycube (and from the breadth-first-search path it took)  
  
  
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
  
Because the new path pw is obviously case 3, and if there were another path in case 3, (because of the way the race is conducted), pw would still be the race winner.  
  
Therefore, the theorem is true and you could throw away paths that lose the race for polycubes of size n without missing any solutions of size n+1.
  
  
## Lower level explanation of code:  
  
* Tried to reduce memory allocations by having pretty much all the memory declared at the start of the program.
* Decided to be 'wasteful' with space usage by using a bool array that is N^3 in size instead of using a hashset because that meant not constantly allocating memory..  
	* N^3 bits for N=40 is only 32000 bits...  
* Relevant code may be complicated, but it's only around 800 lines.  
* I tried to reduce branching by suggesting that the compiler do arithmetic instead of branching in the getNeighbourIndex() function and I think the compiler actually understood!  
* Probably could be rewritten in C or assembly for better results. If I were interested in writing C or assembly for optimizations, this would be what I would work on.

* It got better results when it was ported to Rust. See Loïc Damien's code: https://gitlab.com/dzamlo/polycubes  
* I didn't use a profiler, and I just used my intuition. I wouldn't be surprised if an expert can do much better.  
  
* Tricks to precompute a few things:  
** Precomputed the 6 orthogonal directions to try in order for all 24 symmetries  
** Precomputed a look-up table for the relevant contenders during the 'race'.  
  
  
## Appendix A: A(n) is greater than a 'nice' exponential: (rough sketch)  
  
Proof by contradiction:  
  
Suppose A(n) < c * b^n for some constants b and c and for all n > n0  
  
Now make a 'table' with b+2 legs and O(b) cubes for the table part.  
  
If you only extend the shape by adding to the legs, you will have b+2 options every time you want to add a cube.  
Therefore, A(n) > c2 * (b+2) ^n for some constant c and for all n > n0  
  
But because c2 * (b+2) ^n > c * b^n for all n > n0 (for some n0), we have a contradiction.  
Therefore, A(n) can't be contained by a simple exponential.  
Therefore, A(n) > c * b^n for constants b and c for all n > n0  
  
Note that we could also make the 'table' 2D, so this proof also works in 2D.