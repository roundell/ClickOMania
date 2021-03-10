package clickomania

import scala.annotation.tailrec

/* Home of the Group, Grid and Moves case classes + type definitions that help clarify */
trait GameDef {

  type GridSquares = List[List[Char]] // representation of a grid as the individual squares

  type GridGroups = GroupList // representation of a grid as groups
  type GroupList = List[Group] // A list of groups could be 1. entire grid as groups or 2. could be sequence of groups removed from a grid (moves)
  type GroupSquares = List[(Int, List[Int])] // Squares in groups are recorded by column then row: (c #, (r #s)), (c #2, (r #s)), ...

  type MovesList = List[Moves] // List of moves, which are each sequences sorted by final scores: lowest (best) score first

  case class Group(colour: Char, groupSquares: GroupSquares) {
    lazy val isSingleSquare: Boolean = {
      if (this.groupSquares.head._2.tail.isEmpty && this.groupSquares.tail.isEmpty) true
      else false
    }
    lazy val isIndestructible: Boolean = {
      @tailrec
      def columnsIndestructible(groupSquares: GroupSquares): Boolean = groupSquares match {
        case List() => true
        case col :: colList => rowsIndestructible(col._2.tail, col._2.head, (col._2.head == 0)) &&
            columnsIndestructible(colList)
      }
      @tailrec
      def rowsIndestructible(rows: List[Int], last: Int, matched: Boolean): Boolean = rows match {
        case List() => matched
        case 0 :: List() =>
          if(matched || (last == 1)) true
          else false
        case row :: rowList =>
          if(last - 1 == row) rowsIndestructible(rowList, row, true)
          else if(matched) rowsIndestructible(rowList, row, false)
          else false
      }
      columnsIndestructible(this.groupSquares)
    }
  }

  case class Grid(gridSquares: GridSquares) {
    lazy val gridGroups: GridGroups = getGridGroups(gridSquares) // representation of this grid as groups
    lazy val (score, unsolvable) = groupScoreList(gridGroups) // unsolvable = false doesn't necessarily mean the grid is solvable
    //lazy val (singles, indestructible) = ???

    def equals(otherGrid: Grid): Boolean = {
      if((score == otherGrid.score) && (gridGroups == otherGrid.gridGroups)) true
      else false
    }

    def lessThan(otherGrid: Grid): Boolean = {
      if(score < otherGrid.score) true
      else false
    }

    // This set of functions includes:
    //  getGroupList, getGroupListRow, getGroupListColumn // functions to travel the grid square by square up the columns left to right
    //  addSquare, addSquareHelper // functions to find the group(s) the square belongs to
    //  makeListNewHoldTail, mergeTwoGroupsSquares, prependTwoLists // functions to add the squares to its group
    // This function initializes the set of functions which will return a list of all the groups that make up a grid
    def getGridGroups(gridSquares: GridSquares): GridGroups = {

      // This function chooses the next column from the grid, starting from left = column 0 and resetting row to bottom = 0
      def getGBColumn(col: Int, gridSquares: GridSquares, gridGroups: GridGroups): GridGroups = gridSquares match {
        case List() => gridGroups
        case column :: gridTail => getGBColumn(col + 1, gridTail, getGBRow(col, 0, column, gridGroups))
      }

      // This function chooses the next square in the column (the next row up)
      def getGBRow(col: Int, row: Int, column: List[Char], gridGroups: GridGroups): GridGroups = column match {
        case List() => gridGroups
        case ch :: chL => getGBRow(col, row + 1, chL, addSquare(Group(ch, List((col, List(row)))), gridGroups))
      }

      // Going to check first group as below and maybe left group, then addSquareFindLeft to look for left group if its not found here
      def addSquare(group: Group, groupList: GroupList): GroupList = groupList match {
        case List() => group :: groupList // empty grouplist so simply add single group
        case g :: gL => {
          val newCol = group.groupSquares.head._1
          val newRow = group.groupSquares.head._2.head
          val firstCol = g.groupSquares.head._1
          val firstColRows = g.groupSquares.head._2
          val secondCol = g.groupSquares.tail

          val below = (newCol == firstCol) && (newRow - 1 == firstColRows.head)
          val left = ((newCol - 1 == firstCol) && firstColRows.contains(newRow) ||
              (secondCol.nonEmpty && (newCol -1 == secondCol.head._1) && secondCol.head._2.contains(newRow)))

          if(left) // Found left so done, merge if same colour; doesn't matter if we found below group or not
            if (group.colour == g.colour) mergeTwoGroups(group, g) :: gL
            else group :: groupList
          else if(below && (group.colour == g.colour)) addSquareFindLeft(mergeTwoGroups(group, g), gL, gL, List())
          else addSquareFindLeft(group, groupList, gL, List(g)) // hold and go looking for the left group
        }
      }

      // Already checked first group on list, going to check until finding the left group
      def addSquareFindLeft(group: Group, groupList: GroupList, groupListTail: GroupList, holdGroups: GroupList): GroupList = groupListTail match {
        case List() => group :: groupList // empty groupList -> group is in first column so left does not exist
        case g :: gL => {
          val newCol = group.groupSquares.head._1
          val newRow = group.groupSquares.head._2.head
          val firstCol = g.groupSquares.head._1
          val firstColRows = g.groupSquares.head._2
          val secondCol = g.groupSquares.tail

          val done = (newCol - 1 > firstCol) || ((newCol - 1 == firstCol) && (newRow > firstColRows.head))

          if(done) group :: groupList // column to the left is shorter than this group, left does not exist
          else {
            val left = ((newCol - 1 == firstCol) && firstColRows.contains(newRow)) ||
              (secondCol.nonEmpty && (newCol - 1 == secondCol.head._1) && secondCol.head._2.contains(newRow))

            if (left)
              if (group.colour == g.colour) mergeTwoGroups(group, g) :: addBackHold(gL, holdGroups)
              else group :: groupList
            else addSquareFindLeft(group, groupList, gL, g :: holdGroups) // hold and go looking again
          }
        }
      }

      def addBackHold(groupListTail: GroupList, holdGroups: GroupList): GroupList = holdGroups match {
        case List() => groupListTail
        case g :: gL => addBackHold(g :: groupListTail, gL)
      }

      def mergeTwoGroups(g1: Group, g2: Group): Group = Group(g1.colour, mergeTwoGroupsSquares(g1.groupSquares, g2.groupSquares))

      // This function merges two GroupSquares (list of squares that make up a group) into one
      def mergeTwoGroupsSquares(gs1: GroupSquares, gs2: GroupSquares): GroupSquares = (gs1, gs2) match {
        case (List(), List()) => List()
        case (List(), gs) => gs
        case (gs, List()) => gs
        case ((col1, rows1) :: gsTail1, (col2, rows2) :: gsTail2) => {
          if (col1 == col2) (col1, mergeTwoGroupsColumns(rows1, rows2)) :: mergeTwoGroupsSquares(gsTail1, gsTail2)
          else (col1, rows1) :: mergeTwoGroupsSquares(gsTail1, gs2)
            // the else catchall is for below group maybe having an extra column on the right
        }
      }

      def mergeTwoGroupsColumns(rows1: List[Int], rows2: List[Int]): List[Int] = (rows1, rows2) match {
        case (List(), List()) => List()
        case (List(), row) => row
        case (row, List()) => row
        case ((row1 :: rest1), (row2 :: rest2)) =>
          if(row1 < row2) row2 :: mergeTwoGroupsColumns(rows1, rest2)
          else row1 :: mergeTwoGroupsColumns(rest1, rows2)
      }

      getGBColumn(0, gridSquares, List())
    }

    // Scores the grid: lowest is best; 1 point for multi-square group and 2 for single-square group
    def groupScoreList(groupList: GroupList): (Int, Boolean) = {
      def groupScoreListHelper(groupList: GroupList, colourSafe: Map[Char, Int], score: Int): (Int, Boolean) = groupList match {
        case List() => {
          val unSafe = colourSafe.foldLeft(0)(_ + _._2)
          (score + (100 * unSafe), if(unSafe > 0) true else false)
        }
        case g :: bl =>
          if (g.isSingleSquare) { // single square is worst
            if (colourSafe.contains(g.colour))
              groupScoreListHelper(bl, colourSafe + (g.colour -> 0), score + 2)
            else
              groupScoreListHelper(bl, colourSafe + (g.colour -> 1), score + 2)
          }
          //else if(b.isIndestructible)
          //  groupScoreListHelper(bl, colourSafe + (b.colour -> 0), score + 1) // indestructible groups score 0
          else
            groupScoreListHelper(bl, colourSafe + (g.colour -> 0), score + 1) // multi column group aint so bad
      }
      groupScoreListHelper(groupList, Map(), 0)
    }

    def findGroup(col: Int, row: Int): Group = {
      @tailrec
      def findGroup(gridGroups: GridGroups): Group = gridGroups match {
        case List() => null
        case group :: groupList =>
          if(isGroupCol(group.groupSquares)) group
          else findGroup(groupList)
      }
      @tailrec
      def isGroupCol(groupSquares: GroupSquares): Boolean = groupSquares match {
        case List() => false
        case column :: colList =>
          if((col == column._1) && column._2.contains(row)) true
          else isGroupCol(colList)
      }
      findGroup(this.gridGroups)
    }

    //  Remove the group's squares from the grid
    //      >> GroupSquares are saved by column highest to lowest, then by row highest to lowest
    //      >> GridSquares are saved column lowest to highest, then row lowest to highest
    //            >> Therefore: reverse the group's columns and then each list of rows to match the GridSquares
    def removeGroup(group: Group): Grid = {
      Grid(removeGroupGetColumn(this.gridSquares, group.groupSquares.reverse, 0))
    }
    def removeMoves(moves: GroupList): Grid = {
      def removeMove(gridColumns: GridSquares, moves: GroupList): GridSquares = moves match {
        case List () => gridColumns
        case group :: groupList => removeMove(removeGroupGetColumn(gridColumns, group.groupSquares.reverse, 0), groupList)
      }
      Grid(removeMove(this.gridSquares, moves))
    }

    def removeGroupGetColumn(gridColumns: GridSquares, groupSquares: GroupSquares, colNumber: Int): GridSquares = gridColumns match {
      case List() => List()
      case gridCol :: gridColList => {
        if (groupSquares.isEmpty) gridColumns
        else if (groupSquares.head._1 == colNumber) {
          val newGridColumn = removeRowsFromColumn(gridCol, groupSquares.head._2.reverse, 0)
          if (newGridColumn.isEmpty) removeGroupGetColumn(gridColList, groupSquares.tail, colNumber + 1)
          else newGridColumn :: removeGroupGetColumn(gridColList, groupSquares.tail, colNumber + 1)
        }
        else gridCol :: removeGroupGetColumn(gridColList, groupSquares, colNumber + 1)
      }
    }

    def removeRowsFromColumn(gridRows: List[Char], groupRows: List[Int], rowNumber: Int): List[Char] = gridRows match {
      case List() => List()
      case square :: rowsTail => {
        if (groupRows.isEmpty) gridRows
        else if (groupRows.head == rowNumber) removeRowsFromColumn(rowsTail, groupRows.tail, rowNumber + 1)
        else square :: removeRowsFromColumn(rowsTail, groupRows, rowNumber + 1)
      }
    }

    def createMovesList(keep: Int): MovesList = {
      def createMovesList(gridGroups: GridGroups): MovesList = gridGroups match {
        case List() => List()
        case group :: groupList =>
          if(group.isSingleSquare) createMovesList(groupList)
          else addMoves(Moves(this.removeGroup(group), group, List(group)), createMovesList(groupList), keep)
      }
      createMovesList(this.gridGroups)
    }
  }

  // Moves: grid is the current grid after the moveList of groups was removed from the original grid.
  // firstMove was the first move and is also the first entry in the moveList
  case class Moves(grid: Grid, firstMove: Group, moveList: GroupList) {

    def newMove(removeGroup: Group): Moves = Moves(grid.removeGroup(removeGroup), firstMove, removeGroup :: moveList)

    def equals(otherMoves: Moves): Boolean = grid.equals(otherMoves.grid)
    def lessThan(otherMoves: Moves): Boolean = grid.lessThan(otherMoves.grid)

    override def toString: String = {
      "\n" + "Moves Score: " + this.grid.score + "\n" + "Grid: " + this.grid.gridGroups + "\n" +
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
