package com.udacity.classroom.bakingrecipe.utils;

import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class BindingUtils {

    @BindingAdapter(value = {"imageUrl", "placeholder", "error"}, requireAll = false)
    public static void setImageUrl(ImageView imageView, String imageUrl, Drawable placeHolder, Drawable error) {
        if (TextUtils.isEmpty(imageUrl)) imageUrl = null;

        Picasso.get()
                .load(imageUrl)
                .placeholder(placeHolder)
                .error(error)
                .into(imageView);
    }
}
