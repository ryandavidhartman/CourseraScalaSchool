package ScalaScheme

object Primitives {

  type SchemeData = Any
  type SD = SchemeData

  type SL = SchemeList

  def cons(first: SchemeData, second: SchemeData): SchemeList = SchemePair(first, second)
  def car(list: SchemeList): SchemeData = list.head
  def cdr(list: SchemeList): SchemeData = list.tail
  def cdrL(list: SchemeList): SchemeList = list.tailSchemeList
  def cadr(list: SchemeList): SchemeData = list.tailSchemeList.head
  def cddr(list: SchemeList): SchemeData = list.tailSchemeList.tail
  def caar(list: SchemeList): SchemeData = list.headSchemeList.head
  def cdar(list: SchemeList): SchemeData = list.headSchemeList.tail

  def append(first: SchemeList, second: SchemeList): SchemeList =
    if(first.isEmpty)
      second
    else
      cons(car(first), append(cdrL(first), second))

  def isPair(arg: SchemeData): Boolean = arg.isInstanceOf[SchemeList]


  def isNull(arg: SchemeData): Boolean = arg == null || arg == SchemeNil || arg == Nil || arg == List.empty
  def isDefined(arg: SchemeData): Boolean = !isNull(arg)

  def multiply(args: SchemeData*): Double = {
    @scala.annotation.tailrec
    def mHelper[T](acc: Double, rest:Seq[Double]):Double =
      if(rest.isEmpty)
        acc
      else
        mHelper(acc * rest.head, rest.tail)
    mHelper(acc=1.0, args.map(_.toString.toDouble))
  }

  def sum(args: SchemeData*): Double = {
    @scala.annotation.tailrec
    def mHelper[T](acc: Double, rest:Seq[Double]):Double =
      if(rest.isEmpty)
        acc
      else
        mHelper(acc + rest.head, rest.tail)
    mHelper(acc=0.0, args.map(_.toString.toDouble))
  }

  def division(args: SchemeData*): Double = {
    if(args.length == 1)  1.0/ multiply(args)
    else if(args.length > 1) args.head.toString.toDouble / multiply(args.tail:_*)
    else throw new IllegalArgumentException("Division needs at least one number")
  }
}
