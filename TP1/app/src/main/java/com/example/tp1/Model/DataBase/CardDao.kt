package com.example.tp1.Model.DataBase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.tp1.Model.api.Card

@Dao
interface CardDao {
    @Query("SELECT * FROM cards")
    fun getAllCards(): List<Card>

    @Insert
    fun insertCard(card: Card)
}
