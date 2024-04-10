package com.example.htechqr;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.webkit.CookieManager;
import java.net.HttpCookie;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

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

    Button btnScan, btnConsultar, btnEditar, btnConfirmar,btnRegistrar,btnconfirmarReg;
    String azul="0x025C91" ;
    String amarillo ="0XFFC61A";
    int opcion;
    Calendar calendar;
    String cadena;
    boolean seguir, campoNS= true;
    int index;
    int idGrupo=0;
    EditText txtResultado; //Número de serie
    EditText txtNombre;
    int idSistema=1;
    int idRadio, idDispositivo=1;
    int idSector = 5;
    int idTipoSubSistema=1;
    EditText txtDescripcion;
    ProgressDialog progressDialog;

    EditText txtBT;

    EditText txtID;
    JSONArray fuentes, redes, plantas;
    Spinner spinnerSistemas, spinnerSectores, spinnerRadios, spinnerDispositivos, spinnerTipo;
    EditText txtTelefono, txtIdTelefono;
    EditText txtDispositivo;
    EditText txtTipoRadio;
    EditText txtSubradio;
    EditText txtVenSaldo;
    List<Dispositivo> dispositivos = new ArrayList<>();
    List<Integer> idsDispositivos = new ArrayList<>();
    List<String> nombresDispositivos = new ArrayList<>();
    List<String> nombreFuentes = new ArrayList<>();
    List<Subsistema> subFuentes = new ArrayList<>();
    List<Integer> idsFuentes = new ArrayList<>();
    List<String> nombreRedes = new ArrayList<>();
    List<Subsistema> subRedes = new ArrayList<>();
    List<Integer> idsRedes = new ArrayList<>();
    List<String> nombrePlantas = new ArrayList<>();
    List<Subsistema> subPlantas = new ArrayList<>();
    List<Integer> idsPlantas = new ArrayList<>();
    List<Radio> radios = new ArrayList<>();
    List<Integer> idsRadios = new ArrayList<>();
    List<String> nombresRadios = new ArrayList<>();
    List<Sistema> sistemas = new ArrayList<>();
    List<Integer> idsSistemas = new ArrayList<>(); // Arreglo de enteros para guardar los IDs
    List<String> nombresSistemas = new ArrayList<>(); // Arreglo de strings para guardar los nombres de los sistemas

    // Lista de opciones para el spinner
    final String[] opciones = {
            "Fuentes de abastecimiento",
            "Fuentes de distribución",
            "Plantas de tratamiento"};

    // Añade esta variable para manejar las cookies
    private CookieManager cookieManager;
    JsonObjectRequest jsonRequestWebService;

    boolean spinerON= true;

    @SuppressLint({"MissingInflatedId", "ResourceAsColor"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_qr, container, false);

        //Txt

        //Iniciamos
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

        btnconfirmarReg=rootView.findViewById(R.id.btnconfirmarReg);
        btnconfirmarReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarCambios();

            }
        });

        btnRegistrar=rootView.findViewById(R.id.btnRegistrar);
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CargarConsultaSistema();
                habilitarEdicion();
                btnRegistrar.setVisibility(View.GONE);
                btnEditar.setVisibility(View.GONE);
                btnConfirmar.setVisibility(View.GONE);
                btnScan.setVisibility(View.GONE);
                btnConsultar.setVisibility(View.GONE);
                btnconfirmarReg.setVisibility(View.VISIBLE);
                opcion=12;
            }
        });
        // Inicializa el CookieManager
        cookieManager = CookieManager.getInstance();
        btnConfirmar=rootView.findViewById(R.id.btnConfirmar);
        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarJson();
                btnConfirmar.setVisibility(View.GONE);
                btnconfirmarReg.setVisibility(View.GONE);
                btnScan.setVisibility(View.VISIBLE);
                btnConsultar.setVisibility(View.VISIBLE);
                btnRegistrar.setVisibility(View.VISIBLE);
            }
        });
        //Proceso txtVenSaldo
        calendar = Calendar.getInstance();
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        txtVenSaldo.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (view, year, month, dayOfMonth) -> {
                        // Establecer la fecha seleccionada en el EditText en el formato deseado
                        calendar.set(year, month, dayOfMonth);
                        txtVenSaldo.setText(dateFormat.format(calendar.getTime()));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));

            datePickerDialog.show();
        });
        btnScan = rootView.findViewById(R.id.btnScan);
        //2196F3



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
                btnRegistrar.setVisibility(View.GONE);
                cargarCookies();
                cadena = txtResultado.getText().toString();
                //CargarConsultaDispositivo();
                //CargarConsultaSistema();
                //CargarConsultaSubsistema();
                //CargarConsultaRadio();
                CargarWebService();
            }
        });

        btnScan.setBackgroundColor(R.color.gray);
        btnScan.setBackgroundColor(Color.rgb(33,150,243));
        btnConsultar.setBackgroundColor(Color.rgb(33,150,243));
        btnEditar.setBackgroundColor(Color.rgb(33,150,243));
        btnConfirmar.setBackgroundColor(Color.rgb(33,150,243));
        ableSpiners(false);
        return rootView;
    }

    void ableSpiners(boolean val){
        if(val) {
            spinnerSistemas.setOnItemSelectedListener(this);
            spinnerTipo.setOnItemSelectedListener(this);
            spinnerRadios.setOnItemSelectedListener(this);
            spinnerDispositivos.setOnItemSelectedListener(this);
            spinnerSectores.setOnItemSelectedListener(this);
        }else{
            spinnerSistemas.setOnItemSelectedListener(null);
            spinnerTipo.setOnItemSelectedListener(null);
            spinnerRadios.setOnItemSelectedListener(null);
            spinnerDispositivos.setOnItemSelectedListener(null);
            spinnerSectores.setOnItemSelectedListener(null);
        }
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
                if (campoNS){
                    txtResultado.setText(result.getContents());
                    cadena = txtResultado.getText().toString();
                    btnConsultar.setVisibility(View.VISIBLE);
                    btnScan.setVisibility(View.GONE);
                }
                else{
                    txtIdTelefono.setText(result.getContents());
                }

            } else {
                Toast.makeText(requireContext(), "Escaneo cancelado o sin éxito", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.i("Respuesta", "Error On Response"+error.toString());
    }

    @Override
    public void onResponse(JSONObject response) {
        ArrayAdapter<String> adapterTipo = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, opciones);

        //Consulta Detalles Dispositivo
        JSONArray detallesArray;
        JSONObject detallesArray2;
        try {
            detallesArray2 =response.getJSONObject("envioDatos");
            if (detallesArray2!= null){
                Log.i("Respuesta", detallesArray2.toString());
            }
        } catch (JSONException e) {
            //throw new RuntimeException(e);
        }
        JSONObject detallesArray3;
        try {
            detallesArray3 =response.getJSONObject("registro");
            if (detallesArray3!= null){
                Log.i("Respuesta", detallesArray3.toString());
            }
        } catch (JSONException e) {
            //throw new RuntimeException(e);
        }


        detallesArray = response.optJSONArray("Detalles");
        if (detallesArray != null) {
            if (detallesArray.length() == 0) {
                Utils.mostrarMensaje(getContext(), "Consulta Incorrecta.");
                seguir=false;
                CamposLimpios();
                btnConsultar.setVisibility(View.VISIBLE);
                btnScan.setVisibility(View.VISIBLE);
                btnEditar.setVisibility(View.GONE);
                progressDialog.dismiss(); // Ocultar el diálogo de progreso
            } else {
                Log.i("msjSpinner", String.valueOf(idTipoSubSistema));
                try {
                    seguir=true;
                    JSONObject detallesObject = detallesArray.getJSONObject(0);
                    idSector = detallesObject.getInt("idSubsistema");
                    idRadio = detallesObject.getInt("radio");
                    idSistema = detallesObject.getInt("idSistema");
                    idDispositivo = detallesObject.getInt("dispositivo");
                    idTipoSubSistema = detallesObject.getInt("tipo_subsistema");
                    Log.i("msjSpinner", String.valueOf(idTipoSubSistema));
                    txtID.setText(detallesObject.optString("idDispositivo"));
                    txtIdTelefono.setText(detallesObject.optString("telefono_num_serie"));
                    txtNombre.setText(detallesObject.optString("nombre"));
                    txtDescripcion.setText(detallesObject.optString("descripcion"));
                    txtBT.setText(detallesObject.optString("bluetooth"));
                    txtTelefono.setText(detallesObject.optString("telefono"));
                    txtVenSaldo.setText(detallesObject.optString("fecha_vencimiento"));
                    txtSubradio.setText(detallesObject.optString("subradio"));
                    idGrupo = detallesObject.getInt("idGrupo");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                btnConsultar.setVisibility(View.GONE);
                btnScan.setVisibility(View.VISIBLE);
                btnEditar.setVisibility(View.VISIBLE);
            }
            if(seguir)
                CargarConsultaSistema();
        }

        // Consulta Sistemas
        detallesArray = response.optJSONArray("Sistemas");
        if (detallesArray != null){
            Log.i("msjjj", "Entro a Sistemas");
            sistemas.clear();
            idsSistemas.clear();
            nombresSistemas.clear();
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

        // Consulta Dispositivos
        detallesArray = response.optJSONArray("Dispositivos");
        if (detallesArray != null){
            Log.i("msjjj", "Entro a Disp");
            dispositivos.clear();
            idsDispositivos.clear();
            nombresDispositivos.clear();

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
            Log.i("msjjj", "Entro a Rad");
            radios.clear();
            idsRadios.clear();
            nombresRadios.clear();
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

        // Consulta Subsistemas

        detallesArray = response.optJSONArray("Subsistemas");

        if (detallesArray != null) {
            String[] opciones = getResources().getStringArray(R.array.opciones_spinner);
            adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerTipo.setAdapter(adapterTipo);
            spinnerTipo.setSelection(idTipoSubSistema);
            Log.i("msjjj", "Entro a SubSistemas");
            nombreFuentes.clear();
            subFuentes.clear();
            idsFuentes.clear();
            nombreRedes.clear();
            subRedes.clear();
            idsRedes.clear();
            nombrePlantas.clear();
            subPlantas.clear();
            idsPlantas.clear();


            Log.i("msj", "Contenido del array detallesArray: " + detallesArray.toString());
            // Crear un adaptador para el Spinner


            if (detallesArray != null && detallesArray.length() > 0) {
                fuentes = detallesArray.optJSONArray(0);
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

            }
            if (detallesArray != null && detallesArray.length() > 1) {
                redes = detallesArray.optJSONArray(1);
                try {
                    for (int i = 0; i < redes.length(); i++) {
                        JSONObject jsonObject = redes.getJSONObject(i);
                        String nombre = jsonObject.getString("nombre");
                        String idSubsistema = jsonObject.getString("idSubsistema");
                        String tipo = jsonObject.getString("tipo");
                        Subsistema redesSubsistema = new Subsistema(idSubsistema, nombre, tipo);
                        subFuentes.add(redesSubsistema);
                        nombreRedes.add(nombre);
                        idsRedes.add(Integer.valueOf(idSubsistema));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (detallesArray != null && detallesArray.length() > 2) {
                plantas = detallesArray.optJSONArray(2);
                try {
                    for (int i = 0; i < plantas.length(); i++) {
                        JSONObject jsonObject = plantas.getJSONObject(i);
                        String nombre = jsonObject.getString("nombre");
                        String idSubsistema = jsonObject.getString("idSubsistema");
                        String tipo = jsonObject.getString("tipo");
                        Subsistema plantasSubsistema = new Subsistema(idSubsistema, nombre, tipo);
                        subPlantas.add(plantasSubsistema);
                        nombrePlantas.add(nombre);
                        idsPlantas.add(Integer.valueOf(idSubsistema));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            seleccionarSub();
            progressDialog.dismiss(); // Ocultar el diálogo de progreso
            //spinerON=true;
            ableSpiners(true);
            Log.e("msj", "cambio a true" );
        } else {
            //Log.e("msj", "El array detallesArray es nulo. " + response.toString());
        }
    }


    public void CargarWebService() {

        String url = "https://webservice.htech.mx/consultar.php?opcion=40&numero_de_serie="+cadena+"&nombre_json=Detalles";
        jsonRequestWebService = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        VolleySingleton.getInstanciaVolley(getContext()).addToRequestQueue(jsonRequestWebService);
        Log.i("msj2", url);
        Log.i("msj2", cadena);

    }

    public void CargarConsultaSistema() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Cargando..."); // Mensaje que se mostrará durante la carga
        progressDialog.setCancelable(false); // No permite cancelar el diálogo
        progressDialog.show(); // Mostrar el diálogo de progreso
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
    public void envioDatos(JSONObject detalles) {
        String url = "https://webservice.htech.mx/insertar.php";
        jsonRequestWebService = new JsonObjectRequest(Request.Method.POST, url, detalles,this, this);
        VolleySingleton.getInstanciaVolley(getContext()).addToRequestQueue(jsonRequestWebService);
        Log.i("msj2", url);
    }

    public void CamposLimpios(){
        txtIdTelefono.setText("-");
        txtNombre.setText("-");
        txtID.setText("-");
        txtDescripcion.setText("-");
        txtBT.setText("-");
        txtTelefono.setText("-");
        txtSubradio.setText("-");
        txtVenSaldo.setText("-");
        spinnerSistemas.setAdapter(null);
        spinnerDispositivos.setAdapter(null);
        spinnerSectores.setAdapter(null);
        spinnerTipo.setAdapter(null);
        spinnerRadios.setAdapter(null);
    }
    private void habilitarEdicion() {
        opcion=13;
        campoNS=false;
        txtNombre.setEnabled(true);
        txtDescripcion.setEnabled(true);
        txtBT.setEnabled(true);
        txtID.setEnabled(true);
        txtTelefono.setEnabled(true);
        txtSubradio.setEnabled(true);
        txtVenSaldo.setEnabled(true);
        txtIdTelefono.setEnabled(true);
        btnConfirmar.setVisibility(View.VISIBLE);
        btnScan.setVisibility(View.VISIBLE);
        btnEditar.setVisibility(View.GONE);
        spinnerTipo.setEnabled(true);
        spinnerSectores.setEnabled(true);
        spinnerRadios.setEnabled(true);
        spinnerSistemas.setEnabled(true);
        spinnerDispositivos.setEnabled(true);
    }

    private void guardarCambios() {
        campoNS=true;
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
        btnConsultar.setVisibility(View.VISIBLE);
        btnconfirmarReg.setVisibility(View.GONE);
        btnRegistrar.setVisibility(View.VISIBLE);
        btnEditar.setVisibility(View.GONE);
        spinnerTipo.setEnabled(false);
        spinnerSectores.setEnabled(false);
        spinnerRadios.setEnabled(false);
        spinnerSistemas.setEnabled(false);
        spinnerDispositivos.setEnabled(false);
         guardarJson();
        // Aquí puedes guardar los cambios en la base de datos o realizar otra acción necesaria
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Log.i("msjspiner2", "Entra. "+ parent.toString() );
        if (spinerON)
        {
            if (parent.getId() == R.id.spinnerSistemas) {
                ableSpiners(false);
                idSistema = position + 1;
                Log.i("SPINER2", String.valueOf(idSistema));
                ableSpiners(false);
                CargarConsultaSubsistema();
            } else if (parent.getId() == R.id.spinnerTipo) {
                idTipoSubSistema = position;
                Log.i("SPINER2","entro a ON2");
                ableSpiners(false);
                seleccionarSub();
            }
            else if (parent.getId() == R.id.spinnerRadios) {
                for (int i = 0; i < idsRadios.size(); i++) {
                    Log.i("msjjj " + i, String.valueOf(idsRadios.get(i)));
                }
                idRadio = idsRadios.get(position) ;
                Log.i("msjjj", String.valueOf(idRadio));
                Log.i("msjjj", String.valueOf(position));
                Log.i("SPINER2","entro a ON3");
            }
            else if (parent.getId() == R.id.spinnerDispositivos) {
                for (int i = 0; i < idsDispositivos.size(); i++) {
                    Log.i("msjjj " + i, String.valueOf(idsDispositivos.get(i)));
                }
                idDispositivo = idsDispositivos.get(position) ;
                Log.i("msjjj", String.valueOf(idDispositivo));
                Log.i("msjjj", String.valueOf(position));
                Log.i("SPINER2","entro a ON4");

            }
            else if (parent.getId() == R.id.spinnerSectores) {
                if (idTipoSubSistema==0){
                    idSector= idsFuentes.get((position));
                } else if (idTipoSubSistema==1) {
                    idSector= idsRedes.get((position));
                }
                else if (idTipoSubSistema==2) {
                    idSector= idsPlantas.get((position));
                }

                Log.i("msjjj", String.valueOf(idSector));
                Log.i("msjjj", String.valueOf(position));
                Log.i("SPINER2","entro a ON4");

            }

//            ableSpiners(true);
//            spinerON= true;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    public void guardarJson() {
        JSONObject detalles = new JSONObject();
        try {
            if (opcion == 12){
                detalles.put("nombre_json", "registro");
            } else if (opcion == 13) {
                detalles.put("nombre_json", "envioDatos");
            }
            detalles.put("opcion", opcion);
            detalles.put("id_sistema", idSistema);
            detalles.put("id_grupo", idGrupo);
            detalles.put("id_subsistema", idSector);
            detalles.put("nombre", txtNombre.getText().toString());
            detalles.put("descripcion", txtDescripcion.getText().toString());
            detalles.put("num_serie", txtResultado.getText().toString());
            detalles.put("telefono_num_serie", txtIdTelefono.getText().toString());
            detalles.put("telefono", txtTelefono.getText().toString());
            detalles.put("fecha_vencimiento", txtVenSaldo.getText().toString());
            detalles.put("bluetooth", txtBT.getText().toString());
            detalles.put("dispositivo", idDispositivo);
            detalles.put("radio", idRadio);
            detalles.put("subradio", txtSubradio.getText().toString());
            Log.i("idSistema", String.valueOf(idSistema));
            envioDatos(detalles);
            Utils.mostrarMensaje(getContext(), detalles.toString());
            Log.i("MensajeJson", detalles.toString());
            CamposLimpios();
            opcion=0;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void seleccionarSub() {
        Log.i("msjSpinner", String.valueOf(idTipoSubSistema));
        switch (idTipoSubSistema) {

            case 0:
                ArrayAdapter<String> adapter0 = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, nombreFuentes);
                adapter0.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerSectores.setAdapter(adapter0);
                int index = idsFuentes.indexOf(idSector);
                if (index != -1) {
                    spinnerSectores.setSelection(index);
                }

                Log.i("msj30", fuentes.toString());
                progressDialog.dismiss(); // Ocultar el diálogo de progreso
                break;

            case 1:
                ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, nombreRedes);
                adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerSectores.setAdapter(adapter1);
                Log.i("msj30", redes.toString());
                index = idsRedes.indexOf(idSector);
                if (index != -1) {
                    spinnerSectores.setSelection(index);
                }
                progressDialog.dismiss(); // Ocultar el diálogo de progreso
                break;

            case 2:
                ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, nombrePlantas);
                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerSectores.setAdapter(adapter2);
                Log.i("msj30", plantas.toString());
                index = idsPlantas.indexOf(idSector);
                if (index != -1) {
                    spinnerSectores.setSelection(index);
                }
                progressDialog.dismiss(); // Ocultar el diálogo de progreso
                break;
        }
        ableSpiners(true);
    }
}