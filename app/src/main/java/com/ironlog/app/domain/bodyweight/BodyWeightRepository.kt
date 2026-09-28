package com.ironlog.app.domain.bodyweight

import com.ironlog.app.domain.model.BodyWeight

interface BodyWeightRepository {
    suspend fun upsert(localDate: String, weightGrams: Int)
    suspend fun recent(limit: Int = 30): List<BodyWeight>
    suspend fun delete(localDate: String)
}
