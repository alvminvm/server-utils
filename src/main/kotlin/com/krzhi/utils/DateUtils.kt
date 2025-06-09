import com.krzhi.utils.annotation.Slf4j.Companion.log
import java.text.SimpleDateFormat
import java.util.*

private val yyyyMMdd = SimpleDateFormat("yyyy-MM-dd")
private val yyyyMMddHHmmss = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

private fun SimpleDateFormat.parseOrFormat(date: Any?): Any? {
    return try {
        when (date) {
            is String -> this.parse(date)
            is Date -> this.format(date)
            is Long -> parseOrFormat(Date(date))
            else -> null
        }
    } catch (t: Throwable) {
        log.warn("$date parse or format failed: $t")
        null
    }
}

fun yyyyMMdd(date: Any?) = yyyyMMdd.parseOrFormat(date)

fun yyyyMMddInMs(date: Any?): Long? = (yyyyMMdd(date) as? Date)?.time

fun yyyyMMddHHmmss(date: Any?) = yyyyMMddHHmmss.parseOrFormat(date)

fun yyyyMMddYyyyMMdd(dateRange: String, pattern: String = "yyyyMMdd", delimiter: String = "-"): Pair<Date, Date>? {
    val dates = dateRange.split(delimiter)
    if (dates.size != 2) return null

    val format = SimpleDateFormat(pattern)
    return format.parse(dates[0]) to format.parse(dates[1])
}