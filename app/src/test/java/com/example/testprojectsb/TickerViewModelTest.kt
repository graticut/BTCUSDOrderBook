package com.example.testprojectsb

import com.example.testprojectsb.network.model.Ticker
import com.example.testprojectsb.network.service.IService
import com.example.testprojectsb.ui.TickerViewModel
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.*
import org.mockito.Mockito

/**
 * Created by grati on 11/24/2019.
 */
class TickerViewModelTest {

    private var service = Mockito.mock(IService::class.java)
    private lateinit var viewModel: TickerViewModel

    @Before
    fun before() {
        viewModel = TickerViewModel(service)
    }

    @Test
    fun testWSConnection_connected() {
        val testObserver = TestObserver<Ticker>()
        val ticker = Ticker(50934677.toBigDecimal(), 7000.0, 6900.0, 7100.0, 205.0, 0.0281)
        given(service.subscribeToTickerUpdates()).willReturn(Observable.just(ticker))
        viewModel.subscribeToTickerUpdates().subscribe(testObserver)
        testObserver.assertComplete()
        testObserver.assertNoErrors()
        testObserver.assertValue(ticker)
    }
}