package com.zailaapp.zaila.extensions

import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.observe

//tv_title_artwork.observe(_viewModel.title)
fun TextView.observe(viewModelData: MutableLiveData<String>) {

    if (context is Fragment) {

        //For Fragment:
        viewModelData.observe(findFragment<Fragment>().viewLifecycleOwner) {
            text = it
        }

    } else {

        //For Activity:
        viewModelData.observe((context as LifecycleOwner)) {
            text = it
        }

    }


}