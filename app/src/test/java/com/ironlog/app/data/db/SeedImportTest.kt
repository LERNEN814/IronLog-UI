package com.ironlog.app.data.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.ironlog.app.core.time.FixedClock
import com.ironlog.app.data.seed.SeedImporter
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class SeedImportTest {

    private lateinit var db: IronLogDatabase
    private lateinit var importer: SeedImporter

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, IronLogDatabase::class.java).build()
        importer = SeedImporter(context, db.muscleGroupDao(), db.exerciseDao(), FixedClock(1_000_000L))
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun importsAllMusclesAndExercises() = runBlocking {
        importer.importFromAssets()

        assertThat(db.muscleGroupDao().count()).isEqualTo(16)
        assertThat(db.exerciseDao().count()).isAtLeast(60)
        assertThat(db.exerciseDao().countWithoutPrimaryMuscle()).isEqualTo(0)
        assertThat(db.muscleGroupDao().getById("chest")?.recoveryHalfLifeHours).isEqualTo(48)
    }

    @Test
    fun secondImportDoesNotDuplicateRows() = runBlocking {
        importer.importFromAssets()
        val exerciseCount = db.exerciseDao().count()

        importer.importFromAssets()

        assertThat(db.muscleGroupDao().count()).isEqualTo(16)
        assertThat(db.exerciseDao().count()).isEqualTo(exerciseCount)
    }

    @Test
    fun importDoesNotOverwriteExistingRows() = runBlocking {
        importer.importFromAssets()
        db.openHelper.writableDatabase.execSQL(
            "UPDATE exercise SET name_zh = 'edited' WHERE id = 'barbell_bench_press'",
        )

        importer.importFromAssets()

        assertThat(db.exerciseDao().getById("barbell_bench_press")?.nameZh).isEqualTo("edited")
    }

    @Test
    fun builtInFlagsAndTimestampsAreSet() = runBlocking {
        importer.importFromAssets()

        val exercise = db.exerciseDao().getById("barbell_bench_press")
        assertThat(exercise?.isCustom).isEqualTo(0)
        assertThat(exercise?.isArchived).isEqualTo(0)
        assertThat(exercise?.createdAt).isEqualTo(1_000_000L)
    }
}
