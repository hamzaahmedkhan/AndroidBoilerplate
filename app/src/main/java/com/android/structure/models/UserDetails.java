package com.android.structure.models;

import com.android.structure.managers.retrofit.GsonFactory;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserDetails {

    @Expose
    @SerializedName("total_points")
    private int totalPoints;
    @Expose
    @SerializedName("agency")
    private String agency;
    @Expose
    @SerializedName("is_social_login")
    private int isSocialLogin;
    @Expose
    @SerializedName("email_updates")
    private int emailUpdates;
    @Expose
    @SerializedName("image")
    private String image = "";
    @Expose
    @SerializedName("gender")
    private int gender;
    @Expose
    @SerializedName("designation")
    private String designation;
    @Expose
    @SerializedName("last_name")
    private String lastName;
    @Expose
    @SerializedName("first_name")
    private String firstName;
    @Expose
    @SerializedName("full_name")
    private String fullName;
    @Expose
    @SerializedName("category_id")
    private int categoryId;
    @Expose
    @SerializedName("user_id")
    private int userId;
    @Expose
    @SerializedName("parent_id")
    private int parentId;
    @Expose
    @SerializedName("department_id")
    private int departmentId;
    @Expose
    @SerializedName("id")
    private int id;
    @Expose
    @SerializedName("avg_rating")
    private double avgRating;
    @Expose
    @SerializedName("review_count")
    private int review_count;
    @Expose
    @SerializedName("about")
    private String about;
    @Expose
    @SerializedName("address")
    private String address;
    @Expose
    @SerializedName("dob")
    private String dob;
    @Expose
    @SerializedName("lat")
    private double lat;
    @Expose
    @SerializedName("lng")
    private double lng;

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }

    public int getReview_count() {
        return review_count;
    }

    public void setReview_count(int review_count) {
        this.review_count = review_count;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public String getAgency() {
        return agency;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }

    public int getIsSocialLogin() {
        return isSocialLogin;
    }

    public void setIsSocialLogin(int isSocialLogin) {
        this.isSocialLogin = isSocialLogin;
    }

    public int getEmailUpdates() {
        return emailUpdates;
    }

    public void setEmailUpdates(int emailUpdates) {
        this.emailUpdates = emailUpdates;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return GsonFactory.getSimpleGson().toJson(this);
    }
}
