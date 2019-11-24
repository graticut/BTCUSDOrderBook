package com.example.testprojectsb

import com.example.testprojectsb.network.service.WSUtil
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

/**
 * Created by grati on 11/23/2019.
 */
@RunWith(MockitoJUnitRunner::class)
class OrderBookTest {

    @Test
    fun givenOrderBookMessage_shouldReturnOrderBookObject() {
        //        Add first bid
        var orderBook = WSUtil.buildOrderBookFromRawMessage("[383136,[7000.0, 1, 1.1]]")
        assertEquals(orderBook.size, 1)
        assertNull(orderBook.first().ask)
        //        Add first ask
        orderBook = WSUtil.buildOrderBookFromRawMessage("[383136,[8000.0, 1, -7.1]]")
        assertEquals(orderBook.size, 1)
        //        Add second ask
        orderBook = WSUtil.buildOrderBookFromRawMessage("[383136,[8001.0, 1, -2.1]]")
        assertEquals(orderBook.size, 2)
        //        First item should contain second added ask and the bid
        assertEquals(orderBook.first().bid?.price, 7000.0)
        assertEquals(orderBook.first().ask?.price, 8001.0)
        //        Add second bid
        orderBook = WSUtil.buildOrderBookFromRawMessage("[383136,[7001.0, 1, 2.1]]")
        assertEquals(orderBook.size, 2)
        assertEquals(orderBook.first().bid?.price, 7001.0)
        assertEquals(orderBook.first().ask?.price, 8001.0)
        //        Add third bid
        orderBook = WSUtil.buildOrderBookFromRawMessage("[383136,[7002.0, 1, 12.1]]")
        assertEquals(orderBook.size, 3)
        assertNull(orderBook.last().ask)
        assertEquals(orderBook.first().bid?.price, 7002.0)
        assertEquals(orderBook.first().ask?.price, 8001.0)
        //       Add third ask
        orderBook = WSUtil.buildOrderBookFromRawMessage("[383136,[8002.0, 1, -2.1]]")
        assertEquals(orderBook.size, 3)
        assertNotNull(orderBook.last().ask)
        assertEquals(orderBook.first().bid?.price, 7002.0)
        assertEquals(orderBook.first().ask?.price, 8002.0)
        //        Remove a bid
        orderBook = WSUtil.buildOrderBookFromRawMessage("[383136,[7002.0, 0, 1.0]]")
        assertEquals(orderBook.size, 3)
        assertEquals(orderBook.filter { it.bid?.price ==  7002.0}.size, 0)
        assertEquals(orderBook.first().bid?.price, 7001.0)
        assertNull(orderBook.last().bid)
        //        Remove an ask
        orderBook = WSUtil.buildOrderBookFromRawMessage("[383136,[8000.0, 0, -1.0]]")
        assertEquals(orderBook.size, 2)
        assertEquals(orderBook.filter { it.ask?.price ==  8000.0}.size, 0)
        assertEquals(orderBook.last().ask?.price, 8001.0)
    }

}