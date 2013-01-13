package com.catamorphic
package ast

import scalaz._, Scalaz._

sealed case class SourceLocation(source: Option[String], start: Position, end: Position)

sealed case class Position(line: Int, column: Int)

sealed trait Node {
  def loc: Option[SourceLocation]
}

sealed trait Statement extends Node

sealed trait Declaration extends Statement

sealed trait Pattern extends Node

case class Literal(loc: Option[SourceLocation], value: LiteralValue) extends Node with Expression

sealed trait Expression extends Node with Pattern

sealed trait Function extends Node {
  def id: Option[Identifier]
  def params: List[Pattern]
  def defaults: List[Expression]
  def rest: Option[Identifier]
  def body: BlockStatement \/ Expression
  def generator: Boolean
  def expression: Boolean
}

sealed case class Identifier(loc: Option[SourceLocation], name: String) extends Node with Expression with Pattern

sealed case class Program(loc: Option[SourceLocation], body: List[Statement]) extends Node

sealed case class EmptyStatement(loc: Option[SourceLocation]) extends Statement

sealed case class BlockStatement(loc: Option[SourceLocation], body: List[Statement]) extends Statement

sealed case class ExpressionStatement(loc: Option[SourceLocation], expression: Expression) extends Statement

sealed case class IfStatement(loc: Option[SourceLocation], test: Expression, consequent: Statement, alternate: Option[Statement]) extends Statement

sealed case class LabeledStatement(loc: Option[SourceLocation], label: Identifier, body: Statement) extends Statement

sealed case class BreakStatement(loc: Option[SourceLocation], label: Option[Identifier]) extends Statement

sealed case class ContinueStatement(loc: Option[SourceLocation], label: Option[Identifier]) extends Statement

sealed case class WithStatement(loc: Option[SourceLocation], obj: Expression /* TODO : json will come in as 'object' */, body: Statement) extends Statement

sealed case class SwitchStatement(loc: Option[SourceLocation], discriminant: Expression, cases: List[SwitchCase], lexical: Boolean) extends Statement

sealed case class ReturnStatement(loc: Option[SourceLocation], argument: Option[Expression]) extends Statement

sealed case class ThrowStatement(loc: Option[SourceLocation], argument: Expression) extends Statement

sealed case class TryStatement(loc: Option[SourceLocation], block: BlockStatement, handler: Option[CatchClause], guardedHandlers: List[CatchClause], finalizer: Option[BlockStatement]) extends Statement

sealed case class WhileStatement(loc: Option[SourceLocation], test: Expression, body: Statement) extends Statement

sealed case class DoWhileStatement(loc: Option[SourceLocation], body: Statement, test: Expression) extends Statement

sealed case class ForStatement(loc: Option[SourceLocation], left: Option[VariableDeclaration \/ Expression], test: Option[Expression], update: Option[Expression], body: Statement) extends Statement

sealed case class ForInStatement(loc: Option[SourceLocation], left: VariableDeclaration \/ Expression, right: Expression, body: Statement, each: Boolean) extends Statement

sealed case class LetStatement(loc: Option[SourceLocation], head: List[LetStatementHead], body: Statement) extends Statement

sealed case class LetStatementHead(id: Pattern, init: Option[Expression])

sealed case class DebuggerStatement(loc: Option[SourceLocation]) extends Statement

sealed case class FunctionDeclaration(loc: Option[SourceLocation], id: Identifier, params: List[Pattern], defaults: List[Expression], rest: Option[Identifier], body: BlockStatement \/ Expression, generator: Boolean, expression: Boolean) extends Declaration

sealed case class VariableDeclaration(loc: Option[SourceLocation], declarations: List[VariableDeclarator], kind: VariableDeclarationKind) extends Declaration

sealed abstract class VariableDeclarationKind
case object VariableDeclarationKindVar extends VariableDeclarationKind
case object VariableDeclarationKindLet extends VariableDeclarationKind
case object VariableDeclarationKindConst extends VariableDeclarationKind

sealed case class VariableDeclarator(loc: Option[SourceLocation], id: Pattern, init: Option[Expression]) extends Declaration

sealed case class ThisExpression(loc: Option[SourceLocation]) extends Expression

sealed case class ArrayExpression(loc: Option[SourceLocation], elements: List[Expression]) extends Expression

sealed case class ObjectExpression(loc: Option[SourceLocation], properties: List[ObjectExpressionProperty]) extends Expression

sealed case class ObjectExpressionProperty(key: Literal \/ Identifier, value: Expression, kind: ObjectExpressionPropertyKind)

sealed abstract class ObjectExpressionPropertyKind
case object ObjectExpressionPropertyKindInit extends ObjectExpressionPropertyKind
case object ObjectExpressionPropertyKindGet extends ObjectExpressionPropertyKind
case object ObjectExpressionPropertyKindSet extends ObjectExpressionPropertyKind

sealed case class FunctionExpression(loc: Option[SourceLocation],
                                     id: Option[Identifier],
                                     params: List[Pattern],
                                     defaults: List[Expression],
                                     rest: Option[Identifier],
                                     body: BlockStatement \/ Expression,
                                     generator: Boolean,
                                     expression: Boolean 
                                    ) extends Function with Expression

sealed case class SequenceExpression(loc: Option[SourceLocation], expressions: List[Expression]) extends Expression

sealed abstract class UnaryOperator
case object UnaryMinus extends UnaryOperator
case object UnaryPlus extends UnaryOperator
case object UnaryNot extends UnaryOperator
case object UnaryBitwiseNot extends UnaryOperator

sealed case class UnaryExpression(loc: Option[SourceLocation], operator: UnaryOperator, prefix: Boolean, argument: Expression) extends Expression

sealed case class BinaryExpression(loc: Option[SourceLocation], operator: BinaryOperator, left: Expression, right: Expression) extends Expression

sealed abstract class BinaryOperator
case object BinaryEquals extends BinaryOperator
case object BinaryDoesNotEquals extends BinaryOperator
case object BinaryStrictEquals extends BinaryOperator
case object BinaryStrictDoesNotEquals extends BinaryOperator
case object BinaryLessThan extends BinaryOperator
case object BinaryLessThanEquals extends BinaryOperator
case object BinaryGreaterThan extends BinaryOperator
case object BinaryGreaterThanEquals extends BinaryOperator
case object BinaryLeftShift extends BinaryOperator
case object BinarySignedRightShift extends BinaryOperator
case object BinaryUnsignedRightShift extends BinaryOperator
case object BinaryPlus extends BinaryOperator
case object BinaryMinus extends BinaryOperator
case object BinaryTimes extends BinaryOperator
case object BinaryDivides extends BinaryOperator
case object BinaryMod extends BinaryOperator
case object BinaryBitwiseAnd extends BinaryOperator
case object BinaryBitwiseOr extends BinaryOperator
case object BinaryBitwiseXor extends BinaryOperator
case object BinaryIn extends BinaryOperator
case object BinaryInstanceOf extends BinaryOperator

sealed case class AssignmentExpression(loc: Option[SourceLocation], operator: AssignmentOperator, left: Expression, right: Expression) extends Expression

sealed abstract class AssignmentOperator
case object AssignmentSimple extends BinaryOperator
case object AssignmentPlus extends BinaryOperator
case object AssignmentMinus extends BinaryOperator
case object AssignmentTimes extends BinaryOperator
case object AssignmentDivides extends BinaryOperator
case object AssignmentMod extends BinaryOperator
case object AssignmentLeftShift extends BinaryOperator
case object AssignmentSignedRightShift extends BinaryOperator
case object AssignmentUnsignedRightShift extends BinaryOperator
case object AssignmentBitwiseOr extends BinaryOperator
case object AssignmentBitwiseXor extends BinaryOperator
case object AssignmentBitwiseAnd extends BinaryOperator

case class UpdateExpression(loc: Option[SourceLocation], operator: UpdateOperator, argument: Expression, prefix: Boolean) extends Expression

sealed abstract class UpdateOperator
case object UpdateIncrement extends UpdateOperator
case object UpdateDecrement extends UpdateOperator

case class LogicalExpression(loc: Option[SourceLocation], operator: LogicalOperator, left: Expression, right: Expression) extends Expression

sealed abstract class LogicalOperator
case object LogicalOr extends LogicalOperator
case object LogicalAnd extends LogicalOperator

case class ConditionalExpression(loc: Option[SourceLocation], test: Expression, alternate: Expression, consequent: Expression) extends Expression 

case class NewExpression(loc: Option[SourceLocation], callee: Expression, arguments: List[Option[Expression]]) extends Expression

case class CallExpression(loc: Option[SourceLocation], callee: Expression, arguments: List[Option[Expression]]) extends Expression

case class MemberExpression(loc: Option[SourceLocation], obj: Expression/* TODO: json will come in as object */, property: Identifier \/ Expression, computed: Boolean) extends Expression 

case class ObjectPattern(loc: Option[SourceLocation], properties: List[ObjectPatternProperty]) extends Pattern

case class ObjectPatternProperty(key: Literal \/ Identifier, value: Pattern)

case class ArrayPattern(loc: Option[SourceLocation], elements: List[Option[Pattern]]) extends Pattern

case class SwitchCase(loc: Option[SourceLocation], test: Option[Expression], consequent: List[Statement]) extends Node

case class CatchClause(loc: Option[SourceLocation], param: Pattern, guard: Option[Expression], body: BlockStatement) extends Node
