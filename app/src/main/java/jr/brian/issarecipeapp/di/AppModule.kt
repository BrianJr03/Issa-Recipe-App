package jr.brian.issarecipeapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jr.brian.issarecipeapp.model.repository.RepoImpl
import jr.brian.issarecipeapp.model.repository.Repository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideRepository(): Repository = RepoImpl()

//    @Provides
//    @Singleton
//    fun provideDao(appDatabase: AppDatabase): ChatsDao = appDatabase.dao()

//    @Provides
//    @Singleton
//    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
//        return Room.databaseBuilder(
//            appContext,
//            AppDatabase::class.java,
//            "chats"
//        )
//            .allowMainThreadQueries()
//            .fallbackToDestructiveMigration()
//            .build()
//    }
}