package com.android.structure.models.wrappers;

import com.android.structure.managers.retrofit.entities.ErrorModel;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by khanhamza on 09-Mar-17.
 */

public class WebResponse<T> {

    @SerializedName("message")
    public String message;

    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    public T result;

    @SerializedName("errors")
    public ArrayList<ErrorModel> errorList;

    public boolean isSuccess() {
        return success;
    }
}
