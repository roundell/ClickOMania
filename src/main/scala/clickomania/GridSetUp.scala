package clickomania

object GridSetUp {

  def GridSetUp(Colours: Int): List[List[Char]] = Colours match {
    case 2 => return List(
      List('B', 'R', 'R', 'B', 'R', 'R', 'B', 'R', 'R', 'B', 'R', 'B', 'R', 'B', 'B', 'B', 'B', 'B', 'R', 'B'),
      List('B', 'B', 'R', 'R', 'B', 'B', 'R', 'B', 'R', 'R', 'R', 'R', 'B', 'B', 'R', 'B', 'R', 'R', 'B', 'R'),
      List('B', 'B', 'B', 'R', 'R', 'R', 'B', 'B', 'B', 'R', 'R', 'R', 'R', 'R', 'B', 'B', 'R', 'B', 'R', 'B'),
      List('B', 'R', 'R', 'R', 'B', 'B', 'B', 'B', 'R', 'R', 'R', 'R', 'B', 'B', 'R', 'B', 'R', 'B', 'B', 'B'),
      List('R', 'B', 'B', 'B', 'R', 'B', 'R', 'B', 'R', 'B', 'B', 'R', 'R', 'R', 'R', 'R', 'B', 'R', 'B', 'B'),
      List('R', 'B', 'B', 'R', 'B', 'B', 'B', 'B', 'B', 'B', 'R', 'R', 'B', 'B', 'B', 'B', 'B', 'R', 'R', 'B'),
      List('B', 'R', 'B', 'B', 'R', 'R', 'R', 'B', 'B', 'B', 'R', 'B', 'R', 'R', 'R', 'R', 'B', 'R', 'R', 'R'),
      List('B', 'B', 'B', 'R', 'B', 'R', 'B', 'B', 'B', 'R', 'R', 'B', 'R', 'B', 'R', 'R', 'R', 'B', 'R', 'R'),
      List('R', 'R', 'B', 'R', 'B', 'R', 'B', 'B', 'R', 'B', 'R', 'R', 'B', 'B', 'R', 'B', 'B', 'B', 'R', 'B'),
      List('B', 'B', 'R', 'R', 'B', 'B', 'R', 'B', 'B', 'R', 'B', 'B', 'R', 'B', 'B', 'R', 'R', 'B', 'R', 'B'))

    case 5 => return List(
      List('G', 'Y', 'G', 'G', 'R', 'O', 'Y', 'R', 'Y', 'R', 'Y', 'Y', 'G', 'R', 'O', 'G', 'G', 'B', 'Y', 'O'),
      List('Y', 'B', 'G', 'O', 'G', 'B', 'B', 'G', 'B', 'R', 'R', 'O', 'R', 'R', 'B', 'O', 'Y', 'O', 'Y', 'B'),
      List('R', 'Y', 'Y', 'G', 'B', 'Y', 'Y', 'B', 'B', 'G', 'B', 'B', 'Y', 'G', 'B', 'B', 'Y', 'Y', 'R', 'Y'),
      List('O', 'O', 'B', 'O', 'O', 'G', 'O', 'Y', 'R', 'O', 'O', 'O', 'R', 'Y', 'R', 'G', 'O', 'G', 'G', 'R'),
      List('O', 'R', 'O', 'B', 'O', 'O', 'B', 'Y', 'B', 'Y', 'G', 'O', 'G', 'B', 'B', 'G', 'G', 'Y', 'O', 'O'),
      List('O', 'Y', 'B', 'R', 'R', 'G', 'R', 'B', 'B', 'B', 'Y', 'G', 'Y', 'R', 'O', 'Y', 'Y', 'R', 'B', 'R'),
      List('O', 'G', 'Y', 'O', 'B', 'R', 'B', 'B', 'R', 'Y', 'B', 'O', 'G', 'B', 'Y', 'O', 'O', 'Y', 'R', 'B'),
      List('B', 'B', 'R', 'R', 'B', 'O', 'O', 'R', 'G', 'Y', 'B', 'B', 'G', 'G', 'R', 'G', 'B', 'O', 'B', 'Y'),
      List('O', 'O', 'G', 'G', 'B', 'O', 'G', 'G', 'G', 'Y', 'G', 'G', 'O', 'O', 'B', 'R', 'B', 'Y', 'Y', 'G'),
      List('G', 'R', 'B', 'G', 'R', 'R', 'G', 'Y', 'G', 'Y', 'G', 'G', 'R', 'Y', 'B', 'R', 'Y', 'R', 'B', 'B'))

    case 6 => return List(
      List('V', 'G', 'R', 'V', 'G', 'G', 'O', 'G', 'G', 'O', 'R', 'B', 'R', 'O', 'V', 'O', 'V', 'V', 'Y', 'B'),
      List('B', 'V', 'G', 'G', 'O', 'O', 'G', 'O', 'Y', 'G', 'R', 'R', 'O', 'O', 'G', 'O', 'R', 'Y', 'B', 'G'),
      List('O', 'V', 'V', 'Y', 'R', 'Y', 'V', 'O', 'B', 'Y', 'Y', 'R', 'R', 'Y', 'B', 'O', 'R', 'O', 'Y', 'O'),
      List('B', 'G', 'Y', 'Y', 'V', 'V', 'O', 'B', 'Y', 'G', 'R', 'Y', 'O', 'O', 'Y', 'V', 'R', 'O', 'B', 'B'),
      List('Y', 'V', 'O', 'B', 'Y', 'O', 'R', 'O', 'Y', 'V', 'B', 'Y', 'Y', 'Y', 'Y', 'Y', 'V', 'Y', 'V', 'V'),
      List('Y', 'V', 'O', 'Y', 'B', 'V', 'V', 'V', 'B', 'O', 'G', 'R', 'O', 'V', 'O', 'B', 'O', 'Y', 'G', 'B'),
      List('O', 'R', 'V', 'B', 'Y', 'R', 'G', 'O', 'O', 'V', 'G', 'O', 'Y', 'G', 'Y', 'Y', 'B', 'Y', 'R', 'G'),
      List('O', 'B', 'B', 'G', 'O', 'R', 'V', 'V', 'V', 'Y', 'R', 'V', 'Y', 'O', 'Y', 'Y', 'G', 'B', 'R', 'R'),
      List('R', 'Y', 'O', 'Y', 'B', 'G', 'V', 'O', 'G', 'O', 'R', 'Y', 'O', 'B', 'G', 'V', 'B', 'B', 'G', 'O'),
      List('O', 'B', 'G', 'G', 'V', 'V', 'Y', 'B', 'V', 'R', 'V', 'B', 'Y', 'V', 'B', 'R', 'G', 'B', 'G', 'B'))

    case 10 => return List(
      List('V', 'O', 'O', 'V', 'G', 'C'),
      List('B', 'V', 'G', 'C'),
      List('B', 'V', 'V', 'O'),
      List('B'))

    case 25 => return List(
      List('V', 'R', 'V', 'G', 'B', 'R', 'V', 'O', 'V', 'V', 'Y', 'B'),
      List('B', 'O', 'G', 'O', 'G', 'O', 'G'),
      List('V', 'Y', 'R', 'V', 'Y', 'R', 'Y', 'O'),
      List('V', 'Y', 'O', 'O', 'R', 'O'),
      List('B', 'Y', 'V', 'Y', 'Y', 'Y', 'V', 'V'),
      List('V', 'B', 'V', 'G', 'R', 'O', 'V', 'B', 'O', 'Y', 'G', 'B'),
      List('R', 'G', 'O', 'G', 'O', 'Y', 'G', 'Y', 'Y', 'B', 'Y', 'R', 'G'),
      List('Y', 'R', 'V', 'Y', 'O', 'Y', 'Y', 'G', 'B', 'R', 'R'),
      List('V', 'O', 'G', 'O', 'R', 'Y', 'O', 'B', 'G', 'V', 'B', 'B', 'G', 'O'),
      List('B', 'G', 'G', 'V', 'V', 'Y', 'B', 'V', 'R', 'V', 'B', 'Y', 'V', 'B', 'R', 'G', 'B', 'G', 'B'))
  }
}
