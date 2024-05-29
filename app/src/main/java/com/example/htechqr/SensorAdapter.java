package com.example.htechqr;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SensorAdapter extends RecyclerView.Adapter<SensorAdapter.SensorViewHolder> {

    private List<SensorData> sensorList;
    private List<String> nombreFuentes;
    private List<String> nombreRedes;
    private List<String> nombrePlantas;
    private List<Integer> idsFuentes;
    private List<Integer> idsRedes;
    private List<Integer> idsPlantas;
    static final String[] opciones = {
            "Fuentes de abastecimiento",
            "Fuentes de distribución",
            "Plantas de tratamiento"
    };

    public SensorAdapter(List<SensorData> sensorList, List<String> nombreFuentes, List<String> nombreRedes, List<String> nombrePlantas, List<Integer> idsFuentes, List<Integer> idsRedes, List<Integer> idsPlantas) {
        this.sensorList = sensorList;
        this.nombreFuentes = nombreFuentes;
        this.nombreRedes = nombreRedes;
        this.nombrePlantas = nombrePlantas;
        this.idsFuentes = idsFuentes;
        this.idsRedes = idsRedes;
        this.idsPlantas = idsPlantas;
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
        holder.bind(sensorData);
    }

    @Override
    public int getItemCount() {
        return sensorList.size();
    }

    public class SensorViewHolder extends RecyclerView.ViewHolder {
        private Spinner spinnerSectores1, spinnerTipo1;
        private TextView txtDispositivo, txtIntervalo, txtConectado, txtRssi, txtUnidades, txtIdDispositivo;

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
        }

        public void bind(SensorData sensorData) {
            txtIdDispositivo.setText(sensorData.getIdDispositivo());
            txtIntervalo.setText(sensorData.getIntervalo());
            txtConectado.setText(sensorData.getConectado());
            txtRssi.setText(sensorData.getRssi());
            txtUnidades.setText(sensorData.getUnidades());

            ArrayAdapter<String> tiposAdapter = new ArrayAdapter<>(itemView.getContext(),
                    android.R.layout.simple_spinner_item, opciones);
            tiposAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerTipo1.setAdapter(tiposAdapter);
            configurarSpinnerSectores();
        }

        private void configurarSpinnerSectores() {
            ArrayAdapter<String> adapter;
            switch (SensorFragment.idTipoSubSistema) {
                case 0:
                    adapter = new ArrayAdapter<>(itemView.getContext(), android.R.layout.simple_spinner_item, nombreFuentes);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerSectores1.setAdapter(adapter);
                    break;
                case 1:
                    adapter = new ArrayAdapter<>(itemView.getContext(), android.R.layout.simple_spinner_item, nombreRedes);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerSectores1.setAdapter(adapter);
                    break;
                case 2:
                    adapter = new ArrayAdapter<>(itemView.getContext(), android.R.layout.simple_spinner_item, nombrePlantas);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerSectores1.setAdapter(adapter);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + SensorFragment.idTipoSubSistema);
            }
            adapter.notifyDataSetChanged(); // Notificar cambios en el adaptador
        }
    }
}
