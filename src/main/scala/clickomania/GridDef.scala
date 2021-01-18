package clickomania

trait GridDef {

  type GridSquares = List[List[Char]] // representation of a grid as the individual squares
  type GridBlocks = BlockList // representation of a grid as blocks

  type BlockSquares = List[(Int, List[Int])] // Squares are recorded by column then row: (c #, (r #s)), (c #2, (r #s)), ...
  //type Block = (Char, BlockSquares) // Add above to the colour to completely define the block
  type BlockList = List[Block] // A list of blocks could be 1. entire grid as blocks or 2. could be sequence of blocks removed from a grid

  type Moves = (Int, List[Int], BlockList) // Sequence of moves (Blocklist) with scores of each move (List[Int]) and final score (Int)
  type MovesList = List[Moves] // List of move sequences sorted by final scores: lowest (best) score first

  case class Block(colour: Char, blockSquares: BlockSquares) {
    lazy val isSingleSquare: Boolean = {
      if (this.blockSquares.head._2.tail.isEmpty && this.blockSquares.tail.isEmpty) true
      else false
    }
  }

  case class Grid(gridSquares: GridSquares) {
    lazy val gridBlocks: GridBlocks = getGridBlocks(gridSquares) // representation of this grid as blocks
    lazy val (score, unsolvable) = blockScoreList(gridBlocks)

    // This set of functions includes:
    //  getBlockList, getBlockListRow, getBlockListColumn // functions to travel the grid square by square up the columns left to right
    //  addSquare, addSquareHelper // functions to find the block(s) the square belongs to
    //  makeListNewHoldTail, mergeTwoBlocksSquares, prependTwoLists // functions to add the squares to its block
    // This function initializes the set of functions which will return a list of all the blocks that make up a grid
    def getGridBlocks(gridSquares: GridSquares): GridBlocks = {
      getGBColumn(0, gridSquares, List())

      // This function chooses the next column from the grid, starting from left = column 0 and resetting row to bottom = 0
      def getGBColumn(col: Int, gridSquares: GridSquares, gridBlocks: GridBlocks): GridBlocks = gridSquares match {
        case List() => gridBlocks
        case column :: gridTail => getGBColumn(col + 1, gridTail, getGBRow(col, 0, column, gridBlocks))
      }

      // This function chooses the next square in the column (the next row up)
      def getGBRow(col: Int, row: Int, column: List[Char], gridBlocks: GridBlocks): GridBlocks = column match {
        case List() => gridBlocks
        case ch :: chL => getGBRow(col, row + 1, chL, addSquare(Block(ch, List((col, List(row)))), gridBlocks))
      }

      // Going to check first block as below and maybe left block, then addSquareFindLeft to look for left block if its not found here
      def addSquare(block: Block, blockList: BlockList): BlockList = blockList match {
        case List() => block :: blockList // empty blocklist so simply add single block
        case b :: bL => {
          val newCol = block.blockSquares.head._1
          val newRow = block.blockSquares.head._2.head
          val firstCol = b.blockSquares.head._1
          val firstColRows = b.blockSquares.head._2
          val secondCol = b.blockSquares.tail

          val below = (newCol == firstCol) && (newRow == firstColRows.head - 1)
          val left = ((newCol - 1 == firstCol) && firstColRows.contains(newRow) ||
              (secondCol.nonEmpty && (newCol -1 == secondCol.head._1) && secondCol.head._2.contains(newRow)))

          if(left) // Found left so done, merge if same colour; doesn't matter if we found below block or not
            if (block.colour == b.colour) mergeTwoBlocks(block, b) :: bL
            else block :: blockList
          else if(below && (block.colour == b.colour)) addSquareFindLeft(mergeTwoBlocks(block, b), blockList, bL, List())
          else addSquareFindLeft(block, blockList, bL, List(b)) // hold and go looking for the left block
        }
      }

      // Already checked first block on list, going to check until finding the left block
      def addSquareFindLeft(block: Block, blockList: BlockList, blockListTail: BlockList, holdBlocks: BlockList): BlockList = blockListTail match {
        case List() => block :: blockList // empty blocklist -> block is in first column so left does not exist
        case b :: bL => {
          val newCol = block.blockSquares.head._1
          val newRow = block.blockSquares.head._2.head
          val firstCol = b.blockSquares.head._1
          val firstColRows = b.blockSquares.head._2
          val secondCol = b.blockSquares.tail

          val done = (newCol - 1 > firstCol) || ((newCol - 1 == firstCol) && (newRow > firstColRows.head))

          if(done) block :: blockList // column to the left is shorter than this block, left does not exist
          else {
            val left = ((newCol - 1 == firstCol) && firstColRows.contains(newRow)) ||
              (secondCol.nonEmpty && (newCol - 1 == secondCol.head._1) && secondCol.head._2.contains(newRow))

            if (left)
              if (block.colour == b.colour) mergeTwoBlocks(block, b) :: addBackHold(blockListTail, holdBlocks)
              else block :: blockList
            else addSquareFindLeft(block, blockList, bL, b :: holdBlocks) // hold and go looking again
          }
        }
      }

      def addBackHold(blockListTail: BlockList, holdBlocks: BlockList): BlockList = holdBlocks match {
        case List() => blockListTail
        case b :: bL => addBackHold(b :: blockListTail, bL)
      }

      def mergeTwoBlocks(b1: Block, b2: Block): Block = Block(b1.colour, mergeTwoBlocksSquares(b1.blockSquares, b2.blockSquares))

      // This function merges two BlockSquares (list of squares that make up a block) into one
      def mergeTwoBlocksSquares(bs1: BlockSquares, bs2: BlockSquares): BlockSquares = (bs1, bs2) match {
        case (List(), List()) => List()
        case (List(), bs) => bs
        case (bs, List()) => bs
        case ((col1, rows1) :: bsTail1, (col2, rows2) :: bsTail2) => {
          if (col1 == col2) (col1, mergeTwoBlocksColumns(rows1, rows2)) :: mergeTwoBlocksSquares(bsTail1, bsTail2)
          else (col1, rows1) :: mergeTwoBlocksSquares(bsTail1, bs2)
            // the else catchall is for below block maybe having an extra column on the right
        }
      }

      def mergeTwoBlocksColumns(rows1: List[Int], rows2: List[Int]): List[Int] = (rows1, rows2) match {
        case (List(), List()) => List()
        case (List(), row) => row
        case (row, List()) => row
        case ((row1 :: rest1), (row2 :: rest2)) =>
          if(row1 < row2) row2 :: mergeTwoBlocksColumns(rows1, rest2)
          else row1 :: mergeTwoBlocksColumns(rest1, rows2)
      }
    }

    def blockScoreList(blockList: BlockList): (Int, Boolean) = {
      blockScoreListHelper(blockList, Map(), 0)

      def blockScoreListHelper(blockList: BlockList, colourSafe: Map[Char, Int], score: Int): (Int, Boolean) = blockList match {
        case List() => {
          val unSafe = colourSafe.foldLeft(0)(_ + _._2)
          (score + (100 * unSafe), if(unSafe > 0) true else false)
        }
        case b :: bl =>
          if (b.isSingleSquare) { // single square is worst
            if (colourSafe.contains(b.colour))
              blockScoreListHelper(bl, colourSafe + (b.colour -> 0), score + 2)
            else
              blockScoreListHelper(bl, colourSafe + (b.colour -> 1), score + 2)
          }
          else blockScoreListHelper(bl, colourSafe + (b.colour -> 0), score + 1) // multi column block aint so bad
      }
    }
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
