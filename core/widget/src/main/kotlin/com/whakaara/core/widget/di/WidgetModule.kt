package com.whakaara.core.widget.di

import android.content.Context
import com.whakaara.core.WidgetUpdater
import com.whakaara.core.widget.WidgetUpdaterImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object WidgetModule {
    @Provides
    fun provideWidgetUpdater(
        @ApplicationContext context: Context
    ): WidgetUpdater = WidgetUpdaterImpl(context)
}
