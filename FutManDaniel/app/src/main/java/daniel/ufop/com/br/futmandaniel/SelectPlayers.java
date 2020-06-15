package daniel.ufop.com.br.futmandaniel;

/**
 * Created by daniel on 06/07/17.
 */

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import daniel.ufop.com.br.futmandaniel.object.Player;

import static daniel.ufop.com.br.futmandaniel.util.Constants.ACTIVITY_LIST_PLAYERS;
import static daniel.ufop.com.br.futmandaniel.util.Constants.ACTIVITY_LIST_PRESENCE_PLAYERS;
import static daniel.ufop.com.br.futmandaniel.util.Constants.ACTIVITY_LIST_REMOVE_PLAYER;
import static daniel.ufop.com.br.futmandaniel.util.Constants.AUSENTE;
import static daniel.ufop.com.br.futmandaniel.util.Constants.NO;
import static daniel.ufop.com.br.futmandaniel.util.Constants.PRESENTE;
import static daniel.ufop.com.br.futmandaniel.util.Constants.YES;

public class SelectPlayers extends Activity {

    MyCustomAdapter dataAdapter = null;
    ArrayList<Player> players = new ArrayList<Player>();
    ArrayList<Player> playersBackup = new ArrayList<Player>();
    private int type_activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Generate list View from ArrayList
        Intent it = getIntent();
        Bundle params = it.getExtras();
        players = (ArrayList<Player>) params.get("players");
        for (Player p: players) {
            playersBackup.add(p);
        }

        type_activity = (int) params.get("type_activity");

        setContentView(R.layout.list_players);
        TextView tv = findViewById(R.id.textViewTitleListPlayers);

        if (type_activity == ACTIVITY_LIST_PLAYERS) {
            tv.setText("Selecione os Jogadores");
        } else if (type_activity == ACTIVITY_LIST_PRESENCE_PLAYERS) {
            tv.setText("Lista de Presença");
        } else if (type_activity == ACTIVITY_LIST_REMOVE_PLAYER) {
            tv.setText("Selecione o Jogador");
        }

        //Create an ArrayAdaptar from the String Array
        dataAdapter = new MyCustomAdapter(this, R.layout.player_info, players);
        ListView listView = (ListView) findViewById(R.id.listViewPlayers);
        //Assign adapter to ListView
        listView.setAdapter(dataAdapter);

        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //When clicked, show a toast with the TextView text
                final Player player = (Player) parent.getItemAtPosition(position);
//                Toast.makeText(getApplicationContext(), player.getName(), Toast.LENGTH_LONG).show();

                AlertDialog dialog = new AlertDialog.Builder(SelectPlayers.this)
                        .setTitle("Ligação")
                        .setMessage("Deseja ligar para o " + player.getName() + "?")
                        .setPositiveButton("Ligar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                makeCall(player.getPhone());
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .create();
                dialog.show();
            }
        });
    }

    private void makeCall(String phone) {
        Uri uri = Uri.parse("tel:" + phone);
        Intent it = new Intent(Intent.ACTION_CALL, uri);

        //Check if app has permission to perform intent and starts if so
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(it);
        }
    }

    private class MyCustomAdapter extends ArrayAdapter<Player> {

        private ArrayList<Player> playerList;

        public MyCustomAdapter(Context context, int textViewResourceId, ArrayList<Player> playerList) {
            super(context, textViewResourceId, playerList);
            this.playerList = new ArrayList<Player>();
            this.playerList.addAll(playerList);
        }

        private class ViewHolder {
            TextView information;
            CheckBox checkBox;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.player_info, null);

                holder = new ViewHolder();
                holder.information = (TextView) convertView.findViewById(R.id.listViewPresenceInformation);
                holder.checkBox = (CheckBox) convertView.findViewById(R.id.listViewPresenceCheckbox);
                convertView.setTag(holder);

                holder.checkBox.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        Player player = (Player) cb.getTag();
//                        Toast.makeText(getApplicationContext(), "Clicked on Checkbox: " + cb.getText() + " is " + cb.isChecked(), Toast.LENGTH_LONG).show();
                        if (cb.isChecked()) {
                            player.setIs_checked(PRESENTE);
                        } else {
                            player.setIs_checked(AUSENTE);
                        }
                    }
                });
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Player player = playerList.get(position);
            holder.information.setText(" (" + player.getPhone() + ")");
            holder.checkBox.setText("   " + player.getName());
            if (player.getIs_checked() == PRESENTE) {
                holder.checkBox.setChecked(true);
            } else {
                holder.checkBox.setChecked(false);
            }
            holder.checkBox.setTag(player);

            return convertView;
        }
    }

    private void confirmar() {
//        StringBuffer responseText = new StringBuffer();
//        responseText.append("Jogadores selecionados...\n");

        ArrayList<Player> playersList = dataAdapter.playerList;
        int cont = 0;
        for (int i = 0; i < playersList.size(); i++) {
            if (playersList.get(i).getIs_checked() == PRESENTE) {
//                responseText.append("\n" + playersList.get(i).getName());
                cont++;
            } else {
                playersList.get(i).setIs_checked(AUSENTE);
            }
        }
//        Toast.makeText(getApplicationContext(), responseText + "\n\nTotal: " + cont, Toast.LENGTH_LONG).show();

        if (type_activity == ACTIVITY_LIST_REMOVE_PLAYER) {
            if (cont <= 1) { // One selected
                Intent it = new Intent();
                it.putExtra("players", playersList);
                it.putExtra("status", YES);
                setResult(RESULT_OK, it);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Selecione apenas um jogador!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Intent it = new Intent();
            it.putExtra("players", playersList);
            it.putExtra("status", YES);
            setResult(RESULT_OK, it);
            finish();
        }
    }

    public void confirm(View view) {
        confirmar();
    }

    @Override
    public void onBackPressed() {
        Intent it = new Intent();
        it.putExtra("players", playersBackup);
        it.putExtra("status", NO);
        setResult(RESULT_OK, it);
        finish();
    }

}

