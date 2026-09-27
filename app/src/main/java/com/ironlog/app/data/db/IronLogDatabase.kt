package com.ironlog.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ironlog.app.data.db.dao.BodyWeightDao
import com.ironlog.app.data.db.dao.ExerciseDao
import com.ironlog.app.data.db.dao.MuscleGroupDao
import com.ironlog.app.data.db.dao.WorkoutDao
import com.ironlog.app.data.db.entity.BodyWeightEntity
import com.ironlog.app.data.db.entity.ExerciseEntity
import com.ironlog.app.data.db.entity.ExerciseMuscleEntity
import com.ironlog.app.data.db.entity.MuscleGroupEntity
import com.ironlog.app.data.db.entity.SessionExerciseEntity
import com.ironlog.app.data.db.entity.WorkoutSessionEntity
import com.ironlog.app.data.db.entity.WorkoutSetEntity

@Database(
    entities = [
        MuscleGroupEntity::class,
        ExerciseEntity::class,
        ExerciseMuscleEntity::class,
        WorkoutSessionEntity::class,
        SessionExerciseEntity::class,
        WorkoutSetEntity::class,
        BodyWeightEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class IronLogDatabase : RoomDatabase() {
    abstract fun muscleGroupDao(): MuscleGroupDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun bodyWeightDao(): BodyWeightDao
}
