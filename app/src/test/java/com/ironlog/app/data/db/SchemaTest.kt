package com.ironlog.app.data.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.ironlog.app.testutil.ProjectFiles
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class SchemaTest {

    @Test
    fun databaseVersionIsOne() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, IronLogDatabase::class.java).build()
        try {
            val cursor = db.openHelper.writableDatabase.query("PRAGMA user_version")
            cursor.use {
                assertThat(it.moveToFirst()).isTrue()
                assertThat(it.getInt(0)).isEqualTo(1)
            }
        } finally {
            db.close()
        }
    }

    @Test
    fun exportedSchemaFileExistsForVersionOne() {
        val schema = ProjectFiles.file("app/schemas/com.ironlog.app.data.db.IronLogDatabase/1.json")
        assertThat(schema.exists()).isTrue()
        val root = Json.parseToJsonElement(schema.readText()).jsonObject
        assertThat(root["formatVersion"]?.jsonPrimitive?.int).isEqualTo(1)
        assertThat(root["database"]?.jsonObject?.get("version")?.jsonPrimitive?.int).isEqualTo(1)
    }
}
