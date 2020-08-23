package com.example.dogsgallery.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface Dao {
    @Insert
    suspend fun insertAll(vararg  dogs:DogBreed): List<Long>

    @Query("SELECT * FROM DogBreedTable")
    suspend fun getAllDogs():List<DogBreed>

    @Query("SELECT * FROM DogBreedTable WHERE uuid = :dogId")
    suspend fun getDog(dogId:Int):DogBreed

    @Query("DELETE FROM DogBreedTable")
    suspend fun deleteAllDogs()
}