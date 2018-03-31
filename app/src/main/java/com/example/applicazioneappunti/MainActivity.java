package com.example.applicazioneappunti;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final int detail_Activity_ID = 1000;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    public static ArrayList listaTitoli = new ArrayList();
    ListView listView;
    ArrayAdapter adapter;
    int posizione;

/*-------------------------------------------------------------------------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cleanTitle();

        //toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        //fine toolbar

        //menu laterale
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.apri, R.string.chiudi);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //fine menu laterale


        //lettura titoli
        boolean controllo = false;

        try {
            InputStream inputStream = this.openFileInput("titoli.txt");

            if (inputStream != null) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String receiveString = "";

                while ((receiveString = bufferedReader.readLine()) != null) {
                    listaTitoli.add(receiveString);
                }
                inputStream.close();
            }
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "File non esistente", Toast.LENGTH_SHORT).show();
            controllo = true; //passa a "creare file titoli se non esiste
        } catch (IOException e) {
            Toast.makeText(this, "Errore input file", Toast.LENGTH_SHORT).show();
        }
        //fine lettura titoli

        //crea file titoli se non esiste
        if(controllo)
        {
            try {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.openFileOutput("titoli.txt", Context.MODE_PRIVATE));
                outputStreamWriter.write("Nuova nota");
                outputStreamWriter.close();
                Toast.makeText(this, "File titoli creato", Toast.LENGTH_SHORT).show();
            }
            catch (IOException e) {
                Toast.makeText(this, "Errore scrittura su File", Toast.LENGTH_SHORT).show();
            }
            try
            {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.openFileOutput("Nuova nota.txt", Context.MODE_PRIVATE));
                outputStreamWriter.write("empty note");
                outputStreamWriter.close();
                listaTitoli.add("Nuova nota");
            }catch(IOException e)
            {
                Toast.makeText(this, "Errore scrittura nota", Toast.LENGTH_SHORT).show();
            }
        }
        //fine crea file titoli se non esiste

        //listView note
        listView = (ListView) findViewById(R.id.listView);
        adapter = new ArrayAdapter(getBaseContext(), android.R.layout.simple_list_item_1, listaTitoli);
        listView.setAdapter(adapter);
        //fine listView note

        //apertura note
        final Intent intentToNote = new Intent(this, NoteActivity.class);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intentToNote.putStringArrayListExtra("listaTitoli", listaTitoli);
                intentToNote.putExtra("Posizione", position);
                startActivity(intentToNote);
            }
        });
        //fine apertura note

        //pressione prolungata nota
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                posizione = position;
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setMessage("Vuoi cancellare la nota?").setCancelable(false)
                        .setPositiveButton("Sì", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String fileToRemove = listaTitoli.get(posizione).toString() + ".txt";
                                listaTitoli.remove(posizione);
                                try {
                                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getBaseContext().openFileOutput("titoli.txt", Context.MODE_PRIVATE));
                                    for(int i = 0; i < listaTitoli.size(); i++)
                                    {
                                        outputStreamWriter.write(listaTitoli.get(i).toString()+"\n");
                                    }
                                    outputStreamWriter.close();
                                }
                                catch (IOException e) {
                                    Toast.makeText(getBaseContext(), "Errore scrittura su File titoli", Toast.LENGTH_SHORT).show();
                                }
                                deleteFile(fileToRemove);

                                listView = null;
                                adapter = null;
                                listView = (ListView) findViewById(R.id.listView);
                                adapter = new ArrayAdapter(getBaseContext(), android.R.layout.simple_list_item_1, listaTitoli);
                                listView.setAdapter(adapter);

                                Snackbar snackbar = Snackbar.make(findViewById(R.id.listView), "Nota correttamente cancellata", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null);
                                snackbar.show();
                                posizione = 0;
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = dialog.create();
                alert.setTitle("Eliminazione nota");
                alert.show();
                return true;
            }
        });
        //fine pressione prolungata note

        //Floating action button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.Fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newNoteIntent = new Intent(getBaseContext(), EmptyNoteActivity.class);
                newNoteIntent.putStringArrayListExtra("listaTitoli", listaTitoli);
                startActivityForResult(newNoteIntent, detail_Activity_ID);
            }
        });
        //fine floating action button
    }
/*-------------------------------------------------------------------------------------------*/

    private void cleanTitle()
    {
        setTitle("");
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            Intent intent = getIntent();

            String testo = data.getStringExtra("testo");
            String titolo = data.getStringExtra("titolo");

            listaTitoli = null;
            listaTitoli = data.getStringArrayListExtra("listaTitoli");
            listView = null;
            adapter = null;
            listView = (ListView) findViewById(R.id.listView);
            adapter = new ArrayAdapter(getBaseContext(), android.R.layout.simple_list_item_1, listaTitoli);
            listView.setAdapter(adapter);

            String type = "insert";
            BackgroundWorker backgroundWorker = new BackgroundWorker(this);
            backgroundWorker.execute(type, titolo, testo);
        }
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.item_sync:
                Toast.makeText(this, "Sincronizzazione completata", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_settings) {
            Toast.makeText(this, "impostazioni", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_logout) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}