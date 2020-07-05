package com.ruqqus;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class errorHandlerActivity extends AppCompatActivity {
    public CharSequence error;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_handler);

        TextView errorOutput = findViewById(R.id.errorHandlerOutput);

        String[] error_data = getIntent().getStringArrayExtra("ERROR_DATA");

        assert error_data != null;
        errorOutput.append("\nError code: " + error_data[0] + " \nError description: " + error_data[1] + " \nFailingURL: " + error_data[2]);

        error = errorOutput.getText();

        final Button copyButton = findViewById(R.id.copyErrorButton);

        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("RUQQUS_ERROR", error);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "Copied to clipboard", Toast.LENGTH_LONG).show();
            }
        });
    }
}
