package com.example.passwordmanager.ioc

import android.content.Context
import com.example.passwordmanager.data.coil.ImageLoader
import coil.disk.DiskCache
import com.example.passwordmanager.data.database.DataBaseSource
import com.example.passwordmanager.data.network.Network
import com.example.passwordmanager.data.repositories.DatabaseRepository
import com.example.passwordmanager.data.repositories.NetworkRepository

class ApplicationComponent(context: Context) {
    private val network: Network = Network()
    private val database: DataBaseSource = DataBaseSource(context)
    private val imageLoader = ImageLoader(context).imageLoader

    private val networkRepository: NetworkRepository = NetworkRepository(network.api)
    private val databaseRepository: DatabaseRepository = DatabaseRepository(database.dao)

    val viewModelFactory: ViewModelFactory = ViewModelFactory(
        networkRepository = networkRepository,
        databaseRepository = databaseRepository
    )
}