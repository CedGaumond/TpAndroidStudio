import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.tp1.Model.api.CardStatisticsDao

@Database(entities = [StaticticRepository::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cardStatisticsDao(): CardStatisticsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "statistics_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
