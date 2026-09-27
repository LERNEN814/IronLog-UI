package com.ironlog.app.testutil

import com.ironlog.app.domain.fatigue.FatigueRepository

/** In-memory FatigueRepository for ViewModel tests. */
class FakeFatigueRepository(var result: Map<String, Int> = emptyMap()) : FatigueRepository {

    val requestedAt = mutableListOf<Long>()

    override suspend fun scores(nowMillis: Long): Map<String, Int> {
        requestedAt += nowMillis
        return result
    }
}
