package com.example.testprojectsb

import com.example.testprojectsb.network.service.MessageType
import com.example.testprojectsb.network.service.WSUtil
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

/**
 * Created by grati on 11/23/2019.
 */
@RunWith(MockitoJUnitRunner::class)
class WSMessageParsingTest {

    private val tickerChannelId = "1"
    private val bookChannelId = "2"

    @Test
    fun givenValidMessage_shouldReturnTickerType() {
        val messageType = WSUtil.getMessageType("[1, [1.0, 2.0, 3,0]]", tickerChannelId, bookChannelId)
        assertEquals(messageType, MessageType.TICKER)
    }

    @Test
    fun givenValidOrderBookMessage_shouldReturnOrderBookType() {
        val messageType = WSUtil.getMessageType("[2, [1.0, 2.0, 3,0]]", tickerChannelId, bookChannelId)
        assertEquals(messageType, MessageType.ORDERBOOK)
    }

    @Test
    fun givenValidOrderBookSnapshotMessage_shouldReturnOrderBookSnapshotType() {
        val messageType = WSUtil.getMessageType("[2, [[1.0, 2.0, 3,0], [1.0, 2.0, 3,0]]]", tickerChannelId, bookChannelId)
        assertEquals(messageType, MessageType.ORDERBOOK_SNAPSHOT)
    }

    @Test
    fun givenInvalidOrderBookSnapshotMessage_shouldReturnOrderBookSnapshotType() {
        val messageType = WSUtil.getMessageType("[1, [[1.0, 2.0, 3,0], [1.0, 2.0, 3,0]]]", tickerChannelId, bookChannelId)
        assertEquals(messageType, MessageType.ORDERBOOK_SNAPSHOT)
    }

    @Test
    fun givenInvalidChannelId_shouldReturnUnknownType() {
        val messageType = WSUtil.getMessageType("[3, [1.0, 2.0, 3,0]]", tickerChannelId, bookChannelId)
        assertEquals(messageType, MessageType.UNKNOWN)
    }

    @Test
    fun givenInvalidFormat_shouldReturnUnknownType() {
        val messageType = WSUtil.getMessageType("12345", tickerChannelId, bookChannelId)
        assertEquals(messageType, MessageType.UNKNOWN)
    }

    @Test
    fun givenValidSubscribedMessage_shouldReturnSubscribedType() {
        val message = "{\"event\":\"subscribed\"}"
        val messageType = WSUtil.getMessageType(message, tickerChannelId, bookChannelId)
        assertEquals(messageType, MessageType.SUBSCRIBED)
    }

    @Test
    fun givenInvalidSubscribedMessage_shouldReturnUnknownType() {
        val message = "{\"event\":\"subscri\"}"
        val messageType = WSUtil.getMessageType(message, tickerChannelId, bookChannelId)
        assertEquals(messageType, MessageType.UNKNOWN)
    }

}