package com.example.noteapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteapp.data.Note
import com.example.noteapp.data.NoteDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class NotesViewModel @Inject constructor(
    private val noteDao: NoteDao
) : ViewModel() {

    //Sorting state – changing this triggers a new database query
    private val sortOrder = MutableStateFlow(true)

    //User input fields – changing these does NOT trigger any database query
    private val titleInput = MutableStateFlow("")
    private val descriptionInput = MutableStateFlow("")

    // The note currently being edited. If null, we are in "create new note" mode.
    private var editingNote: Note? = null

    // Flow to hold validation errors or any error messages to show in the UI.
    // Setting this value will update the state and trigger a Toast in the UI.
    private val errorFlow = MutableStateFlow<String?>(null)

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()

    // Database data flow – only re-executes when sortOrder changes
    // flatMapLatest automatically cancels the previous flow to avoid redundant queries
    private val notesFlow = sortOrder.flatMapLatest {
        if (it) {
            noteDao.getNoteOrderByDateAdded()
        } else {
            noteDao.getNoteOrderByTitle()
        }
    }

    //Combine all flows into a single UI state
    //Any change in notes, title, description, or sort order will update the UI
    val state = combine(
        notesFlow,
        titleInput,
        descriptionInput,
        sortOrder,
        errorFlow
    ) { notes, title, desc, sort, error ->
        NotesState(
            noteList = notes,
            noteTitle = title,
            noteDescription = desc,
            isSortedByDateAdded = sort,
            error = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NotesState()
    )

    //Handle all user events
    fun onEvent(events: NotesEvents) {
        when (events) {
            is NotesEvents.DeleteNote -> {
                viewModelScope.launch {
                    noteDao.deleteNote(events.note)
                }
            }

            NotesEvents.NoteSort -> {
                sortOrder.value = !sortOrder.value
            }

            is NotesEvents.UpdateDescription -> {
                descriptionInput.value = events.description
            }

            is NotesEvents.UpdateTitle -> {
                titleInput.value = events.title
            }

            is NotesEvents.SaveNote -> {
                if (events.title.isBlank()) {
                    errorFlow.value = "Title can not be empty"
                    _saveSuccess.value = false
                    return
                }
                val note = if (editingNote != null) {
                    editingNote!!.copy(
                        title = events.title,
                        description = events.description
                    )
                } else {
                    Note(
                        title = events.title,
                        description = events.description,
                        dateAdded = System.currentTimeMillis()
                    )
                }

                viewModelScope.launch {
                    noteDao.upsertNote(note)
                    errorFlow.value = null
                    editingNote = null
                    titleInput.value = ""
                    descriptionInput.value = ""
                    _saveSuccess.value =true
                }
            }

            is NotesEvents.EditNote -> {
                editingNote = events.note
                titleInput.value = events.note.title
                descriptionInput.value = events.note.description
                _saveSuccess.value =false
            }

            NotesEvents.ClearError -> {
                errorFlow.value = null
                _saveSuccess.value = false
            }
            NotesEvents.DeleteAllNotes -> {
                viewModelScope.launch {
                    noteDao.deleteAllNotes()
                }
            }
        }
    }

}

