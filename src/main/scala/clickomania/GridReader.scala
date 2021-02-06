package clickomania

import scala.annotation.tailrec

trait GridReader extends GameDef {

  // The following four functions read the grid row by row from the top down and flip it in to
  //  a list of lists that represent columns from the bottom up, left to right
  def readGrid(rows: Int): GridSquares = {

    @tailrec
    def readGrid(rows: Int, gridBuilder: GridSquares): GridSquares = rows match {
      case 0 => gridBuilder
      case _ => readGrid(rows - 1, zipGridLine(gridBuilder, scala.io.StdIn.readLine().toList))
    }

    def zipGridLine(gridBuilder: GridSquares, gridLine: List[Char]): GridSquares = gridLine match {
      case List() => List()
      case ch :: chL =>
        if (ch == '-') gridBuilder.head :: zipGridLine(gridBuilder.tail, chL)
        else (ch :: gridBuilder.head) :: zipGridLine(gridBuilder.tail, chL)
    }

    def zipGridFirstLine(gridLine: List[Char]): GridSquares = gridLine match {
      case List() => List()
      case ch :: chL =>
        if (ch == '-') List() :: zipGridFirstLine(chL)
        else (ch :: List()) :: zipGridFirstLine(chL)
    }
    readGrid(rows - 1, zipGridFirstLine(scala.io.StdIn.readLine().toList))
  }

  /*
  def printGridList(grid: List[List[Char]]): Unit = grid match {
    case List() => ()
    case x :: xs =>
      printGridList(xs)
      println(x.mkString)
  }*/
}
