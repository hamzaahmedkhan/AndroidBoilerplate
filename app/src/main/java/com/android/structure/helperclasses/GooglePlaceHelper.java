package com.android.structure.helperclasses;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import androidx.fragment.app.Fragment;
import android.util.Log;

import com.android.structure.activities.MapsActivity;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.gms.common.api.Status;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_CANCELED;


/**
 * Created by khanhamza on 26-Apr-17.
 */

public class GooglePlaceHelper {


    public static final int REQUEST_CODE_PLACE_HELPER = 6666;
    public static final int PLACE_PICKER = 0;
    public static final int PLACE_AUTOCOMPLETE = 1;
    private static final String GEO_API_KEY = "AIzaSyCkoOvnd1_eougL23wAx7DP65C_duaJRjQ";
    private final boolean isFullScreen;
    private int apiType;
    private GooglePlaceDataInterface googlePlaceDataInterface;

    private static final String TAG = "Google Place";
    private Activity activity;
    private Fragment fragment;
    //    https://maps.googleapis.com/maps/api/staticmap?center=LAT,LONG&zoom=14&size=400x400&markers=color:red%7C24.8294085,67.07382199999999&key=AIzaSyCXRaQmQ7-RMrb_zRoLF1NW3H8eksUzc8sz


    /**
     * @param activity
     * @param apiType                  Api Type can be PLACE_PICKER or PLACE_AUTOCOMPLETE
     * @param googlePlaceDataInterface implement this interface in your fragment then pass its instance as this.
     * @param fragment                 your current fragment
     * @param isFullScreen
     */

    public GooglePlaceHelper(Activity activity, int apiType, GooglePlaceDataInterface googlePlaceDataInterface, Fragment fragment, boolean isFullScreen) {
        this.activity = activity;
        this.apiType = apiType;
        this.googlePlaceDataInterface = googlePlaceDataInterface;
        this.fragment = fragment;
        this.isFullScreen = isFullScreen;
    }


    /**
     * Call this method in a fragment when you want to open the map
     * CHOOSE MODE_FULLSCREEN OR OVERLAY for Popup
     */

    public void openAutocompleteActivity() {
        // The autocomplete activity requires Google Play Services to be available. The intent
        // builder checks this and throws an exception if it is not the case.
        Intent intent;
//            if (apiType == PLACE_PICKER) {
//                intent = new Plac.IntentBuilder()
//                        .build(activity);
//            } else {
        /**
         * Initialize Places. For simplicity, the API key is hard-coded. In a production
         * environment we recommend using a secure mechanism to manage API keys.
         */
        if (!Places.isInitialized()) {
            Places.initialize(activity, GEO_API_KEY);
        }

        // Set the fields to specify which types of place data to return.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

        // Start the autocomplete intent.
        AutocompleteActivityMode autocompleteActivityMode;
        if (isFullScreen) {
            autocompleteActivityMode = AutocompleteActivityMode.FULLSCREEN;
        } else {
            autocompleteActivityMode = AutocompleteActivityMode.OVERLAY;
        }

        intent = new Autocomplete.IntentBuilder(
                autocompleteActivityMode, fields)
                .build(activity);


        if (fragment == null) {
            activity.startActivityForResult(intent, REQUEST_CODE_PLACE_HELPER);
        } else {
            fragment.startActivityForResult(intent, REQUEST_CODE_PLACE_HELPER);
        }
    }


    /**
     * Call this method in a fragment when you want to open the map
     */

    public void openMapsActivity() {


        Intent i = new Intent(activity, MapsActivity.class);


        if (fragment == null) {
            activity.startActivityForResult(i, REQUEST_CODE_PLACE_HELPER);
        } else {
            fragment.startActivityForResult(i, REQUEST_CODE_PLACE_HELPER);
        }
    }


    /**
     * Override fragment's onActivityResult and pass its parameters here.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GooglePlaceHelper.REQUEST_CODE_PLACE_HELPER && data != null) {

            if (apiType == PLACE_PICKER) {
                double lat = data.getDoubleExtra(MapsActivity.KEY_LATITUDE, 0);
                double lng = data.getDoubleExtra(MapsActivity.KEY_LONGITUTUDE, 0);

                GoogleAddressModel googleAddressModel = getAddress(activity, lat, lng);
                googlePlaceDataInterface.onPlaceActivityResult(lng, lat, googleAddressModel.address);

            } else {

                if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                    // TODO: Handle the error.
                    Status status = Autocomplete.getStatusFromIntent(data);
                    Log.e(TAG, "Error: Status = " + status.toString());
                    googlePlaceDataInterface.onError(status.toString());

                } else if (resultCode == RESULT_CANCELED) {
                    // Indicates that the activity closed before a selection was made. For example if
                    // the user pressed the back button.
                } else {

                    Place place = Autocomplete.getPlaceFromIntent(data);

                    String locationName = place.getName();
                    double latitude = place.getLatLng().latitude;
                    double longitude = place.getLatLng().longitude;


                    Log.d(TAG, "onActivityResult MAP: locationName = " + locationName);
                    Log.d(TAG, "onActivityResult MAP: latitude = " + latitude);
                    Log.d(TAG, "onActivityResult MAP: longitude = " + longitude);

                    googlePlaceDataInterface.onPlaceActivityResult(longitude, latitude, locationName);
                }
            }
        }
    }


    /**
     * GET ADDRESS from Geo Coder.
     *
     * @param context
     * @param LATITUDE
     * @param LONGITUDE
     * @return
     */

    public static GoogleAddressModel getAddress(Context context, double LATITUDE, double LONGITUDE) {

        GoogleAddressModel googleAddressModel = new GoogleAddressModel("", "", "", "", "", "");

        //Set Address
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null && addresses.size() > 0) {


                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String streetName = addresses.get(0).getFeatureName(); // Only if available else return NULL


                googleAddressModel = new GoogleAddressModel(address, city, state, country, postalCode, streetName);

                Log.d(TAG, "getAddress:  address -" + address);
                Log.d(TAG, "getAddress:  city -" + city);
                Log.d(TAG, "getAddress:  country -" + country);
                Log.d(TAG, "getAddress:  state -" + state);
                Log.d(TAG, "getAddress:  postalCode -" + postalCode);
                Log.d(TAG, "getAddress:  knownName" + streetName);


                String countryCode = addresses.get(0).getCountryCode();
                Log.d(TAG, "getAddress:  countryCode" + countryCode);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return googleAddressModel;
    }


    public static class GoogleAddressModel {
        private String address;
        private String city;
        private String state;
        private String country;
        private String postalCode;
        private String streetName;


        public GoogleAddressModel(String address, String city, String state, String country, String postalCode, String streetName) {
            this.address = address;
            this.city = city;
            this.state = state;
            this.country = country;
            this.postalCode = postalCode;
            this.streetName = streetName;
        }


        public String getAddress() {
            return address == null ? "" : address;
        }

        public String getCity() {
            return city == null ? "" : city;
        }

        public String getState() {
            return state == null ? "" : state;
        }

        public String getCountry() {
            return country == null ? "" : country;
        }

        public String getPostalCode() {
            return postalCode == null ? "" : postalCode;
        }

        public String getStreetName() {
            return streetName == null ? "" : streetName;
        }
    }

    /**
     * You can change the key with your geo API Key.
     *
     * @param LAT
     * @param LONG
     * @return
     */

    public static String getMapSnapshotURL(double LAT, double LONG) {
        return "https://maps.googleapis.com/maps/api/staticmap?center=" + LAT + "," + LONG + "&zoom=15&size=200x200&markers=color:red%7C" + LAT + "," + LONG + "&key=" + GEO_API_KEY;
    }


    public static void intentOpenMap(Context activity, double latitude, double longitude, String label) {
//        double latitude = 25.161156;
//        double longitude = 55.237092;
//        String label = "Label";
        String uriBegin = "geo:" + latitude + "," + longitude;
        String query = latitude + "," + longitude + "(" + label + ")";
        String encodedQuery = Uri.encode(query);
        String uriString = uriBegin + "?q=" + encodedQuery + "&z=20";
        Uri uri = Uri.parse(uriString);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);

        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(intent);
        }
    }


    public interface GooglePlaceDataInterface {
        void onPlaceActivityResult(double longitude, double latitude, String locationName);

        void onError(String error);
    }


    public static GooglePlaceHelper.GoogleAddressModel getCurrentLocation(Context context, boolean showSettingAlert) {
        GPSTracker2 gpsTracker = new GPSTracker2(context);

        if (gpsTracker.isGPSEnabled()) {
            gpsTracker.getLocation();
            double lat = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();
            return getAddress(context, lat, longitude);
        } else {
            if (showSettingAlert) {
                gpsTracker.showSettingsAlert();
            }
            return null;
        }
    }


}

