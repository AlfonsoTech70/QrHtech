package com.example.htechqr;

import org.json.JSONException;
import org.json.JSONObject;
import android.widget.EditText;

public class DatosDispositivo {
    private String idTelefono;
   // private String sistema;
    private String sector;
    private String nombre;
    private String descripcion;
    private String bt;
    private String id;
    private String telefono;
    private String dispositivo;
    private String tipoRadio;
    private String subradio;
    private String venSaldo;

    // Constructores, getters y setters

    public void guardarEnDocumento(JSONObject documentos, EditText txtIdTelefono, EditText txtSector, EditText txtNombre,
                                   EditText txtDescripcion, EditText txtBT, EditText txtID, EditText txtTelefono, EditText txtDispositivo,
                                   EditText txtTipoRadio, EditText txtSubradio, EditText txtVenSaldo) {
        try {
            JSONObject datosJson = new JSONObject();
            datosJson.put("idTelefono", txtIdTelefono.getText().toString());
           // datosJson.put("sistema", txtSistema.getText().toString());
            datosJson.put("sector", txtSector.getText().toString());
            datosJson.put("nombre", txtNombre.getText().toString());
            datosJson.put("descripcion", txtDescripcion.getText().toString());
            datosJson.put("bt", txtBT.getText().toString());
            datosJson.put("id", txtID.getText().toString());
            datosJson.put("telefono", txtTelefono.getText().toString());
            datosJson.put("dispositivo", txtDispositivo.getText().toString());
            datosJson.put("tipoRadio", txtTipoRadio.getText().toString());
            datosJson.put("subradio", txtSubradio.getText().toString());
            datosJson.put("venSaldo", txtVenSaldo.getText().toString());

            documentos.put("datosDispositivo", datosJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
