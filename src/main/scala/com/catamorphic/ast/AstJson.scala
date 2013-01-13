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

  implicit val identifierReads: Reads[Identifier] = Json.reads[Identifier]

  implicit val programReads: Reads[Program] = Json.reads[Program]

  implicit val statementReads: Reads[Statement] = TODO

  implicit val blockStatementReads: Reads[BlockStatement] = Json.reads[BlockStatement]
  val ifStatementReads: Reads[IfStatement] = Json.reads[IfStatement] 
  val labeledStatementReads: Reads[LabeledStatement] = Json.reads[LabeledStatement] 
  val breakStatementReads: Reads[BreakStatement] = Json.reads[BreakStatement]
  val continueStatementReads: Reads[ContinueStatement] = Json.reads[ContinueStatement]  
  val withStatementReads: Reads[WithStatement] = TODO
  val switchStatementReads: Reads[SwitchStatement] = Json.reads[SwitchStatement] 
  val returnStatementReads: Reads[ReturnStatement] = Json.reads[ReturnStatement]
  val throwStatementReads: Reads[ThrowStatement] = Json.reads[ThrowStatement]  
  val tryStatementReads: Reads[TryStatement] = Json.reads[TryStatement]
  val whileStatementReads: Reads[WhileStatement] = Json.reads[WhileStatement] 
  val doWhileStatementReads: Reads[DoWhileStatement] = Json.reads[DoWhileStatement] 
  val forStatementReads: Reads[ForStatement] = TODO  
  val forInStatementReads: Reads[ForInStatement] = TODO  
  val letStatementReads: Reads[LetStatement] = Json.reads[LetStatement]
  val debuggerStatementReads: Reads[DebuggerStatement] = TODO


  implicit val catchClauseReads: Reads[CatchClause] = Json.reads[CatchClause]
  implicit val letStatementHeadReads: Reads[LetStatementHead] = Json.reads[LetStatementHead]
  implicit val switchCaseReads: Reads[SwitchCase] = Json.reads[SwitchCase]
  implicit val declarationReads: Reads[Declaration] = TODO
  
  implicit val patternReads: Reads[Pattern] = typeReads("ObjectPattern" -> objectPatternReads.asInstanceOf[Reads[Pattern]], 
                                                        "ArrayPattern" -> arrayPatternReads.asInstanceOf[Reads[Pattern]])

  implicit val expressionReads: Reads[Expression] = TODO
  implicit val literalReads: Reads[Literal] = TODO
  implicit val literalValueReads: Reads[LiteralValue] = TODO
  implicit val variableDeclarationKindReads: Reads[VariableDeclarationKind] = variableDeclarationKindVarReads or variableDeclarationKindLetReads or variableDeclarationKindConstReads

  val variableDeclarationKindVarReads: Reads[VariableDeclarationKind] = tokenReads(VariableDeclarationKindVar, "var")
  val variableDeclarationKindLetReads: Reads[VariableDeclarationKind] = tokenReads(VariableDeclarationKindLet, "let")
  val variableDeclarationKindConstReads: Reads[VariableDeclarationKind] = tokenReads(VariableDeclarationKindConst, "const")

  val arrayPatternReads: Reads[ArrayPattern] = Json.reads[ArrayPattern]
  val objectPatternReads: Reads[ObjectPattern] = Json.reads[ObjectPattern]

  implicit val objectPatternPropertyReads: Reads[ObjectPatternProperty] = TODO

  private def typeReads[T](types: (String, Reads[T])*): Reads[T] = new Reads[T] {
    private def typeof(json: JsValue): Option[String] = (__ \ "type")(json).headOption.flatMap(_.validate[String].asOpt)
    def reads(json: JsValue): JsResult[T] = {
      typeof(json).map((t: String) => types.toMap.get(t).map(_.reads(json)).getOrElse(JsError(Seq(JsPath() -> Seq(ValidationError("validate.error.unexpected.type", t))))))
        .getOrElse(JsError(Seq(JsPath() -> Seq(ValidationError("validate.error.missing.type", json)))))
    }
  } 

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