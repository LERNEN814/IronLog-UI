package com.ironlog.app.data.repository

import com.ironlog.app.core.id.IdGenerator
import com.ironlog.app.core.time.Clock
import com.ironlog.app.data.db.dao.BodyWeightDao
import com.ironlog.app.data.db.entity.BodyWeightEntity
import com.ironlog.app.domain.bodyweight.BodyWeightRepository
import com.ironlog.app.domain.model.BodyWeight
import javax.inject.Inject

class BodyWeightRepositoryImpl @Inject constructor(
    private val dao: BodyWeightDao,
    private val clock: Clock,
    private val idGenerator: IdGenerator,
) : BodyWeightRepository {
    override suspend fun upsert(localDate: String, weightGrams: Int) {
        val now = clock.nowMillis()
        val existing = dao.getByDate(localDate)
        dao.upsert(BodyWeightEntity(existing?.id ?: idGenerator.newId(), localDate, weightGrams, now, existing?.createdAt ?: now, now))
    }

    override suspend fun recent(limit: Int): List<BodyWeight> = dao.getRecent(limit).map { it.toDomain() }

    override suspend fun delete(localDate: String) {
        dao.getByDate(localDate)?.let { dao.delete(it.id) }
    }
}
