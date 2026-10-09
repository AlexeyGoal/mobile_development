import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

// Модель для хранения информации о приложении
data class AppInfo(
    val app: String,
    val rating: Double?,
    val reviews: Long,
    val size: String,
    val installs: Long,
    val type: String,
    val price: Boolean, // true = платное, false = бесплатное
    val contentRating: String,
    val genres: List<String>,
    val lastUpdated: String, // ISO 8601
    val currentVer: String,
    val androidVer: Int? // минимальный API
)

// Словарь переводов категорий на русский
val categoryTranslations = mapOf(
    "ART_AND_DESIGN" to "Искусство и дизайн",
    "AUTO_AND_VEHICLES" to "Авто и транспорт",
    "BEAUTY" to "Красота",
    "BOOKS_AND_REFERENCE" to "Книги и справочники",
    "BUSINESS" to "Бизнес",
    "COMICS" to "Комиксы",
    "COMMUNICATION" to "Общение",
    "DATING" to "Знакомства",
    "EDUCATION" to "Образование",
    "ENTERTAINMENT" to "Развлечения",
    "EVENTS" to "События",
    "FINANCE" to "Финансы",
    "FOOD_AND_DRINK" to "Еда и напитки",
    "HEALTH_AND_FITNESS" to "Здоровье и фитнес",
    "HOUSE_AND_HOME" to "Дом и быт",
    "LIBRARIES_AND_DEMO" to "Библиотеки и демо",
    "LIFESTYLE" to "Стиль жизни",
    "GAME" to "Игры",
    "FAMILY" to "Семья",
    "MEDICAL" to "Медицина",
    "SOCIAL" to "Социальные сети",
    "SHOPPING" to "Покупки",
    "PHOTOGRAPHY" to "Фотография",
    "SPORTS" to "Спорт",
    "TRAVEL_AND_LOCAL" to "Путешествия",
    "TOOLS" to "Инструменты",
    "PRODUCTIVITY" to "Продуктивность",
    "PERSONALIZATION" to "Персонализация",
    "PARENTING" to "Воспитание детей",
    "WEATHER" to "Погода",
    "VIDEO_PLAYERS" to "Видеоплееры",
    "NEWS_AND_MAGAZINES" to "Новости и журналы",
    "MAPS_AND_NAVIGATION" to "Карты и навигация"
)

// Соответствие версий Android и API уровней
val androidVersionToApi = mapOf(
    "1.5" to 3, "1.6" to 4, "2.0" to 5, "2.0.1" to 6, "2.1" to 7,
    "2.2" to 8, "2.3" to 9, "2.3.3" to 10, "3.0" to 11, "3.1" to 12,
    "3.2" to 13, "4.0" to 14, "4.0.3" to 15, "4.1" to 16, "4.2" to 17,
    "4.3" to 18, "4.4" to 19, "4.4W" to 20, "5.0" to 21, "5.1" to 22,
    "6.0" to 23, "7.0" to 24, "7.1" to 25, "8.0" to 26, "8.1" to 27,
    "9.0" to 28
)

/**
 * Парсинг строки CSV с учётом кавычек
 */
fun parseCsvLine(line: String): List<String> {
    val result = mutableListOf<String>()
    val current = StringBuilder()
    var inQuotes = false
    var i = 0

    while (i < line.length) {
        val c = line[i]
        when {
            c == '"' -> {
                if (inQuotes && i + 1 < line.length && line[i + 1] == '"') {
                    current.append('"')
                    i++
                } else {
                    inQuotes = !inQuotes
                }
            }
            c == ',' && !inQuotes -> {
                result.add(current.toString())
                current.clear()
            }
            else -> current.append(c)
        }
        i++
    }
    result.add(current.toString())
    return result
}

/**
 * Преобразование Installs к числу
 * "10,000+" -> 10000, "1,000,000+" -> 1000000
 */
fun parseInstalls(installs: String): Long {
    val cleaned = installs.replace(",", "").replace("+", "").trim()
    return cleaned.toLongOrNull() ?: 0L
}

/**
 * Преобразование Price к boolean
 * "0" или "Free" -> false (бесплатное)
 * "$4.99" -> true (платное)
 */
fun parsePrice(price: String, type: String): Boolean {
    if (type.equals("Free", ignoreCase = true)) return false
    val cleaned = price.replace("$", "").replace(",", "").trim()
    val value = cleaned.toDoubleOrNull() ?: 0.0
    return value > 0.0
}

/**
 * Преобразование Android Ver к минимальному API
 * "4.0.3 and up" -> 15
 * "Varies with device" -> null
 */
fun parseAndroidVer(androidVer: String): Int? {
    val cleaned = androidVer
        .replace(" and up", "")
        .replace(" - ", " ")
        .trim()

    if (cleaned.contains("Varies", ignoreCase = true)) return null

    // Если диапазон вида "4.0.3 - 7.1.1", берём первую версию
    val firstPart = cleaned.split(" ").firstOrNull()?.trim() ?: return null

    // Точное совпадение
    androidVersionToApi[firstPart]?.let { return it }

    // Если версия не найдена точно, ищем наиболее подходящую
    // Например "5.0" для "5.0"
    val majorMinor = firstPart.split(".").take(2).joinToString(".")
    return androidVersionToApi[majorMinor]
}

/**
 * Преобразование даты к ISO 8601
 * "January 7, 2018" -> "2018-01-07"
 */
fun parseDate(dateStr: String): String {
    return try {
        val formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH)
        val date = LocalDate.parse(dateStr.trim(), formatter)
        date.toString() // ISO 8601: yyyy-MM-dd
    } catch (e: Exception) {
        dateStr // возвращаем как есть, если не удалось распарсить
    }
}

/**
 * Экранирование строки для JSON
 */
fun escapeJson(s: String): String {
    return s.replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t")
}

/**
 * Преобразование AppInfo в JSON-строку
 */
fun appInfoToJson(app: AppInfo): String {
    val genresJson = app.genres.joinToString(", ") { "\"${escapeJson(it)}\"" }
    val ratingStr = app.rating?.toString() ?: "null"
    val androidApiStr = app.androidVer?.toString() ?: "null"

    return """
        {
          "app": "${escapeJson(app.app)}",
          "rating": $ratingStr,
          "reviews": ${app.reviews},
          "size": "${escapeJson(app.size)}",
          "installs": ${app.installs},
          "type": "${escapeJson(app.type)}",
          "price": ${app.price},
          "content_rating": "${escapeJson(app.contentRating)}",
          "genres": [$genresJson],
          "last_updated": "${app.lastUpdated}",
          "current_ver": "${escapeJson(app.currentVer)}",
          "android_ver_api": $androidApiStr
        }""".trimIndent()
}

fun main() {
    val inputFile = File("googleplaystore.csv")
    val outputFile = File("googleplaystore.json")

    if (!inputFile.exists()) {
        println("Файл ${inputFile.absolutePath} не найден")
        return
    }

    val lines = inputFile.readLines()
    if (lines.isEmpty()) {
        println("Файл пуст")
        return
    }

    // Пропускаем заголовок
    val dataLines = lines.drop(1)

    // Группируем приложения по категориям
    // Map<Category, MutableList<AppInfo>>
    val groupedApps = mutableMapOf<String, MutableList<AppInfo>>()

    for ((index, line) in dataLines.withIndex()) {
        if (line.isBlank()) continue

        try {
            val fields = parseCsvLine(line)
            if (fields.size < 13) {
                println("Пропущена строка ${index + 2}: недостаточно полей (${fields.size})")
                continue
            }

            val category = fields[1].trim()
            if (category.isEmpty()) continue

            val rating = fields[2].trim().toDoubleOrNull()
            val reviews = fields[3].trim().toLongOrNull() ?: 0L
            val size = fields[4].trim()
            val installs = parseInstalls(fields[5])
            val type = fields[6].trim()
            val price = parsePrice(fields[7], type)
            val contentRating = fields[8].trim()
            val genres = fields[9].split(";").map { it.trim() }.filter { it.isNotEmpty() }
            val lastUpdated = parseDate(fields[10])
            val currentVer = fields[11].trim()
            val androidVer = parseAndroidVer(fields[12])

            val appInfo = AppInfo(
                app = fields[0].trim(),
                rating = rating,
                reviews = reviews,
                size = size,
                installs = installs,
                type = type,
                price = price,
                contentRating = contentRating,
                genres = genres,
                lastUpdated = lastUpdated,
                currentVer = currentVer,
                androidVer = androidVer
            )

            groupedApps.getOrPut(category) { mutableListOf() }.add(appInfo)

        } catch (e: Exception) {
            println("Ошибка при обработке строки ${index + 2}: ${e.message}")
        }
    }

    // Формируем JSON
    val jsonBuilder = StringBuilder()
    jsonBuilder.append("{\n")

    // Сортируем категории по алфавиту для стабильного вывода
    val sortedCategories = groupedApps.keys.sorted()

    sortedCategories.forEachIndexed { catIndex, category ->
        val apps = groupedApps[category] ?: return@forEachIndexed
        // Сортируем приложения по количеству установок (по убыванию)
        val sortedApps = apps.sortedByDescending { it.installs }

        val russianName = categoryTranslations[category] ?: category

        jsonBuilder.append("  \"${escapeJson(category)}\": {\n")
        jsonBuilder.append("    \"name_ru\": \"${escapeJson(russianName)}\",\n")
        jsonBuilder.append("    \"count\": ${sortedApps.size},\n")
        jsonBuilder.append("    \"apps\": [\n")

        sortedApps.forEachIndexed { appIndex, app ->
            val json = appInfoToJson(app)
            // Добавляем отступы
            val indentedJson = json.lines().joinToString("\n") { "      $it" }
            jsonBuilder.append(indentedJson)
            if (appIndex < sortedApps.size - 1) jsonBuilder.append(",")
            jsonBuilder.append("\n")
        }

        jsonBuilder.append("    ]\n")
        jsonBuilder.append("  }")
        if (catIndex < sortedCategories.size - 1) jsonBuilder.append(",")
        jsonBuilder.append("\n")
    }

    jsonBuilder.append("}\n")

    outputFile.writeText(jsonBuilder.toString())

    println("Готово! Обработано категорий: ${groupedApps.size}")
    println("Всего приложений: ${groupedApps.values.sumOf { it.size }}")
    println("Результат сохранён в: ${outputFile.absolutePath}")
}