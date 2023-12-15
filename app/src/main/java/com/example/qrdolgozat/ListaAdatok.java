package com.example.qrdolgozat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.PixelCopy;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListaAdatok extends AppCompatActivity {

    private EditText id;
    private EditText nev;
    private EditText jegy;
    private Button modosit;
    private Button megse;
    private ListView listView;
    private SharedPreferences sharedPreferences;
    private List<Person> people = new ArrayList<>();
    private String seged;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_adatok);
        init();
        RequestTask task = new RequestTask(seged,"GET");
        task.execute();
        modosit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                embermodositas();
            }
        });
        megse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                urlapAlaphelyzetbe();
            }
        });
    }
    private void urlapAlaphelyzetbe(){
        id.setText("");
        nev.setText("");
        jegy.setText("");
        RequestTask task = new RequestTask(seged,"GET");
        task.execute();
    }
    public void init(){
        listView = findViewById(R.id.listview_main);
        sharedPreferences = getSharedPreferences("Data", Context.MODE_PRIVATE);
        modosit = findViewById(R.id.modosit);
        megse = findViewById(R.id.megse);
        id = findViewById(R.id.id);
        nev = findViewById(R.id.enev);
        jegy = findViewById(R.id.ejegy);
        listView.setAdapter(new PersonAdapter());
        editor = sharedPreferences.edit();
        seged = sharedPreferences.getString("restapi","Nincs ilyen adat");
    }
    private class PersonAdapter extends ArrayAdapter<Person> {
        public PersonAdapter(){
            super(ListaAdatok.this,R.layout.person_list_adapter,people);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.person_list_adapter,null);
            Person actualPerson = people.get(position);
            TextView tnev = view.findViewById(R.id.Nev);
            TextView tjegy = view.findViewById(R.id.Jegy);
            TextView tszerkeszt = view.findViewById(R.id.szerkeszt);

            tnev.setText(actualPerson.getName());
            tjegy.setText(actualPerson.getGrade());
            tszerkeszt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    id.setText(String.valueOf(actualPerson.getId()));
                    nev.setText(actualPerson.getName());
                    jegy.setText(actualPerson.getGrade());
                }
            });
            return view;
        }
    }
    private boolean validacio(){
        if(id.getText().toString().isEmpty() || nev.getText().toString().isEmpty() || jegy.getText().toString().isEmpty()){
            return true;
        }
        return false;
    }
    private void embermodositas(){
        String name = nev.getText().toString();
        String grade = jegy.getText().toString();
        String idtext = id.getText().toString();
        boolean valid = validacio();
        if(valid){
            Toast.makeText(this, "Minden mezőt ki kell tölteni", Toast.LENGTH_SHORT).show();
        }else{
            int id = Integer.parseInt(idtext);
            Person person = new Person(id,name,grade);
            Gson jsonConverter = new Gson();
            RequestTask task = new RequestTask(seged+"/"+id,"PUT", jsonConverter.toJson(person));
            task.execute();
        }
    }
    private class RequestTask extends AsyncTask<Void,Void,Response> {
        public RequestTask(String requesturl, String requestType, String requestParans) {
            Requesturl = requesturl;
            RequestType = requestType;
            this.requestParans = requestParans;
        }

        public RequestTask(String requesturl, String requestType) {
            Requesturl = requesturl;
            RequestType = requestType;
        }

        String Requesturl;
        String RequestType;
        String requestParans;

        @Override
        protected Response doInBackground(Void... voids) {
            Response response = null;
            try {
                switch (RequestType) {
                    case "GET":
                        response = RequestHandler.get(Requesturl);
                        break;
                    case "PUT":
                        response = RequestHandler.put(Requesturl, requestParans);
                        break;
                }
            } catch (IOException e) {
                Toast.makeText(ListaAdatok.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
            return response;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(Response response) {
            Gson converter = new Gson();
            if (response.getResponsecode()>=400){
                Toast.makeText(ListaAdatok.this, "Hiba történt a kérés feldolgozása közben", Toast.LENGTH_SHORT).show();
                Log.d("onPostExecuteError: ",response.getResponsemessage());
            }
            switch (RequestType){
                case "GET":
                    Person[] peopleArray = converter.fromJson(response.getResponsemessage(),Person[].class);
                    people.clear();
                    people.addAll(Arrays.asList(peopleArray));
                    Toast.makeText(ListaAdatok.this, response.getResponsemessage(), Toast.LENGTH_SHORT).show();
                    break;
                case "PUT":
                    Person updateperson = converter.fromJson(response.getResponsemessage(), Person.class);
                    people.replaceAll(person1 -> person1.getId() == updateperson.getId() ? updateperson: person1);
                    urlapAlaphelyzetbe();
                    break;
            }
        }
    }

}