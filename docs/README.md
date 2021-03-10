## Background
I coded 100% of this scala project.  I used no templates and copied no code.  The code organization, data structure and accompanying algorithm design is completely my own.  I learned scala from Coursera's Scala "specialization" courses by École Polytechnique Fédérale de Lausanne and used the course material and assignments as inspiration, as well, of course, as stackoverflow and every Scala blog on the internet.

I chose to code and open source this project to show that I am skilled in code organization and design of data structures and alogorithms.  I also chose this project to show my skill in functional and object-oriented programming.  There are no vars in the entire codebase.  I coded the project using Test Directed Development, creating Unit Tests to cover and pass each unit of code before moving on to develop the next unit.

I also chose this project because it was a very fun one to solve.  HackerRank gives out a whopping 80 points to solve it!  Though it took much longer than 8x any of the regular coding exercises.

### Current Functionality
When running "ClickOMania" you will receive a prompt to play a few grids from a library or enter your own.  The grids are not all 20x10 as described below in the Problem Description but are played the same way.  Future iterations will demonstrate the program's ability to solve the difficult grids from HackerRank, as well as a fast game style with small solveable grids.  

## Origin of the Project
This project was created as a solution to HackerRank's "Artificial Intelligence > Bot Building > Click-o-Mania" challenge.  While it was originally coded such that it could be copied back to HackerRank, after achieving full marks I lost interest in keeping it in one page format and reformatted it to the current multi-file project design.

### Problem Description
HackerRank has an excellent description of the challenge [here](https://www.hackerrank.com/challenges/click-o-mania/problem), including some pictures of a simple grid with moves.  I would add one important point: there is a timeout across all HackerRank challenges depending on the language used, which is an essential consideration for this challenge in particular.  Here is a part of the challenge from HackerRank:

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
## Algorithms
There are several algorithms at play in the solution.  The too-simplistic overall pseudo algorithm can be stated: try all possibilities and choose the one that removes all of the squares (ie. wins).  Here are the steps:
1. Represent the grid with lists (list of lists) 
2. Extract a list of all the blocks (multi-square and single-square) from a grid
3. With each multi-square block from 2. create a new grid by 
    i)  removing it from the original grid and 
    ii) scoring it and keeping the resulting grids in order of their score
4. Repeat via recursion with all of the new grids

The algorithms described below are very high level.  For details, look at the code!

### 1. Represent the grid with lists (list of lists)
HackerRank describes the grid with the coordinates (0, 0) at the top left.  But, the falling rules mean that the squares move down and to the left.  Therefore, to preserve the grid coordinates of the squares (ex. third square in the sixth list) without saving empty cells, the code flips the coordinates upside down so that (0, 0) is found at the bottom left and I do a translation if needed for HackerRank or for future display purposes.  The coordinates are not saved explicitly, they are found by the position of the cell in the list (row) and the position of the list in the list of lists (column).

Also, it is the columns I represent as lists, again because of the falling rules that the squares fall down.  The squares fall left only when an entire column is empty.  This creates an efficiency when removing squares.  

### 2. Extract a list of all the blocks (multi-square and single-square) from a grid

Each square will belong to a block and be represented in the list of blocks, even if it is as a single square.  The blocks are NOT ordered the same as the grid.  They are ordered by the block's rightmost and then highest square.  This is an efficiency in how the code is creating the block list.

The code travels (builds) the grid as it is represented: up each column (along each list).  The grid is built square by square while the complete block list is updated at each iteration.  Each square can belong to one or two blocks that are already existing in the block list (merge) or none (create a new single-square block).  It cannot belong to any block to its right or above because those squares are not added yet.  It can belong to the block that contains the square below and/or the block that contains the square to its left, if colours of those squares match the current.  Either of these two blocks may not exist if the current square is in the bottom row, left column or if the column to the left is shorter (has fewer rows) than the current square's row.

### 3i. Remove a block from a grid

Take a block from the list described in 2. and remove it from the grid represented in 1. to create a new list represented as in 1.  Check the columns then the rows match then remove.

### 3ii. Score a grid

This I am fuzzy on.  I scored multi-square blocks as 1 and single-square blocks as 2 and got the best results for HackerRank's grids and some others I tried.  I played around with counting single-square blocks worse and even indestructible blocks as 0 (ones that cannot be split apart such that any of its component squares would end up as a single-square block).  Nothing produced as good of results though I sometimes revisit when I get new ideas.

### Not quite all possibilities

So the code only keeps a certain number of grids on each iteration.  The starting grids have 10x20 squares and so contain about 40 multi-square blocks.  Winning solutions take dozens of iterations to reduce the number of multi-square blocks down to 30, and 50 or more moves to solve the grid.  There are obviously way too many paths to try!


