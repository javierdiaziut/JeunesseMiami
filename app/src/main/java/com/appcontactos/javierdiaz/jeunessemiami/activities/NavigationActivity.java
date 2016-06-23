package com.appcontactos.javierdiaz.jeunessemiami.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.appcontactos.javierdiaz.jeunessemiami.MainActivity;
import com.appcontactos.javierdiaz.jeunessemiami.R;
import com.appcontactos.javierdiaz.jeunessemiami.fragments.SendsSmsFragment;
import com.appcontactos.javierdiaz.jeunessemiami.fragments.SincronizarFragment;
import com.appcontactos.javierdiaz.jeunessemiami.modelos.RowContactsModel;
import com.appcontactos.javierdiaz.jeunessemiami.util.ApplicationController;
import com.appcontactos.javierdiaz.jeunessemiami.util.Config;
import com.appcontactos.javierdiaz.jeunessemiami.util.JsonObjectRequestUtil;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.HttpStatus;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static ArrayList<RowContactsModel> rows = new ArrayList<>();
    protected ProgressDialog mProgressDialog;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nevigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mProgressDialog = new ProgressDialog(this);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setFragment(0);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nevigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_sincronizar:
                setFragment(0);
                break;
            case R.id.nav_enviarsms:
                setFragment(1);
                break;
            case R.id.nav_logout:
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Logut")
                        .setMessage("Desea salir de la aplicación?")
                        .setPositiveButton("Si", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                logout(LoginActivity.userid);

                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
                break;
            default:
                setFragment(0);
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    public void setFragment(int position) {
        FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;
        switch (position) {
            case 0:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                SincronizarFragment inboxFragment = new SincronizarFragment();
                fragmentTransaction.replace(R.id.fragment, inboxFragment);
                fragmentTransaction.commit();
                break;
            case 1:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                SendsSmsFragment sendsSmsFragment = new SendsSmsFragment();
                fragmentTransaction.replace(R.id.fragment, sendsSmsFragment);
                fragmentTransaction.commit();
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }
    private void logout(String iduser){
        showProgressDialog(getString(R.string.cargando));

        String url = String
                .format(Config.url+ Config.metodo_logout+"user_id=%s",
                        iduser);
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                dismissProgressDialog();
                Toast.makeText(getApplicationContext(), getString(R.string.gracias_por_su_visita),Toast.LENGTH_LONG).show();
                finish();
            }
        };


        Response.ErrorListener errorListener = new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dismissProgressDialog();
                Toast.makeText(getApplicationContext(), getString(R.string.error_servicios),Toast.LENGTH_LONG).show();
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null && networkResponse.statusCode == HttpStatus.SC_UNAUTHORIZED) {
                    VolleyLog.e("Error: ", networkResponse.statusCode);
                }
                VolleyLog.e("Error: ", error.getMessage());

            }
        };

        JsonObjectRequestUtil jsonObjectRequest = new JsonObjectRequestUtil(Request.Method.GET, url, null, responseListener, errorListener);
        jsonObjectRequest.setShouldCache(false);
        ApplicationController.getInstance(this).addToRequestQueue(jsonObjectRequest);

    }

    /**
     * show a progress dialog with a custom message
     *
     * @param message
     */
    public void showProgressDialog(String message) {
        if (mProgressDialog != null) {
            mProgressDialog.setMessage(message);
            mProgressDialog.show();
        } else {
            Log.e("MainActivity.class", "Error al mostrar el Progress Dialog");
        }
    }

    /**
     * hide the progress dialog
     */
    public void dismissProgressDialog() {
        if (mProgressDialog != null) {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        } else {
            Log.e("MainActivity.class", "Error al mostrar el Progress Dialog");
        }
    }

}