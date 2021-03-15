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

    addMoveMoves(movesList.reverse, List())
  }

  // recursive method that checks score and timelimit on each iteration to see if it can solve the grid
  def findBestMove(grid: Grid, timeLimit: Long, keep: Int): MovesList = {
    val startTime = System.nanoTime()

    def findBestMoveHelper(movesList: MovesList, last: Long, counter: Int): MovesList = {
      val startTimeIteration = System.nanoTime()
      val iterationTime = startTimeIteration - last
      val totalTime = startTimeIteration - startTime

      val iterationTimeFormatted = iterationTime.toFloat / 1000000000
      val totalTimeFormatted = totalTime.toFloat / 1000000000
      println(f"Iteration Time: $iterationTimeFormatted%2.3fs")
      println(f"    Total Time: $totalTimeFormatted%2.3fs")
      println(f"     Iteration: " + counter)

      if(movesList.nonEmpty) {
        println("     Top Score     " + movesList.head.grid.score)
        println("     Last Score    " + movesList.last.grid.score)
        println("     Moves in List " + movesList.length)
      }


      if (movesList.isEmpty) {
        println("Ran out of moves to make")
        //print(movesList)
        movesList
      }
      else if (movesList.head.grid.score == 0) {
        println("Solved the Grid in " + counter + " moves!")
        println(movesList.head.moveList)
        movesList
      }
      else if (totalTime > timeLimit) {
        println("Time ran out!")
        //print(movesList)
        movesList
      }
      else findBestMoveHelper(addMove(movesList, keep), startTimeIteration, counter + 1)
    }
    findBestMoveHelper(grid.createMovesList(50), startTime, 0)
  }

  def solveGrid(gridSquares: GridSquares): Moves = {
    findBestMove(Grid(gridSquares), 1000000000, 150).head
  }
}
