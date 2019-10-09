package com.BuG.message;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    boolean active;
    SharedPreferences sharedPreferences;
    SmsListener messReceiver;
    boolean flag = true;
    private int Read_SMS_Request = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        messReceiver = new SmsListener();
        sharedPreferences = getSharedPreferences("pref", Context.MODE_PRIVATE);
        active = sharedPreferences.getBoolean("ACTIVATE", false);
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.RECEIVE_SMS)) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.RECEIVE_SMS}, Read_SMS_Request);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.RECEIVE_SMS}, Read_SMS_Request);
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (active) {
            unregisterReceiver(messReceiver);
            startService(new Intent(getApplicationContext(), BGService.class));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (active) {
            stopService(new Intent(getApplicationContext(), BGService.class));
            messReceiver = new SmsListener();
            registerReceiver(messReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
        }

        new AlertDialog.Builder(this)
                .setTitle("Confirmation: ")
                .setMessage("Are you sure you want to get notified?")

                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (!active) {
                            sharedPreferences.edit().putBoolean("ACTIVATE", true).apply();
                            active = true;
                            messReceiver = new SmsListener();
                            flag = false;
                            registerReceiver(messReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
                            finish();
                            Toast.makeText(MainActivity.this, "Notifier Activated", Toast.LENGTH_SHORT).show();
                        } else {
                            flag = false;
                            finish();
                            Toast.makeText(MainActivity.this, "Already Activated", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (active) {
                            sharedPreferences.edit().putBoolean("ACTIVATE", false).apply();
                            active = false;
                            flag = false;
                            unregisterReceiver(messReceiver);
                            finish();
                            Toast.makeText(MainActivity.this, "Notifier Deactivated", Toast.LENGTH_SHORT).show();

                        } else {
                            finish();
                            Toast.makeText(MainActivity.this, "Already Inactive", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Read_SMS_Request) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Required", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

}