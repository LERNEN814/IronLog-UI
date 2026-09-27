package com.ironlog.app.data.db

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Test

/** D8: the IN-clause chunking helper stays below SQLite's variable limit. */
class ChunkedQueryTest {

    @Test
    fun splitsIntoChunksOfNineHundredAndMergesResults() = runBlocking {
        val calls = mutableListOf<Int>()

        val result = chunkedQuery((1..2_500).toList()) { chunk ->
            calls += chunk.size
            chunk
        }

        assertThat(calls).containsExactly(900, 900, 700).inOrder()
        assertThat(result).hasSize(2_500)
        assertThat(result.first()).isEqualTo(1)
        assertThat(result.last()).isEqualTo(2_500)
    }

    @Test
    fun exactlyNineHundredItemsUseASingleCall() = runBlocking {
        val calls = mutableListOf<Int>()

        val result = chunkedQuery((1..900).toList()) { chunk ->
            calls += chunk.size
            chunk
        }

        assertThat(calls).containsExactly(900)
        assertThat(result).hasSize(900)
    }

    @Test
    fun emptyListSkipsTheQuery() = runBlocking {
        var called = false

        val result = chunkedQuery(emptyList<String>()) { chunk ->
            called = true
            chunk
        }

        assertThat(called).isFalse()
        assertThat(result).isEmpty()
    }
}
