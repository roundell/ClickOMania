

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

So in my own words, you have a 20x10 grid filled with two (simpler) to six (more complicated) different colours of square.  There are single squares, not touching any other squares of the same colour, which you cannot remove directly.  You may only remove multi-square blocks.  The point is to remove multi-square blocks in a way such that all the single squares get joined to multi-square blocks before you run out of multi-square blocks to remove using the two "falling" rules above.  HackerRank's test will request the next move with each execution of your code and iterate by re-executing with the new grid until you have lost (no more multi-square blocks, invalid move, error, etc.) or won (all squares removed).  It will not request the entire solution at once.

## Reasoning of my Solution
### Kicking the tires
So thinking like a human maybe there are some rules to follow?  Like starting in the top right and try and marry some single squares of the same colour by removing multi-blocks that are in between?  Proud to say I did not spend too much time without figuring a computer thinking like a computer would be a better bet.  Still did revisit those human thoughts a bit later in the scoring section...

### First Try: Python and 2D Array
In each iteration, the code would remove each of the multi-blocks from the grid and test the new grids to see how "well they scored".  Each iteration would then figure out all the multi-blocks in the new grid and take out each of those, testing each new grid.  This solved the first two grids in the test cases quickly and nowhere close on the other two.

I originally wrote the solution in Python with the grid coded as an array.  On first thought I believed there would be mostly coordinate references into the grid and I would want these to be the fastest possible.  Also, who doesn't think of a 2D array when they are faced with a grid of a fixed number of rows and columns.  Anyways, I butted up against the timeout pretty quickly so reasoned there had to be something faster, especially when using deepcopy tens of thousands of times per step.

### Major Change: Scala and Lists
So then I implemented the solution in Scala with the grid as lists.  I devised an algorithm to travel the grid only once to figure out all of the blocks.  Removing a block turned out to be simpler with lists than arrays algorithmically, and way faster since the travel time was related to the size of the block instead of the grid (deepcopy!).  I did some head to head testing between the 2D array and the lists code and although it was significantly faster it was not that much faster per step.  I was still bumping up against the timeout as I tried to increase the number of steps I looked ahead in the code.

## Algorithms
There are several algorithms at play in the solution.  The too-simplistic overall pseudo algorithm can be stated: try all possibilities and choose the one that removes all of the squares (ie. wins).  Here are the steps:
1. Represent the grid with lists (list of lists) 
2. Extract a list of all the blocks (multi-square and single-square) from a grid
3. Remove a block from a grid to create a new grid
4. Score a grid
5. Repeat via recursion

### 1. Represent the grid with lists (list of lists)
HackerRank describes the grid with the coordinates (0, 0) at the top left.  But, the falling rules mean that the blocks move down and to the left.  Therefore, to preserve the grid coordinates of the squares (ex. third square in the sixth list) without saving empty cells, the code flips the coordinates upside down so that (0, 0) is found at the bottom left and I do a translation at the very end to the HackerRank coordinate system.  The coordinates are not saved explicitly, they are found by the position of the cell in the list (row) and the position of the list in the list of lists (column).

Also, it is the columns I represent as lists, again because of the falling rules that the squares fall down.  The squares fall left only when an entire column is empty.  This creates an efficiency in removing blocks.  

### 2. Extract a list of all the blocks (multi-square and single-square) from a grid

Each square will belong to a block and be represented in the list of blocks, even if it is as a single square.  The blocks are NOT ordered the same as the grid.  They are ordered by the block's rightmost and then lowest square.  This is an efficiency in how the code is creating the block list.

The code travels (builds) the grid as it is represented: up each column (along each list).  The grid is built square by square while a complete block list is updated at each iteration.  Each square can belong to one or two blocks that are already existing in the block list (merge) or none (create a new single-square block).  It cannot belong to any block to its right or above because those squares are not added yet.  It can belong to the block that contains the square below and/or the block that contains the square to its left, if colours of those squares match the current.  Either of these two blocks may not exist if the current square is in the bottom row, left column or if the column to the left is shorter (has fewer rows) than the current square's row.

### (more work to be done on this section) Major efficiencies:
The grids start with 200 squares (10x20) and the five and six-colour test grids start with about 40 multi-blocks.  Without efficiencies, each iteration explodes in the number of grids to test.  The one I implemented from the start was only testing the best five new grids in the next iteration.  I later did some testing on this and found that increasing this to keeping the best ten made no difference in the next move chosen.  Five seemed like the magic number.

Then there is a balance between the number of iterations forward before reducing the number of sequences to explore.  



