package com.example.noteapp.di

import android.app.Application
import androidx.room.Room
import com.example.noteapp.data.NoteDao
import com.example.noteapp.data.NoteDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun providesDatabase(app: Application): NoteDataBase {
        return Room.databaseBuilder(
            context = app,
            NoteDataBase::class.java,
            "note.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideNoteDao(database: NoteDataBase): NoteDao {
        return database.noteDao
    }
}