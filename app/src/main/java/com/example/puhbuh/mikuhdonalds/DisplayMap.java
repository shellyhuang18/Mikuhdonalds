package com.example.puhbuh.mikuhdonalds;

import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class DisplayMap extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    MapFragment fragment;

    String choice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_map);

        fragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);

        Bundle extras = getIntent().getExtras();
        choice = extras.getString("RESULT");
        Toast.makeText(this, choice, Toast.LENGTH_LONG).show();


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

        try{
            Geocoder geocode = new Geocoder(this, Locale.getDefault());
            List<Address> list = geocode.getFromLocationName(choice, 1);
                System.out.println("list ad length: " + list.size());
                if(list.size() > 0) {
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(list.get(0).getLatitude(), list.get(0).getLongitude()))
                            .title(choice)
                    );
                }

            LatLng rand_place_coord = new LatLng(list.get(0).getLatitude(), list.get(0).getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(rand_place_coord, 15));

            mMap.animateCamera(CameraUpdateFactory.zoomIn());
            // Zoom out to zoom level 10, animating with a duration of 2 seconds.
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
        }catch(IOException e){
        }


    }
}
