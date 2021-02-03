package clickomania

import org.junit._
//import org.scalacheck._
import org.scalatest._
//import org.scalacheck.Test.{Failed, PropException, Result, check}

class GameDefTest extends GameDef {

  @Test def `GameDef Block isSingleSquare single square`(): Unit = {
    assert(Block('R', List((2, List(4)))).isSingleSquare)
  }

  @Test def `GameDef Block isSingleSquare 2-column 1-row`(): Unit = {
    assert(!Block('R', List((3, List(4)), (2, List(4)))).isSingleSquare)
  }

  @Test def `GameDef Block isSingleSquare 1-column 2-row`(): Unit = {
    assert(!Block('R', List((2, List(4, 3)))).isSingleSquare)
  }

  @Test def `GameDef Grid null: gridBlocks, score and unsolveable`(): Unit = {
    val gridSquares = List()
    val gridCode = Grid(gridSquares)
    val gridBlocksWritten = List()

    assert(gridCode.gridBlocks == gridBlocksWritten, "The block list should be null, got: " + gridCode.gridBlocks)
    assert(gridCode.score == 0, "This grid score is incorrect, got: " + gridCode.score)
    assert(!gridCode.unsolvable, "This grid is solvable, got: unsolvable == true")
  }

  @Test def `GameDef Grid 1-square: gridBlocks, score and unsolveable`(): Unit = {
    val gridSquares = List(List('B'))
    val gridCode = Grid(gridSquares)
    val gridBlocksWritten = List(Block('B', List((0, List(0)))))

    assert(gridCode.gridBlocks == gridBlocksWritten, "The block list is incorrect, got: " + gridCode.gridBlocks)
    assert(gridCode.score == 102, "This grid score is incorrect, got: " + gridCode.score)
    assert(gridCode.unsolvable, "This grid is unsolvable, got: unsolvable == false")
  }

  @Test def `GameDef Grid 1-block: gridBlocks, score, unsolveable and remove`(): Unit = {
    val gridSquares = List(List('B', 'B'))
    val gridCode = Grid(gridSquares)
    val gridBlocksWritten = List(Block('B', List((0, List(1, 0)))))

    val gridCode2 = gridCode.removeBlock(Block('B', List((0, List(1, 0)))))
    val gridBlocksWritten2 = List()

    assert(gridCode.gridBlocks == gridBlocksWritten, "The block list is incorrect, got: " + gridCode.gridBlocks)
    assert(gridCode.score == 1, "This grid score is incorrect, got: " + gridCode.score)
    assert(!gridCode.unsolvable, "This grid is solvable, got: unsolvable == true")

    assert(gridCode2.gridBlocks == gridBlocksWritten2, "With big block removed, grid is incorrect, got: " + gridCode2.gridBlocks)
    assert(gridCode2.score == 0, "This grid score is incorrect, got: " + gridCode2.score)
    assert(!gridCode2.unsolvable, "This grid is solvable, got: unsolvable == true")
  }

  @Test def `GameDef Grid small: gridBlocks, score and unsolveable`(): Unit = {
    val gridSquares = List(List('B', 'V', 'G', 'C'), List('B', 'V', 'V', 'O'), List('B'))
    val gridCode = Grid(gridSquares)
    val gridBlocksWritten = List(Block('B', List((2, List(0)), (1, List(0)), (0, List(0)))), Block('O', List((1, List(3)))),
      Block('V', List((1, List(2, 1)), (0, List(1)))), Block('C', List((0, List(3)))), Block('G', List((0, List(2)))))

    assert(gridCode.gridBlocks == gridBlocksWritten, "The block list is incorrect, got: " + gridCode.gridBlocks)
    assert(gridCode.score == 308, "This grid score is incorrect, got: " + gridCode.score)
    assert(gridCode.unsolvable, "This grid is unsolvable, got: unsolvable == false")
  }

  @Test def `GameDef Grid small U-shaped: gridBlocks, score, solveable and use removeMoves to win the game`(): Unit = {
    val gridSquares = List(List('B', 'V', 'O', 'O'), List('B'), List('B', 'V', 'V', 'O'))
    val gridCode = Grid(gridSquares)
    val gridBlocksWritten = List(Block('O', List((2, List(3)))), Block('V', List((2, List(2, 1)))),
      Block('B', List((2, List(0)), (1, List(0)), (0, List(0)))), Block('O', List((0, List(3, 2)))), Block('V', List((0, List(1)))))

    val gridCodeRM = gridCode.removeMoves(List(Block('B', List((2, List(0)), (1, List(0)), (0, List(0)))),
      Block('V', List((1, List(1, 0)), (0, List(0)))), Block('O', List((1, List(0)), (0, List(1, 0))))))

    assert(gridCode.gridBlocks == gridBlocksWritten, "The block list is incorrect, got: " + gridCode.gridBlocks)
    assert(gridCode.score == 7, "This grid score is incorrect, got: " + gridCode.score)
    assert(!gridCode.unsolvable, "This grid is solvable, got: unsolvable == true")

    assert(gridCodeRM.gridBlocks == List(),
      "Removing empty list of moves using removeMoves, grid is incorrect, got: " + gridCodeRM.gridBlocks)
  }

  // Tricky because the square at the top of one column is the same colour as the bottom of the next column
  //  but not part of the same block.  See below two blocks same colour in a row.
  @Test def `GameDef Grid tricky block: gridBlocks, score, solveable, remove, equals`(): Unit = {
    val gridSquares = List(List('B', 'V', 'O', 'B'), List('B'), List('B', 'V', 'V', 'O'))
    val gridCode = Grid(gridSquares)
    val gridBlocksWritten = List(Block('O', List((2, List(3)))), Block('V', List((2, List(2, 1)))),
      Block('B', List((2, List(0)), (1, List(0)), (0, List(0)))), Block('B', List((0, List(3)))),
      Block('O', List((0, List(2)))), Block('V', List((0, List(1)))))

    val gridCode2a = gridCode.removeBlock(Block('V', List((0, List(1)))))
    val gridCode2b = Grid(List(List('B', 'O', 'B'), List('B'), List('B', 'V', 'V', 'O')))
    val gridBlocksWritten2 = List(Block('O', List((2, List(3)))), Block('V', List((2, List(2, 1)))),
      Block('B', List((2, List(0)), (1, List(0)), (0, List(0)))), Block('B', List((0, List(2)))),
      Block('O', List((0, List(1)))))

    assert(gridCode.gridBlocks == gridBlocksWritten, "The block list is incorrect, got: " + gridCode.gridBlocks)
    assert(gridCode.score == 10, "This grid score is incorrect, got: " + gridCode.score)
    assert(!gridCode.unsolvable, "This grid is solvable (according to our simple test, actually unsolvable) got: unsolvable == true")
    assert(gridCode2a.gridBlocks == gridBlocksWritten2, "With big block removed, grid is incorrect, got: " + gridCode2a.gridBlocks)
    assert(gridCode2a.equals(gridCode2b), "The two grids should be .equal = true, got: false")
    assert(gridCode2a == gridCode2b, "The two grids should be .equal = true, got: false")
  }

  // Tricky because the last square added combines two really big funny shaped blocks
  @Test def `GameDef Grid tricky block 2: gridBlocks, score, solveable, remove (2x), remove list of moves, equals`(): Unit = {
    val gridSquares = List(List('B', 'B', 'B', 'O', 'B'), List('B', 'C', 'C', 'O', 'B', 'B', 'B', 'B'),
      List('B', 'P', 'R', 'P', 'B', 'B'), List('B', 'Y', 'R', 'Y', 'B'), List('B', 'B', 'B', 'B', 'B'))
    val gridCode = Grid(gridSquares)
    val redBlock = Block('R',List((3,List(2)), (2,List(2))))
    val bigBlock = Block('B',List((4,List(4, 3, 2, 1, 0)), (3,List(3, 0)), (2,List(4, 3, 0)), (1,List(7, 6, 5, 4, 0)), (0,List(4, 2, 1, 0))))
    val gridBlocksWritten = List(
      Block('B',List((4,List(4, 3, 2, 1, 0)), (3,List(4, 0)), (2,List(5, 4, 0)), (1,List(7, 6, 5, 4, 0)), (0,List(4, 2, 1, 0)))),
      Block('Y',List((3,List(3)))), redBlock, Block('Y',List((3,List(1)))),
      Block('P',List((2,List(3)))), Block('P',List((2,List(1)))), Block('O',List((1,List(3)), (0,List(3)))),
      Block('C',List((1,List(2, 1)))))

    val gridCode2a = gridCode.removeBlock(redBlock)
    val gridBlocksWritten2 = List(bigBlock,
      Block('Y',List((3,List(2, 1)))), Block('P',List((2,List(2, 1)))),
      Block('O',List((1,List(3)), (0,List(3)))), Block('C',List((1,List(2, 1)))))
    val gridCode2b = Grid(List(List('B', 'B', 'B', 'O', 'B'), List('B', 'C', 'C', 'O', 'B', 'B', 'B', 'B'),
      List('B', 'P', 'P', 'B', 'B'), List('B', 'Y', 'Y', 'B'), List('B', 'B', 'B', 'B', 'B')))

    val gridCode3a = gridCode2a.removeBlock(bigBlock)
    val gridBlocksWritten3 = List(
      Block('Y',List((3,List(1, 0)))), Block('P',List((2,List(1, 0)))),
      Block('O',List((1,List(2)))), Block('C',List((1,List(1, 0)))),
      Block('O',List((0,List(0)))))
    val gridCode3b = Grid(List(List('O'), List('C', 'C', 'O'),
      List('P', 'P'), List('Y', 'Y')))

    val gridCodeRM = gridCode.removeMoves(List())
    val gridCode2RM = gridCode.removeMoves(List(redBlock))
    val gridCode3RM = gridCode.removeMoves(List(redBlock, bigBlock))

    assert(gridCode.gridBlocks == gridBlocksWritten, "The block list is incorrect, got: " + gridCode.gridBlocks)
    assert(gridCode.score == 12, "This grid score is incorrect, got: " + gridCode.score)
    assert(!gridCode.unsolvable, "This grid is solvable got: unsolvable == true")

    assert(gridCode2a.gridBlocks == gridBlocksWritten2, "With red block removed, grid is incorrect, got: " + gridCode2a.gridBlocks)
    assert(gridCode3a.gridBlocks == gridBlocksWritten3, "With big block removed, grid is incorrect, got: " + gridCode3a.gridBlocks)

    assert(gridCodeRM.gridBlocks == gridBlocksWritten,
      "Removing empty list of moves using removeMoves, grid is incorrect, got: " + gridCodeRM.gridBlocks)
    assert(gridCode2RM.gridBlocks == gridBlocksWritten2,
      "Removing one move using removeMoves, grid is incorrect, got: " + gridCode2RM.gridBlocks)
    assert(gridCode3RM.gridBlocks == gridBlocksWritten3,
      "Removing two moves using removeMoves, grid is incorrect, got: " + gridCode3RM.gridBlocks)

    assert(gridCode2a.equals(gridCode2b), "The two grids should be .equal = true, got: false")
    assert(gridCode3a.equals(gridCode3b), "The two grids should be .equal = true, got: false")
  }

  // Remove blocks from a grid in different orders, getting some same intermediate grids and some different
  @Test def `GameDef Moves: create, equals`(): Unit = {
    val originalGrid = Grid(List(List('B', 'B', 'B', 'O', 'B'), List('B', 'C', 'C', 'O', 'B', 'B', 'B', 'B'),
      List('B', 'P', 'R', 'P', 'B', 'B'), List('B', 'Y', 'R', 'Y', 'B'), List('B', 'B', 'B', 'B', 'B')))
    val redBlockA = Block('R',List((3,List(2)), (2,List(2))))
    val redBlockB = Block('R',List((3,List(1)), (2,List(1))))
    val blueBlockA = Block('B',List((4,List(4, 3, 2, 1, 0)), (3,List(3, 0)), (2,List(4, 3, 0)), (1,List(7, 6, 5, 4, 0)), (0,List(4, 2, 1, 0))))
    val blueBlockB = Block('B',List((4,List(4, 3, 2, 1, 0)), (3,List(4, 0)), (2,List(5, 4, 0)), (1,List(7, 6, 5, 4, 0)), (0,List(4, 2, 1, 0))))
    val yellowBlockA = Block('Y',List((3,List(1, 0))))
    val yellowBlockB = Block('Y',List((2,List(1, 0))))
    val purpleBlock = Block('P',List((2,List(1, 0))))

    val movesA1 = Moves(originalGrid.removeBlock(redBlockA), redBlockA, List(redBlockA))
    val movesA2 = movesA1.newMove(blueBlockA)
    val movesA3 = movesA2.newMove(yellowBlockA)
    val movesA4 = movesA3.newMove(purpleBlock)

    val movesB1 = Moves(originalGrid.removeBlock(blueBlockB), blueBlockB, List(blueBlockB))
    val movesB2 = movesB1.newMove(redBlockB)
    val movesB3 = movesB2.newMove(purpleBlock)
    val movesB4 = movesB3.newMove(yellowBlockB)

    assert(!movesA1.equals(movesB1), "The Moves A1 and B1 should be .equal = false: " + movesA1 + " and " + movesB1)
    assert(movesA2.equals(movesB2), "The Moves A2 and B2 should be .equal = true: " + movesA2 + " and " + movesB2)
    assert(!movesA3.equals(movesB3), "The Moves A3 and B3 should be .equal = false: " + movesA3 + " and " + movesB3)
    assert(movesA4.equals(movesB4), "The Moves A4 and B4 should be .equal = true: " + movesA4 + " and " + movesB4)
  }
}
