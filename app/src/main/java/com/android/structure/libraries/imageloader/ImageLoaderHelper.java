package com.android.structure.libraries.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.ImageView;

import com.android.structure.constatnts.WebServiceConstants;
import com.android.structure.helperclasses.StringHelper;
import com.android.structure.managers.SharedPreferenceManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.Map;

import com.android.structure.R;
import com.android.structure.constatnts.AppConstants;

/**
 * Created by khanhamza on 08-Mar-17.
 */

public class ImageLoaderHelper {


    /**
     * WITHOUT ANIMATION, WITH HEADER
     *
     * @param context
     * @param imageView
     * @param url
     */
    public static void loadImageWithConstantHeadersWithoutAnimation(Context context, ImageView imageView, String url, boolean isUser) {
        ImageLoader.getInstance().displayImage(url,
                imageView,
                ImageLoaderHelper.getOptionsSimple(WebServiceConstants
                        .getHeaders(SharedPreferenceManager.getInstance(context).getString(AppConstants.KEY_TOKEN)), isUser));
    }

    /**
     * WITH ANIMATION, WITH HEADER
     *
     * @param context
     * @param imageView
     * @param url
     */
    public static void loadImageWithConstantHeaders(Context context, ImageView imageView, String url, boolean isUser) {
        ImageLoader.getInstance().displayImage(url,
                imageView,
                ImageLoaderHelper.getOptionsWithAnimation(WebServiceConstants
                        .getHeaders(SharedPreferenceManager.getInstance(context).getString(AppConstants.KEY_TOKEN)), isUser));
    }


    /**
     * WITHOUT ANIMATION, WITHOUT HEADER
     *
     * @param imageView
     * @param url
     */

    public static void loadImageWithouAnimation(ImageView imageView, String url, boolean isUser) {
        ImageLoader.getInstance().displayImage(url,
                imageView,
                ImageLoaderHelper.getOptionsSimple(isUser));
    }


    /**
     * WITH ANIMATION, WITHOUT HEADER
     *
     * @param imageView
     * @param url
     * @param isUser
     */

    public static void loadImageWithAnimations(ImageView imageView, String url, boolean isUser) {
        ImageLoader.getInstance().displayImage(url,
                imageView,
                ImageLoaderHelper.getOptionsWithAnimation(isUser));
    }


    /**
     * WITHOUT ANIMATION, WITHOUT HEADER, will create URL from path
     *
     * @param imageView
     * @param path
     */

    public static void loadImageWithouAnimationByPath(ImageView imageView, String path, boolean isUser) {
        if (!StringHelper.isNullOrEmpty(path) && path.startsWith("https")) {
            loadImageWithAnimations(imageView, path, isUser);
            return;
        }
        ImageLoader.getInstance().displayImage(getImageURLFromPath(path),
                imageView,
                ImageLoaderHelper.getOptionsSimple(isUser));
    }


    /**
     * WITH ANIMATION, WITHOUT HEADER,  will create URL from path
     *
     * @param imageView
     * @param path
     * @param isUser
     */

    public static void loadImageWithAnimationsByPath(ImageView imageView, String path, boolean isUser) {
        if (!StringHelper.isNullOrEmpty(path) && path.startsWith("https")) {
            loadImageWithAnimations(imageView, path, isUser);
            return;
        }

        ImageLoader.getInstance().displayImage(getImageURLFromPath(path),
                imageView,
                ImageLoaderHelper.getOptionsWithAnimation(isUser));
    }


    public static void loadBase64Image(ImageView imageView, String base64) {
        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        if (decodedByte != null) {
            imageView.setImageBitmap(decodedByte);
        } else {
            imageView.setImageResource(R.drawable.profile_placeholder);
        }
    }


    public static String getImageURLFromPath(String path) {
        return WebServiceConstants.IMAGE_BASE_URL + path;
    }


    public static String getImageURLFromPath(String path, String width, String height) {
        return WebServiceConstants.IMAGE_BASE_URL + path + "?w=" + width + "&h=" + height;
    }

    public static DisplayImageOptions getOptionsSimple(boolean isUser) {

        if (isUser) {
            return new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true)
                    .showImageForEmptyUri(R.color.base_dark_gray)
                    .showImageOnFail(R.drawable.profile_placeholder)
                    .showImageOnLoading(R.drawable.profile_placeholder)
                    .build();
        } else {
            return new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true)
                    .showImageForEmptyUri(R.color.base_dark_gray)
                    .showImageOnFail(R.color.background_gray)
                    .showImageOnLoading(R.color.background_gray)
                    .build();
        }


    }


    public static DisplayImageOptions getOptionsSimple(Map<String, String> headers, boolean isUser) {

        if (isUser) {
            return new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true)
                    .showImageForEmptyUri(R.color.base_dark_gray)
                    .showImageOnFail(R.drawable.profile_placeholder)
                    .showImageOnLoading(R.drawable.profile_placeholder)
                    .extraForDownloader(headers)
                    .build();
        } else {
            return new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true)
                    .showImageForEmptyUri(R.color.base_dark_gray)
                    .showImageOnFail(R.color.background_gray)
                    .showImageOnLoading(R.color.background_gray)
                    .extraForDownloader(headers)
                    .build();
        }


    }

    public static DisplayImageOptions getOptionsWithAnimation(boolean isUser) {

        if (isUser) {
            return new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true)
                    .showImageForEmptyUri(R.color.base_dark_gray)
                    .showImageOnFail(R.drawable.profile_placeholder)
                    .showImageOnLoading(R.drawable.profile_placeholder)
                    .imageScaleType(ImageScaleType.EXACTLY).displayer(new FadeInBitmapDisplayer(200)).build();
        } else {
            return new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true)
                    .showImageForEmptyUri(R.color.base_dark_gray)
                    .showImageOnFail(R.color.background_gray)
                    .showImageOnLoading(R.color.background_gray)
                    .imageScaleType(ImageScaleType.EXACTLY).displayer(new FadeInBitmapDisplayer(200)).build();
        }


    }


    public static DisplayImageOptions getOptionsWithAnimation(Map<String, String> headers, boolean isUser) {

        if (isUser) {
            return new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true)
                    .showImageForEmptyUri(R.color.base_dark_gray)
                    .showImageOnFail(R.drawable.profile_placeholder)
                    .showImageOnLoading(R.drawable.profile_placeholder)
                    .extraForDownloader(headers)
                    .imageScaleType(ImageScaleType.EXACTLY).displayer(new FadeInBitmapDisplayer(200)).build();
        } else {
            return new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true)
                    .showImageForEmptyUri(R.color.base_dark_gray)
                    .showImageOnFail(R.color.background_gray)
                    .showImageOnLoading(R.color.background_gray)
                    .extraForDownloader(headers)
                    .imageScaleType(ImageScaleType.EXACTLY).displayer(new FadeInBitmapDisplayer(200)).build();
        }


    }
}
