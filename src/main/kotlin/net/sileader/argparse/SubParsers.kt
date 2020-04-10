package net.sileader.argparse

class SubParsers {
    class SubParserInfo(val parser: ArgumentParser, val help: String)

    private val mSubParsers = mutableMapOf<String, SubParserInfo>()

    fun addParser(name: String, help: String=""): ArgumentParser {
        val subParserInfo = mSubParsers[name]
        if(subParserInfo != null) {
            return subParserInfo.parser
        }
        val parser = ArgumentParser(name)
        mSubParsers[name] = SubParserInfo(parser, help)

        return parser
    }

    internal fun parseArgs(state: ArgumentsParsingState): Boolean {
        for(parser in mSubParsers) {
            if(parser.key == state.current) {
                state.appendSubCommand(parser.key)
                state.advance()
                parser.value.parser.parseArgs(state)
                return true
            }
        }
        return false
    }

    internal val subCommandNames
        get() = mSubParsers.map { it.key }

    fun formatUsage(subCommandHierarchy: List<String>) = mSubParsers[subCommandHierarchy[0]]?.parser?.formatUsageImpl(subCommandHierarchy.drop(1), isTopLevel = false) ?: ""

    fun formatHelp(subCommandHierarchy: List<String>) = mSubParsers[subCommandHierarchy[0]]?.parser?.formatHelpImpl(subCommandHierarchy.drop(1), isTopLevel = false) ?: ""
}