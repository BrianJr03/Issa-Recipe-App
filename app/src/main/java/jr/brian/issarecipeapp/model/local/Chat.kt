package jr.brian.issarecipeapp.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chats")
data class Chat(
    @PrimaryKey val fullTimeStamp: String,
    val text: String,
    val dateSent: String,
    val timeSent: String,
    val senderLabel: String,
)