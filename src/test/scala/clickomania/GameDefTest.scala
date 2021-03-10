package clickomania

import org.junit._
//import org.scalacheck._
import org.scalatest._
//import org.scalacheck.Test.{Failed, PropException, Result, check}

class GameDefTest extends GameDef {

  @Test def `GameDef Group isSingleSquare single square`(): Unit = {
    assert(Group('R', List((2, List(4)))).isSingleSquare)
  }

  @Test def `GameDef Group isSingleSquare 2-column 1-row`(): Unit = {
    assert(!Group('R', List((3, List(4)), (2, List(4)))).isSingleSquare)
  }

  @Test def `GameDef Group isSingleSquare 1-column 2-row`(): Unit = {
    assert(!Group('R', List((2, List(4, 3)))).isSingleSquare)
  }
  /*
  @Test def `GameDef Group isIndestructible on different group shapes`(): Unit = {
    val groupO  = Group('O', List((2, List(3))))
    val groupV  = Group('V', List((2, List(2, 1))))
    val groupVV = Group('V', List((3, List(4, 3, 2, 1)), (2, List(5, 4, 2, 1))))
    val groupR  = Group('R', List((0, List(4, 3, 2))))
    val groupRR = Group('R', List((1, List(9, 8, 7, 4, 3, 2)), (0, List(8, 7, 6, 5, 4))))

    val groupB1  = Group('B', List((2, List(10)), (1, List(10)), (0, List(10))))
    val groupB2  = Group('B', List((2, List(12, 11, 10)), (1, List(10)), (0, List(10))))
    val groupB3  = Group('B', List((2, List(12, 11, 10)), (1, List(14, 13, 12, 11, 10)), (0, List(11))))
    val groupB4  = Group('B', List((2, List(12, 11, 10)), (1, List(14, 13, 11, 10)), (0, List(11))))
    val groupB5  = Group('B', List((2, List(16, 15, 14, 11)), (1, List(17, 16, 12, 11)), (0, List(16, 15, 13, 12))))
    val groupB6  = Group('B', List((9, List(18, 17, 16)), (8, List(16, 15, 14, 13, 12, 11, 10)), (7, List(16, 15, 13, 11, 10))))

    val groupY1  = Group('Y', List((2, List(0)), (1, List(0)), (0, List(0))))
    val groupY2  = Group('Y', List((2, List(2, 1, 0)), (1, List(0)), (0, List(0))))
    val groupY3  = Group('Y', List((2, List(2, 1, 0)), (1, List(4, 3, 2, 1, 0)), (0, List(0))))
    val groupY4  = Group('Y', List((2, List(2, 1, 0)), (1, List(4, 3, 1, 0)), (0, List(0))))
    val groupY5  = Group('Y', List((2, List(6, 5, 4, 0)), (1, List(7, 6, 2, 1, 0)), (0, List(6, 5, 3, 2))))
    val groupY6  = Group('Y', List((9, List(8, 7, 6)), (8, List(6, 5, 4, 3, 2, 1, 0)), (7, List(6, 5, 0))))

    assert(!groupO.isIndestructible, "Single square group should be false")
    assert(groupV.isIndestructible, "Two square one column is classic indestructible!")
    assert(groupVV.isIndestructible, "Two square 2x in one column, attached by 2nd column")
    assert(groupR.isIndestructible, "Three square one column is indestructible")
    assert(groupRR.isIndestructible, "Three square 2x in one column, attached by 2nd column")

    assert(!groupB1.isIndestructible, "B1 Various multi column groups with at least one row unattached")
    assert(!groupB2.isIndestructible, "B2 Various multi column groups with at least one row unattached")
    assert(!groupB3.isIndestructible, "B3 Various multi column groups with at least one row unattached")
    assert(!groupB4.isIndestructible, "B4 Various multi column groups with at least one row unattached")
    assert(!groupB5.isIndestructible, "B5 Various multi column groups with at least one row unattached")
    assert(!groupB6.isIndestructible, "B6 Various multi column groups with at least one row unattached")

    assert(groupY1.isIndestructible, "Y1 Various multi column groups unattached row is at the bottom")
    assert(groupY2.isIndestructible, "Y2 Various multi column groups unattached row is at the bottom")
    assert(groupY3.isIndestructible, "Y3 Various multi column groups unattached row is at the bottom")
    assert(groupY4.isIndestructible, "Y4 Various multi column groups unattached row is at the bottom")
    assert(groupY5.isIndestructible, "Y5 Various multi column groups unattached row is at the bottom")
    assert(groupY6.isIndestructible, "Y6 Various multi column groups unattached row is at the bottom")
  }
  */

  @Test def `GameDef Grid null: gridGroups, score and unsolveable`(): Unit = {
    val gridSquares = List()
    val gridCode = Grid(gridSquares)
    val gridGroupsWritten = List()

    assert(gridCode.gridGroups == gridGroupsWritten, "The group list should be null, got: " + gridCode.gridGroups)
    assert(gridCode.score == 0, "This grid score is incorrect, got: " + gridCode.score)
    assert(!gridCode.unsolvable, "This grid is solvable, got: unsolvable == true")
  }

  @Test def `GameDef Grid 1-square: gridGroups, score and unsolveable`(): Unit = {
    val gridSquares = List(List('B'))
    val gridCode = Grid(gridSquares)
    val gridGroupsWritten = List(Group('B', List((0, List(0)))))

    assert(gridCode.gridGroups == gridGroupsWritten, "The group list is incorrect, got: " + gridCode.gridGroups)
    assert(gridCode.score == 102, "This grid score is incorrect, got: " + gridCode.score)
    assert(gridCode.unsolvable, "This grid is unsolvable, got: unsolvable == false")
  }

  @Test def `GameDef Grid 1-group: gridGroups, score, unsolveable and remove`(): Unit = {
    val gridSquares = List(List('B', 'B'))
    val gridCode = Grid(gridSquares)
    val gridGroupsWritten = List(Group('B', List((0, List(1, 0)))))

    val gridCode2 = gridCode.removeGroup(Group('B', List((0, List(1, 0)))))
    val gridGroupsWritten2 = List()

    assert(gridCode.gridGroups == gridGroupsWritten, "The group list is incorrect, got: " + gridCode.gridGroups)
    assert(gridCode.score == 1, "This grid score is incorrect, got: " + gridCode.score)
    assert(!gridCode.unsolvable, "This grid is solvable, got: unsolvable == true")

    assert(gridCode2.gridGroups == gridGroupsWritten2, "With big group removed, grid is incorrect, got: " + gridCode2.gridGroups)
    assert(gridCode2.score == 0, "This grid score is incorrect, got: " + gridCode2.score)
    assert(!gridCode2.unsolvable, "This grid is solvable, got: unsolvable == true")
  }

  @Test def `GameDef Grid small: gridGroups, score and unsolveable`(): Unit = {
    val gridSquares = List(List('B', 'V', 'G', 'C'), List('B', 'V', 'V', 'O'), List('B'))
    val gridCode = Grid(gridSquares)
    val gridGroupsWritten = List(Group('B', List((2, List(0)), (1, List(0)), (0, List(0)))), Group('O', List((1, List(3)))),
      Group('V', List((1, List(2, 1)), (0, List(1)))), Group('C', List((0, List(3)))), Group('G', List((0, List(2)))))

    assert(gridCode.gridGroups == gridGroupsWritten, "The group list is incorrect, got: " + gridCode.gridGroups)
    assert(gridCode.score == 308, "This grid score is incorrect, got: " + gridCode.score)
    assert(gridCode.unsolvable, "This grid is unsolvable, got: unsolvable == false")
  }

  @Test def `GameDef Grid small U-shaped: gridGroups, score, solveable and use removeMoves to win the game`(): Unit = {
    val gridSquares = List(List('B', 'V', 'O', 'O'), List('B'), List('B', 'V', 'V', 'O'))
    val gridCode = Grid(gridSquares)
    val gridGroupsWritten = List(Group('O', List((2, List(3)))), Group('V', List((2, List(2, 1)))),
      Group('B', List((2, List(0)), (1, List(0)), (0, List(0)))), Group('O', List((0, List(3, 2)))), Group('V', List((0, List(1)))))

    val gridCodeRM = gridCode.removeMoves(List(Group('B', List((2, List(0)), (1, List(0)), (0, List(0)))),
      Group('V', List((1, List(1, 0)), (0, List(0)))), Group('O', List((1, List(0)), (0, List(1, 0))))))

    assert(gridCode.gridGroups == gridGroupsWritten, "The group list is incorrect, got: " + gridCode.gridGroups)
    assert(gridCode.score == 7, "This grid score is incorrect, got: " + gridCode.score)
    assert(!gridCode.unsolvable, "This grid is solvable, got: unsolvable == true")

    assert(gridCodeRM.gridGroups == List(),
      "Removing empty list of moves using removeMoves, grid is incorrect, got: " + gridCodeRM.gridGroups)
  }

  // Tricky because the square at the top of one column is the same colour as the bottom of the next column
  //  but not part of the same group.  See below two groups same colour in a row.
  @Test def `GameDef Grid tricky group: gridGroups, score, solveable, remove, equals`(): Unit = {
    val gridSquares = List(List('B', 'V', 'O', 'B'), List('B'), List('B', 'V', 'V', 'O'))
    val gridCode = Grid(gridSquares)
    val gridGroupsWritten = List(Group('O', List((2, List(3)))), Group('V', List((2, List(2, 1)))),
      Group('B', List((2, List(0)), (1, List(0)), (0, List(0)))), Group('B', List((0, List(3)))),
      Group('O', List((0, List(2)))), Group('V', List((0, List(1)))))

    val gridCode2a = gridCode.removeGroup(Group('V', List((0, List(1)))))
    val gridCode2b = Grid(List(List('B', 'O', 'B'), List('B'), List('B', 'V', 'V', 'O')))
    val gridGroupsWritten2 = List(Group('O', List((2, List(3)))), Group('V', List((2, List(2, 1)))),
      Group('B', List((2, List(0)), (1, List(0)), (0, List(0)))), Group('B', List((0, List(2)))),
      Group('O', List((0, List(1)))))

    assert(gridCode.gridGroups == gridGroupsWritten, "The group list is incorrect, got: " + gridCode.gridGroups)
    assert(gridCode.score == 10, "This grid score is incorrect, got: " + gridCode.score)
    assert(!gridCode.unsolvable, "This grid is solvable (according to our simple test, actually unsolvable) got: unsolvable == true")
    assert(gridCode2a.gridGroups == gridGroupsWritten2, "With big group removed, grid is incorrect, got: " + gridCode2a.gridGroups)
    assert(gridCode2a.equals(gridCode2b), "The two grids should be .equal = true, got: false")
    assert(gridCode2a == gridCode2b, "The two grids should be .equal = true, got: false")
  }

  // Tricky because the last square added combines two really big funny shaped groups
  @Test def `GameDef Grid tricky group 2: gridGroups, score, solveable, remove (2x), remove list of moves, equals`(): Unit = {
    val gridSquares = List(List('B', 'B', 'B', 'O', 'B'), List('B', 'C', 'C', 'O', 'B', 'B', 'B', 'B'),
      List('B', 'P', 'R', 'P', 'B', 'B'), List('B', 'Y', 'R', 'Y', 'B'), List('B', 'B', 'B', 'B', 'B'))
    val gridCode = Grid(gridSquares)
    val redGroup = Group('R',List((3,List(2)), (2,List(2))))
    val bigGroup = Group('B',List((4,List(4, 3, 2, 1, 0)), (3,List(3, 0)), (2,List(4, 3, 0)), (1,List(7, 6, 5, 4, 0)), (0,List(4, 2, 1, 0))))
    val gridGroupsWritten = List(
      Group('B',List((4,List(4, 3, 2, 1, 0)), (3,List(4, 0)), (2,List(5, 4, 0)), (1,List(7, 6, 5, 4, 0)), (0,List(4, 2, 1, 0)))),
      Group('Y',List((3,List(3)))), redGroup, Group('Y',List((3,List(1)))),
      Group('P',List((2,List(3)))), Group('P',List((2,List(1)))), Group('O',List((1,List(3)), (0,List(3)))),
      Group('C',List((1,List(2, 1)))))

    val gridCode2a = gridCode.removeGroup(redGroup)
    val gridGroupsWritten2 = List(bigGroup,
      Group('Y',List((3,List(2, 1)))), Group('P',List((2,List(2, 1)))),
      Group('O',List((1,List(3)), (0,List(3)))), Group('C',List((1,List(2, 1)))))
    val gridCode2b = Grid(List(List('B', 'B', 'B', 'O', 'B'), List('B', 'C', 'C', 'O', 'B', 'B', 'B', 'B'),
      List('B', 'P', 'P', 'B', 'B'), List('B', 'Y', 'Y', 'B'), List('B', 'B', 'B', 'B', 'B')))

    val gridCode3a = gridCode2a.removeGroup(bigGroup)
    val gridGroupsWritten3 = List(
      Group('Y',List((3,List(1, 0)))), Group('P',List((2,List(1, 0)))),
      Group('O',List((1,List(2)))), Group('C',List((1,List(1, 0)))),
      Group('O',List((0,List(0)))))
    val gridCode3b = Grid(List(List('O'), List('C', 'C', 'O'),
      List('P', 'P'), List('Y', 'Y')))

    val gridCodeRM = gridCode.removeMoves(List())
    val gridCode2RM = gridCode.removeMoves(List(redGroup))
    val gridCode3RM = gridCode.removeMoves(List(redGroup, bigGroup))

    assert(gridCode.gridGroups == gridGroupsWritten, "The group list is incorrect, got: " + gridCode.gridGroups)
    assert(gridCode.score == 12, "This grid score is incorrect, got: " + gridCode.score)
    assert(!gridCode.unsolvable, "This grid is solvable got: unsolvable == true")

    assert(gridCode2a.gridGroups == gridGroupsWritten2, "With red group removed, grid is incorrect, got: " + gridCode2a.gridGroups)
    assert(gridCode3a.gridGroups == gridGroupsWritten3, "With big group removed, grid is incorrect, got: " + gridCode3a.gridGroups)

    assert(gridCodeRM.gridGroups == gridGroupsWritten,
      "Removing empty list of moves using removeMoves, grid is incorrect, got: " + gridCodeRM.gridGroups)
    assert(gridCode2RM.gridGroups == gridGroupsWritten2,
      "Removing one move using removeMoves, grid is incorrect, got: " + gridCode2RM.gridGroups)
    assert(gridCode3RM.gridGroups == gridGroupsWritten3,
      "Removing two moves using removeMoves, grid is incorrect, got: " + gridCode3RM.gridGroups)

    assert(gridCode2a.equals(gridCode2b), "The two grids should be .equal = true, got: false")
    assert(gridCode3a.equals(gridCode3b), "The two grids should be .equal = true, got: false")
  }

  // Remove groups from a grid in different orders, getting some same intermediate grids and some different
  @Test def `GameDef Moves: create, equals`(): Unit = {
    val originalGrid = Grid(List(List('B', 'B', 'B', 'O', 'B'), List('B', 'C', 'C', 'O', 'B', 'B', 'B', 'B'),
      List('B', 'P', 'R', 'P', 'B', 'B'), List('B', 'Y', 'R', 'Y', 'B'), List('B', 'B', 'B', 'B', 'B')))
    val redGroupA = Group('R',List((3,List(2)), (2,List(2))))
    val redGroupB = Group('R',List((3,List(1)), (2,List(1))))
    val blueGroupA = Group('B',List((4,List(4, 3, 2, 1, 0)), (3,List(3, 0)), (2,List(4, 3, 0)), (1,List(7, 6, 5, 4, 0)), (0,List(4, 2, 1, 0))))
    val blueGroupB = Group('B',List((4,List(4, 3, 2, 1, 0)), (3,List(4, 0)), (2,List(5, 4, 0)), (1,List(7, 6, 5, 4, 0)), (0,List(4, 2, 1, 0))))
    val yellowGroupA = Group('Y',List((3,List(1, 0))))
    val yellowGroupB = Group('Y',List((2,List(1, 0))))
    val purpleGroup = Group('P',List((2,List(1, 0))))

    val movesA1 = Moves(originalGrid.removeGroup(redGroupA), redGroupA, List(redGroupA))
    val movesA2 = movesA1.newMove(blueGroupA)
    val movesA3 = movesA2.newMove(yellowGroupA)
    val movesA4 = movesA3.newMove(purpleGroup)

    val movesB1 = Moves(originalGrid.removeGroup(blueGroupB), blueGroupB, List(blueGroupB))
    val movesB2 = movesB1.newMove(redGroupB)
    val movesB3 = movesB2.newMove(purpleGroup)
    val movesB4 = movesB3.newMove(yellowGroupB)

    assert(!movesA1.equals(movesB1), "The Moves A1 and B1 should be .equal = false: " + movesA1 + " and " + movesB1)
    assert(movesA2.equals(movesB2), "The Moves A2 and B2 should be .equal = true: " + movesA2 + " and " + movesB2)
    assert(!movesA3.equals(movesB3), "The Moves A3 and B3 should be .equal = false: " + movesA3 + " and " + movesB3)
    assert(movesA4.equals(movesB4), "The Moves A4 and B4 should be .equal = true: " + movesA4 + " and " + movesB4)
  }
}
