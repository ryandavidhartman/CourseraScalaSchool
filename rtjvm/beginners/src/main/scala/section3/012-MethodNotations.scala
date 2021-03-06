package section3

import scala.language.postfixOps

object MethodNotations extends App {

  class Person(val name: String, favoriteMovie: String) {

    // methods with one parameter can be called with INfIX notation
    // e.g. mary likes "Inception"
    def likes(movie: String): Boolean = movie == favoriteMovie

    // methods with one parameter of the same type can be thought of at OPERATORS
    def hangOutWith(person: Person): String = s"${this.name} is hanging out with ${person.name}"

    // Nothing special about the + here
    def +(person: Person): String = this hangOutWith person

    // specially defined unary "operators" can be called with PREFIX notation
    // this can be called like !mary
    def unary_! : String = s"$name, what the heck?!?"  //notice a SPACE before the :

    // zero parameter methods can be called with POSTIFX notation
    // mary isAlive
    def isAlive: Boolean = true

    // the apply function
    def apply(): String = s"Hi, my name is $name and I like $favoriteMovie"
  }

  val mary = new Person("Mary", "Inception")

  // INFIX NOTATION

  // calling a method with standard syntax marry.likes("movie")
  println(mary.likes("Inception"))

  // calling with a natural language syntax mary likes "movie"
  // this is infix or operator notation.   It works with methods that only take
  // 1 parameter
  println(mary likes "Inception")

  // "operators" in Scala
  val tom = new Person("Tom", "Fight Club")
  println(mary hangOutWith tom)  // same as marry.hangOutWith(tom)

  // ALL OPERATORS A METHODS.  e.g. marry + tom works because
  // there is a method called + in one of the Persons base class

  val sum = 1 + 5
  // is the same as
  val sum2 = 1.+(5)

  //PREFIX NOTATION
  // works with specially defined unary methods named -, + ~, and !
  val x = -1  // === 1.unary_-
  val y = 1.unary_-

  println(!mary)
  println(mary.unary_!)

  // POSTFIX NOTATION
  //requires us to enabled the postfix advanced language feature
  println(mary.isAlive)
  println(mary isAlive)

  // the apply method
  println(mary.apply())
  // If I "call" an object like a function.  The compiler will automatically call the apply function!!!!
  // This is the link between functions and objects!!!!
  println(mary())

}
