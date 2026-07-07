package com.example.noteapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Upsert
    suspend fun upsertNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("SELECT * FROM note ORDER by dateAdded")
    fun getNoteOrderByDateAdded(): Flow<List<Note>>

    @Query("SELECT * FROM note ORDER by title ASC")
    fun getNoteOrderByTitle(): Flow<List<Note>>

    @Query("DELETE FROM note")
    suspend fun deleteAllNotes()

    @Query("""
        SELECT * FROM note 
        WHERE lower(title) LIKE '%' || lower(:query) || '%' 
           OR description LIKE '%' || :query || '%'
        ORDER BY dateAdded
    """)
    fun searchNotesByDateAdded(query: String): Flow<List<Note>>

    @Query("""
        SELECT * FROM note 
        WHERE lower(title) LIKE '%' || lower(:query) || '%' 
           OR description LIKE '%' || :query || '%'
        ORDER BY title ASC
    """)
    fun searchNotesByTitle(query: String): Flow<List<Note>>

}