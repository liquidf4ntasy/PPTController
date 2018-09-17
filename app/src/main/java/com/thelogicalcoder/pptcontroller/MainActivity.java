package com.thelogicalcoder.pptcontroller;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    Socket socket;
    int slideCount;
    TextView status;
    int currentSlide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        status = (TextView) findViewById(R.id.status);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    socket = new Socket("192.168.1.71", 9878);
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    slideCount = Integer.parseInt(bufferedReader.readLine().trim().replace("slideCount", ""));

                    updateStatus(1);
                    currentSlide = 1;
                    for (int i = 0; i < 4; i++) {
                        System.out.println("--------");
                    }
                    System.out.println("Connection established");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            status.setVisibility(View.VISIBLE);
                            Toast.makeText(MainActivity.this, "Connection Established", Toast.LENGTH_SHORT).show();
                        }
                    });
                    for (int i = 0; i < 4; i++) {
                        System.out.println("--------");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
        findViewById(R.id.previous).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentSlide > 1) {
                    currentSlide--;
                    updateStatus(currentSlide);
                    doThis("p");
                }


            }
        });
        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentSlide < slideCount) {
                    currentSlide++;
                    updateStatus(currentSlide);
                    doThis("n");
                }

            }
        });
        findViewById(R.id.startPPT).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doThis("s");
            }
        });
        findViewById(R.id.endPPT).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateStatus(currentSlide = 1);
                doThis("e");
            }
        });
        findViewById(R.id.gotoSlide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int slide = Integer.parseInt(((EditText) findViewById(R.id.slideNumber)).getText().toString().trim());

                if (slide <= slideCount && slide >= 1) {
                    currentSlide = slide;
                    updateStatus(currentSlide);
                    doThis("number" + currentSlide);
                }
            }
        });

        //throw new RuntimeException("haahhaha");
    }

    void updateStatus(int slideNumber) {
        System.out.println(slideNumber);
        status.setText(slideNumber + "/" + slideCount);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    void doThis(final String command) {
        System.out.println("entering");
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                try {
                    OutputStream outputStream = socket.getOutputStream();
                    PrintWriter printWriter = new PrintWriter(outputStream, true);
                    // BufferedReader keyRead = new BufferedReader(new InputStreamReader(System.in));
                    String sendMessage = command;
                    //char c = 'y';
                    //while (true) {
                    //sendMessage = keyRead.readLine();
                    printWriter.println(sendMessage);
                    printWriter.flush();
                    // printWriter.close();
                    //socket.close();
                    //    System.out.println("Sent");
                    //  if (sendMessage.equalsIgnoreCase("quit")) break;
                    //}
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }
}
