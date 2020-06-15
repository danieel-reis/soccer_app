package daniel.ufop.com.br.futmandaniel;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import daniel.ufop.com.br.futmandaniel.object.Game;

import static daniel.ufop.com.br.futmandaniel.util.Constants.LIST_DURATION_MATCH;
import static daniel.ufop.com.br.futmandaniel.util.Constants.LIST_SIZE_TEAM;

/**
 * Created by daniel on 06/07/17.
 */

public class GameEdit extends Activity implements AdapterView.OnItemSelectedListener {

    private ArrayList<Game> games = new ArrayList<Game>();
    private int position = 0;
    private EditText dateView;
    private int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent it = getIntent();
        Bundle params = it.getExtras();
        games = (ArrayList<Game>) params.get("games");
        position = (int) params.get("position");
        setContentView(R.layout.game_edit);

        initSpinners();

        //Fill fields with game's data
        Spinner spinner1 = (Spinner) findViewById(R.id.spinnerSizeTeam);
        spinner1.setSelection(games.get(position).getSize_team()-5);
        Spinner spinner2 = (Spinner) findViewById(R.id.spinnerDurationMatch);
        spinner2.setSelection(games.get(position).getDuration_match()-5);
        EditText et1 = (EditText) findViewById(R.id.editTextDescription);
        et1.setText("" + games.get(position).getDescription());;
        EditText et2 = (EditText) findViewById(R.id.editTextTime);
        et2.setText("" + games.get(position).getTime());
        EditText et3 = (EditText) findViewById(R.id.editTextLocal);
        et3.setText("" + games.get(position).getLocal());

        dateView = (EditText) findViewById(R.id.editTextDate);
        String date = games.get(position).getDate();
        if(date != null) {
            String d[] = date.split("/");
            day = Integer.parseInt(d[0]);
            month = Integer.parseInt(d[1]);
            year = Integer.parseInt(d[2]);
            showDate(year, month, day);
        }
    }

    public void initSpinners() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, LIST_SIZE_TEAM);
        Spinner spinner = (Spinner) findViewById(R.id.spinnerSizeTeam);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, LIST_DURATION_MATCH);
        Spinner spinner1 = (Spinner) findViewById(R.id.spinnerDurationMatch);
        spinner1.setAdapter(adapter1);
        spinner1.setOnItemSelectedListener(this);
    }

    public void confirm(View view) {
        Spinner spinner1 = (Spinner) findViewById(R.id.spinnerSizeTeam);
        Spinner spinner2 = (Spinner) findViewById(R.id.spinnerDurationMatch);
        TextView tv1 = (TextView) findViewById(R.id.editTextDescription);
        TextView tv2 = (TextView) findViewById(R.id.editTextDate);
        TextView tv3 = (TextView) findViewById(R.id.editTextTime);
        TextView tv4 = (TextView) findViewById(R.id.editTextLocal);

        boolean erro = false;

        int size_team = -1;
        try{
            String s = (String) spinner1.getSelectedItem();
            String s1[] = s.split(" ");
            size_team = Integer.parseInt(s1[0]);
        } catch(NumberFormatException e) {
            Toast.makeText(this, "Tamanho do time inválido!", Toast.LENGTH_SHORT).show(); erro = true;
        }

        int duration_match = -1;
        try{
            String s = (String) spinner2.getSelectedItem();
            String s1[] = s.split(" ");
            duration_match = Integer.parseInt(s1[0]);
        } catch(NumberFormatException e) {
            Toast.makeText(this, "Duração da partida inválida!", Toast.LENGTH_SHORT).show(); erro = true;
        }

        String description = tv1.getText().toString();
        if(tv1.getText().toString().equals("")) {
            Toast.makeText(this, "Descrição inválida!", Toast.LENGTH_SHORT).show(); erro = true;
        }

        String date = tv2.getText().toString();
        if(tv2.getText().toString().equals("")) {
            Toast.makeText(this, "Data inválida!", Toast.LENGTH_SHORT).show(); erro = true;
        } else {
            if(date != null) {
                try {
                    String d[] = date.split("/");
                    int day = Integer.parseInt(d[0]);
                    int month = Integer.parseInt(d[1]);
                    int year = Integer.parseInt(d[2]);

                    if(day <= 0 || day > 31 || month <= 0 || month > 12 || year < 2017) {
                        Toast.makeText(this, "Data inválida!", Toast.LENGTH_SHORT).show(); erro = true;
                    }
                } catch(Exception e) {
                    Toast.makeText(this, "Data inválida!", Toast.LENGTH_SHORT).show(); erro = true;
                }
            }
        }

        String time = tv3.getText().toString();
        if(tv3.getText().toString().equals("")) {
            Toast.makeText(this, "Hora inválida!", Toast.LENGTH_SHORT).show(); erro = true;
        }

        String local = tv4.getText().toString();
        if(tv4.getText().toString().equals("")) {
            Toast.makeText(this, "Local inválido!", Toast.LENGTH_SHORT).show(); erro = true;
        }

        if(erro == false) {
            // Refresh
            games.get(position).setSize_team(size_team);
            games.get(position).setDuration_match(duration_match);
            games.get(position).setDescription(description);
            games.get(position).setDate(date);
            games.get(position).setTime(time);
            games.get(position).setLocal(local);

            Toast.makeText(this, "Jogo " + games.get(position).getDescription() + " editado!", Toast.LENGTH_SHORT).show();
            Intent it = new Intent();
            it.putExtra("games", games);
            setResult(RESULT_OK, it);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        Intent it = new Intent();
        it.putExtra("games", games);
        setResult(RESULT_OK, it);
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    /* Date */
    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate(arg1, arg2+1, arg3);
                }
            };

    private void showDate(int year, int month, int day) {
        dateView.setText(new StringBuilder().append(day).append("/").append(month).append("/").append(year));
    }

}