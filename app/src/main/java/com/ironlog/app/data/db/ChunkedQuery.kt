package com.ironlog.app.data.db

/** D8: SQLite allows at most 999 bound variables per statement; stay below it with one chunk. */
internal const val SQLITE_IN_CHUNK = 900

/**
 * Runs [query] for every chunk of [items] and merges the results.
 * Returns an empty list without calling [query] when there is nothing to look up.
 */
internal suspend fun <T, R> chunkedQuery(items: List<T>, query: suspend (List<T>) -> List<R>): List<R> {
    if (items.isEmpty()) return emptyList()
    return items.chunked(SQLITE_IN_CHUNK).flatMap { chunk -> query(chunk) }
}
