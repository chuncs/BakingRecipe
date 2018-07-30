package com.udacity.classroom.bakingrecipe.utils;

import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class BindingUtils {

    @BindingAdapter(value = {"imageUrl", "placeholder", "error"}, requireAll = false)
    public static void setImageUrl(ImageView imageView, String imageUrl, Drawable placeHolder, Drawable error) {
        if (imageUrl == null) return;

        if (imageUrl.trim().length() == 0) imageUrl = null;

        Picasso.get()
                .load(imageUrl)
                .placeholder(placeHolder)
                .error(error)
                .into(imageView);
    }
}
