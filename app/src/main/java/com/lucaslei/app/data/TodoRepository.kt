package com.lucaslei.app.data

import com.lucaslei.app.data.api.ApiClient
import com.lucaslei.app.data.model.TodoItem

class TodoRepository {
    private val api = ApiClient.cloudflareApi

    suspend fun loadTodos(): List<TodoItem> {
        val response = api.getTodoData()
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        }
        throw Exception(response.message() ?: "加载数据失败")
    }

    suspend fun saveTodos(todos: List<TodoItem>) {
        val response = api.putTodoData(todos)
        if (!response.isSuccessful) {
            throw Exception(response.message() ?: "同步数据失败")
        }
    }
}
