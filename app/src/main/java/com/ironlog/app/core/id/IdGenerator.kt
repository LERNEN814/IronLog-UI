package com.ironlog.app.core.id

import java.util.UUID

/** Source of every id in the app (UUID strings). Inject it so tests can be deterministic. */
interface IdGenerator {
    fun newId(): String
}

object UuidGenerator : IdGenerator {
    override fun newId(): String = UUID.randomUUID().toString()
}

/** Test double: produces "fixed-1", "fixed-2", ... */
class FixedIdGenerator : IdGenerator {
    private var counter = 0

    override fun newId(): String {
        counter += 1
        return "fixed-$counter"
    }
}
