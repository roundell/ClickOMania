package clickomania

trait UserIO extends GameDef with GridReader {

  def getAGrid(): Grid = {
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
