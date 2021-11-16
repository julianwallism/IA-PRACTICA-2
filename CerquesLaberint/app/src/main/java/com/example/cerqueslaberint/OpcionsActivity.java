package com.example.cerqueslaberint;

/**
 * Created by Ramon Mas on 10/10/21.
 * Gestiona la pantalla d'opcions per crear un nou laberint
 */

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;



public class OpcionsActivity extends GeneralActivity {
    SharedPreferences mGameSettings;
    String numeros[] = {"2","3","4","5","6","7","8","9","10","11","12","13","14",
                        "15","16","17","18","19","20","21","22","23","24","25",
                        "26","27","28","29","30"};                                   // per files i columnes
    String numeros2[] = {"0","10","20","30","40","50","60","70","80","90","100"};    // per % parets llevades
    String numeros3[] = {"1","2","3","4","5","6","7","8","9","10"};                  // per velocitat

    int files, ofiles;
    int columnes, ocolumnes;
    int parets, oparets;
    int velocitat, ovelocitat;

    // es crida quan es crea l'activitat
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opcions);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        files    = sharedPref.getInt("files", 5);     // 5x5 per defecte
        columnes = sharedPref.getInt("columnes", 5);  // 5x5 per defecte
        parets   = sharedPref.getInt("parets", 6);   // 60 %  (6x10)
        velocitat   = sharedPref.getInt("velocitat", 3);   // 0.3

        ofiles = files;
        ocolumnes = columnes;
        oparets = parets;
        initEntradaFiles();
        initEntradaColumnes();
        initEntradaParets();
        initEntradaVelocitat();
        initOK();
        initCANCEL();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initEntradaFiles()
    {
        final Spinner spinner = (Spinner) findViewById(R.id.Spinner_files);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, numeros);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spinner.setAdapter(adapter);
        spinner.setSelection(files);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View itemSelected, int selectedItemPosition,
                                       long selectedId) {
                files = selectedItemPosition;
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void initEntradaColumnes()
    {
        final Spinner spinner = (Spinner) findViewById(R.id.Spinner_columnes);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, numeros);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spinner.setAdapter(adapter);
        spinner.setSelection(columnes);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View itemSelected, int selectedItemPosition,
                                       long selectedId) {
                columnes = selectedItemPosition;
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void initEntradaParets()
    {
        // Omplir l'Spinner amb els números

        final Spinner spinner = (Spinner) findViewById(R.id.Spinner_parets);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, numeros2);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spinner.setAdapter(adapter);
        spinner.setSelection(parets);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View itemSelected, int selectedItemPosition,
                                       long selectedId) {
                parets = selectedItemPosition;
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void initEntradaVelocitat()
    {
        // Omplir l'Spinner amb els números

        final Spinner spinner = (Spinner) findViewById(R.id.Spinner_velocitat);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, numeros3);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spinner.setAdapter(adapter);
        spinner.setSelection(velocitat);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View itemSelected, int selectedItemPosition,
                                       long selectedId) {
                velocitat = selectedItemPosition;
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void guarda() // si pitjam ok, posa els nous valors
    {
        // Per guardar els valors
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Editor editor = sharedPref.edit();
        editor.putInt("files", files);
        editor.putInt("columnes", columnes);
        editor.putInt("parets", parets);
        editor.putInt("velocitat", velocitat);

        //commits your edits
        editor.commit();
        finish();
    }

    private void ignora()  // si pitjam cancel·lar, posa els valors antics
    {
        // Per guardar els valors
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Editor editor = sharedPref.edit();
        editor.putInt("files", ofiles);
        editor.putInt("columnes", ocolumnes);
        editor.putInt("parets", oparets);
        editor.putInt("velocitat", ovelocitat);

        // fa un commit dels canvi (els guarda)
        editor.commit();
        finish();
    }

    private void initOK() {
        // Boto OK
        Button okButton = (Button) findViewById(R.id.buttonOK);
        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                guarda();

            }
        });
    }

    private void initCANCEL() {
        // Boto Cancel·lar
        Button cancelButton = (Button) findViewById(R.id.buttonCANCEL);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ignora();
            }
        });
    }
}