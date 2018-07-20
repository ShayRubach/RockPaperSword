package com.pwnz.www.rockpapersword.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.pwnz.www.rockpapersword.R;

public class InstructionsActivity extends AppCompatActivity {

    private TextView mTextViewInstructions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        mTextViewInstructions = findViewById(R.id.textview_instructions);
        mTextViewInstructions.setMovementMethod(new ScrollingMovementMethod());
    }
}
