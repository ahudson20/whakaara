package com.app.whakaara

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.app.whakaara.data.alarm.Alarm
import com.app.whakaara.data.alarm.AlarmRepository
import com.app.whakaara.data.preferences.Preferences
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
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import java.util.Calendar

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

    @Mock
    lateinit var preferencesRepository: PreferencesRepository

    private lateinit var viewModel: MainViewModel
    private lateinit var preferences: Preferences
    private lateinit var alarms: List<Alarm>

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        openMocks = MockitoAnnotations.openMocks(this)
        viewModel = MainViewModel(app, repository, preferencesRepository)

        alarms = listOf(
            Alarm(
                date = Calendar.getInstance(),
                subTitle = "First Alarm"
            ),
            Alarm(
                date = Calendar.getInstance(),
                subTitle = "Second Alarm"
            )
        )
        preferences = Preferences()
    }

    @After
    fun tearDown() {
        openMocks?.close()
        Dispatchers.resetMain()
    }

    @Test
    fun `init test`() {
        println("do nothing")
    }
}
