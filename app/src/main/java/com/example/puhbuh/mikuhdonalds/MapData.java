package com.example.puhbuh.mikuhdonalds;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.AsyncTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

import android.support.v4.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.location.LocationManager;
import android.location.Location;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.util.Random;
import java.util.concurrent.ExecutionException;

public class MapData extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    GoogleMap map;
    boolean loc_permission = false;
    int LOCATION_PERMISSION_REQUEST_CODE = 1;

    LocationManager lm;
    Location location = null;
    private String SEARCH_URL = "";

    String choice;

    private ImageView arrow;
    private int last_angle = -1;

    List<Place> places;

    public static final Random rand = new Random();
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_random_results);

        places = new ArrayList<Place>();

        //rand.setSeed(System.currentTimeMillis());

        arrow = findViewById(R.id.arrow);


        animateWheel();
        populateWheel();


        System.out.println("ANGLEEEEE!!!@@@@@@@@@@@@@@@@@@@@@:  " + last_angle%360 + " " + determineChoice());

    }

    //find view from wheel by angle on arrow
    int determineChoice(){
        last_angle = last_angle%360;
        if(last_angle >= 0 && last_angle <= 45){
            return 3;
        }
        if(last_angle >= 46 && last_angle <= 90){
            return 4;
        }
        if(last_angle >= 91 && last_angle <= 135){
            return 5;
        }
        if(last_angle >= 136 && last_angle <= 180){
            return 6;
        }
        if(last_angle >= 181 && last_angle <= 225){
            return 7;
        }
        if(last_angle >= 226 && last_angle <= 270){
            return 0;
        }
        if(last_angle >= 271 && last_angle <= 315){
            return 1;
        }
        if(last_angle >= 316 && last_angle <= 359){
            return 2;
        }

        return -1;

    }

    //random based on length
    int randomNum(int i){
        return rand.nextInt(i);
    }

    void animateWheel(){
        int angle = rand.nextInt(3600);

        //set angle at which animation stops rotating
        Animation rotate_animation = new RotateAnimation(0, angle, 300, 300);
        last_angle = angle;

        rotate_animation.setDuration(2500);
        rotate_animation.setFillAfter(true);


        arrow.startAnimation(rotate_animation);
    }

    JSONObject randomPlace(String query){
        enableMyLocation();

        Bundle extras = getIntent().getExtras();

        System.out.println("query: " + query);
        if(location != null){
            SEARCH_URL = "https://maps.googleapis.com/maps/api/place/textsearch/json?key=AIzaSyCj3C19qv2jF3N1SER-qr62CJnNq_1hv-Q" +
                    "&query=" + query +
                    "&location=" + location.getLatitude() + "," + location.getLongitude() +
                    "&radius=200" +
                    "types=food" +
                    "num=1";
        }
        else{
            SEARCH_URL = "https://maps.googleapis.com/maps/api/place/textsearch/json?key=AIzaSyCj3C19qv2jF3N1SER-qr62CJnNq_1hv-Q" +
                    "&query=" + query +
                    "&location=40.7128,-74.0060" +
                    "&radius=200" +
                    "&types=food" +
                    "&num=1";

            System.out.println("location is null, reverting to default coordinate values: nyc");
        }

        SEARCH_URL += "&types=" + extras.getString("DELIVERY") + "&types=" + extras.getString("TAKEAWAY");
        try {
            JSONArray places = new GetHttpRequest().execute(SEARCH_URL).get();
            JSONObject placeData = places.getJSONObject(0);

            return placeData;
        }catch(InterruptedException e){
        }catch(ExecutionException e){
        }catch(JSONException e){
        }

        return null;
    }


    public void onButtonClick(View v){
        Toast.makeText(this, choice, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, DisplayMap.class);
        intent.putExtra("RESULT", choice);

        startActivity(intent);
    }
    public class Place{
        String name;
        String address;

        Place(){}
        Place(String name, String address){
            this.name = name;
            this.address = address;
        }

    }

    //generates search results based on random selection
    private void populateWheel(){

        try{
            String[] food_types = {"Koreanfood", "Noodles", "Potatoes", "Seafood", "Rice", "Italianfood", "Spanishfood",
                    "Malaysianfood", "Russianfood", "Hotdog", "Steak", "Americanfood", "Chipoltle", "Mexicanfood",
                    "Indianfood", "Vietnamesefood", "mcdonalds", "Candy", "Cake", "Sushi", "Salmon", "Fish",
                    "Tuna", "Ramen", "Baklava", "Icecream", "Pizza"};

            //get 7 places and store address and name in places list
            for (int i = 0; i <= 7; ++i) {
                String rand_food_type = food_types[randomNum(food_types.length)];
                if(randomPlace(rand_food_type) != null) {
                    JSONObject placeData = randomPlace(rand_food_type);

                    if (placeData.length() > 0) {
                        places.add(new Place(placeData.getString("name"), placeData.getString("formatted_address")));

                        //find view by name
                        int id = getResources().getIdentifier("textView"+i, "id", getBaseContext().getPackageName());
                        TextView text = findViewById(id);
                        text.setText(places.get(i).name);

                        if(i == determineChoice()){
                            choice = placeData.getString("formatted_address");
                        }

                        System.out.println(i+ " name" + places.get(i).name);
                        System.out.println(i+ " address" + places.get(i).address);
                    } else {
                        System.out.println("placedata is null");
                    }
                }else{
                    System.out.println("jsonarray is null");

                }
            }
        }catch(JSONException e){
        }

    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (map != null) {
            // Access to the location has been granted to the app.

            map.setMyLocationEnabled(true);
            loc_permission = true;
            lm = (LocationManager)getSystemService(getBaseContext().LOCATION_SERVICE);
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            loc_permission = true;
        }
    }

    public JSONArray getSearchResults(){
        try {

            //makes a request
            HttpGet httpreq = new HttpGet(SEARCH_URL);
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(httpreq);

            int status = response.getStatusLine().getStatusCode();

            //if status is ok
            if(status == 200){
                HttpEntity entity = response.getEntity();
                System.out.println("connection secured!!!!@@@@@@");

                String datastuff = EntityUtils.toString(entity);

                JSONObject j = new JSONObject(datastuff);

                System.out.println("Is places null?: "+j.length());

                System.out.println("status: " + j.get("status"));

                JSONArray places = j.getJSONArray("results");
                System.out.println("json data: " + j.get("results"));

                return places;
            }else{
                System.out.println("aww fuck");
            }

            return null;
        }catch(IOException ex){
            System.out.println("No connection or someething");
        }
        catch(JSONException ex){
            System.out.println("There is no such resulsts memeber");

        }
        return null;
    }

    public class GetHttpRequest extends AsyncTask<String, Void, JSONArray>{

        @Override
        protected JSONArray doInBackground(String... param) {
            return getSearchResults();
        }
    }

}
