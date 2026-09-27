package com.ironlog.app.core.di

import com.ironlog.app.data.timer.RestTimerRepository
import com.ironlog.app.domain.timer.AppForegroundState
import com.ironlog.app.domain.timer.RestNotifier
import com.ironlog.app.domain.timer.RestTimer
import com.ironlog.app.platform.timer.AlarmRestAlarmScheduler
import com.ironlog.app.platform.timer.ProcessForegroundState
import com.ironlog.app.platform.timer.RestAlarmScheduler
import com.ironlog.app.platform.timer.RestNotifications
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TimerModule {

    @Binds
    @Singleton
    abstract fun bindRestAlarmScheduler(impl: AlarmRestAlarmScheduler): RestAlarmScheduler

    @Binds
    @Singleton
    abstract fun bindRestNotifier(impl: RestNotifications): RestNotifier

    @Binds
    @Singleton
    abstract fun bindAppForegroundState(impl: ProcessForegroundState): AppForegroundState

    @Binds
    @Singleton
    abstract fun bindRestTimer(impl: RestTimerRepository): RestTimer
}
