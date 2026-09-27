package com.ironlog.app.core.di

import android.content.Context
import androidx.room.Room
import com.ironlog.app.data.db.ALL_MIGRATIONS
import com.ironlog.app.data.db.IronLogDatabase
import com.ironlog.app.data.db.dao.BodyWeightDao
import com.ironlog.app.data.db.dao.ExerciseDao
import com.ironlog.app.data.db.dao.MuscleGroupDao
import com.ironlog.app.data.db.dao.WorkoutDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): IronLogDatabase =
        Room.databaseBuilder(context, IronLogDatabase::class.java, "ironlog.db")
            .addMigrations(*ALL_MIGRATIONS)
            .build()

    @Provides
    fun provideMuscleGroupDao(database: IronLogDatabase): MuscleGroupDao = database.muscleGroupDao()

    @Provides
    fun provideExerciseDao(database: IronLogDatabase): ExerciseDao = database.exerciseDao()

    @Provides
    fun provideWorkoutDao(database: IronLogDatabase): WorkoutDao = database.workoutDao()

    @Provides
    fun provideBodyWeightDao(database: IronLogDatabase): BodyWeightDao = database.bodyWeightDao()
}
