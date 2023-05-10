package jr.brian.issarecipeapp.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jr.brian.issarecipeapp.model.local.AppDatabase
import jr.brian.issarecipeapp.model.local.RecipeDao
import jr.brian.issarecipeapp.model.repository.RepoImpl
import jr.brian.issarecipeapp.model.repository.Repository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideRepository(): Repository = RepoImpl()

    @Provides
    @Singleton
    fun provideDao(appDatabase: AppDatabase): RecipeDao = appDatabase.dao()

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "recipes"
        )
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }
}