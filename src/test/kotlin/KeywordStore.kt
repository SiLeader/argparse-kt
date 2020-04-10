import net.sileader.argparse.ArgumentParser
import org.junit.Assert
import org.junit.Test

class KeywordStore {
    private val parser = ArgumentParser("test")
    init {
        parser.addArgument("--data")
    }

    @Test
    fun pass() {
        val args = parser.parseArgs(arrayOf("--data", "value"), throwOnError = true)
        Assert.assertEquals("value", args.get("data"))
    }
}