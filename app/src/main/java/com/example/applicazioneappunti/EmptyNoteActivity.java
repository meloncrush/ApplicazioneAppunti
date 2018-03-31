package com.example.applicazioneappunti;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class EmptyNoteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_empty);
        cleanTitle();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.save:
                //chiudi tastiera
                View view = this.getCurrentFocus();
                if (view != null)
                {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                //fine chiudi tastiera

                EditText editNewTitle = (EditText) findViewById(R.id.editNewTitle);
                EditText editNewText = (EditText) findViewById(R.id.editNewText);
                String titolo = editNewTitle.getText().toString();
                String testo = editNewText.getText().toString();

                //controlla se titolo già presente
                Intent intent = getIntent();
                ArrayList listaTitoli = intent.getStringArrayListExtra("listaTitoli");
                boolean controllo = false;
                for(int i = 0; i < listaTitoli.size(); i++)
                {
                    if(titolo.equals(listaTitoli.get(i).toString()))
                    {
                        controllo = true;
                    }
                }
                //fine controllo se titolo già presente


                if(!controllo)
                {
                    try {
                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.openFileOutput(titolo + ".txt", this.MODE_PRIVATE));
                        outputStreamWriter.write(testo);
                        outputStreamWriter.close();
                    } catch (IOException e) {
                        Toast.makeText(this, "Errore scrittura file", Toast.LENGTH_SHORT).show();
                    }

                    try {
                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.openFileOutput("titoli.txt", Context.MODE_APPEND));
                        outputStreamWriter.append("\n" + titolo);
                        outputStreamWriter.close();
                    } catch (IOException e) {
                        Toast.makeText(this, "Errore scrittura titolo File", Toast.LENGTH_SHORT).show();
                    }



                    listaTitoli.add(titolo);
                    Intent ritorno = new Intent();
                    ritorno.putStringArrayListExtra("listaTitoli", listaTitoli);
                    ritorno.putExtra("titolo", titolo);
                    ritorno.putExtra("testo", testo);
                    setResult(RESULT_OK, ritorno);
                    finish();
                    return true;
                }
                else
                {
                    Snackbar snackbar = Snackbar.make(view, "Titolo già presente", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
        }
        return super.onOptionsItemSelected(item);
    }

}