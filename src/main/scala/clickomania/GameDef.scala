package clickomania

import scala.annotation.tailrec

/* Home of the Block, Grid and Moves case classes + type definitions that help clarify */
trait GameDef {

  type GridSquares = List[List[Char]] // representation of a grid as the individual squares

  type GridBlocks = BlockList // representation of a grid as blocks
  type BlockList = List[Block] // A list of blocks could be 1. entire grid as blocks or 2. could be sequence of blocks removed from a grid (moves)
  type BlockSquares = List[(Int, List[Int])] // Squares are recorded by column then row: (c #, (r #s)), (c #2, (r #s)), ...

  type MovesList = List[Moves] // List of moves, which are each sequences sorted by final scores: lowest (best) score first

  case class Block(colour: Char, blockSquares: BlockSquares) {
    lazy val isSingleSquare: Boolean = {
      if (this.blockSquares.head._2.tail.isEmpty && this.blockSquares.tail.isEmpty) true
      else false
    }
  }

  case class Grid(gridSquares: GridSquares) {
    lazy val gridBlocks: GridBlocks = getGridBlocks(gridSquares) // representation of this grid as blocks
    lazy val (score, unsolvable) = blockScoreList(gridBlocks) // unsolvable = false doesn't necessarily mean the grid is solvable

    def equals(otherGrid: Grid): Boolean = {
      if((score == otherGrid.score) && (gridBlocks == otherGrid.gridBlocks)) true
      else false
    }

    def lessThan(otherGrid: Grid): Boolean = {
      if(score < otherGrid.score) true
      else false
    }

    // This set of functions includes:
    //  getBlockList, getBlockListRow, getBlockListColumn // functions to travel the grid square by square up the columns left to right
    //  addSquare, addSquareHelper // functions to find the block(s) the square belongs to
    //  makeListNewHoldTail, mergeTwoBlocksSquares, prependTwoLists // functions to add the squares to its block
    // This function initializes the set of functions which will return a list of all the blocks that make up a grid
    def getGridBlocks(gridSquares: GridSquares): GridBlocks = {

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

          val below = (newCol == firstCol) && (newRow - 1 == firstColRows.head)
          val left = ((newCol - 1 == firstCol) && firstColRows.contains(newRow) ||
              (secondCol.nonEmpty && (newCol -1 == secondCol.head._1) && secondCol.head._2.contains(newRow)))

          if(left) // Found left so done, merge if same colour; doesn't matter if we found below block or not
            if (block.colour == b.colour) mergeTwoBlocks(block, b) :: bL
            else block :: blockList
          else if(below && (block.colour == b.colour)) addSquareFindLeft(mergeTwoBlocks(block, b), bL, bL, List())
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
              if (block.colour == b.colour) mergeTwoBlocks(block, b) :: addBackHold(bL, holdBlocks)
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

      getGBColumn(0, gridSquares, List())
    }

    // Scores the grid: lowest is best; 1 point for multi-square block and 2 for single-square block
    def blockScoreList(blockList: BlockList): (Int, Boolean) = {
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
      blockScoreListHelper(blockList, Map(), 0)
    }

    def findBlock(col: Int, row: Int): Block = {
      @tailrec
      def findBlock(gridBlocks: GridBlocks): Block = gridBlocks match {
        case List() => null
        case block :: blockList =>
          if(isBlockCol(block.blockSquares)) block
          else findBlock(blockList)
      }
      @tailrec
      def isBlockCol(blockSquares: BlockSquares): Boolean = blockSquares match {
        case List() => false
        case column :: colList =>
          if((col == column._1) && column._2.contains(row)) true
          else isBlockCol(colList)
      }
      findBlock(this.gridBlocks)
    }

    //  Remove the block's squares from the grid
    //      >> BlockSquares are saved by column highest to lowest, then by row highest to lowest
    //      >> GridSquares are saved column lowest to highest, then row lowest to highest
    //            >> Therefore: reverse the block's columns and then each list of rows to match the GridSquares
    def removeBlock(block: Block): Grid = {
      Grid(removeBlockGetColumn(this.gridSquares, block.blockSquares.reverse, 0))
    }
    def removeMoves(moves: BlockList): Grid = {
      def removeMove(gridColumns: GridSquares, moves: BlockList): GridSquares = moves match {
        case List () => gridColumns
        case block :: blockList => removeMove(removeBlockGetColumn(gridColumns, block.blockSquares.reverse, 0), blockList)
      }
      Grid(removeMove(this.gridSquares, moves))
    }

    def removeBlockGetColumn(gridColumns: GridSquares, blockSquares: BlockSquares, colNumber: Int): GridSquares = gridColumns match {
      case List() => List()
      case gridCol :: gridColList => {
        if (blockSquares.isEmpty) gridColumns
        else if (blockSquares.head._1 == colNumber) {
          val newGridColumn = removeRowsFromColumn(gridCol, blockSquares.head._2.reverse, 0)
          if (newGridColumn.isEmpty) removeBlockGetColumn(gridColList, blockSquares.tail, colNumber + 1)
          else newGridColumn :: removeBlockGetColumn(gridColList, blockSquares.tail, colNumber + 1)
        }
        else gridCol :: removeBlockGetColumn(gridColList, blockSquares, colNumber + 1)
      }
    }

    def removeRowsFromColumn(gridRows: List[Char], blockRows: List[Int], rowNumber: Int): List[Char] = gridRows match {
      case List() => List()
      case square :: rowsTail => {
        if (blockRows.isEmpty) gridRows
        else if (blockRows.head == rowNumber) removeRowsFromColumn(rowsTail, blockRows.tail, rowNumber + 1)
        else square :: removeRowsFromColumn(rowsTail, blockRows, rowNumber + 1)
      }
    }

    def createMovesList(keep: Int): MovesList = {
      def createMovesList(gridBlocks: GridBlocks): MovesList = gridBlocks match {
        case List() => List()
        case block :: blockList =>
          if(block.isSingleSquare) createMovesList(blockList)
          else addMoves(Moves(this.removeBlock(block), block, List(block)), createMovesList(blockList), keep)
      }
      createMovesList(this.gridBlocks)
    }
  }

  // Moves: grid is the current grid after the moveList of blocks was removed from the original grid.
  // firstMove was the first move and is also the first entry in the moveList
  case class Moves(grid: Grid, firstMove: Block, moveList: BlockList) {

    def newMove(removeBlock: Block): Moves = Moves(grid.removeBlock(removeBlock), firstMove, removeBlock :: moveList)

    def equals(otherMoves: Moves): Boolean = grid.equals(otherMoves.grid)
    def lessThan(otherMoves: Moves): Boolean = grid.lessThan(otherMoves.grid)

    override def toString: String = {
      "\n" + "Moves Score: " + this.grid.score + "\n" + "Grid: " + this.grid.gridBlocks + "\n" +
        "  First Move: " + this.firstMove + "\n" + "  Move List: " + this.moveList + "\n"
    }
  }

  // addMoves adds an element (Moves) to the sorted (by score) moveList, creating a new MovesList
  def addMoves(moves: Moves, movesList: MovesList, keep: Int): MovesList = {

    def addMoves(oldMovesList: MovesList, keep: Int): MovesList = oldMovesList match {
      case List() =>
        if(keep > 0) List(moves)
        else List()
      case movesHead :: movesTail =>
        if (moves.lessThan(movesHead)) moves :: oldMovesList.take(keep - 1)
        else if (moves.equals(movesHead)) oldMovesList
        else movesHead :: addMoves(movesTail, keep - 1)
    }

    if(moves.grid.unsolvable) movesList
    else addMoves(movesList, keep)
  }
}
