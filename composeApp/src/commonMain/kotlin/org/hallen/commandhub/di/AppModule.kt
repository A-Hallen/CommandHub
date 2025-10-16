package org.hallen.commandhub.di

import org.hallen.commandhub.data.repository.CategoryRepositoryImpl
import org.hallen.commandhub.data.repository.CommandRepositoryImpl
import org.hallen.commandhub.data.repository.ExecutionRepositoryImpl
import org.hallen.commandhub.data.repository.ProjectRepositoryImpl
import org.hallen.commandhub.domain.repository.CategoryRepository
import org.hallen.commandhub.domain.repository.CommandRepository
import org.hallen.commandhub.domain.repository.ExecutionRepository
import org.hallen.commandhub.domain.repository.ProjectRepository
import org.hallen.commandhub.presentation.MainViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Módulo principal de Koin con todas las dependencias
 */
val appModule = module {
    // Repositorios
    singleOf(::CommandRepositoryImpl) bind CommandRepository::class
    singleOf(::ProjectRepositoryImpl) bind ProjectRepository::class
    singleOf(::CategoryRepositoryImpl) bind CategoryRepository::class
    singleOf(::ExecutionRepositoryImpl) bind ExecutionRepository::class
    
    // ViewModels
    viewModelOf(::MainViewModel)
}

/**
 * Módulo específico de plataforma (se definirá en jvmMain)
 */
expect val platformModule: Module
