package com.example.noteapp.presentation

import com.example.noteapp.data.Note

//All user actions in the Notes screen.
sealed interface NotesEvents {
    data class UpdateTitle(val title: String) : NotesEvents
    data class UpdateDescription(val description: String) : NotesEvents
    data object NoteSort : NotesEvents
    data class DeleteNote(val note: Note) : NotesEvents
    data class SaveNote(val title: String, val description: String) : NotesEvents
    data class EditNote(val note: Note) : NotesEvents
    data object ClearError : NotesEvents
    data object DeleteAllNotes : NotesEvents
    data object ToggleSearch : NotesEvents
    data class UpdateSearchQuery(val query: String) : NotesEvents
    data object ClearSearch : NotesEvents
}