/* Exercises

 1) a function which takes 2 strings and concatenates them

 2) transform the MyPredicate and MyTransformer from exercises/part1oop/GenericList3.scala
    SEE: GenericList4.scala
 3) Define a function which takes an int and returns another function which takes an int and returns and
    Int.
     - what is the type?
     - how to you implement it?
  */


// Exercise #1:
val myConcat: (String, String) => String =
  (s1: String, s2: String ) => {
  s1 + s2
}

val bob = "Bob"
val sally = "Sally"
val bobSally = myConcat(bob, sally)

val dansConcat: (String, String) => String = new Function2[String, String, String] {
    override def apply(s1: String, s2:String): String =
      s1 + s2
  }

val bobSally2 = dansConcat(bob, sally)


// Exercise #3
// First the type is Int => Int => Int

val mySuperAdder: Int => Int => Int = { i =>
  val add: Int => Int = { j =>
    i + j
  }
  add
}


val myMoreSuperAdder = { i:Int =>  { j:Int => i + j }}

val dansSuperAdder: Function1[Int, Function1[Int, Int]]  = new Function[Int, Function1[Int, Int]] {
  def apply(i: Int): Function1[Int, Int] = new Function[Int, Int] {
    def apply(j: Int): Int = {
      i + j
    }
  }
}

//tests
val test1 = mySuperAdder(3)
assert(test1(10) == 13)
assert(test1(6) == mySuperAdder(3)(6))

val test2 = myMoreSuperAdder(3)
assert(test2(10) == 13)
assert(test2(6) == myMoreSuperAdder(3)(6))

val test3 = dansSuperAdder(3)
assert(test3(10) == 13)
assert(test3(6) == dansSuperAdder(3)(6))

// here is how I'd normally write this

def adder(i:Int)(j:Int): Int = {
  i + j
}

val test4 = adder(3)(_)
assert(test4(10) == 13)
assert(test4(6) == adder(3)(6))