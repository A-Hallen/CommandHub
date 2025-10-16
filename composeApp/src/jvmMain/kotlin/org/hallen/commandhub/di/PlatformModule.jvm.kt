package org.hallen.commandhub.di

import app.cash.sqldelight.ColumnAdapter
import org.hallen.commandhub.data.database.CommandEntity
import org.hallen.commandhub.data.database.CommandHubDatabase
import org.hallen.commandhub.data.database.DatabaseDriverFactory
import org.hallen.commandhub.execution.CommandExecutor
import org.hallen.commandhub.execution.CommandExecutorInterface
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Módulo de dependencias específico de JVM
 */
actual val platformModule: Module = module {
    // Database
    single { DatabaseDriverFactory() }
    single { 
        val driverFactory: DatabaseDriverFactory = get()
        CommandHubDatabase(driver = driverFactory.createDriver())
    }
    
    // Command Executor
    singleOf(::CommandExecutor) bind CommandExecutorInterface::class
}
