package com.example.htechqr;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;

public class VolleySingleton {
    private static VolleySingleton instanciaVolley;
    private RequestQueue request;
    private static Context context;

    private static Cache cache;
    private static File fileCache;
    public static CookieStore defaultCookies;

    private VolleySingleton(Context context) {
        this.context = context;
        request = getRequestQueue();
    }

    public RequestQueue getRequestQueue() {
        if(request == null){
            fileCache = new File(context.getCacheDir(),"cache/volley");
            cache = new DiskBasedCache(fileCache,2048*2048);
            defaultCookies = new CookieStore2(context);
            java.net.CookieManager manager = new CookieManager(defaultCookies, CookiePolicy.ACCEPT_ALL);
            CookieHandler.setDefault(manager);
            Network network = new BasicNetwork( new HurlStack());
            request = new RequestQueue(cache, network);
            request.start();
        }
        return request;
    }

    public <T> void addToRequestQueue(Request<T> request){
        getRequestQueue().add(request);
    }

    public static synchronized VolleySingleton getInstanciaVolley(Context context){
        if (instanciaVolley == null) {
            instanciaVolley = new VolleySingleton(context);
        }
        return instanciaVolley;
    }

    public static void clearCookie() {
        defaultCookies.removeAll();
    }
}
