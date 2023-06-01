package ru.mirea.sukhovav.osmmaps;


import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.preference.PreferenceManager;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;


import ru.mirea.sukhovav.osmmaps.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private MapView mapView;
    private TextView textView;
    private ActivityMainBinding binding;
    boolean isWork;
    private static final int REQUEST_CODE_PERMISSION = 100;
    MyLocationNewOverlay locationNewOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().load(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int accessCoarseLocation = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int accessFineLocation = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);

        if (accessCoarseLocation == PackageManager.PERMISSION_GRANTED
                && accessFineLocation == PackageManager.PERMISSION_GRANTED) {
            isWork = true;
        } else {
            // Выполняется запрос к пользователь на получение необходимых разрешений
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_PERMISSION);
        }

        textView = binding.text1;
        mapView = binding.mapView;
        mapView.setZoomRounding(true);
        mapView.setMultiTouchControls(true);

        IMapController mapController = mapView.getController();
        mapController.setZoom(15.0);
        GeoPoint startPoint = new GeoPoint(55.794229, 37.700772);
        mapController.setCenter(startPoint);

        locationNewOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getApplicationContext()),
                mapView);
        locationNewOverlay.enableMyLocation();
        mapView.getOverlays().add(locationNewOverlay);


        CompassOverlay compassOverlay = new CompassOverlay(getApplicationContext(), new
                InternalCompassOrientationProvider(getApplicationContext()), mapView);
        compassOverlay.enableCompass();
        mapView.getOverlays().add(compassOverlay);

        final Context context = this.getApplicationContext();
        final DisplayMetrics dm = context.getResources().getDisplayMetrics();
        ScaleBarOverlay scaleBarOverlay = new ScaleBarOverlay(mapView);
        scaleBarOverlay.setCentred(true);
        scaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);
        mapView.getOverlays().add(scaleBarOverlay);


        createIcons("МИРЭА - Российский техногический университет",
                R.drawable.rtumireaicons, R.drawable.rtumirea, 55.669993, 37.479766);
        createIcons("Макдональдс",
                R.drawable.macdonaldsicons, R.drawable.macdonalds, 55.707153, 37.766538);
        createIcons("Кузьминский лесопарк",
                R.drawable.park, R.drawable.parkicons, 55.684020, 37.803419);


    }

    public void createIcons(String text, Integer image, Integer icons, Double aLatitude, Double aLongitude){
        Marker marker = new Marker(mapView);
        marker.setPosition(new GeoPoint(aLatitude, aLongitude));
        marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                textView.setText(text);
                binding.imageView.setImageResource(image);
                return true;
            }
        });
        mapView.getOverlays().add(marker);

        marker.setIcon(ResourcesCompat.getDrawable(getResources(),
                icons, null));
    }

    @Override
    public void onResume() {
        super.onResume();
        Configuration.getInstance().load(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        if (mapView != null) {
            mapView.onResume();
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        Configuration.getInstance().save(getApplicationContext(),

                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        if (mapView != null) {
            mapView.onPause();
        }
    }
}