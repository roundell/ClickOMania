package clickomania

trait Solver extends GameDef {

  // addMove: with each Moves in the given MovesList, addMove removes all possible groups, creating a new Moves for
  //  each group removed, adding them all to the new MovesList (sorted) but de-duping along the way
  def addMove(movesList: MovesList, keep: Int): MovesList = {

    // Moves through each move in the original MovesList
    def addMoveMoves(movesList: MovesList, newMovesList: MovesList): MovesList = movesList match {
      case List() => newMovesList
      case moves :: movesTail => addMoveMoves(movesTail, addMoveGroups(moves, newMovesList))
    }

    // Creates the new Moves and adds them to the new MovesList by removing all possible groups from this moves' grid
    def addMoveGroups(moves: Moves, newMovesList: MovesList): MovesList = {

      def addMoveGroups(gridGroups: GridGroups, newMovesList: MovesList): MovesList = gridGroups match {
        case List () => newMovesList
        case group :: groupList =>
          if (group.isSingleSquare) addMoveGroups(groupList, newMovesList)
          else addMoveGroups(groupList, addMoves(moves.newMove(group), newMovesList, keep))
      }
      addMoveGroups(moves.grid.gridGroups, newMovesList)
    }

    addMoveMoves(movesList, List())
  }

  // recursive method that checks score and timelimit on each iteration to see if it can solve the grid
  def findBestMove(movesList: MovesList, time: Long, timeLimit: Long, keep: Int): MovesList = {
    val now = System.nanoTime()
    val iterationTime = now - time
    val timeLeft = timeLimit - iterationTime

    println("Iteration Time: " + iterationTime)
    println("     Time Left: " + timeLeft)
    if(movesList.nonEmpty) {
      println("     Top Score     " + movesList.head.grid.score)
      println("     Last Score    " + movesList.last.grid.score)
      println("     Moves in List " + movesList.length)
    }

    if(movesList.isEmpty) {
      println("Ran out of moves to make")
      //print(movesList)
      movesList
    }
    else if(movesList.head.grid.score == 0) {
      println("Solved the Grid!")
      movesList
    }
    else if(timeLeft < 0) {
      println("Time ran out!")
      //print(movesList)
      movesList
    }
    else findBestMove(addMove(movesList, keep), now, timeLeft, keep)
  }

  def solveGrid(gridSquares: GridSquares): Moves = {
    val grid = Grid(gridSquares)
    val time = System.nanoTime()
    val movesList = grid.createMovesList(50)

    findBestMove(movesList, time, 1000000000, 150).head
  }
}
