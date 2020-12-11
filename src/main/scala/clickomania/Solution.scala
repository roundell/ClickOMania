package clickomania

import clickomania.NextMove._

object Solution {

  def getNextMove(grid: List[List[Char]]): MovesList = {
    val blockList = getBlockList(grid)
    val grid_score = blockScoreList(blockList)
    val mBlockCount = multiBlockCount(blockList, 0)

    val movesScores = List(grid_score, grid_score, grid_score, grid_score, grid_score,
      grid_score, grid_score, grid_score, grid_score, grid_score, grid_score)

    var buffer = 3
    var movesDepth = List(5, 2, 2, 2)
    var keepVert = 25
    var keepHor = List(25, 25, 25, 50)
    val movesWidth = 5


    if (mBlockCount < 15) {
      buffer = 10
      movesDepth = List(5, 4, 4, 3)
      keepHor = List(40, 35, 35, 50)
    }
    else if (mBlockCount < 20) {
      buffer = 10
      movesDepth = List(5, 4, 4, 3)
      keepHor = List(35, 35, 35, 50)
    }
    else if (mBlockCount < 25) {
      buffer = 4
      movesDepth = List(5, 4, 3, 3)
      keepHor = List(35, 35, 35, 50)
    }
    else if (mBlockCount < 30) {
      buffer = 4
      movesDepth = List(5, 3, 3, 3)
      keepHor = List(35, 35, 35, 50)
    }
    else if (mBlockCount < 35) {
      buffer = 4
      movesDepth = List(5, 3, 2, 2)
      keepHor = List(30, 25, 25, 50)
    }

    val moves_list = getNextMoveHelper(grid,
      getBestMoveList(grid, movesDepth.head, movesWidth, movesScores, buffer, keepVert),
      movesScores, movesDepth.tail, movesWidth, buffer, keepHor, keepVert)

    moves_list
  }
}
