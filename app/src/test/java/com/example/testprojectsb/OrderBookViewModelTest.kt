package com.example.testprojectsb

import com.example.testprojectsb.network.model.OrderBookItem
import com.example.testprojectsb.network.model.Transaction
import com.example.testprojectsb.network.service.IService
import com.example.testprojectsb.ui.OrderBookViewModel
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

    @Before
    fun before() {
        viewModel = OrderBookViewModel(service)
    }

    @Test
    fun subscribeToOrderBook_success() {
        val testObserver = TestObserver<List<OrderBookItem>>()
        val orderBookItems = listOf(
            OrderBookItem(Transaction(7000.0, 3.0), Transaction(7000.0, -2.0)),
            OrderBookItem(Transaction(7001.0, 2.0), Transaction(7002.0, 11.0))
        )
        given(service.subscribeToBookOrderUpdates()).willReturn(Observable.just(orderBookItems))
        viewModel.subscribeToBookOrderUpdates().subscribe(testObserver)
        testObserver.assertComplete()
        testObserver.assertNoErrors()
        testObserver.assertValue(orderBookItems)
    }
}