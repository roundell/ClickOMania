package clickomania


object main {

  def main(args: Array[String]): Unit = {
    println("Hello James!")

    NextMove.getNextMove(GridSetUp.GridSetUp(6))
  }
}

//TODO: figure out the classpath or something so that I can use IntelliJ properly

//TODO: figure out where I should keep the control variables for the program

//TODO: properly rename variables (camelCase, etc. + change "squares" to "cells", etc.)

//TODO: transfer the main setup in NextMove to main.scala

//TODO: create a NextMove function that calls getBestMove

//TODO: create test suite