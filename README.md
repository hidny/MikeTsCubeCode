# MikeTsCubeCode

# ROUGH DRAFT

## Purpose

Find the answer to N=17
Have fun

## Plan:

* Find the answer to N=17
* If all goes well, I'll have the answer to N=16 by July 22nd 2023, and I'll hopefully confirm that it matches what Kevin Gong said it would be.
(I'm currently 15/15)
* Find N=17 by creating a program that slices up the problem into 1000 digestible pieces.
** For the template I'll be working off of, see: https://github.com/hidny/weirdMathStuff/blob/master/cuboidNetSearch/READ_ME.md
** I might bother to attach a GUI that has the option of stopping the program and explaining a bit about the program
* Once it's set up, I will run the program on 3 cores of my laptop for about 2 weeks and get the answer for N = 17. (I hope)

* Once I find the answer for N=17:
* I might contact Kevin Gong about it once the README is cleaned up
** Link: http://kevingong.com/Polyominoes/Enumeration.html
* I could move on to finding the answer when we say that chiral polyshapes are the same
** In the worst case, it will take twice as long.

* If I feel like it, maybe I could find the answer for N=18, but optimistically, that will take 4 months.

* I might explore solving this for 4D spaces, and then 5D, and so on...
** I just have to learn how rotations work in 4+D space.
** Once the # of dimensions is high enough, I might need to conserve space by using hash sets instead of bool arrays. We'll see.

## High-level explanation

** Reuse some of the 'race' explanation from your previous github post...

### Idea 1

Generate all possible breadth-first-search paths and only count the 'race' winners:

For every race winner, there's up to N*24 paths to check and running the race takes N*log(N) time

So the time complexity seems to be:

O( A(n) * n^3 * log(n))
where A(n) is the number of answers as a function of n.

(The time complexity is probably smaller than C * 8^n)

### Idea 2: throw away non-first polycube shapes

An improvement that I found by accident is that if we don't delve deeper into breadth-first search paths
that aren't first, for a complicated reason, we still get the right answer, but this time, the time complexity is only:

O( A(n) * n^2 * log(n))
We got rid of a factor of n.
(The time complexity is probably smaller than C * 8^n)

* Explanation of the time complexity:
(For every answer, there were up to n answers of size n-1 that would find the answer, and running the race still takes O(n log n) time.)
So: A(n) answer * (n ways it's found) * (n * log n for the race)
= A(n) * n^2 * log(n)

#### Proof for why you could throw those away non-first polycube shapes

Rough sketch of the proof:
* race winner in polycube size N+1 implies existence of race winner in polycube size N for N>2.
* Note that for the base-case of N=1 or 2, there's only 1 answer to work with anyways.

** Take the path the race winner of polycube size N+1 took and remove the last cube (Ce) it used from the polycube (and from the path)
** This new path is also the race winner for the smaller polycube
Why?
If we took another path from the N+1 polycube and removed the cube (Ce), it would only bog down the performance of the path.
TODO: explain the arrayStandard[] and the effect taking away an element has:
ex:
1,4,5:
to
1,5

(unless the path started on Ce...)
If it started on Ce, that path wouldn't even be a contender for the smaller cuboid.


## Lower level explanation of code:

* Tried to reduce memory allocations
* Decided to be 'wasteful' with space usage by using a bool array that N^3 in size instead of using a hashset.
** N^3 bits for N=40 is only 32000 bits...
* Relevant code may be complicated, but it's only around 800 lines.
* Tried to reduce branching
* Hope that the Java compiler figures it out...
* Probably could be rewritten in C or assembly for better results. If I were interested in writing C or assembly for optimizations,
this would be what I would work on.
* I didn't use a profiler, and I just used my intuition. I wouldn't be surprised if an expert can do much better.

* Tricks to precompute a few things:
** Precomputed directions for all 24 symmetries
** Precomputed a look-up table for the relevant contenders during the 'race'.