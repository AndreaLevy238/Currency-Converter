package com.example.andrea.currencyconverter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void convertToPounds(View view)
    {
        EditText amountEntered = (EditText) findViewById(R.id.amount);
        Double dollarAmount = Double.parseDouble(amountEntered.getText().toString());
        Log.i("Dollar Amount", amountEntered.getText().toString());
        Double poundAmount = dollarAmount * 0.75;
        String pounds = poundAmount.toString();
        Toast.makeText(getApplicationContext(), "£" + pounds, Toast.LENGTH_LONG).show();
    }
}

