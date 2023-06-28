package com.lanier.rocobgm

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

/**
 * Created by Eric
 * on 2023/6/2
 */
@Module
@InstallIn(ViewModelComponent::class)
abstract class PlayEventModule {

    @Binds
    abstract fun bindPlayEvent(event: SongEnvironment): IPlayEvent
}