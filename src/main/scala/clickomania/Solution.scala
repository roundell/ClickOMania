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

  def getNextMoveHelper(grid: List[List[Char]], movesList: MovesList, movesScores: List[Int],
                        movesDepth: List[Int], movesWidth: Int, buffer: Int,
                        keepHor: List[Int], keepVert: Int): MovesList = {
    if (movesDepth.isEmpty) return movesList

    var movesListNext: MovesList = List()
    var movesScoresNext = movesScores

    for (moves <- movesList) {
      movesListNext = mergeMovesListMulti(keepHor.head, moves._1, moves._2, moves._3, movesListNext,
        getBestMoveList(removeMovesFromGrid(grid, moves._3),
          movesDepth.head, movesWidth, movesScoresNext, buffer, keepVert))

      if ((movesListNext.nonEmpty) && (movesListNext.head._1 == 0))
        return movesListNext
    }

    if (movesListNext.isEmpty)
      movesList
    else if (movesDepth.tail.isEmpty)
      movesListNext
    else
      getNextMoveHelper(grid, movesListNext, movesScores, movesDepth.tail, movesWidth, buffer,
        keepHor.tail, keepVert)
  }

  def getBestMoveList(grid: List[List[Char]], moves_depth: Int, moves_width: Int, move_scores: List[Int],
                      buffer: Int, keep: Int): MovesList = {
    val blockList = getBlockList(grid)
    var best_move_list: MovesList = List()
    var moves_list_this: MovesList = List()
    var new_moves_scores: List[Int] = List()


    for (block <- blockList) {
      if (!isSingleSquare(block._2)) {
        // multi-square block
        val newGrid = removeBlockFromList(grid, block)
        val new_block_list = getBlockList(newGrid)
        val new_block_score = blockScoreList(new_block_list)

        best_move_list = putInMoveList(moves_width, new_block_score, block, best_move_list)

        if (new_block_score == 0) return best_move_list
      }
    }

    if (moves_depth > 1) {
      for ((b_score, scores, blocks) <- best_move_list) {
        if (moves_list_this.isEmpty) new_moves_scores = updateMoveScores(List(), move_scores)
        else new_moves_scores = updateMoveScores(moves_list_this.head._2, move_scores)

        if (b_score < new_moves_scores.head + buffer) {
          val moves_list_next = getBestMoveList(removeBlockFromList(grid, blocks.head),
            moves_depth - 1, moves_width, new_moves_scores.tail, buffer, keep)
          moves_list_this = mergeMovesListMulti(keep, b_score, scores, blocks, moves_list_this, moves_list_next)

          if ((!moves_list_this.isEmpty) && (moves_list_this.head._1 == 0)) return moves_list_this
        }
      }
      if (moves_list_this.isEmpty)
        best_move_list
      else
        moves_list_this
    }
    else
      best_move_list
  }
}
