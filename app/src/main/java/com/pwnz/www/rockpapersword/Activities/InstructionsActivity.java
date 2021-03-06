package com.pwnz.www.rockpapersword.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pwnz.www.rockpapersword.R;

public class InstructionsActivity extends AppCompatActivity {

    private Button mBtnBackToMainMenu;
    private TextView mTextViewInstructions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        mTextViewInstructions = findViewById(R.id.textview_instructions);
        mTextViewInstructions.setMovementMethod(new ScrollingMovementMethod());

        mBtnBackToMainMenu = findViewById(R.id.btn_back_from_instruction);

        mBtnBackToMainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InstructionsActivity.this, MainMenuActivity.class));
                finish();
            }
        });
    }
}
