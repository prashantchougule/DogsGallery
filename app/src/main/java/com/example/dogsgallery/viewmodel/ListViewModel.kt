package com.example.dogsgallery.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dogsgallery.model.DogBreed
import com.example.dogsgallery.model.DogDatabase
import com.example.dogsgallery.model.DogsApiService
import com.example.dogsgallery.util.NotificationsHelper
import com.example.dogsgallery.util.SharedPreferencesHelper
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch

class ListViewModel(application : Application) : BaseViewModel(application) {

    private var prefHelper = SharedPreferencesHelper(getApplication())
    private var refrshTime = 5 * 60 * 1000 * 1000 * 1000L
    private val dogsService = DogsApiService()
    private val disposible = CompositeDisposable()
    val dogs = MutableLiveData<List<DogBreed>>()
    val dogsLoadError = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()

    fun refresh(){
        val updateTime = prefHelper.getUpdateTime()
        if(updateTime != null && updateTime != 0L && System.nanoTime() - updateTime < refrshTime){
            fetchFromDatabase()
        }else{
            fetchFromRemote()
        }
    }

    fun refreshFromRemote(){
        fetchFromRemote()
    }

    private fun fetchFromDatabase(){
        loading.value = true;
        launch {
            val dogs = DogDatabase(getApplication()).dogDao().getAllDogs()
            dogRetrived(dogs)
            Toast.makeText(getApplication(),"retrieved from DB",Toast.LENGTH_SHORT).show()
        }
    }
    private fun fetchFromRemote(){
        loading.value = true
        disposible.add(
            dogsService.getDogs()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object :DisposableSingleObserver<List<DogBreed>>(){
                    override fun onSuccess(doglist: List<DogBreed>) {
                        storeDogToDB(doglist)
                        Toast.makeText(getApplication(),"retrieved from remote",Toast.LENGTH_SHORT).show()
                        NotificationsHelper(getApplication()).createNotification()
                    }

                    override fun onError(e: Throwable) {
                        dogsLoadError.value = true
                        loading.value = false
                        e.printStackTrace()
                    }

                })
        )
    }

    private fun dogRetrived(dogList: List<DogBreed>){
        dogs.value = dogList
        dogsLoadError.value = false
        loading.value= false
    }
    private fun storeDogToDB(dogList :List<DogBreed>){
        launch {
            val dao = DogDatabase(getApplication()).dogDao()
            dao.deleteAllDogs()
            val result = dao.insertAll(*dogList.toTypedArray())
            var i = 0
            while (i< dogList.size){
                dogList[i].uuid = result[i].toInt()
                ++i
            }
            dogRetrived(dogList)
        }
        prefHelper.saveUpdatedTime(System.nanoTime())
    }

    override fun onCleared() {
        super.onCleared()
        disposible.clear()
    }
}