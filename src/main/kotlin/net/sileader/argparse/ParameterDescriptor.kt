package net.sileader.argparse

/**
 * parameter information descriptor
 *
 * @param parameterNames parameter name
 * @param action action object
 * @param req required
 * @param mHelp help message
 */
class ParameterDescriptor(
    val parameterNames: List<String>,
    val action: Action,
    req: Boolean?,
    mHelp: String) {

    /**
     * canonical parameter name
     * get first long parameter name without leading '-' characters
     */
    val canonicalName = (parameterNames.firstOrNull { Regex("""(--[\w\d_][\w\d\-_]*|[\w\d][\w\d\-_]*)""").matches(it) } ?: parameterNames[0]).trimStart('-')

    /**
     * check flag or keyword parameter
     */
    val isFlagOrKeyword = parameterNames[0].startsWith("-")

    /**
     * check matching to argument
     */
    fun matches(argumentKey: String): Boolean = if(isFlagOrKeyword) {
        parameterNames.contains(argumentKey)
    }else{
        !argumentKey.startsWith("-")
    }

    /**
     * get shortest name
     */
    private val mShortName = parameterNames.reduce { a, b -> if(a.length < b.length) a else b }

    /**
     * usage fragment
     */
    private val mUsage = mShortName + if(isFlagOrKeyword){
        when(action.valueCount) {
            0 -> ""
            1 -> " VALUE"
            else -> " VALUE..."
        }
    }else{
        ""
    }

    private val mDefaultRequireState = !isFlagOrKeyword

    val required = req ?: mDefaultRequireState

    /**
     * usage message string
     */
    val usage = if(required) {
        mUsage
    }else{
        "[$mUsage]"
    }

    /**
     * help message string
     */
    val help = parameterNames.sortedBy { it.length }.joinToString(", ") + "    " + mHelp
}
