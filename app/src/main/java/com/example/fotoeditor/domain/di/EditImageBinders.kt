package com.example.fotoeditor.domain.di

import com.example.fotoeditor.data.EditImageMapperImpl
import com.example.fotoeditor.data.EditImageRepositoryImpl
import com.example.fotoeditor.data.FileHelperImpl
import com.example.fotoeditor.domain.EditImageMapper
import com.example.fotoeditor.domain.EditImageRepository
import com.example.fotoeditor.domain.FileHelper
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class EditImageBinders {
    @Binds
    abstract fun bindFileHelper(impl: FileHelperImpl): FileHelper

    @Binds
    abstract fun bindEditImageRepository(impl: EditImageRepositoryImpl): EditImageRepository

    @Binds
    abstract fun bindEditImageMapper(impl: EditImageMapperImpl): EditImageMapper
}