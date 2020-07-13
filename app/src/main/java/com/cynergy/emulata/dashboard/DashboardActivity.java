package com.cynergy.emulata.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.cynergy.emulata.ARModelView;
import com.cynergy.emulata.R;

public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        findViewById(R.id.solar_card).setOnClickListener(this);
        findViewById(R.id.bohr_card).setOnClickListener(this);
        findViewById(R.id.rutherford_card).setOnClickListener(this);
        findViewById(R.id.thompson_card).setOnClickListener(this);
        findViewById(R.id.plant_cell_card).setOnClickListener(this);
        findViewById(R.id.mitochondria_card).setOnClickListener(this);
        findViewById(R.id.methane_card).setOnClickListener(this);
        findViewById(R.id.ethane_card).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.solar_card:
                startModel("Solar System");
                break;
            case R.id.bohr_card:
                startModel("Bohr Model");
                break;
            case R.id.rutherford_card:
                startModel("Rutherford Model");
                break;
            case R.id.thompson_card:
                startModel("Thompson Model");
                break;
            case R.id.plant_cell_card:
                startModel("Plant Cell");
                break;
            case R.id.mitochondria_card:
                startModel("Mitochondria");
                break;
            case R.id.methane_card:
                startModel("Methane");
                break;
            case R.id.ethane_card:
                startModel("Ethane");
                break;
            default:
                break;
        }
    }

    public void startModel(String model) {
        Intent intent = new Intent(DashboardActivity.this, ARModelView.class);
        intent.putExtra("MODEL", model);
        startActivity(intent);
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
        getMenuInflater().inflate(R.menu.main2, menu);
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

        if (id == R.id.nav_manage) {
            // Handle the camera action
            // TODO: Logout User
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
