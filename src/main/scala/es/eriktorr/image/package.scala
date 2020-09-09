package es.eriktorr

package object image {
  type Quality = Double

  type Width = Int
  type Height = Int

  def ignoreResult[A, B](f: A => B, a: A): Unit = f(a) match {
    case _ => ()
  }
}
