import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity(tableName = "card_statistics")
data class CardStatistic(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cardCode: String,
    val odds: Float
)

@Dao
interface CardStatisticsDao {
    // Insert a card statistic
    @Insert
    suspend fun insert(statistic: CardStatistic)

    // Get all card statistics
    @Query("SELECT * FROM card_statistics")
    suspend fun getAll(): List<CardStatistic>
}
