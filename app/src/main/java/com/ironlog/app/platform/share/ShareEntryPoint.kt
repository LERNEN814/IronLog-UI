package com.ironlog.app.platform.share

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/** Lets Composables obtain the share exporter without injecting the repository layer. */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface ShareEntryPoint {
    fun shareImageExporter(): ShareImageExporter
}
