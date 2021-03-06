package com.example.testprojectsb

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.testprojectsb.network.service.ConnectionState
import com.example.testprojectsb.network.service.ConnectionType
import com.example.testprojectsb.network.service.IService
import com.example.testprojectsb.ui.viewmodel.ServiceConnectionViewModel
import com.example.testprojectsb.ui.viewmodel.TrampolineSchedulerProvider
import io.reactivex.Observable
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.verify
import org.mockito.Mockito


/**
 * Created by grati on 11/24/2019.
 */
class ServiceConnectionViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private var service = Mockito.mock(IService::class.java)
    private lateinit var viewModel: ServiceConnectionViewModel
    private var schedulerProvider = TrampolineSchedulerProvider()

    private val observer: Observer<ConnectionState> = mock()

    @Before
    fun before() {
        viewModel = ServiceConnectionViewModel(service, schedulerProvider)
        viewModel.getToConnectionUpdates().observeForever(observer)
    }

    @Test
    fun givenConnectedState_viewModelReceivesConnected() {
        val expectedState = ConnectionState(ConnectionType.CONNECTED)
        given(service.subscribeToConnectionUpdates()).willReturn(Observable.just(expectedState))
        viewModel.subscribeToConnectionUpdates()

        verify(observer).onChanged(ConnectionState(ConnectionType.CONNECTED))
    }

    @Test
    fun givenConnectionClosedState_viewModelReceivesConnectionClosed() {
        val expectedState = ConnectionState(ConnectionType.CONNECTION_CLOSED)
        given(service.subscribeToConnectionUpdates()).willReturn(Observable.just(expectedState))
        viewModel.subscribeToConnectionUpdates()

        verify(observer).onChanged(ConnectionState(ConnectionType.CONNECTION_CLOSED))
    }
}