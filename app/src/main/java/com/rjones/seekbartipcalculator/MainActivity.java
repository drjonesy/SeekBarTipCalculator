package com.rjones.seekbartipcalculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity
        implements SeekBar.OnSeekBarChangeListener, View.OnKeyListener{

    //Decimal Format
    DecimalFormat twoDec = new DecimalFormat("#.00");

    //define variables for widgets
    private EditText billAmountEditText;
    private TextView percentTextView;
    private SeekBar percentSeekBar;
    private TextView tipTextView;
    private TextView totalTextView;

    //define SharedPreferences
    private SharedPreferences savedValues;

    //define String Values
    private String billAmountString = "";
    private float tipPercent = .15f;
    private String tipAmountString;
    float billAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_RIGHT_ICON);
        setContentView(R.layout.activity_main);
        setFeatureDrawableResource(Window.FEATURE_RIGHT_ICON, R.mipmap.ic_launcher);

        //get references to widgets
        billAmountEditText = (EditText) findViewById(R.id.billAmountEditText);
        percentTextView = (TextView) findViewById(R.id.percentTextView);
        percentSeekBar = (SeekBar) findViewById(R.id.percentSeekBar);
        tipTextView = (TextView) findViewById(R.id.tipTextView);
        totalTextView = (TextView) findViewById(R.id.totalTextView);

        //set listeners
        percentSeekBar.setOnSeekBarChangeListener(this);
        percentSeekBar.setOnKeyListener(this);

        // get SharedPreferences object
        savedValues = getSharedPreferences("SavedValues", MODE_PRIVATE);
    }
    // needs to be converted to anonymous inner class ?
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                percentTextView.setText(progress + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                calculateAndDisplay();
            }

    public void calculateAndDisplay(){
        //get bill amount -- need to check for empty value
        billAmountString = billAmountEditText.getText().toString();

        if(billAmountString.equals("")){
            billAmount = 0;
        }else{
            billAmount = Float.parseFloat(billAmountString);
        }

        //set tip amount
        int progress = percentSeekBar.getProgress();
        tipPercent = (float) progress / 100;
        float tipAmount = tipPercent * billAmount;

        tipAmountString = twoDec.format(tipAmount);
        //display the results with formatting
        tipTextView.setText(tipAmountString);
    }

            @Override
            public void onPause(){
                //save the instance variables
                Editor editor = savedValues.edit();
                editor.putString("billAmountString",billAmountString);
                editor.putFloat("tipPercent", tipPercent);
                editor.commit();

                super.onPause();
            }

            @Override
            public void onResume(){
                billAmountString = savedValues.getString("billAmountString","");
                tipPercent = savedValues.getFloat("tipPercent", 0.15f);

                billAmountEditText.setText(billAmountString);

                int progress = percentSeekBar.getProgress();
                tipPercent = (float) progress / 100;

                float tipAmount = tipPercent * billAmount;

                tipAmountString = twoDec.format(tipAmount);
                tipTextView.setText(tipAmountString);

                super.onResume();
            }

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                switch (keyCode){

                    case KeyEvent.KEYCODE_ENTER:
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                        if(v.getId() ==  R.id.percentSeekBar){
                            calculateAndDisplay();
                        }
                        break;
                }
                return false;
            }
}
