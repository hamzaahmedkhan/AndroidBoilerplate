package com.android.structure.models.wrappers;

import com.android.structure.managers.retrofit.GsonFactory;
import com.android.structure.models.receiving_model.UserModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserModelWrapper {
    @Expose
    @SerializedName("user")
    private UserModel user;

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return GsonFactory.getSimpleGson().toJson(this);
    }
}
