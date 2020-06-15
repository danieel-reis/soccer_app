package daniel.ufop.com.br.futmandaniel;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.SystemClock;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;

import daniel.ufop.com.br.futmandaniel.object.Game;
import daniel.ufop.com.br.futmandaniel.object.Player;
import daniel.ufop.com.br.futmandaniel.object.PlayerXGame;
import daniel.ufop.com.br.futmandaniel.util.CreateID;

import static daniel.ufop.com.br.futmandaniel.util.Constants.ACTIVITY_LIST_PLAYERS;
import static daniel.ufop.com.br.futmandaniel.util.Constants.ACTIVITY_LIST_REMOVE_PLAYER;
import static daniel.ufop.com.br.futmandaniel.util.Constants.AUSENTE;
import static daniel.ufop.com.br.futmandaniel.util.Constants.NAME_FILE_PLAYERS;
import static daniel.ufop.com.br.futmandaniel.util.Constants.NAME_FILE_PLAYERSXGAME;
import static daniel.ufop.com.br.futmandaniel.util.Constants.PRESENTE;
import static daniel.ufop.com.br.futmandaniel.util.Constants.YES;

/**
 * Created by daniel on 06/07/17.
 */

public class ControlGame extends Activity {

    private boolean isStopped = true;
    private long millisElapsed = 0;
    private int positionGame;
    private int player_remove;
    private ArrayList<Player> players = new ArrayList<Player>();
    private ArrayList<Player> playersInGame = new ArrayList<Player>();
    private ArrayList<Player> waiting = new ArrayList<Player>();
    private ArrayList<Player> not = new ArrayList<Player>();
    private ArrayList<Player> time1 = new ArrayList<Player>();
    private ArrayList<Player> time2 = new ArrayList<Player>();
    public ArrayList<Game> games = new ArrayList<Game>();
    public ArrayList<PlayerXGame> playerXGame = new ArrayList<PlayerXGame>();
    private int minHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int min = Math.min(dm.widthPixels, dm.heightPixels);
        minHeight = min;

        initTabHost();

        reset();

        Intent it = getIntent();
        Bundle params = it.getExtras();
        players = (ArrayList<Player>) params.get("players");
        games = (ArrayList<Game>) params.get("games");
        playerXGame = (ArrayList<PlayerXGame>) params.get("playerxgame");
        positionGame = (int) params.get("position");

        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getIs_checked() == PRESENTE) {
                waiting.add(players.get(i));
            }
        }

        embaralha();

        initChronometer();
    }

    public void initTabHost() {
        TabHost tabHost = (TabHost) findViewById(R.id.tabHostGame);
        tabHost.setup();

        // Tab 1
        TabHost.TabSpec spec1 = tabHost.newTabSpec("Tab 1");
        spec1.setContent(R.id.tab1);
        spec1.setIndicator("Time 1");
        tabHost.addTab(spec1);

        // Tab 2
        TabHost.TabSpec spec2 = tabHost.newTabSpec("Tab 2");
        spec2.setContent(R.id.tab2);
        spec2.setIndicator("Time 2");
        tabHost.addTab(spec2);

        // Tab 3
        TabHost.TabSpec spec3 = tabHost.newTabSpec("Tab 3");
        spec3.setContent(R.id.tab3);
        spec3.setIndicator("Espera");
        tabHost.addTab(spec3);
    }

    private void setPlayers(int id_game) {
        playersInGame = null;
        playersInGame = new ArrayList<Player>();

        for (int j = 0; j < players.size(); j++) {
            playersInGame.add(players.get(j));
        }
        for (int k = 0; k < playersInGame.size(); k++) {
            playersInGame.get(k).setIs_checked(AUSENTE);
        }

        for (int i = 0; i < playerXGame.size(); i++) {
            if (playerXGame.get(i).getGame() == id_game) {
                for (int j = 0; j < playersInGame.size(); j++) {
                    if (playerXGame.get(i).getPlayer() == playersInGame.get(j).getPlayer()) {
                        playersInGame.get(j).setIs_checked(PRESENTE);
                    }
                }
            }
        }
    }

    private void alertSubstituicao(String name, final int player) {
        player_remove = player;
        setPlayers(games.get(positionGame).getGame());

        AlertDialog dialog = new AlertDialog.Builder(ControlGame.this)
                .setTitle("Substituição")
                .setMessage("Deseja substituir " + name + " da partida?")
                .setPositiveButton("Substituir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        not = null;
                        not = new ArrayList<Player>();
                        Boolean encontrado1, encontrado2;
                        int cont = 0;
                        for (Player p : playersInGame) {
                            if (p.getIs_checked() == PRESENTE && p.getPlayer() != player_remove) {
                                encontrado1 = false;
                                encontrado2 = false;

                                for (Player t : time1) {
                                    if (t.getPlayer() == p.getPlayer()) {
                                        encontrado1 = true;
                                        break;
                                    }
                                }
                                for (Player t : time2) {
                                    if (t.getPlayer() == p.getPlayer()) {
                                        encontrado2 = true;
                                        break;
                                    }
                                }

                                if (encontrado1 == false && encontrado2 == false) {
                                    not.add(p);
                                    cont++;
                                }
                            }
                        }

                        for (int k = 0; k < not.size(); k++) {
                            not.get(k).setIs_checked(AUSENTE);
                        }

                        if (cont > 0) {
                            Intent it = new Intent(getBaseContext(), SelectPlayers.class);
                            it.putExtra("players", not);
                            it.putExtra("type_activity", ACTIVITY_LIST_REMOVE_PLAYER);
                            startActivityForResult(it, ACTIVITY_LIST_REMOVE_PLAYER);
                        } else {
                            Toast.makeText(getApplicationContext(), "Não há jogadores para substituir.\n" +
                                    "Adicione mais jogadores a partida!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancelar", null)
                .create();
        dialog.show();
    }

    private void alertSubstituicaoEspera(String name, final int player) {
        AlertDialog dialog = new AlertDialog.Builder(ControlGame.this)
                .setTitle("Substituição")
                .setMessage("Deseja remover " + name + " da espera?")
                .setPositiveButton("Remover", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < waiting.size(); i++) {
                            if (waiting.get(i).getPlayer() == player) {
                                Toast.makeText(ControlGame.this, waiting.get(i).getName() + " está sendo removido...", Toast.LENGTH_SHORT).show();

                                for (int j = 0; j < players.size(); j++) {
                                    if (players.get(j).getPlayer() == waiting.get(i).getPlayer()) {
                                        players.get(j).setIs_checked(AUSENTE);
                                    }
                                }

                                for (int j = 0; j < playerXGame.size(); j++) {
                                    if (playerXGame.get(j).getGame() == games.get(positionGame).getGame()) {
                                        if (playerXGame.get(j).getPlayer() == waiting.get(i).getPlayer()) {
                                            playerXGame.remove(playerXGame.get(j));
                                        }
                                    }
                                }

                                waiting.remove(waiting.get(i));
                                initListView3();

                                savePlayersXGame(playerXGame);
                            }
                        }
                    }
                })
                .setNegativeButton("Cancelar", null)
                .create();
        dialog.show();
    }

    public void initListView1() {
        ArrayAdapter<Player> adapter = new ArrayAdapter<Player>(this, android.R.layout.simple_list_item_1, time1);
        ListView lv = (ListView) findViewById(R.id.listView1);
        LayoutParams list = (LayoutParams) lv.getLayoutParams();
        list.height = minHeight;
        lv.setLayoutParams(list);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Player listItem = (Player) parent.getAdapter().getItem(position);
//                Toast.makeText(ControlGame.this, listItem.getName(), Toast.LENGTH_SHORT).show();
                alertSubstituicao(listItem.getName(), listItem.getPlayer());
            }
        });
        lv.setAdapter(adapter);
    }

    public void initListView2() {
        ArrayAdapter<Player> adapter = new ArrayAdapter<Player>(this, android.R.layout.simple_list_item_1, time2);
        ListView lv = (ListView) findViewById(R.id.listView2);
        LayoutParams list = (LayoutParams) lv.getLayoutParams();
        list.height = minHeight;
        lv.setLayoutParams(list);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Player listItem = (Player) parent.getAdapter().getItem(position);
//                Toast.makeText(ControlGame.this, listItem.getName(), Toast.LENGTH_SHORT).show();
                alertSubstituicao(listItem.getName(), listItem.getPlayer());
            }
        });
        lv.setAdapter(adapter);
    }

    public void initListView3() {
        ArrayAdapter<Player> adapter = new ArrayAdapter<Player>(this, android.R.layout.simple_list_item_1, waiting);
        ListView lv = (ListView) findViewById(R.id.listView3);
        LayoutParams list = (LayoutParams) lv.getLayoutParams();
        list.height = minHeight;
        lv.setLayoutParams(list);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Player listItem = (Player) parent.getAdapter().getItem(position);
//                Toast.makeText(ControlGame.this, listItem.getName(), Toast.LENGTH_SHORT).show();
                alertSubstituicaoEspera(listItem.getName(), listItem.getPlayer());
            }
        });
        lv.setAdapter(adapter);
    }

    public void initChronometer() {
        Chronometer chronometer = (Chronometer) findViewById(R.id.chronometer2);
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long elapsedTime = SystemClock.elapsedRealtime() - chronometer.getBase();
                if (elapsedTime > (games.get(positionGame).getDuration_match() * 60 * 1000)) {
                    Toast.makeText(ControlGame.this, games.get(positionGame).getDuration_match() + " minutos!", Toast.LENGTH_SHORT).show();
                    resetChronometer(findViewById(R.id.buttonReset));
                    showDialog();
                }
            }
        });
    }

    public void playStopChronometer(View view) {
        Chronometer chronometer = (Chronometer) findViewById(R.id.chronometer2);
        if (isStopped) {
            chronometer.start();
            chronometer.setBase(SystemClock.elapsedRealtime() + millisElapsed);
            ImageButton btn = (ImageButton) findViewById(R.id.buttonPlayStop);
            btn.setImageResource(R.drawable.ic_pause_circle_outline_black_48dp);
            isStopped = false;
        } else {
            chronometer.stop();
            millisElapsed = chronometer.getBase() - SystemClock.elapsedRealtime();
            ImageButton btn = (ImageButton) findViewById(R.id.buttonPlayStop);
            btn.setImageResource(R.drawable.ic_play_circle_outline_black_48dp);
            isStopped = true;
        }
    }

    public void resetChronometer(View view) {
        reset();
    }

    private void reset() {
        Chronometer chronometer = (Chronometer) findViewById(R.id.chronometer2);
        chronometer.stop();
        chronometer.setBase(SystemClock.elapsedRealtime());
        ImageButton btn = (ImageButton) findViewById(R.id.buttonPlayStop);
        btn.setImageResource(R.drawable.ic_play_circle_outline_black_48dp);
        millisElapsed = 0;
        isStopped = true;
    }

    public void alterList(View view) {
        not = null;
        not = new ArrayList<Player>();
        for (int k = 0; k < players.size(); k++) {
            if (players.get(k).getIs_checked() == AUSENTE) {
                not.add(players.get(k));
            }
        }

        Intent it = new Intent(this, SelectPlayers.class);
        it.putExtra("players", not);
        it.putExtra("type_activity", ACTIVITY_LIST_PLAYERS);
        startActivityForResult(it, ACTIVITY_LIST_PLAYERS);
    }

    @Override
    public void onActivityResult(int code, int result, Intent it) {
        if (code == ACTIVITY_LIST_PLAYERS) {
            not = null;
            not = new ArrayList<Player>();
            not = it.getParcelableArrayListExtra("players");
            refreshPG(games.get(positionGame).getGame());
        } else if (code == ACTIVITY_LIST_REMOVE_PLAYER) {
            Bundle params = it.getExtras();
            int status = (int) params.get("status");
            if (status == YES) {
                not = null;
                not = new ArrayList<Player>();
                not = it.getParcelableArrayListExtra("players");

                String name = "";
                for (Player p : players) {
                    if (p.getPlayer() == player_remove) {
                        name = p.getName();
                        break;
                    }
                }

                int id_player = -1;
                for (Player p : not) {
                    if (p.getIs_checked() == PRESENTE) {
                        Toast.makeText(this, "Trocando " + name + " por " + p.getName() + "...", Toast.LENGTH_SHORT).show();

                        for (int i = 0; i < players.size(); i++) {
                            if (players.get(i).getPlayer() == player_remove) {
                                players.get(i).setIs_checked(AUSENTE);
                            }
                            if (players.get(i).getPlayer() == p.getPlayer()) {
                                players.get(i).setIs_checked(PRESENTE);
                                id_player = p.getPlayer();
                            }
                        }
                        break;
                    }
                }

                if (id_player > 0) {
                    for (int i = 0; i < time1.size(); i++) {
                        if (time1.get(i).getPlayer() == player_remove) {
                            for (int k = 0; k < playerXGame.size(); k++) {
                                if (playerXGame.get(k).getGame() == games.get(positionGame).getGame() && playerXGame.get(k).getPlayer() == player_remove) {
                                    playerXGame.remove(playerXGame.get(k));
                                }
                            }
                            time1.remove(time1.get(i));

                            int id = CreateID.getProxIDPlayerXGame(playerXGame);
                            playerXGame.add(new PlayerXGame(id, games.get(positionGame).getGame(), players.get(i).getPlayer()));

                            for (int j = 0; j < players.size(); j++) {
                                if (players.get(j).getPlayer() == id_player) {
                                    time1.add(players.get(j));
                                    break;
                                }
                            }

                            for (int k = 0; k < waiting.size(); k++) {
                                if (waiting.get(k).getPlayer() == id_player) {
                                    waiting.remove(waiting.get(k));
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    for (int i = 0; i < time2.size(); i++) {
                        if (time2.get(i).getPlayer() == player_remove) {
                            for (int k = 0; k < playerXGame.size(); k++) {
                                if (playerXGame.get(k).getGame() == games.get(positionGame).getGame() && playerXGame.get(k).getPlayer() == player_remove) {
                                    playerXGame.remove(playerXGame.get(k));
                                }
                            }
                            time2.remove(time2.get(i));

                            int id = CreateID.getProxIDPlayerXGame(playerXGame);
                            playerXGame.add(new PlayerXGame(id, games.get(positionGame).getGame(), players.get(i).getPlayer()));

                            for (int j = 0; j < players.size(); j++) {
                                if (players.get(j).getPlayer() == id_player) {
                                    time2.add(players.get(j));
                                    break;
                                }
                            }

                            for (int k = 0; k < waiting.size(); k++) {
                                if (waiting.get(k).getPlayer() == id_player) {
                                    waiting.remove(waiting.get(k));
                                    break;
                                }
                            }
                            break;
                        }
                    }

                    savePlayersXGame(playerXGame);
                    initListView1();
                    initListView2();
                    initListView3();
                }
            }
        }
    }

    private void refreshPG(int id_game) {
        for (int i = 0; i < not.size(); i++) {
            if (not.get(i).getIs_checked() == PRESENTE) {
                int id = CreateID.getProxIDPlayerXGame(playerXGame);
                playerXGame.add(new PlayerXGame(id, id_game, players.get(i).getPlayer()));

                for (int j = 0; j < players.size(); j++) {
                    if (players.get(j).getPlayer() == not.get(i).getPlayer()) {
                        players.get(j).setIs_checked(PRESENTE);
                        waiting.add(players.get(j));
                    }
                }
            }
        }
        savePlayersXGame(playerXGame);
        initListView1();
        initListView2();
        initListView3();
    }

    @Override
    public void onBackPressed() {
        if (isStopped == true) {
            removeTime1();
            removeTime2();

            Intent it = new Intent();
            it.putExtra("players", players);
            it.putExtra("games", games);
            it.putExtra("playerxgame", playerXGame);
            setResult(RESULT_OK, it);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < grantResults.length; ++i) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                //Permission has been denied
                Toast.makeText(this, "Permissão negada!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }


    private void showDialog() {
        final Dialog dialog = new Dialog(this);

        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_layout);

        final Button time1 = (Button) dialog.findViewById(R.id.buttonTime1);
        final Button time2 = (Button) dialog.findViewById(R.id.buttonTime2);
        final Button empate = (Button) dialog.findViewById(R.id.buttonEmpate);

        time1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
                Toast.makeText(getBaseContext(), "Time 1 venceu!", Toast.LENGTH_SHORT).show();
                refresh(1);
                reset();
            }
        });

        time2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
                Toast.makeText(getBaseContext(), "Time 2 venceu!", Toast.LENGTH_SHORT).show();
                refresh(2);
                reset();
            }
        });

        empate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
                Toast.makeText(getBaseContext(), "Empate!", Toast.LENGTH_SHORT).show();
                refresh(0);
                reset();
            }
        });

        dialog.show();

        Vibrator vv = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long milisec = 1000;
        vv.vibrate(milisec);

        MediaPlayer somPickObjeto = MediaPlayer.create(this, R.raw.apitodefutebol);
        somPickObjeto.start();
    }

    private void refresh(int timeWinner) {
        if (timeWinner == 0) { // Empate
            for (Player p : time1) {
                for (int i = 0; i < players.size(); i++) {
                    if (players.get(i).getPlayer() == p.getPlayer()) {
                        players.get(i).setQtd_E(players.get(i).getQtd_E() + 1);
                        players.get(i).setSize_games(players.get(i).getSize_games() + 1);
                    }
                }
            }

            for (Player p : time2) {
                for (int i = 0; i < players.size(); i++) {
                    if (players.get(i).getPlayer() == p.getPlayer()) {
                        players.get(i).setQtd_E(players.get(i).getQtd_E() + 1);
                        players.get(i).setSize_games(players.get(i).getSize_games() + 1);
                    }
                }
            }
            savePlayers(players);

            removeTime1();
            removeTime2();
            insereTime1();
            insereTime2();
            initListView1();
            initListView2();
            initListView3();

        } else if (timeWinner == 1) { // Time 1 ganhou
            for (Player p : time1) {
                for (int i = 0; i < players.size(); i++) {
                    if (players.get(i).getPlayer() == p.getPlayer()) {
                        players.get(i).setQtd_V(players.get(i).getQtd_V() + 1);
                        players.get(i).setSize_games(players.get(i).getSize_games() + 1);
                    }
                }
            }

            for (Player p : time2) {
                for (int i = 0; i < players.size(); i++) {
                    if (players.get(i).getPlayer() == p.getPlayer()) {
                        players.get(i).setQtd_D(players.get(i).getQtd_D() + 1);
                        players.get(i).setSize_games(players.get(i).getSize_games() + 1);
                    }
                }
            }
            savePlayers(players);

            removeTime2();
            insereTime2();
            initListView2();
            initListView3();

        } else if (timeWinner == 2) { // Time 2 ganhou
            for (Player p : time2) {
                for (int i = 0; i < players.size(); i++) {
                    if (players.get(i).getPlayer() == p.getPlayer()) {
                        players.get(i).setQtd_V(players.get(i).getQtd_V() + 1);
                        players.get(i).setSize_games(players.get(i).getSize_games() + 1);
                    }
                }
            }

            for (Player p : time1) {
                for (int i = 0; i < players.size(); i++) {
                    if (players.get(i).getPlayer() == p.getPlayer()) {
                        players.get(i).setQtd_D(players.get(i).getQtd_D() + 1);
                        players.get(i).setSize_games(players.get(i).getSize_games() + 1);
                    }
                }
            }
            savePlayers(players);

            removeTime1();
            insereTime1();
            initListView1();
            initListView3();
        }
    }

    private void embaralha() {
        Collections.shuffle(waiting);
        insereTime1();
        insereTime2();
        initListView1();
        initListView2();
        initListView3();
    }

    private void insereTime1() {
        time1 = null;
        time1 = new ArrayList<Player>();

        for (int i = 0; i < games.get(positionGame).getSize_team(); i++) {
            time1.add(waiting.get(i));
        }
        for (int i = 0; i < games.get(positionGame).getSize_team(); i++) {
            waiting.remove(waiting.get(0));
        }
    }

    private void insereTime2() {
        time2 = null;
        time2 = new ArrayList<Player>();

        for (int i = 0; i < games.get(positionGame).getSize_team(); i++) {
            time2.add(waiting.get(i));
        }
        for (int i = 0; i < games.get(positionGame).getSize_team(); i++) {
            waiting.remove(waiting.get(0));
        }
    }

    private void removeTime1() {
        for (Player p : time1) {
            waiting.add(p);
        }
        time1 = null;
        time1 = new ArrayList<Player>();
    }

    private void removeTime2() {
        for (Player p : time2) {
            waiting.add(p);
        }
        time2 = null;
        time2 = new ArrayList<Player>();
    }

    public boolean savePlayers(ArrayList<Player> players) {
        FileOutputStream fos;
        try {
            fos = this.openFileOutput(NAME_FILE_PLAYERS, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(players);
            oos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean savePlayersXGame(ArrayList<PlayerXGame> playersXgame) {
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
}