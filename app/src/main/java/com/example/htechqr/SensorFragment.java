// SensorFragment.java
package com.example.htechqr;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SensorFragment extends Fragment implements Response.Listener<JSONObject>, Response.ErrorListener {
    private static final int REQUEST_CODE_MAP = 1;
    private JsonObjectRequest jsonRequestWebService;
    private List<SensorData> sensorList;
    private List<String> nombreFuentes = new ArrayList<>();
    private List<String> nombreRedes = new ArrayList<>();
    private List<String> nombrePlantas = new ArrayList<>();
    private SensorAdapter adapter;
    private int idSistema;
    private int idGrupo;
    private JSONObject sensorJsonData;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sensor, container, false);

        if (getArguments() != null) {
            idSistema = getArguments().getInt("idSistema", idSistema);
            idGrupo = getArguments().getInt("idGrupo", idGrupo);
            Log.d("SensorFragment", "Recibido idSistema: " + idSistema + ", idGrupo: " + idGrupo);
        }

        Button btnMostrar = rootView.findViewById(R.id.btnMostrar);
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);

        sensorList = new ArrayList<>();
        adapter = new SensorAdapter(sensorList, this::openMapActivity);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        btnMostrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("idsss", String.valueOf(idGrupo));
                Log.i("idsss", String.valueOf(idSistema));
                cargarConsultaSensores();
            }
        });

        return rootView;
    }

    private void cargarConsultaSensores() {
        String url = "https://webservice.htech.mx/consultar.php?opcion=16&id_grupo=90&id_sistema=1&nombre_json=Sensores";
        jsonRequestWebService = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        VolleySingleton.getInstanciaVolley(getContext()).addToRequestQueue(jsonRequestWebService);
        Log.i("msj2", url);
    }

    public void CargarConsultaSubsistema() {
        String url = "https://webservice.htech.mx/consultar.php?opcion=41&id_sistema=1&nombre_json=Subsistemas";
        jsonRequestWebService = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        VolleySingleton.getInstanciaVolley(getContext()).addToRequestQueue(jsonRequestWebService);
        Log.i("msj2", url);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e("Error", error.toString());
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResponse(JSONObject response) {
        sensorJsonData = response; // Guardar el JSON original

        // Guardar el JSON en SharedPreferences
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("sensorData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("sensorJsonData", sensorJsonData.toString());
        editor.apply();

        JSONArray detallesArray = response.optJSONArray("Subsistemas");
        if (detallesArray != null) {
            nombreFuentes.clear();
            nombreRedes.clear();
            nombrePlantas.clear();

            Log.i("msj", "Contenido del array detallesArray: " + detallesArray.toString());

            try {
                if (detallesArray.length() > 0) {
                    JSONArray fuentes = detallesArray.optJSONArray(0);
                    if (fuentes != null) {
                        for (int i = 0; i < fuentes.length(); i++) {
                            JSONObject jsonObject = fuentes.getJSONObject(i);
                            String nombre = jsonObject.getString("nombre");
                            nombreFuentes.add(nombre);
                        }
                    }
                }
                if (detallesArray.length() > 1) {
                    JSONArray redes = detallesArray.optJSONArray(1);
                    if (redes != null) {
                        for (int i = 0; i < redes.length(); i++) {
                            JSONObject jsonObject = redes.getJSONObject(i);
                            String nombre = jsonObject.getString("nombre");
                            nombreRedes.add(nombre);
                        }
                    }
                }
                if (detallesArray.length() > 2) {
                    JSONArray plantas = detallesArray.optJSONArray(2);
                    if (plantas != null) {
                        for (int i = 0; i < plantas.length(); i++) {
                            JSONObject jsonObject = plantas.getJSONObject(i);
                            String nombre = jsonObject.getString("nombre");
                            nombrePlantas.add(nombre);
                        }
                    }
                }

                // Notificar los cambios en los datos
                notifyDataChange();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        detallesArray = response.optJSONArray("Sensores");
        if (detallesArray != null) {
            sensorList.clear();
            for (int i = 0; i < detallesArray.length(); i++) {
                JSONObject jsonObject = detallesArray.optJSONObject(i);
                if (jsonObject != null) {
                    SensorData sensorData = new SensorData();
                    sensorData.setIdDispositivo(jsonObject.optString("idDispositivo"));
                    sensorData.setIntervalo(jsonObject.optString("intervalo"));
                    sensorData.setConectado(jsonObject.optString("conectado"));
                    sensorData.setRssi(jsonObject.optString("rssi"));
                    sensorData.setUnidades(jsonObject.optString("unidades"));
                    sensorData.setX(jsonObject.optString("x"));
                    sensorData.setY(jsonObject.optString("y"));
                    sensorList.add(sensorData);
                }
            }
            Log.d("SensorFragment", "Número de sensores cargados: " + sensorList.size());
            adapter.notifyDataSetChanged();
            CargarConsultaSubsistema();
        }
    }

    private void notifyDataChange() {
        Log.d("SensorFragment", "Nombre Fuentes: " + nombreFuentes);
        Log.d("SensorFragment", "Nombre Redes: " + nombreRedes);
        Log.d("SensorFragment", "Nombre Plantas: " + nombrePlantas);
        adapter.updateData(nombreFuentes, nombreRedes, nombrePlantas);
    }

    private void openMapActivity(String latitude, String longitude, JSONObject sensorData) {
        Intent intent = new Intent(getContext(), MapActivity.class);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        intent.putExtra("sensorData", sensorData.toString());
        startActivityForResult(intent, REQUEST_CODE_MAP);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_MAP && resultCode == FragmentActivity.RESULT_OK && data != null) {
            String updatedSensorDataStr = data.getStringExtra("updatedSensorData");
            try {
                JSONObject updatedSensorData = new JSONObject(updatedSensorDataStr);
                // Actualizar los datos en la actividad actual o fragmento
                handleUpdatedSensorData(updatedSensorData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleUpdatedSensorData(JSONObject updatedSensorData) {
        // Manejar los datos actualizados del sensor
        // Esto puede incluir actualizar la UI o cualquier otra lógica necesaria
        Log.d("SensorFragment", "Datos del sensor actualizados: " + updatedSensorData.toString());
        // Actualizar la lista de sensores si es necesario
        for (SensorData sensor : sensorList) {
            if (sensor.getIdDispositivo().equals(updatedSensorData.optString("idDispositivo"))) {
                sensor.setX(updatedSensorData.optString("x"));
                sensor.setY(updatedSensorData.optString("y"));
                break;
            }
        }
        adapter.notifyDataSetChanged();
    }
}
