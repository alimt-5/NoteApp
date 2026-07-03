package com.example.noteapp.presentation.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.noteapp.presentation.NotesEvents
import com.example.noteapp.presentation.NotesState
import com.example.noteapp.presentation.components.AutoDirectionTextField

@Composable
fun AddNoteScreen(
    state: NotesState,
    navHost: NavHostController,
    onEvent: (NotesEvents) -> Unit,
    saveSuccess: Boolean
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        onEvent(NotesEvents.ClearError)
    }

    LaunchedEffect(state.error) {
        if (!state.error.isNullOrBlank()) {
            Toast.makeText(context, state.error, Toast.LENGTH_SHORT).show()
            onEvent(NotesEvents.ClearError)
        }
    }
    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            navHost.popBackStack()
            onEvent(NotesEvents.ClearError)
        }
    }

    BackHandler(enabled = true) {
        if (state.noteTitle.isBlank() && state.noteDescription.isBlank()) {
            navHost.popBackStack()
        } else {
            onEvent(NotesEvents.SaveNote(state.noteTitle, state.noteDescription))
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                onClick = {
                    onEvent(
                        NotesEvents.SaveNote(
                            state.noteTitle,
                            state.noteDescription
                        )
                    )
                }) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "SavingNote",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        Column(
            Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                AutoDirectionTextField(
                    value = state.noteTitle,
                    onValueChange = { onEvent(NotesEvents.UpdateTitle(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    hint = "Title",
                    textSize = 18f,
                    isBold = true,
                    maxLines = 2,
                )
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                )

                AutoDirectionTextField(
                    value = state.noteDescription,
                    onValueChange = { onEvent(NotesEvents.UpdateDescription(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    hint = "Description",
                    textSize = 14f,
                    isBold = false,
                )
            }
        }
    }

}