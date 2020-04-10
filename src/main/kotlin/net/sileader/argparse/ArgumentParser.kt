package net.sileader.argparse

import net.sileader.argparse.actions.Store
import net.sileader.argparse.actions.StoreTrue
import net.sileader.argparse.errors.ParseException
import kotlin.system.exitProcess


/**
 * ArgumentParser class (like Python's argparse.ArgumentParser)
 *
 * default actions map:
 *   "store_true" to [net.sileader.argparse.actions.StoreTrue]
 *   "store" to [net.sileader.argparse.actions.Store]
 *
 * @param program program name
 * @param usage custom usage message (override default message)
 * @param epilogue epilogue message
 * @param actions override default actions map
 */
class ArgumentParser(
    private val program: String,
    private val usage: String? = null,
    private val epilogue: String? = null,
    actions: Map<String, () -> Action>? = null) {

    companion object {
        const val STORE = "store"
        const val STORE_TRUE = "store_true"
    }

    /**
     * parameter descriptors
     */
    private val mParameters = mutableListOf<ParameterDescriptor>()

    /**
     * actions map
     */
    private val mActions = actions ?: mapOf(
        STORE to { Store() },
        STORE_TRUE to { StoreTrue() }
    )

    /**
     * sub parser object
     */
    private val mSubParsers = SubParsers()

    /**
     * positional parameters (filtered)
     */
    private val mPositionalParameters
        get() = mParameters.filter { !it.isFlagOrKeyword }

    /**
     * keyword parameters (filtered)
     */
    private val mKeywordParameters
        get() = mParameters.filter { it.isFlagOrKeyword }

    init {
        addArgument("-h", "--help", action = STORE_TRUE, help = "show this help message")
    }

    /**
     * get sub parser object
     */
    fun addSubParser() = mSubParsers

    /**
     * get predefined action
     * if action not found, throw [NoSuchElementException]
     *
     * @throws NoSuchElementException
     */
    private fun getPredefinedAction(name: String) = mActions[name] ?: throw NoSuchElementException()

    /**
     * internal implementation for addArgument methods
     *
     * @param parameters parameters name
     * @param action action object
     * @param required mark required
     * @param help help message
     */
    private fun addArgumentImpl(parameters: List<String>, action: Action, required: Boolean?, help: String)
            = mParameters.add(
        ParameterDescriptor(
            parameters,
            action,
            required,
            help
        )
    )

    /**
     * add argument
     * keyword arguments (e.g. --version, -v, --input {A}, -I {A})
     * positional arguments
     *
     * @param parameters parameter name
     * @param action action object
     * @param required mark this parameter is required
     * @param help help message for this parameter
     */
    fun addArgument(
        vararg parameters: String,
        action: Action,
        required: Boolean? = null,
        help: String = "")
            = addArgumentImpl(parameters.toList(), action, required, help)

    /**
     * add argument
     * keyword arguments (e.g. --version, -v, --input {A}, -I {A})
     * positional arguments
     *
     * @param parameters parameter name
     * @param action action name (default store)
     * @param required mark this parameter is required
     * @param help help message for this parameter
     */
    fun addArgument(
        vararg parameters: String,
        action: String = STORE,
        required: Boolean? = null,
        help: String = "") = addArgumentImpl(parameters.toList(), getPredefinedAction(action).invoke(), required, help)

    /**
     * internal implementation for [formatUsage] and [printUsage]
     *
     * @param subCommandHierarchy sub command name list (e.g. COMMAND sc1 sc2 sc3 -> {sc1, sc2, sc3})
     * @param isTopLevel called as top level usage
     */
    internal fun formatUsageImpl(subCommandHierarchy: List<String>, isTopLevel: Boolean = true): String {
        if(usage != null) {
            return usage
        }

        val builder = StringBuilder()

        if(isTopLevel) {
            builder.append("usage: ")
        }

        builder.append(program)

        if(isTopLevel && subCommandHierarchy.isEmpty() && mSubParsers.subCommandNames.isNotEmpty()) {
            builder.append(" {${mSubParsers.subCommandNames.joinToString(",")}}")
        }

        if(subCommandHierarchy.isEmpty()) {
            for(parameter in mKeywordParameters) {
                builder.append(" ${parameter.usage}")
            }

            for(parameter in mPositionalParameters) {
                builder.append(" ${parameter.usage}")
            }
        }else{
            if(subCommandHierarchy.isNotEmpty()) {
                builder.append(" ")
                builder.append(mSubParsers.formatUsage(subCommandHierarchy))
            }
        }

        return builder.toString()
    }

    /**
     * internal implementation for [formatHelp] and [printHelp]
     *
     * @param subCommandHierarchy sub command name list (e.g. COMMAND sc1 sc2 sc3 -> {sc1, sc2, sc3})
     * @param isTopLevel called as top level usage
     */
    internal fun formatHelpImpl(subCommandHierarchy: List<String>, isTopLevel: Boolean = true): String {
        val builder = StringBuilder()

        if(isTopLevel) {
            builder.append(formatUsageImpl(subCommandHierarchy, isTopLevel))
            builder.append("\n\n")
        }

        if(subCommandHierarchy.isEmpty()) {
            if(mKeywordParameters.isNotEmpty()) {
                builder.append("positional arguments:\n")

                for(parameter in mKeywordParameters) {
                    builder.append("  ${parameter.help}\n")
                }

                builder.append("\n")
            }

            if(mPositionalParameters.isNotEmpty()) {
                builder.append("keyword arguments:\n")

                for(parameter in mPositionalParameters) {
                    builder.append("  ${parameter.help}\n")
                }

                builder.append("\n")
            }
        }else{
            builder.append(mSubParsers.formatHelp(subCommandHierarchy))
        }

        if(isTopLevel) {
            if(epilogue != null) {
                builder.append(epilogue)
            }
        }

        return builder.toString()
    }

    /**
     * format help message
     */
    fun formatHelp() = formatHelpImpl(listOf())

    /**
     * print help message
     */
    fun printHelp() = println(formatHelp())

    /**
     * format usage message
     */
    fun formatUsage() = formatUsageImpl(listOf())

    /**
     * print help message
     */
    fun printUsage() = println(formatUsage())

    /**
     * internal implementation for [parseArgs]
     *
     * @param state parsing state
     */
    internal fun parseArgs(state: ArgumentsParsingState) {
        parse@ while(!state.isFinished) {
            if(mSubParsers.parseArgs(state)) {
                break@parse
            }

            for(parameter in mParameters) {
                if(parameter.matches(state.current)) {
                    parameter.action.doAction(parameter, state)
                    continue@parse
                }
            }

            throw ParseException("unrecognized arguments: ${state.current}")
        }

        val noPassedRequiredArguments = state.getNoPassedRequiredArguments(mParameters).map { "'${it.parameterNames[0]}'" }
        if(noPassedRequiredArguments.isNotEmpty()) {
            throw ParseException("required arguments ${noPassedRequiredArguments.joinToString(", ")} was not passed")
        }
    }

    /**
     * parse arguments
     *
     * @param arguments arguments array
     * @param throwOnError throw [ParseException] on parse error found (default: exitProcess(1))
     */
    fun parseArgs(arguments: Array<String>, throwOnError: Boolean=false): ParsedArguments {
        val preprocessedArgument = arguments.flatMap {
            it.split("=")
        }.flatMap {
            val match = Regex("""-([\w\d])+""").matchEntire(it)
            match?.groupValues?.filter { !it.startsWith("-") }?.map { "-$it" } ?: listOf(it)
        }

        val state = ArgumentsParsingState(preprocessedArgument)
        try{
            parseArgs(state)
        }catch(e: ParseException) {
            printUsage()
            println("$program: error: ${e.message}")
            if(throwOnError) {
                throw e
            }else{
                exitProcess(1)
            }
        }

        val args = state.toParsedArguments()

        if(args.has("help")) {
            println(formatHelpImpl(args.subCommandHierarchy))
            exitProcess(0)
        }



        return args
    }
}