package com.example.data.local.repository.resume

import com.example.domain.model.resume.Resume

interface ResumeDao {
    suspend fun insert(
        categoryName: String,
        imageUrl: String
    ): Resume?

    suspend fun getAllResume(): List<Resume>?
    suspend fun getResumeById(id: Long): Resume?
    suspend fun deleteById(id: Long): Int
    suspend fun updateResume(
        id: Long,
        categoryName: String,
        imageUrl: String
    ): Int
}