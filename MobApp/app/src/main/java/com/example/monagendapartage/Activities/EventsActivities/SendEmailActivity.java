package com.example.monagendapartage.Activities.EventsActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.monagendapartage.R;


public class SendEmailActivity extends AppCompatActivity {

    private EditText eTo;
    private EditText eSubject;
    private EditText eMsg;
    private Button btn;
    private String emailUser;
    private Button btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_email);

        // emailUser = (String) getIntent().getSerializableExtra("emailUser");

        eTo = findViewById(R.id.txtTo);
        eSubject = findViewById(R.id.txtSub);
        eMsg = findViewById(R.id.txtMsg);
        btn = findViewById(R.id.btnSend);
        btnClose = findViewById(R.id.closeButton);

        // eTo.setText(emailUser);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Intent.ACTION_SEND);
                it.putExtra(Intent.EXTRA_EMAIL, new String[]{eTo.getText().toString()});
                it.putExtra(Intent.EXTRA_SUBJECT,eSubject.getText().toString());
                it.putExtra(Intent.EXTRA_TEXT,eMsg.getText());
                it.setType("message/rfc822");
                startActivity(Intent.createChooser(it,"Choisissez votre client mail"));
            }
        });
    }
}
