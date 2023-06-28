package com.lanier.rocobgm.datastore

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

/**
 * Created by 幻弦让叶
 * on 2023/6/27
 */
@Module
@InstallIn(SingletonComponent::class)
object AppPreferencesModule {

    @Provides
    fun provideAppPreferences(
        @ApplicationContext context: Context
    ):AppPreferences = AppPreferences(context)
}