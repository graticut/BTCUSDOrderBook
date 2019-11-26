package com.example.testprojectsb

import com.example.testprojectsb.network.model.OrderBookItem
import com.example.testprojectsb.network.model.Transaction
import com.example.testprojectsb.network.service.IService
import com.example.testprojectsb.ui.viewmodel.OrderBookViewModel
import com.example.testprojectsb.ui.viewmodel.TrampolineSchedulerProvider
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito

/**
 * Created by grati on 11/24/2019.
 */
class OrderBookViewModelTest {

    private var service = Mockito.mock(IService::class.java)
    private lateinit var viewModel: OrderBookViewModel
    private var schedulerProvider = TrampolineSchedulerProvider()

    @Before
    fun before() {
        viewModel = OrderBookViewModel(service, schedulerProvider)
    }

    @Test
    fun subscribeToOrderBook_success() {
        val testObserver = TestObserver<List<OrderBookItem>>()

        val b1 = Transaction(7001.0, 1.1, 1)
        val a1 = Transaction(8001.0, -7.1, 1)
        val b2 = Transaction(7002.0, 2.1, 1)
        val a2 = Transaction(8002.0, -2.1, 1)
        val b3 = Transaction(7003.0, 12.1, 1)
        val a3 = Transaction(8003.0, -2.1, 1)
        val upb3 = Transaction(7003.0, 20.1, 1)
        val upa2 = Transaction(8002.0, -22.1, 1)


        val rb2 = Transaction(7002.0, 1.0, 0)
        val ra1 = Transaction(8001.0, -1.0, 0)

        given(service.subscribeToTransactionUpdates()).willReturn(Observable.just(
            b1,// Add first bid
            a1,// Add first ask
            a2, // Add second ask
            b2, // Add second bid
            b3, // Add third bid
            a3, // Add third ask
            rb2, // Remove second bid
            ra1, // Remove second ask
            upb3, // Update third bid
            upa2// Update second ask
        ))
        viewModel.subscribeToBookOrderUpdates().subscribe(testObserver)

        testObserver.assertValues(
            listOf(OrderBookItem(b1, null)), // Add first bid
            listOf(OrderBookItem(b1, a1)), // Add first ask
            listOf(OrderBookItem(b1, a2), OrderBookItem(null, a1)), // Add second ask
            listOf(OrderBookItem(b2, a2), OrderBookItem(b1, a1)),// Add second bid
            listOf(OrderBookItem(b3, a2), OrderBookItem(b2, a1), OrderBookItem(b1, null)),// Add third bid
            listOf(OrderBookItem(b3, a3), OrderBookItem(b2, a2), OrderBookItem(b1, a1)),// Add third ask
            listOf(OrderBookItem(b3, a3), OrderBookItem(b1, a2), OrderBookItem(null, a1)),// Remove second bid
            listOf(OrderBookItem(b3, a3), OrderBookItem(b1, a2)),// Remove second ask
            listOf(OrderBookItem(upb3, a3), OrderBookItem(b1, a2)),// Update third bid
            listOf(OrderBookItem(upb3, upa2), OrderBookItem(b1, a3))// Update second ask
        )
    }
}