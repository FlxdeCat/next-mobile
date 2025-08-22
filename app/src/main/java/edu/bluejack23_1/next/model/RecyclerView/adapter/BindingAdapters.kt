package edu.bluejack23_1.next.model.recyclerView.adapter

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.databinding.BindingAdapter
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.bluejack23_1.next.R
import edu.bluejack23_1.next.helper.Helper
import edu.bluejack23_1.next.model.recyclerView.BaseAdapter
import edu.bluejack23_1.next.model.recyclerView.ListAdapterItem

@BindingAdapter("setAdapter")
fun setAdapter(
    recyclerView: RecyclerView,
    adapter: BaseAdapter<ViewDataBinding, ListAdapterItem>?
) {
    adapter?.let {
        recyclerView.adapter = it
    }
}

@Suppress("UNCHECKED_CAST")
@BindingAdapter("submitList")
fun submitList(recyclerView: RecyclerView, list: List<ListAdapterItem>?) {
    val adapter = recyclerView.adapter as BaseAdapter<ViewDataBinding, ListAdapterItem>?
    adapter?.updateData(list ?: listOf())
}

@BindingAdapter("manageState")
fun manageState(progressBar: ProgressBar, state: Boolean) {
    progressBar.visibility = if (state) View.VISIBLE else View.GONE
}

@BindingAdapter("imageUrl")
fun imageUrl(imgView: ImageView, imgUrl: String?) {
    Log.d("IMAGE_URL", imgUrl.toString())
    imgUrl?.let {
        Glide.with(imgView.context)
            .load(it)
            .into(imgView)
    }
}

@BindingAdapter("imgUrlByUsername")
fun imgUrlByUsername(imgView: ImageView, username: String?) {
    if (username != null) {
        if(username != "OP") {
            Helper.fetchThumbnailID(username) { imageURL ->
                if (imageURL != null && imageURL != "-") {
                    Glide.with(imgView.context)
                        .load(imageURL)
                        .into(imgView)
                } else {
                    imgView.setImageResource(R.drawable.user_login_icon)
                }
            }
        }
        else{
            imgView.setImageResource(R.drawable.user_login_icon)
        }
    }
}