package com.example.noteapp.presentation.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.noteapp.presentation.NotesViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@Composable
fun Navigation(viewModel: NotesViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()
    val navHostController = rememberNavController()

    NavHost(navHostController, "NoteScreen") {
        composable("NoteScreen") {
            NoteScreen(
                viewModel = viewModel,
                state = state,
                navHost = navHostController,
                onEvent = viewModel::onEvent
            )
        }
        composable("AddNoteScreen") {
            AddNoteScreen(
                state = state,
                navHost = navHostController,
                onEvent = viewModel::onEvent,
                saveSuccess = saveSuccess
            )
        }
    }
}