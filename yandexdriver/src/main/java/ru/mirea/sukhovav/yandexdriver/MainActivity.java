package ru.mirea.sukhovav.yandexdriver;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.RequestPoint;
import com.yandex.mapkit.RequestPointType;
import com.yandex.mapkit.directions.DirectionsFactory;
import com.yandex.mapkit.directions.driving.DrivingOptions;
import com.yandex.mapkit.directions.driving.DrivingRoute;
import com.yandex.mapkit.directions.driving.DrivingRouter;
import com.yandex.mapkit.directions.driving.DrivingSession;
import com.yandex.mapkit.directions.driving.VehicleOptions;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.Error;
import com.yandex.runtime.image.ImageProvider;

import org.osmdroid.config.Configuration;

import java.util.ArrayList;
import java.util.List;


import ru.mirea.sukhovav.yandexdriver.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements
        DrivingSession.DrivingRouteListener {

        private ActivityMainBinding binding;
        private final String MAPKIT_API_KEY = "e1a158d3-4fe9-4832-b8b1-6a030005679f";
        private final int[] colors = {0xAAABBB24, 0xBBAA2525, 0xFFFAB352, 0xFFCCC252};
        private Point start = new Point(55.695409, 37.817874);
        private final Point finish = new Point(55.669965, 37.479565);
        private final Point center = new Point(
                (start.getLatitude() + finish.getLatitude()) / 2,
                (start.getLongitude() + finish.getLongitude()) / 2);
        private MapView mapView;
        private MapObjectCollection mapObjects;
        private DrivingRouter drivingRouter;
        private DrivingSession drivingSession;
        boolean isWork;
        private static final int REQUEST_CODE_PERMISSION = 100;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Configuration.getInstance().load(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        int accessCoarseLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int accessFineLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (accessCoarseLocation == PackageManager.PERMISSION_GRANTED
                && accessFineLocation == PackageManager.PERMISSION_GRANTED) {
            isWork = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_PERMISSION);
        }

        MapKitFactory.initialize(this);
        mapView = binding.mapview;
        mapView.getMap().setRotateGesturesEnabled(false);
        mapView.getMap().move(new CameraPosition(
                center, 10, 0, 0));
        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter();
        mapObjects = mapView.getMap().getMapObjects().addCollection();
        submitRequest();

        PlacemarkMapObject marker = mapView.getMap().getMapObjects().addPlacemark(finish,
                ImageProvider.fromResource(this,R.drawable.icons));
        marker.addTapListener(new MapObjectTapListener() {
            @Override
            public boolean onMapObjectTap(@NonNull MapObject mapObject, @NonNull Point
                    point) {
                Toast.makeText(getApplication(),"РТУ МИРЭА \n" +
                                "Высшее учебное заведение",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    private void submitRequest() {
        DrivingOptions drivingOptions = new DrivingOptions();
        VehicleOptions vehicleOptions = new VehicleOptions();
        drivingOptions.setRoutesCount(4);
        ArrayList<RequestPoint> requestPoints;
        requestPoints = new ArrayList<>();
        requestPoints.add(new RequestPoint(start,
                RequestPointType.WAYPOINT,
                null));
        requestPoints.add(new RequestPoint(finish,
                RequestPointType.WAYPOINT,
                null));
        drivingSession = drivingRouter.requestRoutes(requestPoints, drivingOptions,
                vehicleOptions, this);
    }

    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }
    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }



    public void onDrivingRoutes(@NonNull List<DrivingRoute> list) {
        int color;
        for (int i = 0; i < list.size(); i++) {
            color = colors[i];
            mapObjects.addPolyline(list.get(i).getGeometry()).setStrokeColor(color);
        }
    }

    @Override
    public void onDrivingRoutesError(@NonNull Error error) {

    }
}
// изменить маршрут, поправить код