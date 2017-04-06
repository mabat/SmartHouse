package com.example.demento.smarthouse;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;

import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private DatabaseReference ref;
    private DatabaseReference ref1;
    private TextView txtTemperature;
    private TextView txtHuminidity;
    private ImageView imgHomeDoorMain;
    private ImageView imgHomeDoor1;
    private ArrayList tempMain;
    private ArrayList tempHome;
    private String[] mainDoorValues=new String[3];
    private String[] homeDoorValues=new String[3];
    private ListView lstHomeDoor;
    private ListView lstHomeDoorMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //labele za temperaturu i vlaznost
        txtTemperature = (TextView)findViewById(R.id.textTemp);
        txtHuminidity = (TextView)findViewById(R.id.textHuminidity);

        ref = FirebaseDatabase.getInstance().getReference("HomeClimate1");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot ds) {
                for(DataSnapshot child : ds.getChildren()) {
                    if (child.getKey().equals("temperature")) {
                        txtTemperature.setText(child.getValue().toString());
                    }else if(child.getKey().equals("huminidity")){
                        txtHuminidity.setText(child.getValue().toString());
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError de) {
                // TODO Auto-generated method stub
            }
        });

        imgHomeDoorMain=(ImageView) findViewById(R.id.imgMainDoor); //pronalazimo ImageView sa designa
        ref = FirebaseDatabase.getInstance().getReference("HomeDoorMain").child("LogDoorUnlocking");
        Query recentRef = ref.limitToLast(3); //uzimamo u obzir samo zadnja tri zapisa iz baze
        recentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot ds) {
                tempMain = new ArrayList<>(); //prvo nam treba dinamicka lista u koju zapisujemo vrijednosti
                for(DataSnapshot child : ds.getChildren()) {
                    for (DataSnapshot l : child.getChildren()) {
                        if (l.getKey().equals("unlocked")) {
                            Date time=new Date((Long.parseLong(l.getValue().toString()))); //pretvorba timestampa u datum
                            DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                            String dateFormatted = formatter.format(time);
                            imgHomeDoorMain.setImageResource(R.drawable.unlock); //postavljanje ikonice za unlock
                            tempMain.add(0, "Unlocked \t"+dateFormatted+" h"); //zapis u arraylistu
                        } else if (l.getKey().equals("locked")){
                            Date time=new Date((Long.parseLong(l.getValue().toString())));
                            DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                            String dateFormatted = formatter.format(time);
                            imgHomeDoorMain.setImageResource(R.drawable.lock);
                            tempMain.add(0,"Locked \t"+dateFormatted+" h");
                        }
                    }
                }
                for (int i=0; i<3; ++i) //iz arrayliste prebacivamo vrijednosti u obicni niz
                    mainDoorValues[i]=tempMain.get(i).toString(); //preko obicnog niza radi ArrayAdapter

                lstHomeDoorMain = (ListView)findViewById(R.id.lstMainDoor); //lista sa designa
                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this,R.layout.activity_listview,
                        R.id.textView, mainDoorValues); //setiranje adaptera, kreirali smo i novi layout za potrebe prikaza
                lstHomeDoorMain.setAdapter(adapter);
            }
            @Override
            public void onCancelled(DatabaseError de) {
                // TODO Auto-generated method stub
            }
        });

        imgHomeDoor1=(ImageView) findViewById(R.id.imgHomeDoor1);
        ref1 = FirebaseDatabase.getInstance().getReference("HomeDoor1").child("LogDoorUnlocking");
        Query recentRef1 = ref1.limitToLast(3);
        recentRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot ds) {
                tempHome = new ArrayList<>();
                for(DataSnapshot child : ds.getChildren()) {
                    for (DataSnapshot l : child.getChildren()) {
                        if (l.getKey().equals("unlocked")) {
                            Date time=new Date((Long.parseLong(l.getValue().toString())));
                            DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                            String dateFormatted = formatter.format(time);
                            imgHomeDoor1.setImageResource(R.drawable.unlock);
                            tempHome.add(0,"Unlocked \t"+dateFormatted+" h");
                        } else if (l.getKey().equals("locked")){
                            Date time=new Date((Long.parseLong(l.getValue().toString())));
                            DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                            String dateFormatted = formatter.format(time);
                            imgHomeDoor1.setImageResource(R.drawable.lock);
                            tempHome.add(0, "Locked "+dateFormatted+" h");
                        }
                    }
                }
                for (int i=0; i<3; ++i)
                    homeDoorValues[i]=tempHome.get(i).toString();

                lstHomeDoor = (ListView)findViewById(R.id.lstHomeDoor);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this,R.layout.activity_listview2,
                    R.id.textView, homeDoorValues);
                lstHomeDoor.setAdapter(adapter);
            }
            @Override
            public void onCancelled(DatabaseError de) {
                // TODO Auto-generated method stub
            }
        });
    }
}
