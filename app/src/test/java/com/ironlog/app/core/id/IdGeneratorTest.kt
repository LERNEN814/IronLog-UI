package com.ironlog.app.core.id

import com.google.common.truth.Truth.assertThat
import java.util.UUID
import org.junit.Test

class IdGeneratorTest {

    @Test
    fun uuidGeneratorProducesDistinctUuidStrings() {
        val ids = List(100) { UuidGenerator.newId() }
        assertThat(ids.toSet()).hasSize(100)
        ids.forEach { id -> assertThat(UUID.fromString(id).toString()).isEqualTo(id) }
    }

    @Test
    fun fixedIdGeneratorIncrementsFromOne() {
        val generator = FixedIdGenerator()
        assertThat(generator.newId()).isEqualTo("fixed-1")
        assertThat(generator.newId()).isEqualTo("fixed-2")
        assertThat(generator.newId()).isEqualTo("fixed-3")
    }
}
