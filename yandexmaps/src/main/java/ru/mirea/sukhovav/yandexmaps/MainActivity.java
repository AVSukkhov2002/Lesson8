package ru.mirea.sukhovav.yandexmaps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.widget.Toast;

import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.layers.ObjectEvent;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CompositeIcon;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.RotationType;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;
import com.yandex.runtime.image.ImageProvider;

import ru.mirea.sukhovav.yandexmaps.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements UserLocationObjectListener  {
    private ActivityMainBinding binding;
    private MapView mapView;
    boolean isWork;
    private MapKit mapKit;
    UserLocationLayer userLocationLayer;
    private static final int REQUEST_CODE_PERMISSION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int accessCoarseLocation = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int accessFineLocation = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);

        if (accessCoarseLocation == PackageManager.PERMISSION_GRANTED
                && accessFineLocation == PackageManager.PERMISSION_GRANTED) {
            isWork = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_PERMISSION);
        }





        MapKitFactory.initialize(this);
        mapView = binding.mapview;

        mapView.getMap().move(
                new CameraPosition(new Point(55.751574, 37.573856), 11.0f,
                        0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0),
                null);
        if (isWork) {
            Toast.makeText(this, "доступ к геопозиции разрешён", Toast.LENGTH_SHORT).show();
            loadUserLocationLayer();
        } else {
            Toast.makeText(this, "Нет доступа к геопозиции", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
        MapKitFactory.getInstance().onStart();
    }

    private void loadUserLocationLayer(){
        MapKit mapKit = MapKitFactory.getInstance();
        mapKit.resetLocationManagerToDefault();
        userLocationLayer = mapKit.createUserLocationLayer(mapView.getMapWindow());
        userLocationLayer.setVisible(true);
        userLocationLayer.setHeadingEnabled(true);
        userLocationLayer.setObjectListener(this);

    }

    @Override
    public void onObjectAdded(@NonNull UserLocationView userLocationView) {
        mapKit.resetLocationManagerToDefault();
        userLocationLayer = mapKit.createUserLocationLayer(mapView.getMapWindow());
        userLocationLayer.setAnchor(
                new PointF((float)(mapView.getWidth() * 0.5),
                        (float)(mapView.getHeight() * 0.5)),
                new PointF((float)(mapView.getWidth() * 0.5),
                        (float)(mapView.getHeight() * 0.83)));
        userLocationView.getArrow().setIcon(ImageProvider.fromResource(
                this, android.R.drawable.arrow_up_float));
        CompositeIcon pinIcon = userLocationView.getPin().useCompositeIcon();
        pinIcon.setIcon(
                "pin",
                ImageProvider.fromResource(this, R.drawable.icons),
                new IconStyle().setAnchor(new PointF(0.5f, 0.5f))
                        .setRotationType(RotationType.ROTATE)
                        .setZIndex(1f)
                        .setScale(0.5f)

        );
        userLocationView.getAccuracyCircle().setFillColor(Color.BLUE & 0x99ffffff);

    }

    @Override
    public void onObjectRemoved(@NonNull UserLocationView userLocationView) {

    }

    @Override
    public void onObjectUpdated(@NonNull UserLocationView userLocationView, @NonNull ObjectEvent objectEvent) {

    }
}

