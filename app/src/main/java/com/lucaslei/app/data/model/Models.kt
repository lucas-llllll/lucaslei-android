package com.lucaslei.app.data.model

// ─── Todo ───────────────────────────────────────────────────────────────

/**
 * Represents a single to-do item synced via Cloudflare.
 *
 * GET/PUT {baseUrl}/data/tododata
 */
data class TodoItem(
    val id: String = "",
    val title: String = "",
    val done: Boolean = false,
    val note: String = "",
    val updatedAt: Long = 0L
)

// ─── Recipe ─────────────────────────────────────────────────────────────

/**
 * Represents a recipe (favorite) synced via Cloudflare.
 *
 * GET/PUT {baseUrl}/data/cp:favs:{userId}
 */
data class Recipe(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val tags: List<String> = emptyList(),
    val ingredients: String = "",
    val steps: String = "",
    val isFavorite: Boolean = false
)

// ─── Renovation Item ────────────────────────────────────────────────────

/**
 * Represents a renovation / purchase item synced via Cloudflare.
 *
 * GET/PUT {baseUrl}/data/{id}
 */
data class RenovationItem(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val brand: String = "",
    val spec: String = "",
    val qty: Int = 0,
    val price: Double = 0.0,
    val channel: String = "",
    val status: String = "",
    val note: String = "",
    val updatedAt: Long = 0L
)
