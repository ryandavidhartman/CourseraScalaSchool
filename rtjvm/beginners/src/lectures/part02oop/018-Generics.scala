package lectures.part02oop

object Generics extends  App {

  class MyList[T] {
    // use the type T in the class
  }

  val listOfIntegers = new MyList[Int]
  val listOfStrings = new MyList[String]

  // you can have multiple type parameters
  class MyMap[T, U]

  // generic methods -- make a companion object to MyList
  object MyList {
    def empty[T]: MyList[T] = ???
  }

  val emptyListOfIntegers = MyList.empty[Int]

  // Now consider the variance problem!!!!
  class Animal
  class Cat extends Animal
  class Dog extends Animal

  //
  // BIG QUESTION since Cat <: Animal should List[Cat] <: List[Animal]???
  //

  //
  // BIG QUESTION possible answer #1
  //
  // If we say YES, then List is said to be COVARIANT and we denote that
  // by saying MyList[+T]  where the + on the type parameter tells us we
  // want lists to be covariant
  class CovariantList[+T]

  // now just like we could to the following:
  val animal: Animal = new Cat  // this is regular polymorphic type substitution

  // since CovariantList define to be well covariant we can also do
  val animalList: CovariantList[Animal] = new CovariantList[Cat]

  // OK NOW given our animalList above QUESTION: should I be able to add a
  // Dog to this this??????  TURNS OUT NO!!!!!  But this is not immediately clear
  // why!!!!!!!  This is the Second BIG QUESTION


  //
  // BIG QUESTION possible answer #2
  //
  // If we say the fact the Cat <: Animal doesn't tell us anything about
  // the sub/super type relationship between List[Cat] and List[Animal]
  // then we say List is INVARIANT that is the default and written as
  // List[T]
  class InvariantList[T]

 // This won't work!
  // val animalList2: InvariantList[Animal] = new InvariantList[Cat]


  //
  // BIG QUESTION possible answer #3
  //
  // If we say the fact the Cat <: Animal means List[Cat] >: List[Animal]
  // This seems REALLY WEIRD!!! But it actually makes sense sometimes, this case
  // is called CONTRAVARIANT and we write it like List[-T] with the - sign
  // on the type parameter

  // Ok contravariance doesn't make alot of intuitive sense for lists...
  // but you can do it
  class ContravariantList[-T]
  val animalList3: ContravariantList[Cat] = new ContravariantList[Animal]

  //but consider some another types:
  class Cook[-T]
  class CanCookAnything
  class CanCookVeggies extends CanCookAnything
  // here contravariance makes more sense!
  val cook: Cook[CanCookVeggies] = new Cook[CanCookAnything]


  // type bounds
  class Cage[T <: Animal](animal: T)  //here we have a type parameter that can take a generic T
                                      //that must be an Animal or a subclass of Animal
  val cage = new Cage(new Dog)
  //val cage2 = new Cage(cook)  // this won't compile

  // you can do lower bounds too with the >: operator

  /*
  BACK to BIG QUESTION TWO
  NOW given our animalList above QUESTION: should I be able to add a
  Dog to this this??????  TURNS OUT NO!!!!!  But this is not immediately clear
  why!!!!!!!  This is the Second BIG QUESTION
   */


}
