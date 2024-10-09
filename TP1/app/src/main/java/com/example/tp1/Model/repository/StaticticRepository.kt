
class StatisticsRepository(private val dao: CardStatisticsDao) {

    // Save a card statistic to the database
    suspend fun saveCardStatistic(cardCode: String, odds: Float) {
        val statistic = CardStatistic(cardCode = cardCode, odds = odds) // Corrected line
        dao.insert(statistic)
    }

    // Retrieve all card statistics from the database
    suspend fun getAllStatistics(): List<CardStatistic> {
        return dao.getAll()
    }
}
