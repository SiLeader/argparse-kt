import net.sileader.argparse.ArgumentParser
import net.sileader.argparse.errors.ParseException
import org.junit.Assert
import org.junit.Test

class Positional {
    private val parser = ArgumentParser("test")
    init {
        parser.addArgument("data")
    }

    @Test
    fun positional() {
        val args = parser.parseArgs(arrayOf("value"), throwOnError = true)
        Assert.assertEquals("value", args.get("data"))
    }

    @Test
    fun noPassed() {
        Assert.assertThrows(ParseException::class.java) {
            parser.parseArgs(arrayOf(), throwOnError = true)
        }
    }
}