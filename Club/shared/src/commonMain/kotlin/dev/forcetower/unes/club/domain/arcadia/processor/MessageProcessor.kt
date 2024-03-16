package dev.forcetower.unes.club.domain.arcadia.processor

import dev.forcetower.unes.club.data.storage.database.GeneralDatabase

//class MessagesProcessor(
//    private val page: MessagesDataPage,
//    private val database: GeneralDatabase,
//    private val notified: Boolean = false
//) {
//    override suspend fun execute() {
//        val messages = page.messages
//        database.messageQueries.insertIgnoring(messages.map { Message.fromMessage(it, notified) })
//
//        val newMessages = database.messageDao().getNewMessages()
//        database.messageDao().setAllNotified()
//        newMessages.forEach { it.notify(context) }
//    }
//}