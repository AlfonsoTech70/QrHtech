package com.example.htechqr;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.webkit.CookieManager;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.htechqr.R;
import com.example.htechqr.Utils;
import com.example.htechqr.VolleySingleton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class QrFragment extends Fragment implements Response.Listener<JSONObject>, Response.ErrorListener, AdapterView.OnItemSelectedListener{
    private void cargarCookies() {
        String cookies = cookieManager.getCookie("https://webservice.htech.mx"); // Reemplaza con tu URL
    }

    Button btnScan, btnConsultar, btnEditar, btnConfirmar;
    String cadena;
    EditText txtResultado; //Número de serie
    TextView txtSistema;
    EditText txtSector;
    EditText txtNombre;
    int idSistema=0;
    int idRadio, idSector, idDispositivo, idSubSector;
    EditText txtDescripcion;
    ProgressDialog progressDialog;

    EditText txtBT;
    EditText txtID;
    Spinner spinnerSistemas, spinnerSectores, spinnerRadios, spinnerDispositivos, spinnerTipo;
    EditText txtTelefono, txtIdTelefono;
    EditText txtDispositivo;
    EditText txtTipoRadio;
    EditText txtSubradio;
    EditText txtVenSaldo;
    // Lista de opciones para el spinner
    final String[] opciones = {
            "Fuentes de abastecimiento",
            "Fuentes de distribución",
            "Plantas de tratamiento"};

    // Añade esta variable para manejar las cookies
    private CookieManager cookieManager;
    JsonObjectRequest jsonRequestWebService;

    boolean spinerON= false;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_qr, container, false);

// Este es un comentario de prueba

        spinnerSistemas = rootView.findViewById(R.id.spinnerSistemas);
        spinnerTipo = rootView.findViewById(R.id.spinnerTipo);
        spinnerSectores = rootView.findViewById(R.id.spinnerSectores);
        spinnerRadios = rootView.findViewById(R.id.spinnerRadios);
        spinnerDispositivos = rootView.findViewById(R.id.spinnerDispositivos);
        txtResultado = rootView.findViewById(R.id.txtResultado);
        txtID = rootView.findViewById(R.id.txtID);
        txtDescripcion= rootView.findViewById(R.id.txtDescripcion);
        txtIdTelefono = rootView.findViewById(R.id.txtIdTelefono);
        txtNombre = rootView.findViewById(R.id.txtNombre);
        txtBT = rootView.findViewById(R.id.txtBT);
        txtTelefono = rootView.findViewById(R.id.txtTelefono);
        txtVenSaldo = rootView.findViewById(R.id.txtVenSaldo);
        //txtSistema = rootView.findViewById(R.id.txtSistema);
        txtSubradio = rootView.findViewById(R.id.txtSubRadio);

        spinnerTipo.setEnabled(false);
        spinnerSectores.setEnabled(false);
        spinnerRadios.setEnabled(false);
        spinnerSistemas.setEnabled(false);
        spinnerDispositivos.setEnabled(false);


        // Inicializa el CookieManager
        cookieManager = CookieManager.getInstance();
        btnConfirmar=rootView.findViewById(R.id.btnConfirmar);
        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {CamposLimpios();
               guardarCambios();
            }
        });

        btnScan = rootView.findViewById(R.id.btnScan);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnEditar.setVisibility(View.GONE);
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, 1);
                } else {
                    iniciarEscaneo();
                }
            }
        });
        btnEditar=rootView.findViewById(R.id.btnEditar);
        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                habilitarEdicion();
            }
        });
        btnConsultar = rootView.findViewById(R.id.btnConsultar);
        btnConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarCookies();
                 //CargarConsultaDispositivo();
                //CargarConsultaSistema();
                //CargarConsultaSubsistema();
                //CargarConsultaRadio();
                CargarWebService();
            }
        });

        spinnerSistemas.setOnItemSelectedListener(this);
        spinnerTipo.setOnItemSelectedListener(this);
        return rootView;
    }

    private void iniciarEscaneo() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
        integrator.setOrientationLocked(false);
        integrator.setPrompt("Escanea un código QR");
        integrator.initiateScan();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                iniciarEscaneo();
            } else {
                Toast.makeText(requireContext(), "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                txtResultado.setText(result.getContents());
                cadena = txtResultado.getText().toString();
                btnConsultar.setVisibility(View.VISIBLE);
                btnScan.setVisibility(View.GONE);
            } else {
                Toast.makeText(requireContext(), "Escaneo cancelado o sin éxito", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.i("msj2", "Error On Response");
    }

    @Override
    public void onResponse(JSONObject response) {
        //Consulta Detalles Dispositivo
        JSONArray detallesArray;
        detallesArray = response.optJSONArray("Detalles");
        if (detallesArray != null) {
            if (detallesArray.length() == 0) {
                Utils.mostrarMensaje(getContext(), "Consulta Incorrecta.");
                CamposLimpios();
                btnConsultar.setVisibility(View.GONE);
                btnScan.setVisibility(View.VISIBLE);
                btnEditar.setVisibility(View.GONE);
            } else {
                try {
                    JSONObject detallesObject = detallesArray.getJSONObject(0);
                    idSector = detallesObject.getInt("idSubsistema");
                    idRadio = detallesObject.getInt("radio");
                    idSistema = detallesObject.getInt("idSistema");
                    idDispositivo = detallesObject.getInt("dispositivo");
                    idSubSector = detallesObject.getInt("tipo_subsistema");
                    txtID.setText(detallesObject.optString("idDispositivo"));
                    txtIdTelefono.setText(detallesObject.optString("telefono_num_serie"));
                    txtNombre.setText(detallesObject.optString("nombre"));
                    txtDescripcion.setText(detallesObject.optString("descripcion"));
                    txtBT.setText(detallesObject.optString("bluetooth"));
                    txtTelefono.setText(detallesObject.optString("telefono"));
                    txtVenSaldo.setText(detallesObject.optString("fecha_vencimiento"));
                    txtSubradio.setText(detallesObject.optString("subradio"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                btnConsultar.setVisibility(View.GONE);
                btnScan.setVisibility(View.VISIBLE);
                btnEditar.setVisibility(View.VISIBLE);
            }
            CargarConsultaSistema();
            CargarConsultaSubsistema();
        }

        // Consulta Sistemas
        List<Sistema> sistemas = new ArrayList<>();
        List<Integer> idsSistemas = new ArrayList<>(); // Arreglo de enteros para guardar los IDs
        List<String> nombresSistemas = new ArrayList<>(); // Arreglo de strings para guardar los nombres de los sistemas

        detallesArray = response.optJSONArray("Sistemas");
        if (detallesArray != null){
            JSONObject detallesCompletos = new JSONObject();
            try {
                detallesCompletos.put("Sistemas", detallesArray);
                // Imprimir el objeto completo en el registro
                Log.i("Consulta Completa", detallesCompletos.toString());
                Log.i("Consulta Completaa", detallesArray.toString());
                for (int i = 0; i < detallesArray.length(); i++) {
                    try {
                        JSONObject jsonObject = detallesArray.getJSONObject(i);
                        String idSistema = jsonObject.getString("idSistema");
                        String nombre = jsonObject.getString("nombre");
                        String estado = jsonObject.getString("estado");
                        String logo = jsonObject.getString("logo");

                        // Crear objeto Sistema y añadirlo a la lista
                        Sistema sistema = new Sistema(idSistema, nombre, logo, estado);
                        sistemas.add(sistema);

                        // Agregar el ID del sistema al arreglo de IDs
                        idsSistemas.add(Integer.valueOf(idSistema));

                        // Agregar el nombre del sistema al arreglo de nombres
                        nombresSistemas.add(nombre);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Enlazar el ArrayList de nombres al Spinner
            //Spinner spinner = rootfindViewById(R.id.spinner);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, nombresSistemas);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerSistemas.setAdapter(adapter);

            // Buscar el índice del ID seleccionado en el arreglo de IDs
            int index = idsSistemas.indexOf(idSistema);
            if (index != -1) {
                spinnerSistemas.setSelection(index);
            }
            CargarConsultaDispositivo();
        }


        // Consulta Subsistemas
        List<String> nombreFuentes = new ArrayList<>();
        List<Subsistema> subFuentes = new ArrayList<>();
        List<Integer> idsFuentes = new ArrayList<>();
        List<String> nombreRedes = new ArrayList<>();
        List<Subsistema> subRedes = new ArrayList<>();
        List<Integer> idsRedes = new ArrayList<>();
        List<String> nombrePlantas = new ArrayList<>();
        List<Subsistema> subPlantas = new ArrayList<>();
        List<Integer> idsPlantas = new ArrayList<>();

        detallesArray = response.optJSONArray("Subsistemas");

        if (detallesArray != null) {
            Log.i("msj", "Contenido del array detallesArray: " + detallesArray.toString());
        } else {
            Log.e("msj", "El array detallesArray es nulo. " + response.toString());
        }


        // Crear un adaptador para el Spinner

        String[] opciones = getResources().getStringArray(R.array.opciones_spinner);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, opciones);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(adapter1);
        spinnerTipo.setSelection(idSubSector);

        switch (idSubSector) {
            case 0:
                if (detallesArray != null && detallesArray.length() > 0) {
                    JSONArray fuentes = detallesArray.optJSONArray(0);
                    try {
                        for (int i = 0; i < fuentes.length(); i++) {
                            JSONObject jsonObject = fuentes.getJSONObject(i);
                            String nombre = jsonObject.getString("nombre");
                            String idSubsistema = jsonObject.getString("idSubsistema");
                            String tipo = jsonObject.getString("tipo");
                            Subsistema fuentesSubsistema = new Subsistema(idSubsistema, nombre, tipo);
                            subFuentes.add(fuentesSubsistema);
                            nombreFuentes.add(nombre);
                            idsFuentes.add(Integer.valueOf(idSubsistema));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, nombreFuentes);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerSectores.setAdapter(adapter);
                    Log.i("msj30", fuentes.toString());
                }
                break;

            case 1:
                if (detallesArray != null && detallesArray.length() > 1) {
                    JSONArray redes = detallesArray.optJSONArray(1);
                    try {
                        for (int i = 0; i < redes.length(); i++) {
                            JSONObject jsonObject = redes.getJSONObject(i);
                            String nombre = jsonObject.getString("nombre");
                            int id= Integer.parseInt(jsonObject.getString("idSubsistema"));
                            nombreRedes.add(nombre);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, nombreRedes);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerSectores.setAdapter(adapter);
                    Log.i("msj30", redes.toString());
                }
                break;

            case 2:
                if (detallesArray != null && detallesArray.length() > 2) {
                    JSONArray plantas = detallesArray.optJSONArray(2);
                    try {
                        for (int i = 0; i < plantas.length(); i++) {
                            JSONObject jsonObject = plantas.getJSONObject(i);
                            String nombre = jsonObject.getString("nombre");
                            nombrePlantas.add(nombre);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, nombrePlantas);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerSectores.setAdapter(adapter);
                    Log.i("msj30", plantas.toString());
                }
                break;
        }
        progressDialog.dismiss(); // Ocultar el diálogo de progreso


        // Consulta Dispositivos
        detallesArray = response.optJSONArray("Dispositivos");
        if (detallesArray != null){
            List<Dispositivo> dispositivos = new ArrayList<>();
            List<Integer> idsDispositivos = new ArrayList<>();
            List<String> nombresDispositivos = new ArrayList<>();
            if (detallesArray != null) {
                for (int i = 0; i < detallesArray.length(); i++) {
                    try {
                        JSONObject jsonObject = detallesArray.getJSONObject(i);
                        int idDispositivo = jsonObject.getInt("id");
                        String nombreDispositivo = jsonObject.getString("nombre");

                        // Crear objeto Dispositivo y añadirlo a la lista
                        Dispositivo dispositivo = new Dispositivo(idDispositivo, nombreDispositivo);
                        dispositivos.add(dispositivo);

                        // Agregar el ID del dispositivo al arreglo de IDs
                        idsDispositivos.add(idDispositivo);

                        // Agregar el nombre del dispositivo al arreglo de nombres
                        nombresDispositivos.add(nombreDispositivo);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                // Enlazar el ArrayList de nombres de dispositivos al Spinner (cambia según tus necesidades)
                ArrayAdapter<String> adapterDispositivos = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, nombresDispositivos);
                adapterDispositivos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerDispositivos.setAdapter(adapterDispositivos);

                // Buscar el índice del ID seleccionado en el arreglo de IDs de dispositivos
                int indexDispositivo = idsDispositivos.indexOf(idDispositivo);
                if (indexDispositivo != -1) {
                    spinnerDispositivos.setSelection(indexDispositivo);
                }
                CargarConsultaRadio();
            }
        }



        // Consulta Radios
        JSONArray radiosArray = response.optJSONArray("Radios");
        if (radiosArray != null) {
            List<Radio> radios = new ArrayList<>();
            List<Integer> idsRadios = new ArrayList<>();
            List<String> nombresRadios = new ArrayList<>();
            try {
                for (int i = 0; i < radiosArray.length(); i++) {
                    JSONObject jsonObject = radiosArray.getJSONObject(i);
                    int idRadio = jsonObject.getInt("id");
                    String nombreRadio = jsonObject.getString("radio");
                    // Agregar el ID del dispositivo al arreglo de IDs
                    Radio radio = new Radio(idRadio, nombreRadio);
                    radios.add(radio);
                    idsRadios.add(idRadio);
                    nombresRadios.add(nombreRadio);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ArrayAdapter<String> adapterRadios = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, nombresRadios);
            adapterRadios.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerRadios.setAdapter(adapterRadios);

            // Buscar el índice del ID seleccionado en el arreglo de IDs de dispositivos
            int indexRadio = idsRadios.indexOf(idRadio);
            if (indexRadio != -1) {
                spinnerRadios.setSelection(indexRadio);
            }
            CargarConsultaSubsistema();
        }
    }


    public void CargarWebService() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Cargando..."); // Mensaje que se mostrará durante la carga
        progressDialog.setCancelable(false); // No permite cancelar el diálogo
        progressDialog.show(); // Mostrar el diálogo de progreso
        String url = "https://webservice.htech.mx/consultar.php?opcion=40&numero_de_serie=HTPPM-2321007&nombre_json=Detalles";
        jsonRequestWebService = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        VolleySingleton.getInstanciaVolley(getContext()).addToRequestQueue(jsonRequestWebService);
        Log.i("msj2", url);
    }

    public void CargarConsultaSistema() {
        String url = "https://webservice.htech.mx/consultar.php?opcion=34&nombre_json=Sistemas";
        jsonRequestWebService = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        VolleySingleton.getInstanciaVolley(getContext()).addToRequestQueue(jsonRequestWebService);
        Log.i("msj2", url);
    }
    public void CargarConsultaDispositivo() {
        String url = "https://webservice.htech.mx/consultar.php?opcion=33&nombre_json=Dispositivos";
        jsonRequestWebService = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        VolleySingleton.getInstanciaVolley(getContext()).addToRequestQueue(jsonRequestWebService);
        Log.i("msj2", url);
    }
    public void CargarConsultaSubsistema() {
        String url = "https://webservice.htech.mx/consultar.php?opcion=41&id_sistema="+idSistema+"&nombre_json=Subsistemas";
        jsonRequestWebService = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        VolleySingleton.getInstanciaVolley(getContext()).addToRequestQueue(jsonRequestWebService);
        Log.i("msj2", url);
    }
    public void CargarConsultaRadio() {
        String url = "https://webservice.htech.mx/consultar.php?opcion=42&nombre_json=Radios";
        jsonRequestWebService = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        VolleySingleton.getInstanciaVolley(getContext()).addToRequestQueue(jsonRequestWebService);
        Log.i("msj2", url);
    }

    public void CamposLimpios(){
        txtIdTelefono.setText("-");
        txtNombre.setText("-");
        txtDescripcion.setText("-");
        txtBT.setText("-");
        txtTelefono.setText("-");
        txtSubradio.setText("-");
        txtVenSaldo.setText("-");
    }
    private void habilitarEdicion() {
        txtNombre.setEnabled(true);
        txtDescripcion.setEnabled(true);
        txtBT.setEnabled(true);
        txtID.setEnabled(true);
        txtTelefono.setEnabled(true);
        txtSubradio.setEnabled(true);
        txtVenSaldo.setEnabled(true);
        txtIdTelefono.setEnabled(true);
        btnConfirmar.setVisibility(View.VISIBLE);
        btnScan.setVisibility(View.GONE);
        btnEditar.setVisibility(View.GONE);
        spinnerTipo.setEnabled(true);
        spinnerSectores.setEnabled(true);
        spinnerRadios.setEnabled(true);
        spinnerSistemas.setEnabled(true);
        spinnerDispositivos.setEnabled(true);
    }

    private void guardarCambios() {
        txtNombre.setEnabled(false);
        txtDescripcion.setEnabled(false);
        txtBT.setEnabled(false);
        txtID.setEnabled(false);
        txtTelefono.setEnabled(false);
        txtSubradio.setEnabled(false);
        txtVenSaldo.setEnabled(false);
        txtIdTelefono.setEnabled(false);
        btnConfirmar.setVisibility(View.GONE);
        btnScan.setVisibility(View.VISIBLE);
        btnEditar.setVisibility(View.VISIBLE);

        // Aquí puedes guardar los cambios en la base de datos o realizar otra acción necesaria
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if ( btnEditar.getVisibility() == View.GONE) {
            if (parent.getId() == R.id.spinnerSistemas) {
                idSistema = position + 1;
            } else if (parent.getId() == R.id.spinnerTipo) {
                idSubSector = position;
            }
        }
            Log.i("SPINER2","entro");
            CargarConsultaSubsistema();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
