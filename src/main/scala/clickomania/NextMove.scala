package clickomania

/** To execute the code in HACKERRANK:
 * 1. set the language of the current buffer to Scala
 * 2. copy this entire file below this comment block (must remove package declaration above)
 * 3. rename object to "Solution"
 * */

object NextMove {

  type BlockSquares = List[(Int, List[Int])] // Squares are recorded by column then row: (c #, (r #s)), (c #2, (r #s)), ...
  type Block = (Char, BlockSquares) // Add above to the colour to completely define the block
  type BlockList = List[Block] // A list of blocks >> could be entire grid as blocks
  //                  >> could be sequence of blocks removed from a grid
  type Moves = (Int, List[Int], BlockList) // Sequence of moves (Blocklist) with scores of each move (List[Int])
  //    and final score (Int)
  type MovesList = List[Moves] // List of move sequences sorted by final scores: lowest (best) score first


  def main(args: Array[String]) {
    val tokens = scala.io.StdIn.readLine.split(" ")
    val rows = tokens(0).toInt
    // columns = tokens(1).toInt
    //            Not needed since the readGrid function
    //            does not require number of columns to be specified
    // colours = tokens(2).toInt
    //            At this time not used

    val grid = readGrid(rows)
    val movesList = Solution.getNextMove(grid)
    printNextMove(movesList)
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
      return movesList
    else if (movesDepth.tail.isEmpty)
      return movesListNext
    else
      return getNextMoveHelper(grid, movesListNext, movesScores, movesDepth.tail, movesWidth, buffer,
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
      if (moves_list_this.isEmpty) return best_move_list
      else return moves_list_this
    }
    else
      return best_move_list
  }

  def updateMoveScores(move_scoresA: List[Int], move_scoresB: List[Int]): List[Int] = move_scoresA match {
    case List() => move_scoresB
    case s :: sL =>
      if (move_scoresB.nonEmpty) {
        if (s < move_scoresB.head) s :: updateMoveScores(sL, move_scoresB.tail)
        else move_scoresB.head :: updateMoveScores(sL, move_scoresB.tail)
      }
      else move_scoresA
  }

  def updateMovesScoresWith0or1(movesScores: List[Int], buffer: Int): List[Int] = movesScores match {
    case List() => List()
    case s :: sl =>
      if (s < 2) (s - buffer) :: sl
      else s :: updateMovesScoresWith0or1(sl, buffer)
  }

  def blockScoreList(blockList: BlockList): Int = {
    blockScoreListHelper(blockList, Map(), 0)
  }

  def blockScoreListHelper(blockList: BlockList, colourSafe: Map[Char, Int], score: Int): Int = blockList match {
    case List() => score + (100 * colourSafe.foldLeft(0)(_ + _._2))
    case b :: bl =>
      if (isSingleSquare(b._2)) { // single square is worst
        if (colourSafe.contains(b._1))
          blockScoreListHelper(bl, colourSafe + (b._1 -> 0), score + 2)
        else
          blockScoreListHelper(bl, colourSafe + (b._1 -> 1), score + 2)
      }
      else blockScoreListHelper(bl, colourSafe + (b._1 -> 0), score + 1) // multi column block aint so bad
  }

  def multiBlockCount(blockList: BlockList, multiCount: Int): Int = blockList match {
    case List() => multiCount
    case b :: bl =>
      if (isSingleSquare(b._2)) multiBlockCount(bl, multiCount)
      else multiBlockCount(bl, 1 + multiCount)
  }

  def singleBlockCount(blockList: BlockList, singleCount: Int): Int = blockList match {
    case List() => singleCount
    case b :: bl =>
      if (isSingleSquare(b._2)) singleBlockCount(bl, 1 + singleCount)
      else singleBlockCount(bl, singleCount)
  }

  def isSingleSquare(new_block_sq: BlockSquares): Boolean = {
    if (new_block_sq.head._2.tail.isEmpty && new_block_sq.tail.isEmpty) true
    else false
  }

  def isSingleColumn(new_block_sq: BlockSquares): Boolean = {
    if (new_block_sq.tail.isEmpty) true
    else false
  }

  def putInMoveList(width: Int, score: Int, block: Block, moves_list: MovesList): MovesList = moves_list match {
    case List() => {
      if (width == 0) List()
      else List((score, List(score), List(block)))
    }
    case m :: mL => {
      if (width == 0) List()
      else {
        if (score < m._1) (score, List(score), List(block)) :: putInMoveList(width - 1, m._1, m._3.head, mL)
        else (m._1, List(m._1), List(m._3.head)) :: putInMoveList(width - 1, score, block, mL)
      }
    }
  }

  // moves_listB is the list coming from further depth, so must add current block and its score to the
  // elements as they are merged into the return value
  // What do I do about equal scores?  do i care if they aren't 0?

  def mergeMovesListMulti(width: Int, score: Int, scores: List[Int], block_list: BlockList,
                          moves_listA: MovesList, moves_listB: MovesList): MovesList = moves_listA match {
    case List() => {
      if ((width == 0) || (moves_listB.isEmpty))
        List()
      else (moves_listB.head._1, scores ::: moves_listB.head._2, block_list ::: moves_listB.head._3) ::
        mergeMovesListMulti(width - 1, score, scores, block_list, List(), moves_listB.tail)
    }
    case m :: mL => {
      if (width == 0) List()
      else {
        if ((moves_listB.isEmpty) || (m._1 <= moves_listB.head._1))
          m :: mergeMovesListMulti(width - 1, score, scores, block_list, mL, moves_listB)
        else (moves_listB.head._1, scores ::: moves_listB.head._2, block_list ::: moves_listB.head._3) ::
          mergeMovesListMulti(width - 1, score, scores, block_list, moves_listA, moves_listB.tail)
      }
    }
  }

  // The following four functions read the grid row by row from the top down and flip it in to
  //  a list of lists that represent columns from the bottom up, left to right
  def readGrid(rows: Int): List[List[Char]] =
    readGridHelper(rows - 1, zipGridFirstLine(scala.io.StdIn.readLine().toList))

  def readGridHelper(rows: Int, gridBuilder: List[List[Char]]): List[List[Char]] = rows match {
    case 0 => gridBuilder
    case _ => readGridHelper(rows - 1, zipGridLine(gridBuilder, scala.io.StdIn.readLine().toList))
  }

  def zipGridLine(gridBuilder: List[List[Char]], gridLine: List[Char]): List[List[Char]] = gridLine match {
    case List() => List()
    case ch :: chL => {
      if (ch == '-') gridBuilder.head :: zipGridLine(gridBuilder.tail, chL)
      else (ch :: gridBuilder.head) :: zipGridLine(gridBuilder.tail, chL)
    }
  }

  def zipGridFirstLine(gridLine: List[Char]): List[List[Char]] = gridLine match {
    case List() => List()
    case ch :: chL => {
      if (ch == '-') List() :: zipGridFirstLine(chL)
      else (ch :: List()) :: zipGridFirstLine(chL)
    }
  }

  def printNextMove(movesList: MovesList): Unit = {
    println((19 - movesList.head._3.head._2.head._2.head) + " " + movesList.head._3.head._2.head._1)
  }

  def printGridList(grid: List[List[Char]]): Unit = grid match {
    case List() => ()
    case x :: xs =>
      printGridList(xs)
      println(x.mkString)
  }


  // This set of functions includes:
  //  getBlockList, getBlockListRow, getBlockListColumn // functions to travel the grid square by square up the columns left to right
  //  addSquare, addSquareHelper // functions to find the block(s) the square belongs to
  //  makeListNewHoldTail, mergeTwoBlocksSquares, prependTwoLists // functions to add the squares to its block
  // This function initializes the set of functions which will return a list of all the blocks that make up a grid
  def getBlockList(grid: List[List[Char]]): BlockList = getBlockListRow(0, grid, List())

  // This function chooses the next column from the grid, starting from left = column 0 and resetting row to bottom = 0
  def getBlockListRow(col: Int, grid: List[List[Char]], blockList: BlockList): BlockList = grid match {
    case List() => blockList
    case column :: gridTail => getBlockListRow(col + 1, gridTail, getBlockListColumn(col, 0, column, blockList))
  }

  // This function chooses the next square in the column (the next row up)
  def getBlockListColumn(col: Int, row: Int, column: List[Char], blockList: BlockList): BlockList = column match {
    case List() => blockList
    case ch :: chL => getBlockListColumn(col, row + 1, chL, addSquare(col, row, ch, blockList))
  }

  // This function searches for, and puts the square in either of the two possible blocks that the square could belong to,
  //  the one beneath it, and the one to its left.  If the square belongs
  //  to the two different blocks, these two blocks must be merged in the place of the first block

  //  NB: this square's block goes in the place of the last block with squares in its column (ahead of all those blocks that
  //      do not have block in its column) unless the block it belongs to has other squares in its column (then keeps that place)
  //
  //
  //  The block tested could be:
  //  0: new_block :: b_list
  //  =column:
  //      !below && row-1:
  //          set below = true
  //          !left && column-1 & row:
  //              set left = true
  //              same colour:
  //                  return (merge new_block to current) :: b_list               // found both in this block and same colour
  //              else
  //                  prepend current                                             // found both in this block and NOT same colour
  //          else:
  //              if left=true:
  //                  same colour
  //                      return (merge new_block to current) :: hold? :: b_list  // found below and same colour, left previously passed
  //                  else
  //                      if hold=true
  //                          return (new_block) :: hold? :: b_list               // found below and NOT same colour, left was same colour
  //                      else
  //                          prepend current                                     // found below and NOT same colour, left was NOT same colour
  //              else
  //                  same colour
  //                      merge current with new_block; set hold = true           // found below and same colour, left not yet passed
  //                  else
  //                      prepend current                                         // found below and NOT same colour, left not yet passed
  //      else
  //          !left && column-1 & row:
  //              set left = true
  //              if below=true
  //                  same colour:
  //                      return (merge new_block to current) :: hold? :: b_list  // found left and same colour, below previously passed
  //                  else
  //                      if hold=true
  //                          return (new_block) :: hold? :: b_list               // found left and NOT same colour, below was same colour
  //                      else
  //                          prepend current                                     // found left and NOT same colour, below was NOT same colour
  //              else:
  //                  same colour:
  //                      merge current with new_block; set hold = true           // found left and same colour, below not yet passed
  //                  else
  //                      prepend current                                         // found left and NOT same colour, below not yet passed
  //          else
  //              if hold=true
  //                  hold current                                                // unmatching block in column and below or left matched
  //              else
  //                  prepend current                                             // unmatching block in column and below and left unmatched
  //  else
  //      set hold = true
  //      if left=true
  //          return new_block :: hold? :: b_list                                 // left and below already passed unmatched, now add square
  //      column-1:   // current block has no squares in current column, so new_block must be appended before any of these
  //          row:    //  and also below = true
  //              same colour:
  //                  return (merge new_block to current) :: hold? :: b_list      // found left and same colour, below previously passed
  //              else
  //                  return new_block :: hold? :: b_list                         // found left and NOT same colour, below previously passed
  //          else:
  //              hold current                                                    // unmatching block in column-1, <row, left not yet found
  //      else
  //          return new_block :: hold? :: b_list                                 // no left exists: column-1 shorter than row


  //  i. a non-matching block in the same column before we have found the below or left squares > these blocks are simply prepended
  //  ii. a block with the below or left square
  //              matching: > current square is merged with this block and the block is held
  //              non-matching: > simply prepend
  //  iii. a non-matching block on the same column after we found the non-matching below or left square > simply prepend
  //  iv. a non-matching block on the same or next column after we found the matching below or left square > these blocks must be held
  //  v. a second block either the below or left square
  //              matching:
  //                  > this block must be merged with block held from ii.
  //                  > result must be prepended to blocks held from iii.
  //                  > which must be prepended to the rest of the blocks
  //              non-matching:
  //                  > same as matching but don't merge this block in, just use block held from ii.
  def addSquare(col: Int, row: Int, colour: Char, blockList: BlockList): BlockList = {
    addSquareHelper(col, row, colour, false, false, false, blockList, List(), List((col, List(row))))
  }

  def addSquareHelper(col: Int, row: Int, colour: Char, below: Boolean, left: Boolean, hold: Boolean, blockList: BlockList,
                      blockListHold: BlockList, newBlock: BlockSquares): BlockList = blockList match {
    case List() => makeListNewHoldTail((colour, newBlock), blockListHold, blockList)
    case b :: bL => {
      if (b._2.head._1 == col) {
        if ((!below) && (b._2.head._2.indexOf(row - 1) > -1)) {
          if ((!left) && (!b._2.tail.isEmpty) && (b._2.tail.head._2.indexOf(row) > -1)) {
            if (b._1 == colour) (colour, mergeTwoBlocksSquares(b._2, newBlock)) :: bL
            else b :: addSquareHelper(col, row, colour, true, true, false, bL, blockListHold, newBlock)
          } // ^ left = true
          else {
            if (left) {
              if (b._1 == colour) makeListNewHoldTail((colour, mergeTwoBlocksSquares(b._2, newBlock)), blockListHold, bL)
              else {
                if (hold) makeListNewHoldTail((colour, newBlock), blockListHold, blockList)
                else b :: addSquareHelper(col, row, colour, true, true, false, bL, blockListHold, newBlock)
              }
            }
            else {
              if (b._1 == colour) addSquareHelper(col, row, colour, true, false, true, bL, blockListHold,
                mergeTwoBlocksSquares(b._2, newBlock))
              else b :: addSquareHelper(col, row, colour, true, false, false, bL, blockListHold, newBlock)
            }
          }
        } // ^ below = true
        else {
          if ((!left) && (!b._2.tail.isEmpty) && (b._2.tail.head._2.indexOf(row) > -1)) {
            if (below) {
              if (b._1 == colour) makeListNewHoldTail((colour, mergeTwoBlocksSquares(b._2, newBlock)), blockListHold, bL)
              else {
                if (hold) makeListNewHoldTail((colour, newBlock), blockListHold, blockList)
                else b :: addSquareHelper(col, row, colour, true, true, false, bL, blockListHold, newBlock)
              }
            }
            else {
              if (b._1 == colour) addSquareHelper(col, row, colour, false, true, true, bL, blockListHold,
                mergeTwoBlocksSquares(b._2, newBlock))
              else b :: addSquareHelper(col, row, colour, false, true, false, bL, blockListHold, newBlock)
            }
          } // ^ left = true
          else {
            if (hold) addSquareHelper(col, row, colour, below, left, true, bL, b :: blockListHold, newBlock)
            else b :: addSquareHelper(col, row, colour, false, false, false, bL, blockListHold, newBlock)
          }
        }
      }
      else {
        if (left) (colour, newBlock) :: blockList
        else {
          if (b._2.head._1 == col - 1) {
            if (b._2.head._2.indexOf(row) > -1) {
              if (b._1 == colour) makeListNewHoldTail((colour, mergeTwoBlocksSquares(newBlock, b._2)), blockListHold, bL)
              else makeListNewHoldTail((colour, newBlock), blockListHold, blockList)
            }
            else {
              if (b._2.head._2.head > row)
                makeListNewHoldTail((colour, newBlock), blockListHold, blockList)
              else addSquareHelper(col, row, colour, true, false, true, bL, b :: blockListHold, newBlock)
            }
          } // ^ hold = true
          else makeListNewHoldTail((colour, newBlock), blockListHold, blockList)
        }
      }
    }
  }

  //  Combine the reversal of the hold list with prepending it to the rest of the block list and prepending new_block to that
  // acc: 1               1                       1
  // hold: 5, 4, 3, 2     4, 3, 2                 List()
  // rest: 6, 7, 8        5, 6, 7, 8      ...     2, 3, 4, 5, 6, 7, 8
  def makeListNewHoldTail(new_block: Block, b_list_hold: BlockList, b_list_rest: BlockList): BlockList = b_list_hold match {
    case List() => new_block :: b_list_rest
    case b :: bLhold => makeListNewHoldTail(new_block, bLhold, b :: b_list_rest)
  }

  // This function merges two BlockSquares (list of squares that make up a block) into one
  def mergeTwoBlocksSquares(b1: BlockSquares, b2: BlockSquares): BlockSquares = b1 match {
    case List() => b2
    case b :: bl => {
      if (b2.isEmpty) b1
      else if (b._1 > b2.head._1) b :: mergeTwoBlocksSquares(bl, b2)
      else (b._1, prependTwoLists(b._2, b2.head._2)) :: mergeTwoBlocksSquares(bl, b2.tail)
    }
  }

  def prependTwoLists(list1: List[Int], list2: List[Int]): List[Int] = list1 match {
    case List() => list2
    case e :: el => e :: prependTwoLists(el, list2)
  }

  //  Remove the block's squares from the grid
  //      >> block squares are saved by column highest to lowest, then by row lowest to highest
  //      >> grid lists are saved column lowest to highest, then row lowest to highest
  //            >> Therefore: reverse the block's squares to make them same order
  def removeBlockFromList(grid: List[List[Char]], block: Block): List[List[Char]] = {
    removeBlockFromListHelper(grid, block._2.reverse, 0)
  }

  // TODO: Clean up the val, there must be a better way to evaluate and pass
  def removeBlockFromListHelper(grid: List[List[Char]], blockSquares: BlockSquares, colNumber: Int): List[List[Char]] = grid match {
    case List() => List()
    case col :: colList => {
      if (blockSquares.isEmpty) grid
      else if (blockSquares.head._1 == colNumber) {
        val newColumn = removeSquaresFromColumn(col, blockSquares.head._2, 0)
        if (newColumn.isEmpty) removeBlockFromListHelper(colList, blockSquares.tail, colNumber + 1)
        else newColumn :: removeBlockFromListHelper(colList, blockSquares.tail, colNumber + 1)
      }
      else col :: removeBlockFromListHelper(colList, blockSquares, colNumber + 1)
    }
  }

  def removeSquaresFromColumn(gridColumn: List[Char], blockColumn: List[Int], rowNumber: Int): List[Char] = gridColumn match {
    case List() => List()
    case square :: columnTail => {
      if (blockColumn.isEmpty) gridColumn
      else if (blockColumn.head == rowNumber) removeSquaresFromColumn(columnTail, blockColumn.tail, rowNumber + 1)
      else square :: removeSquaresFromColumn(columnTail, blockColumn, rowNumber + 1)
    }
  }

  def removeMovesFromGrid(grid: List[List[Char]], moves: List[Block]): List[List[Char]] = moves match {
    case List() => grid
    case b :: bL => removeMovesFromGrid(removeBlockFromList(grid, b), bL)
  }
}
