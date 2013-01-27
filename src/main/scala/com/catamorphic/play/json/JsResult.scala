package com.catamorphic
package play
package json

import _root_.play.api.libs.json._
import scalaz._, Scalaz._

trait JsResultInstances {
  implicit val jsresultinstance = new MonadPlus[JsResult] {
    def bind[A, B](fa: JsResult[A])(f: A => JsResult[B]): JsResult[B] = fa.flatMap(f)

    def plus[A](a: JsResult[A], b: => JsResult[A]): JsResult[A] = a orElse b

    def point[A](a: => A): JsResult[A] = JsSuccess(a)

    def empty[A]: JsResult[A] = JsError(Seq())
  }
}

object jsresult extends JsResultInstances