package com.wyh.penApp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.wyh.pen.core.Pen;
import com.wyh.pen.upload.PrepareUploadListener;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private Button mButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButton = findViewById(R.id.button);


        mButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Pen.upload(new PrepareUploadListener() {
                    @Override
                    public void readyToUpload(File zipFile) {
                        Pen.d("pen", "readyToUpload: "+zipFile.getAbsolutePath());
                    }

                    @Override
                    public void failToReady() {
                        Pen.w("pen", "failToReady");
                    }
                });

                return true;
            }
        });


    }

    public void onclick(View view) {
        Pen.d("pen", "onclick: ");
    }
}
