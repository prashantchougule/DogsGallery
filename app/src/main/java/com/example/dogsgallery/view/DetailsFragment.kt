package com.example.dogsgallery.view

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.telephony.SmsManager
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.example.dogsgallery.R
import com.example.dogsgallery.databinding.FragmentDetailsBinding
import com.example.dogsgallery.databinding.SendSmsDailogBinding
import com.example.dogsgallery.model.DogBreed
import com.example.dogsgallery.model.SmsInfo
import com.example.dogsgallery.util.getProgressDrawable
import com.example.dogsgallery.util.loadImage
import com.example.dogsgallery.viewmodel.DetailsViewModel
import kotlinx.android.synthetic.main.fragment_details.*

class DetailsFragment : Fragment() {

    private lateinit var viewModel: DetailsViewModel
    private var dogUuid = 0
    private lateinit var dataBinding: FragmentDetailsBinding
    private var sendSMSStarted = false

    private var currentDog: DogBreed? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_details, container, false)
        dataBinding = DataBindingUtil.inflate<FragmentDetailsBinding>(
            inflater,
            R.layout.fragment_details,
            container,
            false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            dogUuid = DetailsFragmentArgs.fromBundle(it).dogUuid
        }

        viewModel = ViewModelProviders.of(this).get(DetailsViewModel::class.java)
        viewModel.fetch(dogUuid)

        observeDogDetails()
    }

    fun observeDogDetails() {
        viewModel.dogLiveData.observe(this, Observer { dog ->
            dog?.let {
                dataBinding.dogDetails = dog
                currentDog = dog
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.details_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_send -> {
                sendSMSStarted = true
                (activity as MainActivity).checkSMSPermission()

            }
            R.id.action_share -> {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plan"
                intent.putExtra(Intent.EXTRA_SUBJECT, "Checkout this dog breed")
                intent.putExtra(Intent.EXTRA_TEXT, "${currentDog?.dogBreed} breed for ${currentDog?.breedFor}")
                intent.putExtra(Intent.EXTRA_STREAM,currentDog?.imageUrl)

                startActivity(Intent.createChooser(intent, "Share with"))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun onPermissionResult(permissionGranted: Boolean) {
        if (sendSMSStarted && permissionGranted) {
            context?.let {
                val smsInfo = SmsInfo(
                    "",
                    "${currentDog?.dogBreed} bred for ${currentDog?.breedFor}",
                    currentDog?.imageUrl
                )

                val dialogBinding = DataBindingUtil.inflate<SendSmsDailogBinding>(
                    LayoutInflater.from(it),
                    R.layout.send_sms_dailog,
                    null,
                    false
                )
                AlertDialog.Builder(it)
                    .setView(dialogBinding.root)
                    .setPositiveButton("Send SMS"){dialog, which ->
                        if(!dialogBinding.smsDestination.text.isNotEmpty()){
                            smsInfo.to = dialogBinding.smsDestination.text.toString()
                            sendSms(smsInfo)
                        }
                    }
                    .setNegativeButton("Cancel"){ dialog, which -> }
                    .show()

                dialogBinding.smsInfo = smsInfo
            }
        }
    }

    private fun sendSms(smsInfo : SmsInfo){
        val intent = Intent(context,MainActivity::class.java)
        val pi = PendingIntent.getActivity(context, 0, intent,0)
            val sms = SmsManager.getDefault()
        sms.sendTextMessage(smsInfo.to, null, smsInfo.text, pi, null)
    }
}