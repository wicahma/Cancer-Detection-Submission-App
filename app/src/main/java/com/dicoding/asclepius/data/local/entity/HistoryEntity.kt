package com.dicoding.asclepius.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_history")
class HistoryEntity(
    @field:ColumnInfo(name = "id")
    @field:PrimaryKey(autoGenerate = true)
    val id: Int? = null,

    @field:ColumnInfo(name = "prediction_result")
    val predictionResult: String,

    @field:ColumnInfo(name = "image_name")
    val imageName: String,

    @field:ColumnInfo(name = "confidence_score")
    val confidenceScore: String,
)