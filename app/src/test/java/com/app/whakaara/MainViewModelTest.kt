package com.app.whakaara

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.app.whakaara.data.alarm.AlarmRepository
import com.app.whakaara.data.preferences.PreferencesRepository
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
    private var openMocks: AutoCloseable? = null

    @Rule
    @JvmField
    var rule: TestRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    lateinit var app: Application

    @Mock
    lateinit var repository: AlarmRepository

    @Mock
    lateinit var preferencesRepository: PreferencesRepository

    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        openMocks = MockitoAnnotations.openMocks(this)
        viewModel = MainViewModel(app, repository, preferencesRepository)
    }

    @After
    fun tearDown() {
        openMocks?.close()
        Dispatchers.resetMain()
    }
}
