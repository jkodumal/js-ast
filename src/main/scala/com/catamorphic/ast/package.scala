package com.catamorphic

import scalaz._, Scalaz._

package object ast {
  sealed abstract class RegExp

  type LiteralValue = Option[String \/ Boolean \/ RegExp] 
}