package com.example.kesasako.hospitalsearch;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.kesasako.database.MySQLiteOpenHelper;
import com.example.kesasako.view.HospitalDetail;
import com.example.kesasako.view.HospitalsAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends ActionBarActivity implements LocationListener, OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    // location
    private static final int LOCATION_UPDATE_MIN_TIME = 0;        // 更新時間(目安)
    private static final int LOCATION_UPDATE_MIN_DISTANCE = 0;    // 更新距離(目安)
    private LocationManager mLocationManager;

    // hospital view
    private HospitalsAdapter hospitalsAdaptor;
    private ListView hospitalsView;
    private ArrayList<HospitalDetail> hospitalsList = new ArrayList<HospitalDetail>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate");
        mLocationManager = (LocationManager)this.getSystemService(Service.LOCATION_SERVICE);
        requestLocationUpdates();

        // 病院情報の表示
        showHospitals();
    }

    // 病院のリストを表示する
    private void showHospitals() {

        // Adaptorの設定
        hospitalsAdaptor = new HospitalsAdapter(getApplicationContext(), hospitalsList);

        hospitalsView = (ListView) findViewById(R.id.hospitals);
        hospitalsView.setAdapter(hospitalsAdaptor);

        // リストビューのアイテムをクリックしたときに呼出すコールバックリスナーの登録
        hospitalsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                HospitalDetail hospitalDetail = (HospitalDetail) hospitalsAdaptor.getItem(position);

                // 詳細ページを表示
                Intent intent = new Intent(getApplicationContext(), HospitalDetailActivity.class);
                intent.putExtra("tel", hospitalDetail.getTel()); // 電話番号をキーにする
                startActivity(intent);
            }
        });

        // ハードコードでごめんなさい

        // データベースの初期化
        MySQLiteOpenHelper dbHelper = new MySQLiteOpenHelper(getApplicationContext());

        // select * from HospitalMaster;
        StringBuilder sql = new StringBuilder();
        sql.append("select Name, Tel from HospitalMaster;");

        //rawQueryメソッドでデータを取得
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery(sql.toString(), null);
            //TextViewに表示
            while (cursor.moveToNext()){
                StringBuilder text = new StringBuilder();
                text.append(cursor.getString(0));
                text.append(", ");
                text.append(cursor.getString(1));
                hospitalsList.add(new HospitalDetail(cursor.getString(0), cursor.getString(1)));
            }
        } finally {
            db.close();
        }
    }

    // Called when the location has changed.
    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged.");
        showLocation(location);
    }

    // Called when the provider status changed.
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG, "onStatusChanged.");
        showProvider(provider);
        switch (status) {
            case LocationProvider.OUT_OF_SERVICE:
                // if the provider is out of service, and this is not expected to change in the near future.
                String outOfServiceMessage = provider + "が圏外になっていて取得できません。";
                showMessage(outOfServiceMessage);
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                // if the provider is temporarily unavailable but is expected to be available shortly.
                String temporarilyUnavailableMessage = "一時的に" + provider + "が利用できません。もしかしたらすぐに利用できるようになるかもです。";
                showMessage(temporarilyUnavailableMessage);
                break;
            case LocationProvider.AVAILABLE:
                // if the provider is currently available.
                if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
                    String availableMessage = provider + "が利用可能になりました。";
                    showMessage(availableMessage);
                    requestLocationUpdates();
                }
                break;
        }
    }

    // Called when the provider is enabled by the user.
    @Override
    public void onProviderEnabled(String provider) {
        Log.e(TAG, "onProviderEnabled.");
        String message = provider + "が有効になりました。";
        showMessage(message);
        showProvider(provider);
        if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
            requestLocationUpdates();
        }
    }

    // Called when the provider is disabled by the user.
    @Override
    public void onProviderDisabled(String provider) {
        Log.e(TAG, "onProviderDisabled.");
        showProvider(provider);
        if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
            String message = provider + "が無効になってしまいました。";
            showMessage(message);
        }
    }

    private void requestLocationUpdates() {
        Log.e(TAG, "requestLocationUpdates()");
        showProvider(LocationManager.NETWORK_PROVIDER);
        boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        showNetworkEnabled(isNetworkEnabled);
        if (isNetworkEnabled) {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    LOCATION_UPDATE_MIN_TIME,
                    LOCATION_UPDATE_MIN_DISTANCE,
                    this);
            Location location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                showLocation(location);
            }
        } else {
            String message = "Networkが無効になっています。";
            showMessage(message);
        }
    }

    // ローケーション情報の表示
    private void showLocation(Location location) {
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        long time = location.getTime();
        Date date = new Date(time);
        DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
        String dateFormatted = formatter.format(date);

        TextView longitudeTextView = (TextView)findViewById(R.id.longitude);
        longitudeTextView.setText("Longitude : " + String.valueOf(longitude));

        TextView latitudeTextView = (TextView)findViewById(R.id.latitude);
        latitudeTextView.setText("Latitude : " + String.valueOf(latitude));

        TextView geoTimeTextView = (TextView)findViewById(R.id.geo_time);
        geoTimeTextView.setText("取得時間 : " + dateFormatted);
    }

    private void showMessage(String message) {
        TextView textView = (TextView)findViewById(R.id.message);
        textView.setText(message);
    }

    private void showProvider(String provider) {
        TextView textView = (TextView)findViewById(R.id.provider);
        textView.setText("Provider : " + provider);
    }

    private void showNetworkEnabled(boolean isNetworkEnabled) {
        TextView textView = (TextView)findViewById(R.id.enabled);
        textView.setText("NetworkEnabled : " + String.valueOf(isNetworkEnabled));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // メニューの実装
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_search:
                openSearch();
                return true;
            case R.id.action_settings:
                openSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void openSearch() {
    }

    private void openSettings() {
    }

    @Override
    public void onClick(View v) {

        Intent intent = new Intent(
                Intent.ACTION_DIAL,
                Uri.parse("tel:09000000000"));

        startActivity(intent);
    }
}
