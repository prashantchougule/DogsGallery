package com.example.dogsgallery.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.dogsgallery.R
import com.example.dogsgallery.model.DogBreed
import com.example.dogsgallery.util.getProgressDrawable
import com.example.dogsgallery.util.loadImage
import kotlinx.android.synthetic.main.fragment_details.view.*
import kotlinx.android.synthetic.main.list_item.view.*
import java.util.zip.Inflater

class DogsListAdapter(val dogsList: ArrayList<DogBreed>) : RecyclerView.Adapter<DogsListAdapter.DogViewHolder>(){

    class DogViewHolder(var view : View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogViewHolder {
        val inflator: LayoutInflater = LayoutInflater.from(parent.context)
        val view = inflator.inflate(R.layout.list_item, parent, false)
        return DogViewHolder(view)
    }

    override fun getItemCount() = dogsList.size

    override fun onBindViewHolder(holder: DogViewHolder, position: Int) {
        holder.view.titleTextView.text = dogsList[position].dogBreed
        holder.view.lifespanTextView.text = dogsList[position].lifeSpan
        holder.view.setOnClickListener {
            val action = ListFragmentDirections.actionDetailsFragment()
            action.dogUuid = dogsList[position].uuid
            Navigation.findNavController(it).navigate(action)
        }
        holder.view.imageView.loadImage(dogsList[position].imageUrl, getProgressDrawable(holder.view.imageView.context))
    }

    fun updateList(newDogList : List<DogBreed>){
        dogsList.clear()
        dogsList.addAll(newDogList)
        notifyDataSetChanged()
    }
}