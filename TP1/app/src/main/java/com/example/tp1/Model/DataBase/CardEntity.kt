import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class DbCard(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, // Define primary key
    val code: String,
    val imageUrl: String,
    val value: String,
    val signe: String
)
