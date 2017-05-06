package com.example.sylvain.testgps;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener{

    private LocationManager locationManager;
    private TextView txtLat, txtLon, txtAlt, txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);

        txtAlt = (TextView)findViewById(R.id.editTextAlt);
        txtLon = (TextView)findViewById(R.id.editTextLon);
        txtLat = (TextView)findViewById(R.id.editTextLat);
        txt = (TextView)findViewById(R.id.editText);

        locationManager = (LocationManager)getSystemService(getBaseContext().LOCATION_SERVICE);

        String locationProvider = LocationManager.GPS_PROVIDER;
        locationManager.requestLocationUpdates(locationProvider, 0, 0, this);

        setProgressBarIndeterminateVisibility(true);
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onPause(){
        locationManager.removeUpdates(this);
        super.onPause();
    }

    public void onProviderEnabled(String str) {
    }

    @Override
    public void onLocationChanged(Location location) {
        txtAlt.setText(String.format("%+.4f",location.getAltitude()));
        txtLat.setText(String.format("%+.4f",location.getLatitude()));
        txtLon.setText(String.format("%+.4f",location.getLongitude()));

        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            txt.setText("" + address.get(0).getAddressLine(0) + "\n" + address.get(0).getPostalCode() + "\n" + address.get(0).getLocality());
        } catch(IOException ioe){ioe.printStackTrace();}
        //setProgressBarIndeterminateVisibility(false);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
