package net.sileader.argparse

/**
 * parsing state
 *
 * @param mArguments passed arguments list
 */
class ArgumentsParsingState(private val mArguments: List<String>) {
    /**
     * current parsing argument index
     */
    private var mCurrentIndex = 0

    /**
     * increment index
     */
    fun advance() = ++mCurrentIndex

    /**
     * check not remaining arugments
     */
    val isFinished: Boolean
        get() = mCurrentIndex >= mArguments.size

    /**
     * get current argument
     */
    val current: String
        get() = mArguments[mCurrentIndex]

    private val mParsedArguments = mutableMapOf<String, MutableList<String>>()
    private val mParsedFlags = mutableSetOf<String>()
    private val mSubCommandHierarchy = mutableListOf<String>()

    /**
     * put argument
     *
     * @param key parameter key name
     * @param value argument value
     */
    fun putArgument(key: String, value: String) = mParsedArguments.put(key, mutableListOf(value))

    /**
     * put argument
     *
     * @param key parameter key name
     * @param value argument values
     */
    fun putArgument(key: String, value: MutableList<String>) = mParsedArguments.put(key, value)

    /**
     * append argument
     *
     * @param key parameter key name
     * @param value argument value
     */
    fun appendArgument(key: String, value: String) = appendArgument(key, mutableListOf(value))

    /**
     * append argument
     *
     * @param key parameter key name
     * @param value argument value
     */
    fun appendArgument(key: String, value: MutableList<String>) {
        if (mParsedArguments.containsKey(key)) {
            mParsedArguments[key]?.addAll(value)
        }else{
            putArgument(key, value)
        }
    }

    /**
     * check argument exists
     *
     * @param key parameter key name
     */
    fun existsArgument(key: String) = mParsedArguments.containsKey(key)

    /**
     * set flag
     *
     * @param key parameter key name
     */
    fun setFlag(key: String) = mParsedFlags.add(key)

    /**
     * append sub command hierarchy
     */
    fun appendSubCommand(subCommand: String) = mSubCommandHierarchy.add(subCommand)

    /**
     * convert to [ParsedArguments]
     */
    fun toParsedArguments() = ParsedArguments(mSubCommandHierarchy, mParsedArguments, mParsedFlags)

    fun getNoPassedRequiredArguments(descriptors: List<ParameterDescriptor>)
            = descriptors
        .filter { it.required }
        .filter { !existsArgument(it.canonicalName) && !mParsedFlags.contains(it.canonicalName) }
        .toList()
}
