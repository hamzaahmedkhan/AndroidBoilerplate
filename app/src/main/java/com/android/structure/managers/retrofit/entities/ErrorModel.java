package com.android.structure.managers.retrofit.entities;

import com.android.structure.managers.retrofit.GsonFactory;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ErrorModel {


    @Expose
    @SerializedName("message")
    private String message;
    @Expose
    @SerializedName("label")
    private String label;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return GsonFactory.getSimpleGson().toJson(this);
    }
}
