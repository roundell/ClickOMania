package clickomania

trait UserIO extends GameDef with GridReader with GridLibrary {

  def userIntro(): Unit = {
    println("Please make a selection:")
    println("   1. Play a grid from the library")
    println("   2. Insert a grid and play")

    val choice = scala.io.StdIn.readLine.toInt
    if(choice == 1) playAGrid(chooseLibraryGrid())
    else playAGrid(getAUserGrid())
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

    println("Select a multi-square block to remove")
    println("Format: \"col row\"")
    val dimensions = scala.io.StdIn.readLine.split(" ")
    val col = dimensions(0).toInt
    val row = dimensions(1).toInt

    val block = grid.findBlock(col, row)

    if(block == null) {
      println("col row (" + col + " " + row + ") selection invalid")
      playAGrid(grid)
    }
    else if(block.isSingleSquare) {
      println("The block you selected is a single square")
      playAGrid(grid)
    }
    else playAGrid(grid.removeBlock(block))
  }
}
