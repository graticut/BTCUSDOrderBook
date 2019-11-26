package com.example.testprojectsb

import com.example.testprojectsb.network.model.Ticker
import com.example.testprojectsb.network.service.IService
import com.example.testprojectsb.ui.viewmodel.TickerViewModel
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
class TickerViewModelTest {

    private var service = Mockito.mock(IService::class.java)
    private lateinit var viewModel: TickerViewModel
    private var schedulerProvider = TrampolineSchedulerProvider()

    @Before
    fun before() {
        viewModel = TickerViewModel(service, schedulerProvider)
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