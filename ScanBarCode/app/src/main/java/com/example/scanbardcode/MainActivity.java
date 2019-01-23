package com.example.scanbardcode;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public static TextView resultTextView;
    public static TextView authorTextView;
    public static TextView titleTextView;
    public static ImageView bookImageView;
    private Button scan_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resultTextView = (TextView) findViewById(R.id.result_text);
        authorTextView = (TextView) findViewById(R.id.author_text);
        titleTextView = (TextView) findViewById(R.id.title_text);
        bookImageView = (ImageView) findViewById(R.id.book_image);

        scan_btn = (Button) findViewById(R.id.btn_scan);
        scan_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ScannerCodeActivity.class));
            }
        });

    }
}
