package com.sm.arun.smartmsg;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Arun on 07-27-2015.
 */
public class ConfirmationActivity  extends Activity{
    EditText ConfirmationCode;
    Button ConfirmBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmation_layoutview);
        ConfirmationCode=(EditText)findViewById(R.id.ETxtConfirmationCode);
        ConfirmBtn=(Button)findViewById(R.id.butnConfirm);

        Intent myIntent= getIntent();
        Bundle b = myIntent.getExtras();
        if(b!=null)
        {
            String j =(String) b.get("EXTRA_MESSAGE");
            ConfirmationCode.setText(j);
        }
        ConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(ConfirmationActivity.this,UserProfileActivity.class);
                startActivity(intent);
            }
        });

     }

}
