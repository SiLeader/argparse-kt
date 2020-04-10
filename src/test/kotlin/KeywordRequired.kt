import net.sileader.argparse.ArgumentParser
import net.sileader.argparse.errors.ParseException
import org.junit.Assert
import org.junit.Test

class KeywordRequired {
    private val parser = ArgumentParser("test")
    init {
        parser.addArgument("--data", required = true)
    }

    @Test
    fun required() {
        Assert.assertThrows(ParseException::class.java) {
            parser.parseArgs(arrayOf(), throwOnError = true)
        }
    }

    @Test
    fun requiredPassed() {
        val args = parser.parseArgs(arrayOf("--data", "value"), throwOnError = true)
        Assert.assertEquals("value", args.get("data"))
    }
}