// MapActivity.java
package com.example.htechqr;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONObject;
import org.json.JSONException;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker marker;
    private JSONObject sensorData;
    private LatLng newLocation; // Variable para almacenar la nueva ubicación

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SharedPreferences sharedPreferences = getSharedPreferences("sensorData", Context.MODE_PRIVATE);
        String sensorDataStr = sharedPreferences.getString("sensorJsonData", null);

        if (sensorDataStr != null) {
            try {
                sensorData = new JSONObject(sensorDataStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        Button btnConfirmar = findViewById(R.id.btnConfirmar);
        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newLocation != null) {
                    // Actualizar las coordenadas en el objeto JSON
                    try {
                        sensorData.put("y", newLocation.latitude);
                        sensorData.put("x", newLocation.longitude);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(MapActivity.this, "Ubicación confirmada: " + newLocation.latitude + ", " + newLocation.longitude, Toast.LENGTH_SHORT).show();

                    // Guardar el JSON actualizado en SharedPreferences
                    guardarJsonActualizado(sensorData);

                    // Devolver el resultado a la actividad anterior
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("updatedSensorData", sensorData.toString());
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(MapActivity.this, "No se ha movido el marcador", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        String latitudeStr = getIntent().getStringExtra("latitude");
        String longitudeStr = getIntent().getStringExtra("longitude");

        if (latitudeStr != null && longitudeStr != null) {
            try {
                double latitude = Double.parseDouble(latitudeStr);
                double longitude = Double.parseDouble(longitudeStr);

                LatLng location = new LatLng(latitude, longitude);
                marker = mMap.addMarker(new MarkerOptions().position(location).title("Ubicación del sensor").draggable(true));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));

                mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDragStart(Marker marker) {
                        // No action needed
                    }

                    @Override
                    public void onMarkerDrag(Marker marker) {
                        // No action needed
                    }

                    @Override
                    public void onMarkerDragEnd(Marker marker) {
                        newLocation = marker.getPosition();
                        Toast.makeText(MapActivity.this, "Nueva posición provisional: " + newLocation.latitude + ", " + newLocation.longitude, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (NumberFormatException e) {
                e.printStackTrace();
                // Maneja el error si las coordenadas no son válidas
            }
        }
    }

    private void guardarJsonActualizado(JSONObject sensorData) {
        SharedPreferences sharedPreferences = getSharedPreferences("sensorData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("sensorJsonData", sensorData.toString());
        editor.apply();
    }
}