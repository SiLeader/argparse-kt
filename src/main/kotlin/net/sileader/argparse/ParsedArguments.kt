package net.sileader.argparse

class ParsedArguments(val subCommandHierarchy: List<String>, private val mArguments: Map<String, List<String>>, private val mFlags: Set<String>) {

    fun has(optionName: String) = mFlags.contains(optionName) || mArguments.containsKey(optionName)

    fun getList(optionName: String) = mArguments[optionName]

    fun get(optionName: String) = getList(optionName)?.firstOrNull()
}