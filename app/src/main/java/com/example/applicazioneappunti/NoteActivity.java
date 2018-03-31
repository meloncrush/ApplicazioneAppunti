package com.example.applicazioneappunti;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class NoteActivity extends AppCompatActivity {

    ArrayList listaTitoli = new ArrayList();
    int posizione;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        cleanTitle();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);


        Intent intent = getIntent();
        listaTitoli = intent.getStringArrayListExtra("listaTitoli");

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            posizione = 0;
        } else {
            posizione = extras.getInt("Posizione");
        }
        EditText editTitoloNote = (EditText) findViewById(R.id.editTitoloNote);
        editTitoloNote.setText(listaTitoli.get(posizione).toString());
        EditText editTextNote = (EditText) findViewById(R.id.editTextNote);

        try {
            InputStream inputStream = this.openFileInput(listaTitoli.get(posizione).toString()+".txt");

            if ( inputStream != null ) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString+"\n");
                }

                inputStream.close();
                editTextNote.setText(stringBuilder.toString());
            }
        }
        catch (FileNotFoundException e) {
            Toast.makeText(this, "File non trovato", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Errore di input", Toast.LENGTH_SHORT).show();
        }

        editTitoloNote.setKeyListener(null);
    }

    private void cleanTitle()
    {
        setTitle("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_note, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.save:
                EditText editTitoloNote = (EditText) findViewById(R.id.editTitoloNote);
                EditText editTextNote = (EditText) findViewById(R.id.editTextNote);
                String titolo = editTitoloNote.getText().toString();
                String testo = editTextNote.getText().toString();

                try {
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.openFileOutput(titolo+".txt", this.MODE_PRIVATE));
                    outputStreamWriter.write(testo);
                    outputStreamWriter.close();
                }
                catch (IOException e) {
                    Toast.makeText(this, "Errore scrittura file", Toast.LENGTH_SHORT).show();
                }
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}