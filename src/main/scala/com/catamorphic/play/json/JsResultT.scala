package com.catamorphic
package play
package json

import _root_.play.api.libs.json._
import scalaz._, Scalaz._

final case class JsResultT[F[+_], +A](run: F[JsResult[A]]) {
  
}