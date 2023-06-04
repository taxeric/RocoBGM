package com.lanier.rocobgm

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Created by Eric
 * on 2023/6/2
 */
@Module
@InstallIn(SingletonComponent::class)
interface PlayEventModule {

    @Binds
    fun bindPlayEvent(event: SongEnvironment): IPlayEvent
}