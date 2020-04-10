package net.sileader.argparse.actions

import net.sileader.argparse.Action
import net.sileader.argparse.ArgumentsParsingState
import net.sileader.argparse.ParameterDescriptor
import net.sileader.argparse.errors.ParseException

class Store : Action {
    override fun doAction(descriptor: ParameterDescriptor, state: ArgumentsParsingState) {
        if(!descriptor.isFlagOrKeyword && state.existsArgument(descriptor.canonicalName)) {
            throw ParseException("unrecognized arguments: ${state.current}")
        }
        if(descriptor.isFlagOrKeyword) {
            state.advance()
            if(state.isFinished) {
                throw ParseException("not enough arguments passed")
            }
        }
        state.appendArgument(descriptor.canonicalName, state.current);
        state.advance()
    }

    override val valueCount = 1
}