package edu.app.productivity.domain

class ProgramTemplate private constructor(val plan: List<Action>, val currentActionIdx: Int) :
    Iterator<ProgramTemplate> {

    constructor(plan: List<Action>) : this(plan, 0)

    val finished = currentActionIdx >= plan.size

    override fun hasNext(): Boolean = currentActionIdx < plan.lastIndex
    override fun next(): ProgramTemplate = ProgramTemplate(plan, currentActionIdx + 1)
}
