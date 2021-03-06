package com.example.wschat.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.wschat.ext.date

@Entity(tableName = "message")
data class MessageItem(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var id: Long,
    @ColumnInfo(name = "time") val time: String,
    @ColumnInfo(name = "sender") val sender: String,
    @ColumnInfo(name = "content") val content: String
) {
    var checkBox: Boolean = false //是否选中
    override fun toString(): String {
        return "$content\n$id,${time.toLong().date()},$sender"
    }
}