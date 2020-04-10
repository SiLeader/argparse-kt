package net.sileader.argparse.actions

import net.sileader.argparse.Action
import net.sileader.argparse.ArgumentsParsingState
import net.sileader.argparse.ParameterDescriptor

class StoreTrue : Action {
    override fun doAction(descriptor: ParameterDescriptor, state: ArgumentsParsingState) {
        state.setFlag(descriptor.canonicalName)
        state.advance()
    }

    override val valueCount = 0
}