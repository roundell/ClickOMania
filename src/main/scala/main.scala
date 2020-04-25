package clickomania


object main {

  def main(args: Array[String]): Unit = {
    println("Hello James!")

    val moves_list = NextMove.getNextMove(GridSetUp.GridSetUp(6))
    println(moves_list.head._3.head._2.head._2.head + " " + moves_list.head._3.head._2.head._1)

  }
}

//TODO: Install GitHub Desktop

//TODO: figure out the classpath or something so that I can use IntelliJ properly

//TODO: figure out where I should keep the control variables for the program

//TODO: properly rename variables (camelCase, etc. + change "squares" to "cells", etc.)

//TODO: transfer the main setup in NextMove to main.scala

//TODO: create NextMoveHelper, make it recursive

//TODO: create test suite

//TODO: make it multi-threaded so that player can choose next move before the computer does