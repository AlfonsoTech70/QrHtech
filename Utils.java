package com.example.htechqr;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class Utils {

    public static void mostrarMensaje(Context context, String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(mensaje)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Si se hace clic en Aceptar, cierra el diálogo
                        dialog.dismiss();
                    }
                });
        // Crear y mostrar el AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
