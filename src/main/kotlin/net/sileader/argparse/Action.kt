package net.sileader.argparse

interface Action {
    /**
     * do action
     *
     * @param descriptor parameter descriptor associated with this object
     * @param state parsing state
     */
    fun doAction(descriptor: ParameterDescriptor, state: ArgumentsParsingState)

    val valueCount: Int
}
