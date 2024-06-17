package com.mardillu.malami.di

/**
 * Created on 19/05/2024 at 1:27 pm
 * @author mardillu
 */
import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mardillu.malami.data.PreferencesManager
import com.mardillu.malami.ui.courses.player.CommandHandler
import com.mardillu.malami.ui.service.AudioPlayerService1
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirestore() = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun providePreferencesManager(@ApplicationContext context: Context): PreferencesManager {
        return PreferencesManager(context)
    }

    @Provides
    @Singleton
    fun provideCommandHandler(@ApplicationContext context: Context): CommandHandler {
        return CommandHandler(context)
    }

    @Provides
    @Singleton
    fun provideAudioPlayerService(): AudioPlayerService1 {
        return AudioPlayerService1()
    }
    
    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

}