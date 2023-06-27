package com.app.whakaara

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.app.whakaara.data.AlarmRepository
import com.app.whakaara.logic.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {
    var openMocks: AutoCloseable? = null

    @Rule
    @JvmField
    var rule: TestRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    lateinit var app: Application

    @Mock
    lateinit var repository: AlarmRepository

    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        openMocks = MockitoAnnotations.openMocks(this)
        viewModel = MainViewModel(app, repository)
    }

    @After
    fun tearDown() {
        openMocks?.close()
        Dispatchers.resetMain()
    }
}
