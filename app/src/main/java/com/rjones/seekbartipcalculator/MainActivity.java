package com.rjones.seekbartipcalculator;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.Toast;
import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity
implements OnEditorActionListener, OnSeekBarChangeListener, OnKeyListener{

    //widget variables
    EditText billAmountEditText;
    SeekBar percentSeekBar;
    TextView percentTextView;
    TextView tipTextView;
    TextView totalTextView;

    //shared preferences
    private SharedPreferences savedValues;

    // instance variables
    private String billAmountString = "";
    private float tipPercent = .15f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //reference widgets
        billAmountEditText = (EditText) findViewById(R.id.billAmountEditText);
        percentSeekBar = (SeekBar) findViewById(R.id.percentSeekBar);
        percentTextView = (TextView) findViewById(R.id.percentTextView);
        tipTextView = (TextView) findViewById(R.id.tipTextView);
        totalTextView = (TextView) findViewById(R.id.totalTextView);

        //set listeners
        billAmountEditText.setOnEditorActionListener(this);
        billAmountEditText.setOnKeyListener(this);
        percentSeekBar.setOnSeekBarChangeListener(this);
        percentSeekBar.setOnKeyListener(this);

        //get SharedPrefrences
        savedValues = getSharedPreferences("SavedValues", MODE_PRIVATE);
    }

    @Override
    public void onPause(){
        Editor editor = savedValues.edit();
        editor.putString("billAmountString", billAmountString);
        editor.putFloat("tipPercent", tipPercent);
        editor.commit();
        super.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();
        //get instance variables
        billAmountString = savedValues.getString("billAmountString","");
        tipPercent = savedValues.getFloat("tipPercent", 0.15f);
        //set billamount
        billAmountEditText.setText(billAmountString);
        //set tip
        int progress = Math.round(tipPercent * 100);
        percentSeekBar.setProgress(progress);
    }

    public void calculateAndDisplay(){
        //get bill amount
        billAmountString = billAmountEditText.getText().toString();
        float billAmount;
        if(billAmountString.equals("")){
            billAmount = 0;
        }else{
            billAmount = Float.parseFloat(billAmountString);
        }
        //get tip percent
        int progress = percentSeekBar.getProgress();
        tipPercent = (float) progress / 100;
        //calculate tip
        float tipAmount = billAmount * tipPercent;
        //calculate total
        float totalAmount = billAmount + tipAmount;
        //create Number formatter
        NumberFormat currency = NumberFormat.getCurrencyInstance();
        NumberFormat percent = NumberFormat.getPercentInstance();
        //display values
        percentTextView.setText(percent.format(tipPercent));
        tipTextView.setText(currency.format(tipAmount));
        totalTextView.setText(currency.format(totalAmount));
    }

    public void calculateButton(View view){
        calculateAndDisplay();
    }

    /**
     * Event Handler: Used for confirming input into EditText widget
     * @return
     * */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
        if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED){
            calculateAndDisplay();
        }
        return false;
    }

    /**
     * Event Handler: SeekBar: Action taken when SeekBar Value Changes
     * The +"%" make sure the percent symbol doesn't disappear when the SeekBar changes
     * */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
        percentTextView.setText(progress + "%");
    }
    /**
     * Event Handler: SeekBar
     * */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar){
        // this could call a method when it moves from its original position
        // aka. START position
    }
    /**
     * Event Handler: SeekBar
     * Runs calculateAndDisplay method when the SeekBar stop in a position
     * */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar){
        calculateAndDisplay();
    }

    /**
     * Event Handler: Keyboard and D-Pad
     * This effects the soft-keyboard display
     * */
    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        switch (keyCode){
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                calculateAndDisplay();
                imm.hideSoftInputFromWindow(billAmountEditText.getWindowToken(), 0);
                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                calculateAndDisplay();
                imm.hideSoftInputFromWindow(billAmountEditText.getWindowToken(), 0);
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (view.getId() == R.id.percentSeekBar){
                    calculateAndDisplay();
                }
                break;
        }
        return false;
    }
}
