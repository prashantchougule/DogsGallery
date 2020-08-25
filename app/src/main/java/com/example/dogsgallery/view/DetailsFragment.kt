package com.example.dogsgallery.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.example.dogsgallery.R
import com.example.dogsgallery.databinding.FragmentDetailsBinding
import com.example.dogsgallery.util.getProgressDrawable
import com.example.dogsgallery.util.loadImage
import com.example.dogsgallery.viewmodel.DetailsViewModel
import kotlinx.android.synthetic.main.fragment_details.*

class DetailsFragment : Fragment() {

    private lateinit var viewModel: DetailsViewModel
    private var dogUuid = 0
    private lateinit var dataBinding:FragmentDetailsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_details, container, false)
        dataBinding = DataBindingUtil.inflate<FragmentDetailsBinding>(inflater,R.layout.fragment_details,container,false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let{
            dogUuid = DetailsFragmentArgs.fromBundle(it).dogUuid
        }

        viewModel = ViewModelProviders.of(this).get(DetailsViewModel::class.java)
        viewModel.fetch(dogUuid)

        observeDogDetails()
    }
    fun observeDogDetails(){
        viewModel.dogLiveData.observe(this, Observer { dog ->
            dog?.let {
                dataBinding.dogDetails = dog
//                dogName.text = dog.dogBreed
//                dogPurpose.text = dog.breedFor
//                dogTemperment.text = dog.temperament
//                dogLifespam.text= dog.lifeSpan
//                context?.let{
//                    dogImage.loadImage(dog.imageUrl, getProgressDrawable(it) )
//                }
            }
        })
    }
}