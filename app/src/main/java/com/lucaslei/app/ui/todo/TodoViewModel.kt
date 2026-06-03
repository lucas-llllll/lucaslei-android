package com.lucaslei.app.ui.todo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lucaslei.app.data.TodoRepository
import com.lucaslei.app.data.model.TodoItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

enum class SyncStatus {
    IDLE, SYNCING, SUCCESS, FAILED
}

data class TodoUiState(
    val todos: List<TodoItem> = emptyList(),
    val isLoading: Boolean = false,
    val syncStatus: SyncStatus = SyncStatus.IDLE,
    val errorMessage: String? = null
)

class TodoViewModel(private val repository: TodoRepository = TodoRepository()) : ViewModel() {

    private val _uiState = MutableStateFlow(TodoUiState())
    val uiState: StateFlow<TodoUiState> = _uiState.asStateFlow()

    init {
        loadTodos()
    }

    fun loadTodos() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val todos = repository.loadTodos()
                _uiState.value = _uiState.value.copy(
                    todos = todos,
                    isLoading = false,
                    syncStatus = SyncStatus.SUCCESS
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    syncStatus = SyncStatus.FAILED,
                    errorMessage = e.message ?: "加载失败"
                )
            }
        }
    }

    fun syncTodos() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(syncStatus = SyncStatus.SYNCING)
            try {
                repository.saveTodos(_uiState.value.todos)
                _uiState.value = _uiState.value.copy(syncStatus = SyncStatus.SUCCESS)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    syncStatus = SyncStatus.FAILED,
                    errorMessage = e.message ?: "同步失败"
                )
            }
        }
    }

    fun addTodo(title: String, note: String) {
        val newTodo = TodoItem(
            id = UUID.randomUUID().toString(),
            title = title,
            done = false,
            note = note,
            updatedAt = System.currentTimeMillis()
        )
        val updatedTodos = _uiState.value.todos + newTodo
        _uiState.value = _uiState.value.copy(todos = updatedTodos)
        syncTodos()
    }

    fun updateTodo(id: String, title: String, note: String) {
        val updatedTodos = _uiState.value.todos.map { todo ->
            if (todo.id == id) {
                todo.copy(title = title, note = note, updatedAt = System.currentTimeMillis())
            } else {
                todo
            }
        }
        _uiState.value = _uiState.value.copy(todos = updatedTodos)
        syncTodos()
    }

    fun toggleTodo(id: String) {
        val updatedTodos = _uiState.value.todos.map { todo ->
            if (todo.id == id) {
                todo.copy(done = !todo.done, updatedAt = System.currentTimeMillis())
            } else {
                todo
            }
        }
        _uiState.value = _uiState.value.copy(todos = updatedTodos)
        syncTodos()
    }

    fun deleteTodo(id: String) {
        val updatedTodos = _uiState.value.todos.filter { it.id != id }
        _uiState.value = _uiState.value.copy(todos = updatedTodos)
        syncTodos()
    }
}
