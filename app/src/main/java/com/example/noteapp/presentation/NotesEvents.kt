package com.example.noteapp.presentation

import com.example.noteapp.data.Note

//All user actions in the Notes screen.
sealed interface NotesEvents {
    data class UpdateTitle(val title: String) : NotesEvents
    data class UpdateDescription(val description: String) : NotesEvents
    object NoteSort : NotesEvents
    data class DeleteNote(val note: Note) : NotesEvents
    data class SaveNote(val title: String, val description: String) : NotesEvents
    data class EditNote(val note: Note) : NotesEvents
    data object ClearError : NotesEvents
    object DeleteAllNotes : NotesEvents
}