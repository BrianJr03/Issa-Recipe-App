package jr.brian.issarecipeapp.model.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AppDataStore @Inject constructor(private val context: Context) {
    companion object {
        private val Context.dataStore:
                DataStore<Preferences> by preferencesDataStore("app-data-store")
        val API_KEY = stringPreferencesKey("user-api-key")
        val DIETARY_RESTRICTIONS = stringPreferencesKey("dietary-restrictions")
        val FOOD_ALLERGIES = stringPreferencesKey("food-allergies")
        val ASK_CONTEXT = stringPreferencesKey("ask-context")
        val IS_IMAGE_GENERATION_ENABLED = stringPreferencesKey("is-image-generation-enabled")
    }

    val getApiKey: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[API_KEY]
    }

    suspend fun saveApiKey(value: String) {
        context.dataStore.edit { preferences ->
            preferences[API_KEY] = value
        }
    }

    val getAskContext: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[ASK_CONTEXT]
    }

    suspend fun saveAskContext(askContext: String) {
        context.dataStore.edit { preferences ->
            preferences[ASK_CONTEXT] = askContext
        }
    }

    val getIsImageGenerationEnabled: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[IS_IMAGE_GENERATION_ENABLED]
    }

    suspend fun saveIsImageGenerationEnabled(isEnabled: String) {
        context.dataStore.edit { preferences ->
            preferences[IS_IMAGE_GENERATION_ENABLED] = isEnabled
        }
    }

    val getDietaryRestrictions: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[DIETARY_RESTRICTIONS]
    }

    suspend fun saveDietaryRestrictions(value: String) {
        context.dataStore.edit { preferences ->
            preferences[DIETARY_RESTRICTIONS] = value
        }
    }

    val getFoodAllergies: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[FOOD_ALLERGIES]
    }

    suspend fun saveFoodAllergies(value: String) {
        context.dataStore.edit { preferences ->
            preferences[FOOD_ALLERGIES] = value
        }
    }
}