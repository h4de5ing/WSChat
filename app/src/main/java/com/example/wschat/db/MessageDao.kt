package com.example.wschat.db

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MessageDao {
    @Query("SELECT * FROM message")
    fun selectAll(): DataSource.Factory<Int, MessageItem>

    @Insert
    fun insertMessage(vararg messageItem: MessageItem)

    @Query("DELETE FROM message where id=(:id)")
    fun deleteMessage(id: Int)

}