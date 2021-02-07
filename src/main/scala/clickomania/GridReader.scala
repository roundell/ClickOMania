package clickomania

import scala.annotation.tailrec

trait GridReader extends GameDef {

  // The following four functions read the grid row by row from the top down and flip it in to
  //  a list of lists that represent columns from the bottom up, left to right
  def readGrid(columns: Int, gridString: List[Char]): GridSquares = {

    @tailrec
    def readGrid(gridBuilder: GridSquares, gridString: List[Char]): GridSquares = gridString match {
      case List() => gridBuilder
      case _ => readGrid(zipGridLine(gridBuilder, gridString.take(columns)), gridString.drop(columns))
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

    readGrid(zipGridFirstLine(gridString.take(columns)), gridString.drop(columns))
  }

  // Creates a string of the grid as an actual grid with only the Chars
  def gridSquarestoString(gridSquares: GridSquares): String = {
    def gridSquarestoString(gridSquares: GridSquares, gridSquaresNew: GridSquares, row: Int): String = gridSquares match {
      case List() =>
        if(stillChars(gridSquaresNew))
          gridSquarestoString(gridSquaresNew.reverse, List(), row + 1) + Console.WHITE + "\n  " + (if(row<10) " " + row else row.toString) + " |"
        else Console.WHITE + "Row:\n  " + (if(row<10) " " + row else row.toString) + " |"
      case col :: colList =>
        if(col.isEmpty) gridSquarestoString(colList, List() :: gridSquaresNew, row) + " "
        else gridSquarestoString(colList, col.tail :: gridSquaresNew, row) + colour(col.head)
    }
    @tailrec
    def stillChars(gridSquares: GridSquares): Boolean = gridSquares match {
      case List() => false
      case List() :: colList => stillChars(colList)
      case _ => true
    }
    def colour(ch: Char): String = ch match {
      case 'R' => Console.RED + ch
      case 'B' => Console.BLUE + ch
      case 'Y' => Console.YELLOW + ch
      case 'O' => Console.CYAN + ch
      case 'G' => Console.GREEN + ch
      case 'V' => Console.MAGENTA + ch
      case _ => Console.WHITE + ch
    }
    gridSquarestoString(gridSquares.reverse, List(), 0) + Console.WHITE + "\n----------------\n" + "Col:  0123456789"
  }

  // Creates a string of the grid as List[List[Char]], adding the single quotes so it can be fed back
  def gridSquaresListtoString(gridSquares: GridSquares): String = gridSquares match {
    case List() => ""
    case col :: List() => "List(" + gridSquaresListtoStringCol(col) + ")"
    case col :: colList => "List(" + gridSquaresListtoStringCol(col) + "), " + gridSquaresListtoString(colList)
  }
  def gridSquaresListtoStringCol(column: List[Char]): String = column match {
    case List() => ""
    case ch :: List() => "\'" + ch + "\'"
    case ch :: chL => "\'" + ch + "\', " + gridSquaresListtoStringCol(chL)
  }
}
