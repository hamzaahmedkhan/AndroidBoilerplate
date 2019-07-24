package com.android.structure.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.structure.R;
import com.android.structure.helperclasses.GooglePlaceHelper;
import com.android.structure.helperclasses.ui.helper.UIHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {


    public static final int MAPS_ACTIVITY_RESULT = 8900;
    public static final String KEY_LATITUDE = "lat";
    public static final String KEY_LONGITUTUDE = "lng";

    @BindView(R.id.imgSearch)
    ImageView imgSearch;
    @BindView(R.id.imgDone)
    FloatingActionButton imgDone;
    private GoogleMap mMap;
    GooglePlaceHelper googlePlaceHelper;

    LatLng latLng;
    MarkerOptions marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);


        marker = new MarkerOptions();
        marker.draggable(true);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        assert mapFragment != null;
        mapFragment.getMapAsync(this);


        googlePlaceHelper = new GooglePlaceHelper(MapsActivity.this, GooglePlaceHelper.PLACE_AUTOCOMPLETE, new GooglePlaceHelper.GooglePlaceDataInterface() {
            @Override
            public void onPlaceActivityResult(double longitude, double latitude, String locationName) {
                latLng = new LatLng(latitude, longitude);
                if (mMap != null) {

                    marker.position(latLng);
                    mMap.clear();
                    mMap.addMarker(marker);

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 12));

                }
            }

            @Override
            public void onError(String error) {

            }
        }, null, false);

        imgSearch.setOnClickListener(v -> googlePlaceHelper.openAutocompleteActivity());


        imgDone.setOnClickListener(v -> {
            if (latLng == null) {
                UIHelper.showToast(this, "Please select location first");
                return;
            }
            Intent intent = new Intent();
            intent.putExtra(KEY_LATITUDE, latLng.latitude);
            intent.putExtra(KEY_LONGITUTUDE, latLng.longitude);
            setResult(MAPS_ACTIVITY_RESULT, intent);
            MapsActivity.this.finish();
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        googlePlaceHelper.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            UIHelper.showToast(this, "Kindly turn on Location");
            return;
        }

        mMap.setMyLocationEnabled(true);


        if (latLng != null) {
            marker.position(latLng);
            mMap.clear();
            mMap.addMarker(marker);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        }


        mMap.setOnMyLocationClickListener(location -> {
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.clear();
            marker.position(latLng);
            mMap.addMarker(marker);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
         });

        mMap.setOnMapClickListener(point -> {
            mMap.clear();
            marker.position(point);
            mMap.addMarker(marker);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
            latLng = point;
        });


        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                marker.getPosition();
                mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                latLng = marker.getPosition();
            }
        });
    }
}
