package com.example.voicecalculator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private int[] numberButton = {R.id.btnempty,R.id.btn0,R.id.btn1,R.id.btn2,R.id.btn3,R.id.btn4,R.id.btn5,R.id.btn6,R.id.btn7,R.id.btn8,R.id.btn9};

    private int[] operatorButtons = {R.id.btnadd,R.id.btnsub,R.id.btnmul,R.id.btndiv,R.id.btnper,R.id.btnempty};

    private TextView txtScreen;
    private boolean lastNumeric;
    private boolean stateError;
    private boolean lastDot;
    private Button mic;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mic = findViewById(R.id.btnspeak);
        txtScreen = findViewById(R.id.textscreen);

        setNumericOnClickListener();

        setOperatorOnClickListener();
    }

    private void setOperatorOnClickListener() {
        View.OnClickListener listener = new View.OnClickListener() {
           @Override
            public void onClick(View v) {

               if(lastNumeric && !stateError){
                   Button button = (Button) v;
                   txtScreen.append(button.getText());
                   lastNumeric =false;
                   lastDot = false;
               }
            }
        };

        for (int id: operatorButtons){
            findViewById(id).setOnClickListener(listener);
        }
        //decimal point
        findViewById(R.id.btnpoint).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastNumeric && !stateError && !lastDot){
                    txtScreen.append(".");
                    lastNumeric =false;
                    lastDot = false;
                }
            }
        });

        //clear button
        findViewById(R.id.btnclean).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtScreen.setText("");
                lastNumeric =false;
                lastDot = false;
                stateError = false;
            }
        });

        //equal Button
        findViewById(R.id.btnequal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEqual();
            }
        });

        //speakbutton
        findViewById(R.id.btnspeak).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stateError){
                    txtScreen.setText("Try Again");
                    stateError = false;
                }else{
                    promptSpeechInput();
                }

                lastNumeric = true;
            }

        });
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,getString(R.string.speech_promt));

        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);

        }catch (ActivityNotFoundException a){
            Toast.makeText(getApplicationContext(),getString(R.string.speech_not_supported), Toast.LENGTH_SHORT).show();
        }

    }

    //receiving speech input

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQ_CODE_SPEECH_INPUT:
                if (resultCode == RESULT_OK && null !=data){

                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    String change = result.get(0);
                    change = change.replace("x","*");
                    change = change.replace("X","*");
                    change = change.replace("add","+");
                    change = change.replace("to","2");
                    change = change.replace("minus","-");
                    change = change.replace("times","*");
                    change = change.replace("into","*");
                    change = change.replace("in2","*");
                    change = change.replace("multiple by","*");
                    change = change.replace("divide by","/");
                    change = change.replace("divide","/");
                    change = change.replace("equal","=");
                    change = change.replace("equals","=");

                    if (change.contains("=")){
                        change = change.replace("=","");
                        txtScreen.setText(change);
                        onEqual();
                    }else{
                        txtScreen.setText(change);
                    }
                }
                break;
        }
    }

    private void onEqual() {
        if(lastNumeric && !stateError){
            String txt = txtScreen.getText().toString();

            try{
                Expression expression = null;
                try {
                    expression = new ExpressionBuilder(txt).build();
                    double result = expression.evaluate();
                    txtScreen.setText(Double.toString(result));
                }catch (Exception e){
                    txtScreen.setText("Error");
                }
            }catch (ArithmeticException ex){
                txtScreen.setText("Error");
                stateError = true;
                lastNumeric = false;
            }
        }
    }
    // Number Button ClickListener

    private void setNumericOnClickListener() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Button button =(Button) v;
                if (stateError){

                    txtScreen.setText(button.getText());
                    stateError = false;
                } else {
                    txtScreen.append(button.getText());
                }

                lastNumeric = true;
            }
        };

        for (int id: numberButton){
            findViewById(id).setOnClickListener(listener);
        }
    }

}