package clickomania


object main {

  def main(args: Array[String]): Unit = {
    println("Hello James!")

    val colour = 6
    var grid = GridSetUp.GridSetUp(colour)
    var blockList = NextMove.getBlockList(0, grid, List())
    var gridScore = NextMove.blockScoreList(blockList)
    var multiCount = NextMove.multiBlockCount(blockList, 0)
    var singleCells = NextMove.singleBlockCount(blockList, 0)
    var counter = 0
    var timeCounter = 0.0
    var movesHistory: NextMove.BlockList = List()

    println(counter + "          " + " scores: " + gridScore + "  " +
      " >>>>            multis: " + multiCount +
      "    singles: " + singleCells)

    while(NextMove.multiBlockCount(blockList, 0) > 0) {
      val time1 = System.nanoTime()
      val moves_list = NextMove.getNextMove(grid)
      val time2 = System.nanoTime()
      counter = counter + 1


      movesHistory = moves_list.head._3.head :: movesHistory
      grid = NextMove.removeBlockFromList(grid, moves_list.head._3.head)
      blockList = NextMove.getBlockList(0, grid, List())

      val gridScoreCalculated = moves_list.head._1
      val gridScoreCalculatedPads = 3 - gridScoreCalculated.toString().length()
      val move1 = moves_list.head._3.head._2.head._2.head + " " + moves_list.head._3.head._2.head._1
      var move2 = " "
      var move3 = " "
      if(moves_list.tail.nonEmpty) {
        move2 = moves_list.tail.head._3.head._2.head._2.head + " " + moves_list.tail.head._3.head._2.head._1
        if (moves_list.tail.tail.nonEmpty)
          move3 = moves_list.tail.tail.head._3.head._2.head._2.head + " " + moves_list.tail.tail.head._3.head._2.head._1
      }
      val moveDepth = moves_list.head._3.size
      val totalTime = (time2 - time1)/1000000
      val totalTimePads = 8 - totalTime.toString().length() - counter.toString().length()
      timeCounter = timeCounter + totalTime
      gridScore = NextMove.blockScoreList(blockList)
      multiCount = NextMove.multiBlockCount(blockList, 0)
      singleCells = NextMove.singleBlockCount(blockList, 0)

      println("------------------------------------------------------------------------------")
      println(counter + " "*totalTimePads + totalTime + "   " + " scores: " + gridScore + "  " +
        " >>>> " + gridScoreCalculated + "        multis: " + multiCount +
        "    singles: " + singleCells + "        moves: " + moveDepth)
      println("  ")
      println("               " + moves_list.head._1 + " | " + moves_list.head._2 + " | " + blockListToString(moves_list.head._3))
      if(moves_list.tail.nonEmpty) {
        println("               " + moves_list.tail.head._1 + " | " + moves_list.tail.head._2 + " | " + blockListToString(moves_list.tail.head._3))
        if (moves_list.tail.tail.nonEmpty)
          println("               " + moves_list.tail.tail.head._1 + " | " + moves_list.tail.tail.head._2 + " | " + blockListToString(moves_list.tail.tail.head._3))
      }
    }
    if(grid.nonEmpty)
      println("You Lose!")
    else
      println("You Won!")
    println(timeCounter)

    grid = NextMove.removeMovesFromGrid(GridSetUp.GridSetUp(colour), movesHistory.take(25))
    println(grid)
  }
  def blockListToString(blockList: BlockList): String = blockList match {
    case List() => ""
    case b :: bL => b._2.head._2.head + " " + b._2.head._1 + "   " + blockListToString(bL)
  }
  type BlockSquares = List[(Int, List[Int])]
  // Squares are recorded by column then row (columns, (rows))
  type Block = (Char, BlockSquares)
  // Add above to the colour to completely define the block
  type BlockList = List[Block]
}

//TODO: Install GitHub Desktop

//TODO: figure out the classpath or something so that I can use IntelliJ properly

//TODO: figure out where I should keep the control variables for the program

//TODO: properly rename variables (camelCase, etc. + change "squares" to "cells", etc.)

//TODO: create test suite

//TODO: make it multi-threaded so that player can choose next move before the computer does