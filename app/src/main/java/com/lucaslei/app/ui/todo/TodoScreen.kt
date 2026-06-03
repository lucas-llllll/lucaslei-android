package com.lucaslei.app.ui.todo

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lucaslei.app.data.model.TodoItem
import com.lucaslei.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen(
    viewModel: TodoViewModel = viewModel(factory = TodoViewModelFactory())
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }
    var editingTodo by remember { mutableStateOf<TodoItem?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "装修备忘录",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                actions = {
                    SyncButton(
                        syncStatus = uiState.syncStatus,
                        onSync = { viewModel.syncTodos() }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryBlue,
                    titleContentColor = OnPrimary,
                    actionIconContentColor = OnPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingTodo = null
                    showDialog = true
                },
                containerColor = PrimaryBlue,
                contentColor = OnPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "添加待办")
            }
        },
        containerColor = Background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = PrimaryBlue
                    )
                }
                uiState.todos.isEmpty() -> {
                    EmptyState(modifier = Modifier.align(Alignment.Center))
                }
                else -> {
                    TodoList(
                        todos = uiState.todos,
                        onToggle = { id -> viewModel.toggleTodo(id) },
                        onDelete = { id -> viewModel.deleteTodo(id) },
                        onEdit = { todo ->
                            editingTodo = todo
                            showDialog = true
                        }
                    )
                }
            }
        }
    }

    if (showDialog) {
        TodoEditDialog(
            todo = editingTodo,
            onDismiss = { showDialog = false },
            onConfirm = { title, note ->
                if (editingTodo != null) {
                    viewModel.updateTodo(editingTodo!!.id, title, note)
                } else {
                    viewModel.addTodo(title, note)
                }
                showDialog = false
            }
        )
    }
}

@Composable
private fun SyncButton(
    syncStatus: SyncStatus,
    onSync: () -> Unit
) {
    IconButton(onClick = onSync) {
        when (syncStatus) {
            SyncStatus.SYNCING -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = OnPrimary,
                    strokeWidth = 2.dp
                )
            }
            SyncStatus.SUCCESS -> {
                Icon(
                    Icons.Default.CloudDone,
                    contentDescription = "同步成功",
                    tint = OnPrimary
                )
            }
            SyncStatus.FAILED -> {
                Icon(
                    Icons.Default.CloudOff,
                    contentDescription = "同步失败",
                    tint = Color(0xFFFFCDD2)
                )
            }
            SyncStatus.IDLE -> {
                Icon(
                    Icons.Default.CloudSync,
                    contentDescription = "同步",
                    tint = OnPrimary
                )
            }
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "📝",
            fontSize = 48.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "暂无待办，点击+添加",
            color = OnSurfaceVariant,
            fontSize = 16.sp
        )
    }
}

@Composable
private fun TodoList(
    todos: List<TodoItem>,
    onToggle: (String) -> Unit,
    onDelete: (String) -> Unit,
    onEdit: (TodoItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = todos,
            key = { it.id }
        ) { todo ->
            TodoItemCard(
                todo = todo,
                onToggle = { onToggle(todo.id) },
                onDelete = { onDelete(todo.id) },
                onEdit = { onEdit(todo) }
            )
        }
    }
}

@Composable
private fun TodoItemCard(
    todo: TodoItem,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (todo.done) Color(0xFFF0F0F0) else Surface,
        label = "cardBackground"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = todo.done,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = PrimaryBlue,
                    uncheckedColor = PrimaryBlueLight
                )
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp, end = 4.dp)
            ) {
                Text(
                    text = todo.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (todo.done) OnSurfaceVariant else OnSurface,
                    textDecoration = if (todo.done) TextDecoration.LineThrough else TextDecoration.None,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (todo.note.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = todo.note,
                        fontSize = 13.sp,
                        color = OnSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            IconButton(onClick = onEdit) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "编辑",
                    tint = PrimaryBlueLight,
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "删除",
                    tint = Error,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun TodoEditDialog(
    todo: TodoItem?,
    onDismiss: () -> Unit,
    onConfirm: (title: String, note: String) -> Unit
) {
    var title by remember { mutableStateOf(todo?.title ?: "") }
    var note by remember { mutableStateOf(todo?.note ?: "") }
    val isEdit = todo != null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isEdit) "编辑待办" else "新增待办",
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("标题") },
                    placeholder = { Text("输入待办标题") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        cursorColor = PrimaryBlue,
                        focusedLabelColor = PrimaryBlue
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("备注") },
                    placeholder = { Text("添加备注（可选）") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        cursorColor = PrimaryBlue,
                        focusedLabelColor = PrimaryBlue
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(title.trim(), note.trim())
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text(
                    text = if (isEdit) "保存" else "添加",
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消", color = OnSurfaceVariant)
            }
        }
    )
}
