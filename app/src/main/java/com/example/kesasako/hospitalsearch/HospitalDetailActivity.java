package com.example.kesasako.hospitalsearch;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.kesasako.database.MySQLiteOpenHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class HospitalDetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_detail);

        TextView addressTextView = (TextView)findViewById(R.id.address);

        // Google map を表示
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

        GoogleMapOptions options = new GoogleMapOptions();
        options.mapType(GoogleMap.MAP_TYPE_NORMAL)
                .compassEnabled(false)
                .rotateGesturesEnabled(false)
                .tiltGesturesEnabled(false).liteMode(true);
        mapFragment.newInstance(options);
        GoogleMap gmap = mapFragment.getMap();

        // 電話番号をキーに詳細情報を取得
        Intent intent = getIntent();
        String tel = intent.getStringExtra("tel");

        // initialize database.
        MySQLiteOpenHelper hlpr = new MySQLiteOpenHelper(getApplicationContext());

        // 病院の詳細情報を取得
        StringBuilder sql = new StringBuilder();
        sql.append("select Name, Tel, Address, Lat, Long from HospitalMaster where Tel = '");
        sql.append(tel);
        sql.append("';");

        SQLiteDatabase db = hlpr.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery(sql.toString(), null);
            //TextViewに表示
            while (cursor.moveToNext()){
                TextView nameTextView = (TextView)findViewById(R.id.name);
                nameTextView.setText(cursor.getString(0));

                TextView telTextView = (TextView)findViewById(R.id.tel);
                telTextView.setText(cursor.getString(1) + " に電話");

                TextView hogeTextView = (TextView)findViewById(R.id.address);
                hogeTextView.setText(cursor.getString(2));

                LatLng HOSPITAL_POS = new LatLng(cursor.getDouble(3), cursor.getDouble(4));
                gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(HOSPITAL_POS, 15));

                Marker marker = gmap.addMarker(new MarkerOptions()
                        .title(cursor.getString(0))
                        .snippet(cursor.getString(2))
                        .position(HOSPITAL_POS)
                        .icon(BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_AZURE)));
            }
        } finally {
            db.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hospital_detail, menu);
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
}
