package clickomania

trait UserIO extends Solver with GridReader with GridLibrary {

  def userIntro(): Unit = {
    println("Please make a selection:")
    println("   1. Play a grid from the library")
    println("   2. Insert a grid and play")
    println("   3. Watch the output from the algorithm solving the HackerRank grids")
    println("   4. Exit")

    val choice = scala.io.StdIn.readLine.toInt
    if(choice == 1) playAGrid(chooseLibraryGrid())
    else if(choice == 2) playAGrid(getAUserGrid())
    else if(choice == 3) algorithmSolveMenu()
    else if(choice == 4) println("Thanks for playing!")
    else println("Invalid entry, exiting...")
  }

  def chooseLibraryGrid(): Grid = {
    println("Please make a selection:")
    println("   1. Beginner Beginner")
    println("   2. Beginner")
    println("   3. Beginner Large Grid")
    println("   4. Intermediate Large Grid")
    println("   5. Super Duper Hard For The Algorithm To Solve Large Grid")

    val choice = scala.io.StdIn.readLine.toInt
    if(choice == 1) Grid(gridLibrary(11))
    else if(choice == 2) Grid(gridLibrary(12))
    else if(choice == 3) Grid(gridLibrary(2))
    else if(choice == 4) Grid(gridLibrary(3))
    else Grid(gridLibrary(6))
  }

  def getAUserGrid(): Grid = {
    println("Please enter the number of rows and columns of your grid (Type \'q\' to quit)")
    println("Format: \"rows columns\"")
    val dimensions = scala.io.StdIn.readLine.split(" ")
    val rows = dimensions(0).toInt
    val columns = dimensions(1).toInt

    if ((0 < rows) && (rows <= 20) && (columns > 0) && (columns <= 20)) {
      println("Please enter the " + rows + "x" + columns + " grid:")
      val gridString = scala.io.StdIn.readLine.toList
      if(gridString.length == (rows * columns)) Grid(readGrid(columns, gridString))
      else Grid(List())
    }
    else Grid(List())
  }

  def playAGrid(grid: Grid): Boolean = {
    println(gridSquarestoString(grid.gridSquares))
    if(grid.unsolvable) {
      println("The grid is unsolvable, you lose")
      println("Please try again!")
      return false
    }

    println("The stats of the grid are: ")
    println("         Score: " + grid.score)
    println("       Singles: " + grid.score)
    println("Indestructible: " + grid.score)
    println(gridSquaresListtoString(grid.gridSquares))
    println("Select a multi-square group to remove")
    println("Format: \"col row\"")
    val dimensions = scala.io.StdIn.readLine.split(" ")
    val col = dimensions(0).toInt
    val row = dimensions(1).toInt

    val group = grid.findGroup(col, row)

    if(group == null) {
      println("col row (" + col + " " + row + ") selection invalid")
      playAGrid(grid)
    }
    else if(group.isSingleSquare) {
      println("The group you selected is a single square")
      playAGrid(grid)
    }
    else playAGrid(grid.removeGroup(group))
  }

  def algorithmSolveMenu(): Unit = {
    println("Please make a selection:")
    println("   1. 2 colour")
    println("   2. 3 colour")
    println("   3. 5 colour")
    println("   4. 6 colour")
    println("   5. exit")

    val choice = scala.io.StdIn.readLine.toInt
    if(choice == 1) findBestMove(Grid(gridLibrary(2)), 60000000000L, 100)
    else if(choice == 2) findBestMove(Grid(gridLibrary(3)), 10000000000L, 100)
    else if(choice == 3) findBestMove(Grid(gridLibrary(5)), 10000000000L, 150)
    else if(choice == 4) findBestMove(Grid(gridLibrary(610)), 120000000000L, 1000)
    else if(choice == 5) println("Thanks for playing!")
    else println("Invalid entry, exiting...")
  }
}
