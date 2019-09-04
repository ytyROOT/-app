package com.example.newbiboom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class newFileName extends AppCompatActivity {
         @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.newfilename);
            Button bt1=(Button)findViewById(R.id.button1);
            bt1.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    //实例化一个intent
                    Intent data=new Intent();
                    //获得EditText的内容
                    EditText et=(EditText)findViewById(R.id.edittext1);
                    String val=et.getText().toString();
                    data.putExtra("newfilename", val);
                    newFileName.this.setResult(Activity.RESULT_OK,data);
                    finish();


                }
            });
        }
}

