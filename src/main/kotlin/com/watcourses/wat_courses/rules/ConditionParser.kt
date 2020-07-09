package com.watcourses.wat_courses.rules

import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parser
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.parser.Parser

/* Parse a well-formatted condition expression
 * Grammar:
 *   course: MATH 123 ([A-Z]+ [0-9A-Z]+)
 *   label: [stuff] ([\w+])
 *   list: <list_name:2> where 2 is the required number of courses taken in the list (e.g. >=2 courses taken)
 */
object ConditionParser : Grammar<Condition>() {
    val tru by literalToken("true")
    val fal by literalToken("false")
    val and by regexToken("(&&|and)")
    val or by regexToken("(\\|\\||or)")
    val courseCode by regexToken("[A-Z]+ [0-9A-Z]+")
    val id by regexToken("([a-zA-Z]\\w*|[1-4][A-B])")
    val positiveNumber by regexToken("\\d+")
    val lpar by literalToken("(")
    val rpar by literalToken(")")
    val lLabel by literalToken("[")
    val rLabel by literalToken("]")
    val not by literalToken("!")
    val lList by literalToken("<")
    val rList by literalToken(">")
    val listSeparator by literalToken(":")

    val ws by regexToken("\\s+", ignore = true)

    val negation by -not and parser(this::term) map { Condition.not(it) }
    val labelExpression by -lLabel and id and -rLabel
    val listExpression by -lList and id and -listSeparator and positiveNumber and -rList
    val bracedExpression by -lpar and parser(this::orChain) and -rpar

    val term: Parser<Condition> by
    (tru asJust Condition.alwaysTrue()) or
            (fal asJust Condition.alwaysFalse()) or
            (courseCode map { Condition.course(it.text) }) or
            (labelExpression map { Condition.label(it.text) }) or
            (listExpression map { Condition.courseList(it.t1.text, it.t2.text.toInt()) }) or
            negation or
            bracedExpression

    val andChain by leftAssociative(term, and) { a, _, b -> Condition.and(a, b) }
    val orChain by leftAssociative(andChain, or) { a, _, b -> Condition.or(a, b) }

    override val rootParser by orChain
}