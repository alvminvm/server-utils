import java.text.SimpleDateFormat
import java.util.*

private val yyyyMMdd = SimpleDateFormat("yyyy-MM-dd")
private val yyyyMMddHHmmss = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

private fun SimpleDateFormat.parseOrFormat(date: Any?): Any? {
    return try {
        when (date) {
            is String -> this.parse(date)
            is Date -> this.format(date)
            else -> null
        }
    } catch (t: Throwable) {
        null
    }
}

fun yyyyMMdd(date: Any?) = yyyyMMdd.parseOrFormat(date)

fun yyyyMMddHHmmss(date: Any?) = yyyyMMddHHmmss.parseOrFormat(date)