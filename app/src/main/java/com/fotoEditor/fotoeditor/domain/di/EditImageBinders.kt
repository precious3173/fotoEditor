package com.fotoEditor.fotoeditor.domain.di


import com.fotoEditor.fotoeditor.data.EditImageMapperImpl
import com.fotoEditor.fotoeditor.data.EditImageRepositoryImpl
import com.fotoEditor.fotoeditor.data.FileHelperImpl
import com.fotoEditor.fotoeditor.domain.EditImageMapper
import com.fotoEditor.fotoeditor.domain.EditImageRepository
import com.fotoEditor.fotoeditor.domain.FileHelper
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