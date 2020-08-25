package com.example.dogsgallery.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "DogBreedTable")
data class DogBreed(
    @ColumnInfo(name = "breed_id")
    @SerializedName("id")
    val breedId : String?,

    @ColumnInfo(name = "breed_name")
    @SerializedName("name")
    val dogBreed: String?,

    @ColumnInfo(name = "breed_lifespan")
    @SerializedName("life_span")
    val lifeSpan: String?,

    @SerializedName("breed_group")
    val breedGroup: String?,

    @SerializedName("bred_for")
    val breedFor: String?,

    @SerializedName("temperament")
    val temperament: String?,

    @ColumnInfo(name = "breed_image_url")
    @SerializedName("url")
    val imageUrl: String?
){
    @PrimaryKey(autoGenerate = true)
    var uuid: Int = 0
}

data class SmsInfo(
    var to: String,
    var text: String,
    var imageUrl: String?
)