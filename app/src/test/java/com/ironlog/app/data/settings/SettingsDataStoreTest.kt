package com.ironlog.app.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.ironlog.app.domain.model.DistanceUnit
import com.ironlog.app.domain.model.UserSettings
import com.ironlog.app.domain.model.WeightUnit
import java.io.File
import java.util.UUID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class SettingsDataStoreTest {

    private lateinit var scope: CoroutineScope
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var repository: SettingsDataStore

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        val file = File(context.filesDir, "test-settings-${UUID.randomUUID()}.preferences_pb")
        dataStore = PreferenceDataStoreFactory.create(scope = scope) { file }
        repository = SettingsDataStore(dataStore)
    }

    @After
    fun tearDown() {
        scope.cancel()
    }

    @Test
    fun defaultsMatchDataModel() = runBlocking {
        val settings = repository.observeSettings().first()

        assertThat(settings).isEqualTo(UserSettings())
        assertThat(settings.weightUnit).isEqualTo(WeightUnit.KG)
        assertThat(settings.distanceUnit).isEqualTo(DistanceUnit.KM)
        assertThat(settings.defaultRestSeconds).isEqualTo(120)
        assertThat(settings.restPresetsSeconds).isEqualTo("30,90,120,150")
        assertThat(settings.keepScreenOnDuringWorkout).isTrue()
        assertThat(settings.showRirField).isFalse()
        assertThat(settings.themeMode).isEqualTo("system")
        assertThat(settings.seedVersion).isEqualTo(0)
    }

    @Test
    fun writesAreReadBack() = runBlocking {
        repository.setWeightUnit(WeightUnit.LB)
        repository.setDistanceUnit(DistanceUnit.MI)
        repository.setDefaultRestSeconds(90)
        repository.setRestPresetsSeconds("45,60,90,120")
        repository.setKeepScreenOnDuringWorkout(false)
        repository.setShowRirField(true)
        repository.setThemeMode("dark")

        val settings = repository.observeSettings().first()

        assertThat(settings.weightUnit).isEqualTo(WeightUnit.LB)
        assertThat(settings.distanceUnit).isEqualTo(DistanceUnit.MI)
        assertThat(settings.defaultRestSeconds).isEqualTo(90)
        assertThat(settings.restPresetsSeconds).isEqualTo("45,60,90,120")
        assertThat(settings.keepScreenOnDuringWorkout).isFalse()
        assertThat(settings.showRirField).isTrue()
        assertThat(settings.themeMode).isEqualTo("dark")
    }
}
