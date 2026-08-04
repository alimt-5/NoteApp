package com.example.noteapp.di

import android.app.Application
import com.example.noteapp.utils.TtsManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TtsModule {

    @Provides
    @Singleton
    fun provideTtsManager(
        app: Application
    ): TtsManager {
        return TtsManager(app)
    }
}