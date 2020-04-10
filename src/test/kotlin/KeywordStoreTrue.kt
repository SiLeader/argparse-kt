import net.sileader.argparse.ArgumentParser
import net.sileader.argparse.errors.ParseException
import org.junit.Assert
import org.junit.Test

class KeywordStoreTrue {

    private val parser = ArgumentParser("test")
    init {
        parser.addArgument("-v", "--version", action = "store_true")
    }

    @Test
    fun okV() {
        val args = parser.parseArgs(arrayOf("-v"), throwOnError = true)
        Assert.assertTrue(args.has("version"))
    }

    @Test
    fun failC() {
        Assert.assertThrows(ParseException::class.java) {
            parser.parseArgs(arrayOf("-c"), throwOnError = true)
        }
    }

    @Test
    fun okVersion() {
        val args = parser.parseArgs(arrayOf("--version"), throwOnError = true)
        Assert.assertTrue(args.has("version"))
    }
}
