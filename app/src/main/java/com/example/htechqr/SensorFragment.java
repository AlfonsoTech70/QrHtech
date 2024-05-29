package com.example.htechqr;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
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

    private JsonObjectRequest jsonRequestWebService;
    private static int idSector = 0;
    static int idTipoSubSistema = 0;
    private List<SensorData> sensorList;
    private List<String> nombreFuentes = new ArrayList<>();
    private List<Subsistema> subFuentes = new ArrayList<>();
    private List<Integer> idsFuentes = new ArrayList<>();
    private List<String> nombreRedes = new ArrayList<>();
    private List<Subsistema> subRedes = new ArrayList<>();
    private List<Integer> idsRedes = new ArrayList<>();
    private List<String> nombrePlantas = new ArrayList<>();
    private List<Subsistema> subPlantas = new ArrayList<>();
    private List<Integer> idsPlantas = new ArrayList<>();
    private SensorAdapter adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sensor, container, false);

        Button btnMostrar = rootView.findViewById(R.id.btnMostrar);
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);

        sensorList = new ArrayList<>();
        adapter = new SensorAdapter(sensorList, nombreFuentes, nombreRedes, nombrePlantas, idsFuentes, idsRedes, idsPlantas);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        btnMostrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    private void cargarConsultaSubsistema() {
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
        procesarSubsistemas(response.optJSONArray("Subsistemas"));
        procesarSensores(response.optJSONArray("Sensores"));
    }

    private void procesarSubsistemas(JSONArray subsistemasArray) {
        if (subsistemasArray != null) {
            limpiarListas();

            try {
                if (subsistemasArray.length() > 0) {
                    procesarArray(subsistemasArray.getJSONArray(0), nombreFuentes, subFuentes, idsFuentes);
                }
                if (subsistemasArray.length() > 1) {
                    procesarArray(subsistemasArray.getJSONArray(1), nombreRedes, subRedes, idsRedes);
                }
                if (subsistemasArray.length() > 2) {
                    procesarArray(subsistemasArray.getJSONArray(2), nombrePlantas, subPlantas, idsPlantas);
                }
                // Notificar al adaptador de que los datos de los spinners han cambiado
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                Log.e("Error", e.toString());
            }
        }
    }

    private void limpiarListas() {
        nombreFuentes.clear();
        subFuentes.clear();
        idsFuentes.clear();
        nombreRedes.clear();
        subRedes.clear();
        idsRedes.clear();
        nombrePlantas.clear();
        subPlantas.clear();
        idsPlantas.clear();
    }

    private void procesarArray(JSONArray array, List<String> nombres, List<Subsistema> subsistemas, List<Integer> ids) throws JSONException {
        for (int i = 0; i < array.length(); i++) {
            JSONObject jsonObject = array.getJSONObject(i);
            String nombre = jsonObject.getString("nombre");
            String idSubsistema = jsonObject.getString("idSubsistema");
            String tipo = jsonObject.getString("tipo");
            Subsistema subsistema = new Subsistema(idSubsistema, nombre, tipo);
            subsistemas.add(subsistema);
            nombres.add(nombre);
            ids.add(Integer.valueOf(idSubsistema));
        }
    }

    private void procesarSensores(JSONArray sensoresArray) {
        if (sensoresArray != null) {
            sensorList.clear();
            for (int i = 0; i < sensoresArray.length(); i++) {
                JSONObject jsonObject = sensoresArray.optJSONObject(i);
                if (jsonObject != null) {
                    SensorData sensorData = new SensorData();
                    sensorData.setIdDispositivo(jsonObject.optString("idDispositivo"));
                    sensorData.setIntervalo(jsonObject.optString("intervalo"));
                    sensorData.setConectado(jsonObject.optString("conectado"));
                    sensorData.setRssi(jsonObject.optString("rssi"));
                    sensorData.setUnidades(jsonObject.optString("unidades"));
                    sensorList.add(sensorData);
                }
            }
            adapter.notifyDataSetChanged();
            cargarConsultaSubsistema();
        }
    }
}
