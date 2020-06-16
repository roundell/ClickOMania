

## Origin of the Project
This project was created as a solution to HackerRank's "Artificial Intelligence > Bot Building > Click-o-Mania" challenge.  The solution can be copy pasted from NextMove.scala using the instructions in the comments at the top of the file.  HackerRank has an excellent description of the challenge [here](https://www.hackerrank.com/challenges/click-o-mania/problem), including some pictures of a simple grid with moves.  I would add one important point: there is a timeout across all HackerRank challenges depending on the language used, which is an essential consideration for this challenge in particular.  Here is a part of the challenge from HackerRank:

```
Clickomania is a 1-player game consisting of a rectangular grid of square blocks, 
each colored in one of k colors. Adjacent blocks horizontally and vertically of 
the same color are considered to be a part of the same group. A move selects a 
group containing at least two blocks and removes those blocks, followed by two 
"falling" rules;

  1. Any blocks remaining above the holes created, fall down through the same column.
  2. Any empty columns are removed by sliding the succeeding columns left.
```

So in my own words, you have a 20x10 grid filled with two (simpler) to six (more complicated) different colours of square.  There are single squares, not touching any other squares of the same colour.  You cannot remove those directly.  You may only remove multi-squares.  The point is to remove multi-squares in a way such that all the single squares get joined to multi-squares before you run out of multi-squares to remove using the two "falling" rules above.  HackerRank's test will request the next move only, not the entire solution at once.

## Reasoning of my Solution
### Kicking the tires
So thinking like a human maybe there are some rules to follow?  Like starting in the top right and try and marry some single squares of the same colour by removing multi-blocks that are in between?  Proud to say I did not spend too much time without figuring a computer thinking like a computer would be a better bet.  Still did revisit those human thoughts a bit later in the scoring section...

### First Try: Python and 2D Array
In each iteration, the code would remove each of the multi-blocks from the grid and test the new grids to see how "well they scored".  Each iteration would then figure out all the multi-blocks in the new grid and take out each of those, testing each new grid.  This solved the first two grids in the test cases quickly and nowhere close on the other two.

I originally wrote the solution in Python with the grid coded as an array.  On first thought I believed there would be mostly coordinate references into the grid and I would want these to be the fastest possible.  Also, who doesn't think of a 2D array when they are faced with a grid of a fixed number of rows and columns.  Anyways, I butted up against the timeout pretty quickly so reasoned there had to be something faster, especially when using deepcopy tens of thousands of times per step.

### Major Change: Scala and Lists
So then I implemented the solution in Scala with the grid as lists.  I devised an algorithm to travel the grid only once to figure out all of the blocks.  Removing a block turned out to be simpler with lists than arrays algorithmically, and way faster since the travel time was related to the size of the block instead of the grid (deepcopy!).  I did some head to head testing between the 2D array and the lists code and although it was significantly faster it was not that much faster per step.  I was still bumping up against the timeout as I tried to increase the number of steps I looked ahead in the code.

### Major efficiencies:
The grids start with 200 squares (10x20) and the five and six-colour test grids start with about 40 multi-blocks.  Without efficiencies, each iteration explodes in the number of grids to test.  The one I implemented from the start was only testing the best five new grids in the next iteration.  I later did some testing on this and found that increasing this to keeping the best ten made no difference in the next move chosen.  Five seemed like the magic number.

Then there is a balance between the number of iterations forward before reducing the number of sequences to explore.  



