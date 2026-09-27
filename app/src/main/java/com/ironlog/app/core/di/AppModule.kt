package com.ironlog.app.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.ironlog.app.core.id.IdGenerator
import com.ironlog.app.core.id.UuidGenerator
import com.ironlog.app.core.time.Clock
import com.ironlog.app.core.time.ElapsedClock
import com.ironlog.app.core.time.SystemClock
import com.ironlog.app.core.time.SystemElapsedClock
import com.ironlog.app.data.repository.ExerciseRepositoryImpl
import com.ironlog.app.data.repository.FatigueRepositoryImpl
import com.ironlog.app.data.repository.WorkoutRepositoryImpl
import com.ironlog.app.data.settings.SettingsDataStore
import com.ironlog.app.domain.exercise.ExerciseRepository
import com.ironlog.app.domain.fatigue.FatigueRepository
import com.ironlog.app.domain.settings.SettingsRepository
import com.ironlog.app.domain.workout.WorkoutRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/** Long-lived scope for background work tied to the app process (e.g. the seed import). */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationScope

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindExerciseRepository(impl: ExerciseRepositoryImpl): ExerciseRepository

    @Binds
    @Singleton
    abstract fun bindWorkoutRepository(impl: WorkoutRepositoryImpl): WorkoutRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsDataStore): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindFatigueRepository(impl: FatigueRepositoryImpl): FatigueRepository

    companion object {

        @Provides
        @Singleton
        fun provideClock(): Clock = SystemClock

        @Provides
        @Singleton
        fun provideElapsedClock(): ElapsedClock = SystemElapsedClock

        @Provides
        @Singleton
        fun provideIdGenerator(): IdGenerator = UuidGenerator

        @Provides
        @Singleton
        @ApplicationScope
        fun provideApplicationScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        @Provides
        @Singleton
        fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
            PreferenceDataStoreFactory.create { context.preferencesDataStoreFile("settings") }
    }
}
