package com.example.noteapp.presentation

import android.content.Intent
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

    private val sortOrder = MutableStateFlow(true)

    private val titleInput = MutableStateFlow("")
    private val descriptionInput = MutableStateFlow("")

    private val errorFlow = MutableStateFlow<String?>(null)

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val isSearchActive = MutableStateFlow(false)

    private var editingNote: Note? = null

    private val notesFlow = combine(
        sortOrder,
        _searchQuery,
        isSearchActive
    ) { isSortedByDate, query, isActive ->
        Triple(isSortedByDate, query, isActive)
    }.flatMapLatest { (isSortedByDate, query, isActive) ->
        if (isActive && query.isNotBlank()) {
            if (isSortedByDate) noteDao.searchNotesByDateAdded(query)
            else noteDao.searchNotesByTitle(query)
        } else {
            if (isSortedByDate) noteDao.getNoteOrderByDateAdded()
            else noteDao.getNoteOrderByTitle()
        }
    }

    val state: StateFlow<NotesState> = combine(
        listOf(
            notesFlow,
            titleInput,
            descriptionInput,
            sortOrder,
            errorFlow,
            isSearchActive
        )
    ) { values ->
        val notes = values[0] as List<Note>
        val title = values[1] as String
        val desc = values[2] as String
        val sort = values[3] as Boolean
        val error = values[4] as String?
        val isActive = values[5] as Boolean

        NotesState(
            noteList = notes,
            noteTitle = title,
            noteDescription = desc,
            isSortedByDateAdded = sort,
            error = error,
            isSearchActive = isActive
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NotesState()
    )

    fun onEvent(event: NotesEvents) {
        when (event) {

            is NotesEvents.UpdateTitle -> titleInput.value = event.title

            is NotesEvents.UpdateDescription -> descriptionInput.value = event.description

            NotesEvents.NoteSort -> sortOrder.value = !sortOrder.value

            is NotesEvents.DeleteNote -> viewModelScope.launch { noteDao.deleteNote(event.note) }

            NotesEvents.DeleteAllNotes -> viewModelScope.launch { noteDao.deleteAllNotes() }

            is NotesEvents.EditNote -> {
                editingNote = event.note
                titleInput.value = event.note.title
                descriptionInput.value = event.note.description
            }

            is NotesEvents.SaveNote -> {
                if (event.title.isBlank() || event.description.isBlank()) {
                    errorFlow.value = "The title and description cannot be empty"
                    _saveSuccess.value = false
                    return
                }
                val note = if (editingNote != null) {
                    editingNote!!.copy(
                        title = event.title,
                        description = event.description
                    )
                } else {
                    Note(
                        title = event.title,
                        description = event.description,
                        dateAdded = System.currentTimeMillis()
                    )
                }
                viewModelScope.launch {
                    noteDao.upsertNote(note)
                    editingNote = null
                    titleInput.value = ""
                    descriptionInput.value = ""
                    errorFlow.value = null
                    _saveSuccess.value = true
                }
            }

            is NotesEvents.UpdateSearchQuery -> {
                _searchQuery.value = event.query
            }

            NotesEvents.ToggleSearch -> {
                val newState = !isSearchActive.value
                isSearchActive.value = newState
                if (!newState) _searchQuery.value = ""
            }

            NotesEvents.ClearSearch -> {
                isSearchActive.value = false
                _searchQuery.value = ""
                errorFlow.value = null
                _saveSuccess.value = false
            }

            NotesEvents.ClearError -> {
                errorFlow.value = null
                _saveSuccess.value = false
            }
        }
    }

    fun handleIncomingIntent(intent: Intent?) {
        intent?.let {
            if (Intent.ACTION_SEND == it.action && it.type == "text/plain") {
                val sharedText = it.getStringExtra(Intent.EXTRA_TEXT)
                if (!sharedText.isNullOrBlank()) {
                    viewModelScope.launch {
                        onEvent(
                            NotesEvents.SaveNote(
                                title = "New Note",
                                description = sharedText
                            )
                        )
                    }
                }
            }
        }
    }
}