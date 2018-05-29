package com.jakeoliger.calculator;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    String numInProgress = "";
    double currentCalc = 0.0;
    char calcMode = ' ';
    boolean isNewCalc = true;
    boolean justHitEquals = false;
    boolean justChangedMode = false;
    TextView tv;
    boolean backPressedRecently = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        tv = findViewById(R.id.calculation);

        if (savedInstanceState != null) {
            numInProgress = savedInstanceState.getString("numInProgress");
            calcMode = savedInstanceState.getChar("calcMode");
            currentCalc = savedInstanceState.getDouble("currentCalc");
            isNewCalc = savedInstanceState.getBoolean("isNewCalc");
            justHitEquals = savedInstanceState.getBoolean("justHitEquals");
            backPressedRecently = savedInstanceState.getBoolean("backPressedRecently");

            // Reset the display
            tv.setText(savedInstanceState.getString("currentCalcText"));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("numInProgress", numInProgress);
        savedInstanceState.putChar("calcMode", calcMode);
        savedInstanceState.putDouble("currentCalc", currentCalc);
        savedInstanceState.putBoolean("isNewCalc", isNewCalc);
        savedInstanceState.putBoolean("justHitEquals", justHitEquals);
        savedInstanceState.putBoolean("backPressedRecently", backPressedRecently);

        // Ensure the display remains the same
        savedInstanceState.putString("currentCalcText", tv.getText().toString());

        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Continues building a number, only allowing valid characters
     */
    public void buildNumber(View v) {
        justChangedMode = false;

        if (justHitEquals) {
            justHitEquals = false;
            clear();
        }

        String c = ((TextView) v).getText().toString();

        // Don't allow multiple decimals
        String dot = getResources().getString(R.string.dot);
        if (c.equals(dot))
            if (numInProgress.contains(dot))
                return;

        numInProgress = numInProgress.concat(c);
        tv.setText(numInProgress);
    }

    /**
     * Sets the calculation mode we are using (+, -, *, /)
     * and performs the calculation if appropriate
     */
    public void setCalcMode(View v) {
        char newMode = ((TextView) v).getText().toString().charAt(0);
        justHitEquals = false;

        if (justChangedMode) {
            calcMode = newMode;
            return;
        }

        if (isNewCalc) {
            isNewCalc = false;
            currentCalc = getCurrentNumInProgress();
        } else {
            performCalculation();
        }

        numInProgress = "";
        calcMode = newMode;
        justChangedMode = true;
    }

    /**
     * Uses the currently inputted number to update our calculation, then resets the number
     * in progress and updates the display
     */
    protected void performCalculation() {
        double currentInput = getCurrentNumInProgress();

        // Get characters from strings
        char plus = getResources().getString(R.string.plus).charAt(0);
        char minus = getResources().getString(R.string.minus).charAt(0);
        char multiply = getResources().getString(R.string.multiply).charAt(0);
        char divide = getResources().getString(R.string.plus).charAt(0);

        justChangedMode = false;

        if (calcMode == plus)
            currentCalc += currentInput;
        else if (calcMode == minus)
            currentCalc -= currentInput;
        else if (calcMode == multiply)
            currentCalc *= currentInput;
        else if (calcMode == divide)
            currentCalc /= currentInput;
        else
            return;

        numInProgress = "";
        tv.setText(String.valueOf(currentCalc));
    }

    /**
     * Performs the calculation, displays it on the screen, and gets ready for a new calculation,
     * either using the currently displayed value if a function key is pressed or starting anew
     * if a number is pressed (this is mediated by justHitEquals)
     */
    public void equals(View v) {
        if (justChangedMode)
            return;
        performCalculation();
        calcMode = ' ';
        numInProgress = String.valueOf(currentCalc);
        justHitEquals = true;
        isNewCalc = true;
    }

    /**
     * Resets the calculator as if it were being opened for the first time
     */
    public void clear(View v) { clear(); }
    public void clear() {
        currentCalc = 0.0;
        numInProgress = "";
        calcMode = ' ';
        isNewCalc = true;
        justChangedMode = false;
        tv.setText(R.string.defaultText);
    }

    /**
     * Attempts to build the number in progress into a calculable value, clearing the display
     * if somehow the number is invalid. Otherwise, it is returned as a double.
     */
    protected double getCurrentNumInProgress() {
        double num = 0.0;
        try {
            num = Double.parseDouble(numInProgress);
        } catch (NumberFormatException ex) {
            clear();
        }
        return num;
    }

    /**
     * Prevents accidental exiting of the app by a single press of the back button
     */
    public void onBackPressed() {
        if (backPressedRecently)
            super.onBackPressed();
        else {
            backPressedRecently = true;
            Toast.makeText(this, R.string.exitMsg, Toast.LENGTH_LONG).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() { backPressedRecently = false; }
            }, 10000);
        }
    }
}
