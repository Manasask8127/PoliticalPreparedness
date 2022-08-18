package com.example.android.politicalpreparedness.representative.adapter


import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.example.android.politicalpreparedness.R

@BindingAdapter("profileImage")
fun fetchImage(view: ImageView, src: String?) {
    src?.let {
        val uri = src.toUri().buildUpon().scheme("https").build()
        //TODO: Add Glide call to load image and circle crop - user ic_profile as a placeholder and for errors.
        val context=view.context
            Glide.with(context)
                .load(src)
                .error(R.drawable.ic_profile)
                .placeholder(R.drawable.ic_profile)
                .centerCrop()
                .into(view)
    }
}

@BindingAdapter("stateValue")
fun Spinner.setNewValue(value: MutableLiveData<String>) {
    val adapter = toTypedAdapter<String>(this.adapter as ArrayAdapter<*>)
    val position = when (adapter.getItem(0)) {
        is String -> adapter.getPosition(value.value)
        else -> this.selectedItemPosition
    }
    if (position >= 0) {
        setSelection(position)
    }
}

@InverseBindingAdapter(attribute = "stateValue")
fun Spinner.getSelectedValue():String=this.selectedItem.toString()


@BindingAdapter("app:stateValueAttrChanged")
fun Spinner.setListeners(listener: InverseBindingListener?){
    listener?.let {
        this.onItemSelectedListener=object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                listener.onChange()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                //nothing
            }
        }
    }
}

inline fun <reified T> toTypedAdapter(adapter: ArrayAdapter<*>): ArrayAdapter<T>{
    return adapter as ArrayAdapter<T>
}
