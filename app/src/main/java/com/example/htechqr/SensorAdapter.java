// SensorAdapter.java
package com.example.htechqr;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SensorAdapter extends RecyclerView.Adapter<SensorAdapter.SensorViewHolder> {

    private List<SensorData> sensorList;
    private List<String> nombreFuentes = new ArrayList<>();
    private List<String> nombreRedes = new ArrayList<>();
    private List<String> nombrePlantas = new ArrayList<>();
    private final String[] opciones = {
            "Fuentes de abastecimiento",
            "Fuentes de distribución",
            "Plantas de tratamiento"
    };
    private final OnMapClickListener mapClickListener;

    public SensorAdapter(List<SensorData> sensorList, OnMapClickListener mapClickListener) {
        this.sensorList = sensorList;
        this.mapClickListener = mapClickListener;
    }

    @NonNull
    @Override
    public SensorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sensor, parent, false);
        return new SensorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SensorViewHolder holder, int position) {
        SensorData sensorData = sensorList.get(position);
        holder.bind(sensorData, opciones, nombreFuentes, nombreRedes, nombrePlantas, mapClickListener);
    }

    @Override
    public int getItemCount() {
        return sensorList.size();
    }

    public class SensorViewHolder extends RecyclerView.ViewHolder {
        private Spinner spinnerSectores1, spinnerTipo1;
        private TextView txtDispositivo, txtIntervalo, txtConectado, txtRssi, txtUnidades, txtIdDispositivo, txtMapa;

        public SensorViewHolder(@NonNull View itemView) {
            super(itemView);
            txtIdDispositivo = itemView.findViewById(R.id.txtIdDispositivo);
            txtDispositivo = itemView.findViewById(R.id.txtDispositivo);
            txtIntervalo = itemView.findViewById(R.id.txtIntervalo);
            txtConectado = itemView.findViewById(R.id.txtConectado);
            txtRssi = itemView.findViewById(R.id.txtRssi);
            txtUnidades = itemView.findViewById(R.id.txtUnidades);
            spinnerSectores1 = itemView.findViewById(R.id.spinnerSectores1);
            spinnerTipo1 = itemView.findViewById(R.id.spinnerTipo1);
            txtMapa = itemView.findViewById(R.id.txtMapa);
        }

        public void bind(SensorData sensorData, String[] opciones, List<String> nombreFuentes, List<String> nombreRedes, List<String> nombrePlantas, OnMapClickListener mapClickListener) {
            txtIdDispositivo.setText(sensorData.getIdDispositivo());
            txtIntervalo.setText(sensorData.getIntervalo());
            txtRssi.setText(sensorData.getRssi());
            txtUnidades.setText(sensorData.getUnidades());

            String estadoConectado;
            switch (sensorData.getConectado()) {
                case "0":
                    estadoConectado = "Desconectado";
                    break;
                case "1":
                    estadoConectado = "Conectado";
                    break;
                default:
                    estadoConectado = "Sin estado";
                    break;
            }
            txtConectado.setText(estadoConectado);

            ArrayAdapter<String> tiposAdapter = new ArrayAdapter<>(itemView.getContext(),
                    android.R.layout.simple_spinner_item, opciones);
            tiposAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerTipo1.setAdapter(tiposAdapter);

            spinnerTipo1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("SensorAdapter", "spinnerTipo1 onItemSelected: position=" + position);
                    List<String> data;
                    switch (position) {
                        case 0:
                            data = nombreFuentes;
                            break;
                        case 1:
                            data = nombreRedes;
                            break;
                        case 2:
                            data = nombrePlantas;
                            break;
                        default:
                            data = new ArrayList<>();
                            break;
                    }
                    updateSectoresSpinner(data);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // No action needed
                }
            });

            // Set OnClickListener for the txtMapa
            txtMapa.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mapClickListener.onMapClick(sensorData.getY(), sensorData.getX(), sensorData.toJson());
                }
            });

            // Update spinnerSectores1 initially
            int initialPosition = spinnerTipo1.getSelectedItemPosition();
            List<String> initialData = getDataByPosition(initialPosition, nombreFuentes, nombreRedes, nombrePlantas);
            updateSectoresSpinner(initialData);
        }

        private List<String> getDataByPosition(int position, List<String> nombreFuentes, List<String> nombreRedes, List<String> nombrePlantas) {
            switch (position) {
                case 0:
                    return nombreFuentes;
                case 1:
                    return nombreRedes;
                case 2:
                    return nombrePlantas;
                default:
                    return new ArrayList<>();
            }
        }

        private void updateSectoresSpinner(List<String> data) {
            Log.d("SensorAdapter", "updateSectoresSpinner with data: " + data);
            ArrayAdapter<String> sectoresAdapter = new ArrayAdapter<>(itemView.getContext(),
                    android.R.layout.simple_spinner_item, data);
            sectoresAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerSectores1.setAdapter(sectoresAdapter);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<String> nombreFuentes, List<String> nombreRedes, List<String> nombrePlantas) {
        this.nombreFuentes = nombreFuentes;
        this.nombreRedes = nombreRedes;
        this.nombrePlantas = nombrePlantas;
        Log.d("SensorAdapter", "Updated data - Fuentes: " + nombreFuentes.size() + ", Redes: " + nombreRedes.size() + ", Plantas: " + nombrePlantas.size());
        notifyDataSetChanged();
    }

    public interface OnMapClickListener {
        void onMapClick(String latitude, String longitude, JSONObject sensorData);
    }
}
