package com.example.htechqr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    //FragmentTransaction transaction;
   // Fragment fragmentLogin, fragmentQr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // fragmentLogin= new LoginFragment();
        //getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragments,fragmentLogin).
        // commit();
    }

}