package clickomania


object NextMove {

  def main(args: Array[String]) {
    val tokens = scala.io.StdIn.readLine.split(" ")
    val Rows = tokens(0).toInt
    val Columns = tokens(1).toInt
    val Colours = tokens(2).toInt

    val Grid = readGridList(Rows, List())
    getNextMove(Grid)
  }

  type BlockSquares = List[(Int, List[Int])]
  // Squares are recorded by column then row (columns, (rows))
  type Block = (Char, BlockSquares)
  // Add above to the colour to completely define the block
  type BlockList = List[Block]
  type Moves = (Int, List[Int], BlockList)
  type MovesList = List[Moves]

  def getNextMove(Grid: List[List[Char]]): Boolean = {
    val block_list = getBlockList(0, Grid, List())

    var moved = false
    val grid_score = blockScoreList(getBlockList(0, Grid, List()))
    val moves_scores = List(grid_score, grid_score, grid_score, grid_score, grid_score)

    var buffer = 3
    var moves_depth1 = 5
    var moves_depth2 = 2
    var moves_depth3 = 2
    var moves_depth4 = 2
    var moves_width = 5
    var keep1 = 25
    var keep2 = 25
    var keep3 = 25
    var keep4 = 50

    if (grid_score < 50) {
      buffer = 10
      moves_depth2 = 4
      moves_depth3 = 4
      moves_depth4 = 3
      keep1 = 40
      keep2 = 35
      keep3 = 35
    }
    else if (grid_score < 65) {
      buffer = 10
      moves_depth2 = 4
      moves_depth3 = 4
      moves_depth4 = 3
      keep1 = 35
      keep2 = 35
      keep3 = 35
    }
    else if (grid_score < 90) {
      buffer = 4
      moves_depth2 = 4
      moves_depth3 = 3
      moves_depth4 = 3
      keep1 = 35
      keep2 = 35
      keep3 = 35
    }
    else if (grid_score < 110) {
      buffer = 4
      moves_depth2 = 3
      moves_depth3 = 3
      moves_depth4 = 3
      keep1 = 35
      keep2 = 35
      keep3 = 35
    }
    else if (grid_score < 130) {
      buffer = 4
      moves_depth2 = 3
      moves_depth3 = 2
      moves_depth4 = 2
      keep1 = 30
    }

    val moves_list = getBestMoveList(Grid, moves_depth1, moves_width, moves_scores, buffer, keep1)

    var moves_list_round2: MovesList = List()
    var moves_list_round3: MovesList = List()
    var moves_list_round4: MovesList = List()

    if (moves_list.head._1 == 0) {
      println((19 - moves_list.head._3.head._2.head._2.head) + " " + moves_list.head._3.head._2.head._1)
      moved = true
    }
    else {
      for (moves <- moves_list) {
        moves_list_round2 = mergeMovesListMulti(keep2, moves._1, moves._3, moves_list_round2,
          getBestMoveList(removeMovesFromGrid(Grid, moves._3),
            moves_depth2, moves_width, moves_scores, buffer, keep1))

        if ((!moved) && (!moves_list_round2.isEmpty) && (moves_list_round2.head._1 == 0)) {
          println((19 - moves_list_round2.head._3.head._2.head._2.head) + " " + moves_list_round2.head._3.head._2.head._1)
          moved = true
        }
      }
    }
    if (!moved) {
      if (moves_list_round2.isEmpty) {
        println((19 - moves_list.head._3.head._2.head._2.head) + " " + moves_list.head._3.head._2.head._1)
        moved = true
      }
      else {
        for (moves <- moves_list_round2) {
          moves_list_round3 = mergeMovesListMulti(keep3, moves._1, moves._3, moves_list_round3,
            getBestMoveList(removeMovesFromGrid(Grid, moves._3),
              moves_depth3, moves_width, moves_scores, buffer, keep1))

          if ((!moved) && (!moves_list_round3.isEmpty) && (moves_list_round3.head._1 == 0)) {
            println((19 - moves_list_round3.head._3.head._2.head._2.head) + " " + moves_list_round3.head._3.head._2.head._1)
            moved = true
          }
        }
      }
    }
    if (!moved) {
      if (moves_list_round3.isEmpty) {
        println((19 - moves_list_round2.head._3.head._2.head._2.head) + " " + moves_list_round2.head._3.head._2.head._1)
        moved = true
      }
      else {
        for (moves <- moves_list_round2) {
          moves_list_round4 = mergeMovesListMulti(keep4, moves._1, moves._3, moves_list_round4,
            getBestMoveList(removeMovesFromGrid(Grid, moves._3),
              moves_depth4, moves_width, moves_scores, buffer, keep1))

          if ((!moved) && (!moves_list_round4.isEmpty) && (moves_list_round4.head._1 == 0)) {
            println((19 - moves_list_round4.head._3.head._2.head._2.head) + " " + moves_list_round4.head._3.head._2.head._1)
            moved = true
          }
        }
      }
    }
    if (!moved) {
      if (moves_list_round4.isEmpty) {
        println((19 - moves_list_round3.head._3.head._2.head._2.head) + " " + moves_list_round3.head._3.head._2.head._1)
      }
      else {
        println((19 - moves_list_round4.head._3.head._2.head._2.head) + " " + moves_list_round4.head._3.head._2.head._1)
      }
    }
    return true
  }

  def getBestMoveList(G: List[List[Char]], moves_depth: Int, moves_width: Int, move_scores: List[Int],
                      buffer: Int, keep: Int): MovesList = {
    val block_list = getBlockList(0, G, List())
    var best_move_list: MovesList = List()
    var moves_list_this: MovesList = List()
    var new_moves_scores: List[Int] = List()

    for (block <- block_list) {
      if (!isSingleSquare(block._2)) {
        // multi-square block
        val new_grid = removeBlockFromList(G, block)
        val new_block_list = getBlockList(0, new_grid, List())
        val new_block_score = blockScoreList(new_block_list)

        best_move_list = putInMoveList(moves_width, new_block_score, block, best_move_list)

        if (new_block_score == 0) return (best_move_list)
      }
    }

    if (moves_depth > 1) {
      for ((b_score, scores, blocks) <- best_move_list) {
        if (moves_list_this.isEmpty) new_moves_scores = updateMoveScores(List(), move_scores)
        else new_moves_scores = updateMoveScores(moves_list_this.head._2, move_scores)

        if (b_score < new_moves_scores.head + buffer) {
          var moves_list_next = getBestMoveList(removeBlockFromList(G, blocks.head),
            moves_depth - 1, moves_width, new_moves_scores.tail, buffer, keep)
          moves_list_this = mergeMovesListMulti(keep, b_score, blocks, moves_list_this, moves_list_next)

          if ((!moves_list_this.isEmpty) && (moves_list_this.head._1 == 0)) return (moves_list_this)
        }
      }
      if (moves_list_this.isEmpty) return (best_move_list)
      else return (moves_list_this)
    }
    else
      return (best_move_list)
  }

  def updateMoveScores(move_scoresA: List[Int], move_scoresB: List[Int]): List[Int] = move_scoresA match {
    case List() => move_scoresB
    case s :: sL => {
      if (s < move_scoresB.head) s :: updateMoveScores(sL, move_scoresB.tail)
      else move_scoresB.head :: updateMoveScores(sL, move_scoresB.tail)
    }
  }

  def blockScoreList(new_block_list: BlockList): Int = new_block_list match {
    case List() => 0
    case b :: bl =>
      if (isSingleSquare(b._2)) 2 + blockScoreList(bl) // single square is worst
      //else if(isSingleColumn(b._2))   1 + blockScoreList(bl)  // single column is the best (cannot be destroyed)
      else 1 + blockScoreList(bl) // multi column block aint so bad
  }

  def multiBlockCount(block_list: BlockList): Int = block_list match {
    case List() => 0
    case b :: bl =>
      if (isSingleSquare(b._2)) multiBlockCount(bl)
      else 1 + multiBlockCount(bl)
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

  // moves_listB is the list coming from further depth, so must add current block and its score to the elements as they
  //  are merged into the return value
  def mergeMovesListMulti(width: Int, score: Int, block_list: BlockList,
                          moves_listA: MovesList, moves_listB: MovesList): MovesList = moves_listA match {
    case List() => {
      if ((width == 0) || (moves_listB.isEmpty))
        List()
      else (moves_listB.head._1, score :: moves_listB.head._2, block_list ::: moves_listB.head._3) ::
        mergeMovesListMulti(width - 1, score, block_list, List(), moves_listB.tail)
    }
    case m :: mL => {
      if (width == 0) List()
      else {
        if ((moves_listB.isEmpty) || (m._1 <= moves_listB.head._1))
          m :: mergeMovesListMulti(width - 1, score, block_list, mL, moves_listB)
        else (moves_listB.head._1, score :: moves_listB.head._2, block_list ::: moves_listB.head._3) ::
          mergeMovesListMulti(width - 1, score, block_list, moves_listA, moves_listB.tail)
      }
    }
  }

  def readGridList(rows: Int, grid_builder: List[List[Char]]): List[List[Char]] = rows match {
    case 0 => grid_builder
    case 20 => readGridList(rows - 1, zipGridListFirstLine(scala.io.StdIn.readLine().toList))
    case _ => readGridList(rows - 1, zipGridListLine(grid_builder, scala.io.StdIn.readLine().toList))
  }

  def zipGridListLine(grid_builder: List[List[Char]], grid_line: List[Char]): List[List[Char]] = grid_line match {
    case List() => List()
    case x :: xs => {
      if (x == '-') grid_builder.head :: zipGridListLine(grid_builder.tail, xs)
      else (x :: grid_builder.head) :: zipGridListLine(grid_builder.tail, xs)
    }
  }

  def zipGridListFirstLine(grid_line: List[Char]): List[List[Char]] = grid_line match {
    case List() => List()
    case x :: xs => {
      if (x == '-') List() :: zipGridListFirstLine(xs)
      else (x :: List()) :: zipGridListFirstLine(xs)
    }
  }

  def printGridList(grid: List[List[Char]]): Unit = grid match {
    case List() => ()
    case x :: xs =>
      printGridList(xs)
      println(x.mkString)
  }

  // This function chooses the next column from the grid, starting from left = column 0 and resetting row to bottom = 0
  def getBlockList(c: Int, grid: List[List[Char]], block_list: BlockList): BlockList = grid match {
    case List() => block_list
    case column :: grid_tail => getBlockList(c + 1, grid_tail, getBlockListColumn(c, 0, column, block_list))
  }

  // This function chooses the next square (the next row up)
  def getBlockListColumn(c: Int, r: Int, column: List[Char], block_list: BlockList): BlockList = column match {
    case List() => block_list
    case ch :: chL => getBlockListColumn(c, r + 1, chL, addSquare(c, r, ch, block_list))
  }

  // This function searches for, and puts the square in either of the two possible blocks that the square could belong to,
  //  the one beneath it (which must be the first block on the BlockList), and the one to its left.  If the square belongs
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
  def addSquare(c: Int, r: Int, colour: Char, block_list: BlockList): BlockList = {
    addSquareHelper(c, r, colour, false, false, false, block_list, List(), List((c, List(r))))
  }

  def addSquareHelper(c: Int, r: Int, colour: Char, below: Boolean, left: Boolean, hold: Boolean, block_list: BlockList,
                      b_list_hold: BlockList, new_block: BlockSquares): BlockList = block_list match {
    case List() => makeListNewHoldTail((colour, new_block), b_list_hold, block_list)
    case b :: bL => {
      if (b._2.head._1 == c) {
        if ((!below) && (b._2.head._2.indexOf(r - 1) > -1)) {
          if ((!left) && (!b._2.tail.isEmpty) && (b._2.tail.head._2.indexOf(r) > -1)) {
            if (b._1 == colour) (colour, mergeTwoBlocksSquares(b._2, new_block)) :: bL
            else b :: addSquareHelper(c, r, colour, true, true, false, bL, b_list_hold, new_block)
          } // ^ left = true
          else {
            if (left) {
              if (b._1 == colour) makeListNewHoldTail((colour, mergeTwoBlocksSquares(b._2, new_block)), b_list_hold, bL)
              else {
                if (hold) makeListNewHoldTail((colour, new_block), b_list_hold, block_list)
                else b :: addSquareHelper(c, r, colour, true, true, false, bL, b_list_hold, new_block)
              }
            }
            else {
              if (b._1 == colour) addSquareHelper(c, r, colour, true, false, true, bL, b_list_hold,
                mergeTwoBlocksSquares(b._2, new_block))
              else b :: addSquareHelper(c, r, colour, true, false, false, bL, b_list_hold, new_block)
            }
          }
        } // ^ below = true
        else {
          if ((!left) && (!b._2.tail.isEmpty) && (b._2.tail.head._2.indexOf(r) > -1)) {
            if (below) {
              if (b._1 == colour) makeListNewHoldTail((colour, mergeTwoBlocksSquares(b._2, new_block)), b_list_hold, bL)
              else {
                if (hold) makeListNewHoldTail((colour, new_block), b_list_hold, block_list)
                else b :: addSquareHelper(c, r, colour, true, true, false, bL, b_list_hold, new_block)
              }
            }
            else {
              if (b._1 == colour) addSquareHelper(c, r, colour, false, true, true, bL, b_list_hold,
                mergeTwoBlocksSquares(b._2, new_block))
              else b :: addSquareHelper(c, r, colour, false, true, false, bL, b_list_hold, new_block)
            }
          } // ^ left = true
          else {
            if (hold) addSquareHelper(c, r, colour, below, left, true, bL, b :: b_list_hold, new_block)
            else b :: addSquareHelper(c, r, colour, false, false, false, bL, b_list_hold, new_block)
          }
        }
      }
      else {
        if (left) (colour, new_block) :: block_list
        else {
          if (b._2.head._1 == c - 1) {
            if (b._2.head._2.indexOf(r) > -1) {
              if (b._1 == colour) makeListNewHoldTail((colour, mergeTwoBlocksSquares(new_block, b._2)), b_list_hold, bL)
              else makeListNewHoldTail((colour, new_block), b_list_hold, block_list)
            }
            else {
              if (b._2.head._2.head > r)
                makeListNewHoldTail((colour, new_block), b_list_hold, block_list)
              else addSquareHelper(c, r, colour, true, false, true, bL, b :: b_list_hold, new_block)
            }
          } // ^ hold = true
          else makeListNewHoldTail((colour, new_block), b_list_hold, block_list)
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
  //          >> block_squares are saved by column highest to lowest, then by row lowest to highest
  //          >> grid lists are saved column lowest to highest, then row lowest to highest
  def removeBlockFromList(grid: List[List[Char]], block: Block): List[List[Char]] = {
    removeBlockFromListHelper(grid, block._2.reverse, 0)
  }

  def removeBlockFromListHelper(grid: List[List[Char]], block_squares: BlockSquares, colNumber: Int): List[List[Char]] = grid match {
    case List() => List()
    case column :: grid_tail => {
      if (block_squares.isEmpty) grid
      else if (block_squares.head._1 == colNumber) {
        val new_column = removeSquaresFromColumn(column, block_squares.head._2, 0)
        if (new_column.isEmpty) removeBlockFromListHelper(grid_tail, block_squares.tail, colNumber + 1)
        else new_column :: removeBlockFromListHelper(grid_tail, block_squares.tail, colNumber + 1)
      }
      else column :: removeBlockFromListHelper(grid_tail, block_squares, colNumber + 1)
    }
  }

  def removeSquaresFromColumn(column: List[Char], column_squares: List[Int], rowNumber: Int): List[Char] = column match {
    case List() => List()
    case square :: column_tail => {
      if (column_squares.isEmpty) column
      else if (column_squares.head == rowNumber) removeSquaresFromColumn(column_tail, column_squares.tail, rowNumber + 1)
      else square :: removeSquaresFromColumn(column_tail, column_squares, rowNumber + 1)
    }
  }

  def removeMovesFromGrid(G: List[List[Char]], moves: List[Block]): List[List[Char]] = moves match {
    case List() => G
    case b :: bL => removeMovesFromGrid(removeBlockFromList(G, b), bL)
  }
}

