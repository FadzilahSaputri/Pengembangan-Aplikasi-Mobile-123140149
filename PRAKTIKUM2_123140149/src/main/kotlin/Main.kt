import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

// Data class untuk struktur Berita
data class News(val id: Int, val title: String, val category: String)

class NewsFeedSimulator {
    private val _readCount = MutableStateFlow(0)
    val readCount: StateFlow<Int> = _readCount.asStateFlow()

    fun incrementReadCount() {
        _readCount.value += 1
    }

    fun fetchNewsFlow(): Flow<News> = flow {
        val categories = listOf("TECH", "SPORTS", "BUSINESS", "ENTERTAINMENT")

        repeat(5) { id ->
            delay(1000)
            val randomCategory = categories.random()
            emit(News(id + 1, "Kotlin Coroutines Tips #${id + 1}", randomCategory))
        }
    }.flowOn(Dispatchers.IO) // Coroutines Usage: Menggunakan Dispatchers.IO

    suspend fun fetchNewsDetailAsync(newsId: Int): String = withContext(Dispatchers.IO) {
        delay(500) // Simulasi network delay
        "Ini adalah isi detail lengkap untuk berita ID-$newsId..."
    }
}

fun main() = runBlocking {
    println("=== Memulai News Feed Simulator ===")
    val simulator = NewsFeedSimulator()

    // Pantau StateFlow di background menggunakan Dispatchers.Default
    val countJob = launch(Dispatchers.Default) {
        simulator.readCount.collect { count ->
            println("[StateFlow Update] Total berita dibaca: $count")
        }
    }

    // Pipeline Flow
    simulator.fetchNewsFlow()
        .catch { e ->
            println("⚠️ Exception Terdeteksi: ${e.message}")
        }

        .filter { news -> news.category == "TECH" || news.category == "SPORTS" }

        .map { news -> " [${news.category}] ${news.title}" }

        .onEach { formattedNews ->
            println("\n[onEach Log] Memproses aliran data: $formattedNews")
        }
        // Terminal Operator: collect
        .collect { formattedNews ->
            println("Tampil di Screen: $formattedNews")

            // Coroutines Usage: async & await dengan Dispatchers.IO
            val detailDeferred = async(Dispatchers.IO) {
                simulator.fetchNewsDetailAsync(1)
            }
            val detail = detailDeferred.await()
            println("   -> Detail: $detail")

            // Update StateFlow
            simulator.incrementReadCount()
        }

    countJob.cancel()
    println("\n=== Feed Selesai ===")
}