package com.example.tobias.arachnophobiavr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class StartScreen extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner spinner;
    private TextView typeDesc;
    private String ip_address;

    private EditText ipText;

    private int patient_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        ip_address = "";
        ipText = (EditText) findViewById(R.id.ip_text);
        setupTypeDesc();
        setupSpinner();
    }

    private void setupTypeDesc() {
        typeDesc = (TextView) findViewById(R.id.patientDesc);
    }

    private void setupSpinner() {
        spinner = (Spinner) findViewById(R.id.patient_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.patient_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setOnItemSelectedListener(this);
        spinner.setAdapter(adapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        patient_type = i;

        switch(i) {
            case 0: typeDesc.setText(R.string.patient_one_desc);
                    break;
            case 1: typeDesc.setText(R.string.patient_two_desc);
                    break;
            case 2: typeDesc.setText(R.string.patient_three_desc);
                    break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void startClicked(View view) {
        if(ip_address == "") {
            Toast.makeText(getApplicationContext(), "Please set a ip address first!", Toast.LENGTH_LONG).show();
        } else {

            Intent intent = new Intent(StartScreen.this, MainActivity.class);
            intent.putExtra("Type", patient_type);
            intent.putExtra("IP", ip_address);
            startActivity(intent);
        }
    }

    public void setIpClicked(View view) {
        ip_address = ipText.getText().toString();
        Toast.makeText(getApplicationContext(), "IP Address set!", Toast.LENGTH_LONG).show();

    }
}
