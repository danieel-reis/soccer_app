package daniel.ufop.com.br.futmandaniel;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import daniel.ufop.com.br.futmandaniel.object.Game;
import daniel.ufop.com.br.futmandaniel.object.Player;
import daniel.ufop.com.br.futmandaniel.object.PlayerXGame;
import daniel.ufop.com.br.futmandaniel.util.CreateID;

import static daniel.ufop.com.br.futmandaniel.util.Constants.ACTIVITY_EDIT_GAME;
import static daniel.ufop.com.br.futmandaniel.util.Constants.ACTIVITY_GAME;
import static daniel.ufop.com.br.futmandaniel.util.Constants.ACTIVITY_LIST_PLAYERS;
import static daniel.ufop.com.br.futmandaniel.util.Constants.ACTIVITY_LIST_PRESENCE_PLAYERS;
import static daniel.ufop.com.br.futmandaniel.util.Constants.AUSENTE;
import static daniel.ufop.com.br.futmandaniel.util.Constants.DELIVERED;
import static daniel.ufop.com.br.futmandaniel.util.Constants.NAME_FILE_PLAYERSXGAME;
import static daniel.ufop.com.br.futmandaniel.util.Constants.PRESENTE;
import static daniel.ufop.com.br.futmandaniel.util.Constants.SENT;
import static daniel.ufop.com.br.futmandaniel.util.Constants.YES;

/**
 * Created by daniel on 06/07/17.
 */

public class GameOptions extends Activity {

    private ArrayList<Game> games = new ArrayList<Game>();
    private ArrayList<Player> players = new ArrayList<Player>();
    private ArrayList<PlayerXGame> playerXGame = new ArrayList<PlayerXGame>();
    private int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent it = getIntent();
        Bundle params = it.getExtras();
        games = (ArrayList<Game>) params.get("games");
        players = (ArrayList<Player>) params.get("players");
        playerXGame = (ArrayList<PlayerXGame>) params.get("playerxgame");
        position = (int) params.get("position");
        setContentView(R.layout.game_options);

        TextView tv = findViewById(R.id.name);
        tv.setText(games.get(position).getDescription());
    }

    public void edit(View view) {
        //Call GameOptions activity passing info about the selected item
        Intent it = new Intent(this, GameEdit.class);
        it.putExtra("games", games);
        it.putExtra("position", position);
        startActivityForResult(it, ACTIVITY_EDIT_GAME);
    }

    public void delete(View view) {
        Toast.makeText(this, "Jogo " + games.get(position).getDescription() + " removido!", Toast.LENGTH_SHORT).show();
        games.remove(position);

        Intent it = new Intent();
        it.putExtra("games", games);
        it.putExtra("playerxgame", playerXGame);
        it.putExtra("players", players);
        setResult(RESULT_OK, it);
        finish();
    }

    private void setPlayers(int id_game) {
        for (int k = 0; k < players.size(); k++) {
            players.get(k).setIs_checked(AUSENTE);
        }

        for (int i = 0; i < playerXGame.size(); i++) {
            if (playerXGame.get(i).getGame() == id_game) {
                for (int j = 0; j < players.size(); j++) {
                    if (playerXGame.get(i).getPlayer() == players.get(j).getPlayer()) {
                        players.get(j).setIs_checked(PRESENTE);
                    }
                }
            }
        }
    }

    public void listPlayers(View view) {
        setPlayers(games.get(position).getGame());

        Intent it = new Intent(this, SelectPlayers.class);
        it.putExtra("players", players);
        it.putExtra("type_activity", ACTIVITY_LIST_PLAYERS);
        startActivityForResult(it, ACTIVITY_LIST_PLAYERS);
    }

    public void init(View view) {
        setPlayers(games.get(position).getGame());

        Intent it = new Intent(this, SelectPlayers.class);
        it.putExtra("players", players);
        it.putExtra("type_activity", ACTIVITY_LIST_PRESENCE_PLAYERS);
        startActivityForResult(it, ACTIVITY_LIST_PRESENCE_PLAYERS);
    }

    @Override
    public void onActivityResult(int code, int result, Intent it) {
        if (code == ACTIVITY_EDIT_GAME) {
            //Receive parameter(s) from called Activity
            games = it.getParcelableArrayListExtra("games");
            TextView tv = findViewById(R.id.name);
            tv.setText(games.get(position).getDescription());
        } else if (code == ACTIVITY_GAME) {
            //Receive parameter(s) from called Activity
            games = it.getParcelableArrayListExtra("games");
            playerXGame = it.getParcelableArrayListExtra("playerxgame");
            players = it.getParcelableArrayListExtra("players");
        } else if (code == ACTIVITY_LIST_PLAYERS) {
            Bundle params = it.getExtras();
            int status = (int) params.get("status");
            if (status == YES) {
                players = it.getParcelableArrayListExtra("players");
                refreshPlayersXGame(games.get(position).getGame());
            }
        } else if (code == ACTIVITY_LIST_PRESENCE_PLAYERS) {
            Bundle params = it.getExtras();
            int status = (int) params.get("status");
            if (status == YES) {
                players = it.getParcelableArrayListExtra("players");

                refreshPlayersXGame(games.get(position).getGame());

                setPlayers(games.get(position).getGame());

                int cont = 0;
                for (int i = 0; i < players.size(); i++) {
                    if (players.get(i).getIs_checked() == PRESENTE) {
                        cont++;
                    }
                }

                if (cont >= (2 * games.get(position).getSize_team())) {
                    Intent i = new Intent(this, ControlGame.class);
                    i.putExtra("players", players);
                    i.putExtra("games", games);
                    i.putExtra("playerxgame", playerXGame);
                    i.putExtra("position", position);
                    startActivityForResult(i, ACTIVITY_GAME);
                } else {
                    Toast.makeText(this, "Pra criar um jogo, é preciso ter pelo menos " +
                            (2 * games.get(position).getSize_team()) + " jogadores cadastrados!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent it = new Intent();
        it.putExtra("games", games);
        it.putExtra("playerxgame", playerXGame);
        it.putExtra("players", players);
        setResult(RESULT_OK, it);
        finish();
    }

    private void refreshPlayersXGame(int id_game) {
        ArrayList<PlayerXGame> pg = new ArrayList<PlayerXGame>();
        for (int j = 0; j < playerXGame.size(); j++) {
            if (playerXGame.get(j).getGame() != id_game) {
                pg.add(playerXGame.get(j));
            }
        }
        playerXGame = null;
        playerXGame = pg;

        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getIs_checked() == PRESENTE) {
                int id = CreateID.getProxIDPlayerXGame(playerXGame);
                playerXGame.add(new PlayerXGame(id, id_game, players.get(i).getPlayer()));
            }
        }

        for (int i = 0; i < players.size(); i++) {
            players.get(i).setIs_checked(AUSENTE);
        }
        savePlayersXGame(playerXGame);
    }

    private boolean savePlayersXGame(ArrayList<PlayerXGame> playersXgame) {
        FileOutputStream fos;
        try {
            fos = this.openFileOutput(NAME_FILE_PLAYERSXGAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(playersXgame);
            oos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public void sendMessage(View view) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Enviar SMS")
                .setMessage("Deseja enviar as mensagens aos jogadores?")
                .setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showToast("Enviando SMS... ");
                        sendSMSAllPlayersInGame(players, games.get(position).getDate(),
                                games.get(position).getTime(), games.get(position).getLocal());
                    }
                })
                .setNegativeButton("Cancelar", null)
                .create();
        dialog.show();
    }

    public void sendSMSAllPlayersInGame(ArrayList<Player> players, String data, String hora, String local) {
        for (Player p : players) {
            String message = p.getName() + ", vai rolar pelada dia " + data + " às " + hora + "(" + local + "). Bora?";
            sendSMSByAPI(p.getName(), p.getPhone(), message);
        }
    }

    private void sendSMSByAPI(final String namePlayer, final String phoneNumber, String message) {

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
//                        showToast("SMS enviado para " + namePlayer + " em " + phoneNumber);
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        showToast("Falha genérica");
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        showToast("Nenhum serviço");
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        showToast("Null");
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        showToast("Radio desligado");
                        break;
                }
            }
        }, new IntentFilter(SENT));

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
//                        showToast(namePlayer + " em " + phoneNumber + " recebeu o SMS!");
                        break;
                    case Activity.RESULT_CANCELED:
                        showToast(namePlayer + " em " + phoneNumber + " não recebeu o SMS!");
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));
    }

    private void showToast(String message) {
        Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
    }
}