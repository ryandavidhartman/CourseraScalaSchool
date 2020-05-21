/* Exercise 2.26.  Suppose we define x and y to be two lists:

(define x (list 1 2 3))
(define y (list 4 5 6))

What result is printed by the interpreter in response to evaluating each of the following expressions:

(append x y)

(cons x y)

(list x y)
 */

val x = Seq(1, 2, 3)
val y = Seq(4, 5, 6)

x ++ y   // scala append
x +: y   // scala cons
Seq(x,y) // constructor
