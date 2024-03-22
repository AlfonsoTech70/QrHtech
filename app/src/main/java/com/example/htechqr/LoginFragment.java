package com.example.htechqr;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.htechqr.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginFragment extends Fragment implements Response.Listener<JSONObject>, Response.ErrorListener {

    EditText txtContraseña, txtUsuario;
    ProgressDialog progressDialog;
    Button btnIngresar;
    JsonObjectRequest jsonRequestWebService;
    NavController navController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View ventana = inflater.inflate(R.layout.fragment_login, container, false);

        txtContraseña = ventana.findViewById(R.id.txtContraseña);
        txtUsuario = ventana.findViewById(R.id.txtUsuario);
        btnIngresar = ventana.findViewById(R.id.btnIngresar);

        btnIngresar.setVisibility(View.VISIBLE); // Aseguramos que el botón sea visible

        //btnIngresar.setEnabled(false); // Deshabilitado inicialmente

        txtUsuario.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No se necesita implementar en este caso
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Verificar si el texto ingresado tiene un formato válido de correo electrónico
                if (isValidEmail(s.toString())) {
                    // Habilitar el botón si es un correo electrónico válido
                    btnIngresar.setEnabled(true);
                } else {
                    // Deshabilitar el botón si no es un correo electrónico válido
                    btnIngresar.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No se necesita implementar en este caso
            }

            private boolean isValidEmail(String email) {
                return Patterns.EMAIL_ADDRESS.matcher(email).matches();
            }
        });

        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CargarWebService();
            }
        });

        return ventana;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.i("msj11", error.toString());
    }

    @Override
    public void onResponse(JSONObject response) {
        int requestVal;
        try {
            requestVal = response.getInt("Login");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        if (requestVal == 2) {
            Toast.makeText(requireContext(), "Acceso correcto", Toast.LENGTH_SHORT).show();


            // Navegamos al destino del fragmento deseado
            navController.navigate(R.id.qrFragment);
        } else {
            switch (requestVal) {
                case 3:
                    // El nombre de usuario no existe
                    Utils.mostrarMensaje(getContext(), "El nombre de usuario no existe.");
                    break;
                case 4:
                    // La contraseña es incorrecta
                    Utils.mostrarMensaje(getContext(), "La contraseña es incorrecta.");
                    break;
                case 5:
                    // No se ingresaron nombre de usuario y/o contraseña
                    Utils.mostrarMensaje(getContext(), "No se ingresaron nombre de usuario y/o contraseña.");
                    break;
                case 6:
                    // La cuenta no ha sido activada
                    Utils.mostrarMensaje(getContext(), "La cuenta no ha sido activada.");
                    break;
            }
        }
        progressDialog.dismiss();
    }

    public void CargarWebService() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Cargando..."); // Mensaje que se mostrará durante la carga
        progressDialog.setCancelable(false); // No permite cancelar el diálogo
        progressDialog.show(); // Mostrar el diálogo de progreso
        String url = "https://webservice.htech.mx/login.php?usuario=" + txtUsuario.getText().toString() + "&password=" + txtContraseña.getText().toString() + "&nombre_json=Login";
        jsonRequestWebService = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        VolleySingleton.getInstanciaVolley(getContext()).addToRequestQueue(jsonRequestWebService);
        Log.i("msj", url);
    }

    // Necesario si usas Navigation Component
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Obtenemos el NavController desde el fragmento actual
        navController = Navigation.findNavController(view);
    }
}