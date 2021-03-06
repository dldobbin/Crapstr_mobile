package dics.crapstr;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {

    public static final String LOG_TAG = "Crapstr";
    private static final int LOCATION_REQUEST_CODE = 0;
    private static final int REVIEW_ADD_CODE = 1;

    private GoogleMap mMap;
    private MapHandler mapHandler;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private GeoDataClient geoDataClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Location services
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        geoDataClient = Places.getGeoDataClient(this, null);
        Utility.getInstance().setIcon(ContextCompat.getDrawable(this, R.drawable.toilet));
        Utility.getInstance().setRes(getResources());
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
        mapHandler = new MapHandler(mMap, new ReviewsAdapter(this, new ArrayList<Review>()), findViewById(R.id.bottom_sheet), geoDataClient);
        mMap.setOnMarkerClickListener(mapHandler);
        mMap.setOnCameraIdleListener(mapHandler);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(mapHandler);

        findViewById(R.id.add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this, AddReviewActivity.class);
                intent.putExtra("outgoing", ((ButtonWrapper)view).getOutgoing());
                startActivityForResult(intent, REVIEW_ADD_CODE);
            }
        });

        setMapLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (permissions.length == 1
                    && permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setMapLocation();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REVIEW_ADD_CODE) {
            if (resultCode == RESULT_OK) {
                final Outgoing outgoing = (Outgoing)data.getSerializableExtra("outgoing");
                new JSONHandler(new JSONHandler.Callback() {
                    @Override
                    public void call(Object o) {
                        mapHandler.markLocations(outgoing.getLatLng());
                        mapHandler.showReviews(outgoing.getPlaceId());
                    }
                }).execute(Utility.baseURL + outgoing.getURL(), "POST", outgoing.prepForPost());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setMapLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(@NonNull Location location) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                }
            });
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }
    }

    @Override
    public void onBackPressed() {
        BottomSheetBehavior mBottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet));
        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else {
            super.onBackPressed();
        }
    }
}
