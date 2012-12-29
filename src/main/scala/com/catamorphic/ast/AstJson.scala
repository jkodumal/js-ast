package com.catamorphic
package ast

import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.data.validation.ValidationError
import scalaz._, Scalaz._

object AstJson {  
  implicit val sourceLocationReads: Reads[SourceLocation] = Json.reads[SourceLocation]
  implicit val positionReads: Reads[Position] = Json.reads[Position]
  implicit val logicalOperatorReads: Reads[LogicalOperator] = logicalOrReads or logicalAndReads

  val logicalOrReads: Reads[LogicalOperator] = tokenReads(LogicalAnd, "&&")
  val logicalAndReads: Reads[LogicalOperator] = tokenReads(LogicalOr, "||")

  val identifierReads: Reads[Identifier] = Json.reads[Identifier]

  implicit val programReads: Reads[Program] = Json.reads[Program]

  implicit val statementReads: Reads[Statement] = TODO
  implicit val declarationReads: Reads[Declaration] = TODO
  implicit val patternReads: Reads[Pattern] = new Reads[Pattern] {
    def reads(json: JsValue): JsResult[Pattern] = {
      typeof(json).map((t: String) => t match {
                          case "ObjectPattern" => objectPatternReads.reads(json)
                          case "ArrayPattern" => arrayPatternReads.reads(json)
                          case _ => JsError(Seq(JsPath() -> Seq(ValidationError("validate.error.unexpected.type", t))))        
        }).getOrElse(JsError(Seq(JsPath() -> Seq(ValidationError("validate.error.missing.type", json)))))
    }
  }

  implicit val expressionReads: Reads[Expression] = TODO
  implicit val literalReads: Reads[Literal] = TODO
  implicit val literalValueReads: Reads[LiteralValue] = TODO

  val arrayPatternReads: Reads[ArrayPattern] = Json.reads[ArrayPattern]
  val objectPatternReads: Reads[ObjectPattern] = Json.reads[ObjectPattern]
  implicit val objectPatternPropertyReads: Reads[ObjectPatternProperty] = TODO

  private def typeof(json: JsValue): Option[String] = (__ \ "type")(json).headOption.flatMap(_.validate[String].asOpt)

  private def tokenReads[T](v: => T, token : String) : Reads[T] = new Reads[T] {    
    def reads(json: JsValue) = {
      lazy val error = JsError(Seq(JsPath() -> Seq(ValidationError("validate.error.expected.token." + token, json)))) 
      json match {
        case JsString(s) => if (s === token) JsSuccess(v) 
                            else error
        case _ => error
      }    
    }
  }

  private def stringReads(str: String): Reads[String] = tokenReads(str, str)
}