package it.unibo.configgenerator.model.protelis

import org.protelis.lang.datatype.FunctionDefinition
import org.protelis.lang.interpreter.ProtelisAST
import org.protelis.lang.interpreter.impl.*
import org.protelis.parser.protelis.Share
import org.protelis.parser.protelis.impl.NBRImpl
import org.protelis.vm.ExecutionContext
import org.protelis.vm.ProtelisProgram
import org.protelis.vm.impl.SimpleProgramImpl

data class ProtelisProgramWrapper(private val protelisProgram: ProtelisProgram): ProtelisProgram {

    private val prog: ProtelisAST<*>
    private val flatedProg: List<ProtelisAST<*>>

    init {
        val field = SimpleProgramImpl::class.java.getDeclaredField("prog")
        field.isAccessible = true
        prog = field.get(protelisProgram) as ProtelisAST<*>
        flatedProg = flatAst(prog).toList()
    }

    override fun getCurrentValue(): Any = protelisProgram.currentValue

    override fun compute(context: ExecutionContext?) = protelisProgram.compute(context)

    override fun getName(): String = protelisProgram.name

    fun estimateMips(defaultMips: Double, nbrMips: Double) = flatedProg.map { mipsByInstruction(it, defaultMips, nbrMips) }.sum()

    private fun flatAst(ast: ProtelisAST<*>): Sequence<ProtelisAST<*>> = sequenceOf(ast) +
        ast.branches.flatMap { flatAst(it) } +
        when(ast) {
            is Constant<*> -> {
                val maybeLambda = ast.evaluate(null)
                if (maybeLambda is FunctionDefinition) {
                    flatAst(maybeLambda.body)
                } else {
                    emptySequence()
                }
            }
            is Invoke -> flatAst(ast.leftExpression)
            is FunctionCall -> flatAst(ast.functionDefinition.body)
            is ShareCall<*, *> -> ast.yieldExpression.transform { flatAst(it!!) }.or(emptySequence())
            is If<*> -> flatAst(ast.conditionExpression) + flatAst(ast.thenExpression) + flatAst(ast.elseExpression)
            else -> emptySequence()
        }

    private fun mipsByInstruction(ast: ProtelisAST<*>, defaultMips: Double, nbrMips: Double): Double = when(ast) {
        is NBRCall<*> -> nbrMips
        else -> defaultMips
    }
}