package com.example.hockey.db


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.tp1.Model.DataBase.CardDao
import com.example.tp1.Model.api.Card

@Database(entities = [Card::class], version = 37, exportSchema = false)
abstract class CardsDB : RoomDatabase() {
    abstract val dao: CardDao

    companion object {
        @Volatile
        private var INSTANCE: CardsDB? = null

        fun getInstance(context: Context): CardsDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CardsDB::class.java,
                    "BDBlackJack"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
