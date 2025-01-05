package com.app.whakaara.logic

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.whakaara.core.GeneralUtils.Companion.convertStringToColour
import com.whakaara.core.di.IoDispatcher
import com.whakaara.core.widget.AppWidget
import com.whakaara.data.datastore.PreferencesDataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WidgetConfigViewModel @Inject constructor(
    private val app: Application,
    private val preferencesDataStoreRepository: PreferencesDataStoreRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : AndroidViewModel(application = app) {
    private val _textColor = MutableStateFlow<Color?>(null)
    val textColor: StateFlow<Color?> = _textColor.asStateFlow()

    private val _backgroundColor = MutableStateFlow<Color?>(null)
    val backgroundColor: StateFlow<Color?> = _backgroundColor.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesDataStoreRepository.readTextColour()
                .collect { textColourString ->
                    _textColor.value = convertStringToColour(textColourString)
                }
        }

        viewModelScope.launch {
            preferencesDataStoreRepository.readBackgroundColour()
                .collect { backgroundColourString ->
                    _backgroundColor.value = convertStringToColour(backgroundColourString)
                }
        }
    }

    fun saveWidgetConfig(
        backgroundColor: Color,
        textColor: Color,
        appWidgetId: Int
    ) = viewModelScope.launch(ioDispatcher) {
        val glanceAppWidget: GlanceAppWidget = AppWidget()
        val serializedBackground = Gson().toJson(backgroundColor)
        val serializedText = Gson().toJson(textColor)
        val glanceId = GlanceAppWidgetManager(app.applicationContext).getGlanceIdBy(appWidgetId)

        // Save colors to DataStore
        preferencesDataStoreRepository.saveColour(serializedBackground, serializedText)

        // Update the app widget state
        updateAppWidgetState(app.applicationContext, PreferencesGlanceStateDefinition, glanceId) { prefs ->
            prefs.toMutablePreferences().apply {
                this[backgroundKey] = serializedBackground
                this[textKey] = serializedText
            }
        }

        // Apply the update to the widget
        glanceAppWidget.update(app.applicationContext, glanceId)
    }

    companion object {
        val backgroundKey = stringPreferencesKey("colour_background")
        val textKey = stringPreferencesKey("colour_text")
    }
}
