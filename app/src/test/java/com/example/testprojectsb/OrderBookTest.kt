package com.example.testprojectsb

import com.example.testprojectsb.network.service.OrderBookBuilder
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

/**
 * Created by grati on 11/23/2019.
 */
@RunWith(MockitoJUnitRunner::class)
class OrderBookTest {

    private val builder = OrderBookBuilder()

    @Test
    fun buildTransaction_success() {
        val t = builder.buildTransactionFromRawMessage("[1,[7000.0, 1, 2.1]]")
        assertEquals(t.price, 7000.0, 0.0)
        assertEquals(t.amount, 2.1, 0.0)
        assertEquals(t.count, 1)
    }

    @Test
    fun buildSnapshotTransactions_success() {
        val snapshots = builder.buildSnapshotTransactions("[1,[[7000.0, 1, 2.1], [8000.0, 2, -2.1]]]")
        assertEquals(snapshots.size, 2)
        assertEquals(snapshots[0].price, 7000.0, 0.0)
        assertEquals(snapshots[0].amount, 2.1, 0.0)
        assertEquals(snapshots[0].count, 1)
        assertEquals(snapshots[1].price, 8000.0, 0.0)
        assertEquals(snapshots[1].amount, -2.1, 0.0)
        assertEquals(snapshots[1].count, 2)
    }

}