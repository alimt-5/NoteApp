package com.example.noteapp.presentation

import com.example.noteapp.data.Note

//UI state for the Notes screen.
data class NotesState(
    val noteList: List<Note> = emptyList(),
    val noteTitle: String = "",
    val noteDescription: String = "",
    val isSortedByDateAdded: Boolean = true,
    val error: String? = null
)
