package clickomania

import org.junit._
//import org.scalacheck._
import org.scalatest._
//import org.scalacheck.Test.{Failed, PropException, Result, check}

class SolverTest extends Solver with GridLibrary {

  @Test def `Solver Moves: create multi-move MovesLists and solve a Grid`(): Unit = {
    val originalGrid = Grid(List(List('B', 'B', 'B', 'O', 'B'), List('B', 'C', 'C', 'O', 'B', 'B', 'B', 'B'),
      List('B', 'P', 'R', 'P', 'B', 'B'), List('B', 'Y', 'R', 'Y', 'B'), List('B', 'B', 'B', 'B', 'B')))

    val movesList = originalGrid.createMovesList(10) // 4
    val movesList3 = originalGrid.createMovesList(3)

    val movesList2Moves = addMove(movesList, 20) // 8
    val movesList3Moves = addMove(movesList2Moves, 20) // 15
    val movesList4Moves = addMove(movesList3Moves, 30) // 24
    val movesList5Moves = addMove(movesList4Moves, 40) // 25
    val movesList6Moves = addMove(movesList5Moves, 50) // 15 (including a score of 0)

    assert(movesList.length == 4, "The movesList should have length 4: " + movesList)
    assert(movesList.head.grid.score == 5, "The red block should be first (lowest score): " + movesList)

    assert(movesList3.length == 3, "The movesList should now have length 3: " + movesList3)
    assert(movesList3.head.grid.score == 5, "The red block should still be first: " + movesList3)

    assert(movesList2Moves.head.moveList.length == 2, "All of the Moves should have two moves: " + movesList2Moves)
    assert(movesList3Moves.head.moveList.length == 3, "All of the Moves should have three moves: " + movesList3Moves)
    assert(movesList4Moves.head.moveList.length == 4, "All of the Moves should have four moves: " + movesList4Moves)
    assert(movesList5Moves.head.moveList.length == 5, "All of the Moves should have five moves: " + movesList5Moves)
    assert(movesList6Moves.head.moveList.length == 6, "All of the Moves should have six moves: " + movesList6Moves)
    assert(movesList6Moves.head.grid.score == 0, "The grid should be solved after six moves: " + movesList6Moves)
  }
/*
  @Test def `Solver Moves: solve a small grid using findBestMove`(): Unit = {
    val grid = Grid(List(List('B', 'B', 'B', 'O', 'B'), List('B', 'C', 'C', 'O', 'B', 'B', 'B', 'B'),
      List('B', 'P', 'R', 'P', 'B', 'B'), List('B', 'Y', 'R', 'Y', 'B'), List('B', 'B', 'B', 'B', 'B')))

    val time = System.nanoTime()
    val movesList = grid.createMovesList(50)

    val movesListFinal = findBestMove(movesList, time, 10000000000L, 100)

    assert(movesListFinal.head.grid.score == 0, "findBestMove should solve the grid within 1s: " + movesListFinal)
  }

  @Test def `Solver Moves: solve a large 2-colour grid using findBestMove`(): Unit = {
    val grid = Grid(gridLibrary(2))

    val time = System.nanoTime()
    val movesList = grid.createMovesList(50)

    val movesListFinal = findBestMove(movesList, time, 10000000000L, 100)

    assert(movesListFinal.head.grid.score == 0, "findBestMove should solve the grid within 10s: " + movesListFinal)
  }

  @Test def `Solver Moves: solve a large 5-colour grid using findBestMove`(): Unit = {
    val grid = Grid(gridLibrary(5))

    val time = System.nanoTime()
    val movesList = grid.createMovesList(50)

    val movesListFinal = findBestMove(movesList, time, 10000000000L, 150)

    assert(movesListFinal.head.grid.score == 0, "findBestMove should solve the grid within 10s: " + movesListFinal)
  }
*/
  @Test def `Solver Moves: solve a large 6-colour grid using findBestMove`(): Unit = {
    val grid = Grid(gridLibrary(610))

    val time = System.nanoTime()
    val movesList = grid.createMovesList(50)

    val movesListFinal = findBestMove(movesList, time, 100000000000L, 800)

    assert(movesListFinal.head.grid.score == 0, "findBestMove should solve the grid within 40s: ") // + movesListFinal)
  }
}
