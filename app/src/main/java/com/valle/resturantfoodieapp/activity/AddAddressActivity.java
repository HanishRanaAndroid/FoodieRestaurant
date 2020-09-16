package com.valle.resturantfoodieapp.activity;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.valle.resturantfoodieapp.R;
import com.valle.resturantfoodieapp.base.BaseActivity;
import com.valle.resturantfoodieapp.utils.CommonUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.github.douglasjunior.androidSimpleTooltip.OverlayView;
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;

public class AddAddressActivity extends BaseActivity implements OnMapReadyCallback {

    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private GoogleMap.OnCameraIdleListener onCameraIdleListener;

    private String longitude = "", latitude = "";
    private static final int LOCATION_PROVIDER = 999;

    @BindView(R.id.tvMapLocation)
    AppCompatTextView tvMapLocation;

    @BindView(R.id.tvAddress)
    AppCompatTextView tvAddress;

    @BindView(R.id.etFlatNo)
    AppCompatEditText etFlatNo;

    @BindView(R.id.etLandmark)
    AppCompatEditText etLandmark;

    @BindView(R.id.etAdministrador)
    AppCompatEditText etAdministrador;

    @BindView(R.id.upperLayout)
    RelativeLayout upperLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_my_orders);
        bindView(this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        configureCameraIdle();
        showTooltip();
    }

    private void showTooltip() {
        try {

            new SimpleTooltip.Builder(this)
                    .anchorView(upperLayout)
                    .text("Elija la ubicación de su dirección desplazándose por el mapa")
                    .gravity(Gravity.BOTTOM)
                    .textColor(getResources().getColor(R.color.white))
                    .animated(true)
                    .highlightShape(OverlayView.HIGHLIGHT_SHAPE_RECTANGULAR)
                    .transparentOverlay(false)
                    .build()
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == LOCATION_PROVIDER) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                tvAddress.setText(place.getName());
                latitude = String.valueOf(place.getLatLng().latitude);
                longitude = String.valueOf(place.getLatLng().longitude);
                if (place != null && mMap != null) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(place.getLatLng().latitude, place.getLatLng().longitude), 13));

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(place.getLatLng().latitude, place.getLatLng().longitude))      // Sets the center of the map to location user
                            .zoom(17)                   // Sets the zoom
                            .bearing(90)                // Sets the orientation of the camera to east
                            .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                            .build();                   // Creates a CameraPosition from the builder
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }
        }
    }

    @OnClick(R.id.tvSaveAddressAndContinue)
    void OnClicktvSaveAddressAndContinue() {

        String address = tvAddress.getText().toString();
        String administrater = etAdministrador.getText().toString();
        /*String flatNo = etFlatNo.getText().toString();
        String landmark = etLandmark.getText().toString();*/

        if (TextUtils.isEmpty(address)) {
            Toast.makeText(AddAddressActivity.this, getResources().getString(R.string.plz_enter_your_address), Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(administrater)) {
            etAdministrador.setError("Administrador");
            etAdministrador.setFocusable(true);
            return;
        }

        /*if (TextUtils.isEmpty(flatNo)) {
            etFlatNo.setError(getResources().getString(R.string.plz_enter_house_flat_no));
            etFlatNo.setFocusable(true);
            return;
        }

        if (TextUtils.isEmpty(landmark)) {
            etLandmark.setError(getResources().getString(R.string.plz_enter_landmark));
            etLandmark.setFocusable(true);
            return;
        }*/

        try {
            Intent intent = new Intent();
            intent.putExtra("address", address);
            intent.putExtra("administrater", administrater);
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
            setResult(786, intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @OnClick(R.id.tvAddress)
    void OnClickGetPlace() {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)
                .build(AddAddressActivity.this);
        startActivityForResult(intent, LOCATION_PROVIDER);
    }

    private void configureCameraIdle() {
        onCameraIdleListener = () -> {
            LatLng latLng = mMap.getCameraPosition().target;
            Geocoder geocoder = new Geocoder(AddAddressActivity.this);

            try {
                List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                if (addressList != null && addressList.size() > 0) {
                    String locality = addressList.get(0).getAddressLine(0);
                    String country = addressList.get(0).getCountryName();
                    if (!TextUtils.isEmpty(locality) && !TextUtils.isEmpty(country)) {
                        longitude = String.valueOf(latLng.longitude);
                        latitude = String.valueOf(latLng.latitude);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnCameraIdleListener(onCameraIdleListener);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(36.7783, 119.4179), 13));

        Criteria criteria = new Criteria();
        if (ActivityCompat.checkSelfPermission(AddAddressActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AddAddressActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        try {
            Location location = CommonUtils.getLastKnownLocation(AddAddressActivity.this);
            if (location != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                        .zoom(17)                   // Sets the zoom
                        .bearing(90)                // Sets the orientation of the camera to east
                        .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }

         /*   boolean success = mMap.setMapStyle(new MapStyleOptions(getResources()
                    .getString(R.string.style_json)));*/

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
