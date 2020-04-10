import net.sileader.argparse.ArgumentParser
import kotlin.system.exitProcess

fun main(cmd: Array<String>) {
    val parser = ArgumentParser("program", epilogue = "test program")

    parser.addArgument("-v", "--version", action = "store_true", help = "show version")

    val sub = parser.addSubParser()
    val push = sub.addParser("push", "push")
    push.addArgument("-d", "--data", action = "store", help = "data")

    val pull = sub.addParser("pull", "pull")
    pull.addArgument("-b", "--byte", action = "store", help = "data")

    val args = parser.parseArgs(cmd)

    if(args.has("version")) {
        println("version")
        exitProcess(0)
    }

    println("include = ${args.get("I")}")
    println("input = ${args.get("input")}")
}