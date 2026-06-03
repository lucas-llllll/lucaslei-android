package com.lucaslei.app.data.api

import com.lucaslei.app.data.model.Recipe
import com.lucaslei.app.data.model.RenovationItem
import com.lucaslei.app.data.model.TodoItem
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Cloudflare Worker API interface.
 *
 * Base URL should point to the Cloudflare Worker endpoint.
 *
 * Endpoints:
 * - todo:   GET/PUT {baseUrl}/data/tododata
 * - recipe: GET/PUT {baseUrl}/data/cp:favs:{userId}
 * - reno:   GET/PUT {baseUrl}/data/{id}
 */
interface CloudflareApi {

    // ── Todo ──────────────────────────────────────────────

    @GET("data/tododata")
    suspend fun getTodoData(): Response<List<TodoItem>>

    @PUT("data/tododata")
    suspend fun putTodoData(@Body todos: List<TodoItem>): Response<List<TodoItem>>

    // ── Recipe (favorites) ────────────────────────────────

    @GET("data/cp:favs/{userId}")
    suspend fun getRecipeData(
        @Path("userId") userId: String
    ): Response<Recipe>

    @PUT("data/cp:favs/{userId}")
    suspend fun putRecipeData(
        @Path("userId") userId: String,
        @Body recipe: Recipe
    ): Response<Recipe>

    // ── Renovation ────────────────────────────────────────

    @GET("data/{id}")
    suspend fun getRenovationData(
        @Path("id") id: String
    ): Response<RenovationItem>

    @PUT("data/{id}")
    suspend fun putRenovationData(
        @Path("id") id: String,
        @Body item: RenovationItem
    ): Response<RenovationItem>
}
