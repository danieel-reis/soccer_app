package daniel.ufop.com.br.futmandaniel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;

import daniel.ufop.com.br.futmandaniel.object.Game;
import daniel.ufop.com.br.futmandaniel.object.Player;
import daniel.ufop.com.br.futmandaniel.object.PlayerXGame;

import static daniel.ufop.com.br.futmandaniel.util.Constants.ACTIVITY_ADD_GAME;
import static daniel.ufop.com.br.futmandaniel.util.Constants.ACTIVITY_ADD_PLAYER;
import static daniel.ufop.com.br.futmandaniel.util.Constants.ACTIVITY_EDIT_PLAYER;
import static daniel.ufop.com.br.futmandaniel.util.Constants.ACTIVITY_OPTIONS_GAME;
import static daniel.ufop.com.br.futmandaniel.util.Constants.NAME_FILE_GAMES;
import static daniel.ufop.com.br.futmandaniel.util.Constants.NAME_FILE_PLAYERS;
import static daniel.ufop.com.br.futmandaniel.util.Constants.NAME_FILE_PLAYERSXGAME;

public class MainActivity extends Activity {

    ArrayList<Game> games = new ArrayList<Game>();
    ArrayList<Player> players = new ArrayList<Player>();
    ArrayList<PlayerXGame> playerXGame = new ArrayList<PlayerXGame>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        playerXGame = loadPlayersXGameFile();
        games = loadGamesFile();
        players = loadPlayersFile();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initTabHost();
        initFloatingActionButton();
        initListView1();
        initListView2();
        initListView3();
    }

    public void initTabHost() {
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        // Tab 1
        TabHost.TabSpec spec = tabHost.newTabSpec("Tab 1");
        spec.setContent(R.id.tab1);
        ImageView imageView1 = new ImageView(this);
        imageView1.setImageResource(R.drawable.ic_football_black_48dp);
        spec.setIndicator(imageView1);
        tabHost.addTab(spec);

        // Tab 2
        TabHost.TabSpec spec2 = tabHost.newTabSpec("Tab 2");
        spec2.setContent(R.id.tab2);
        ImageView imageView2 = new ImageView(this);
        imageView2.setImageResource(R.drawable.ic_group_black_48dp);
        spec2.setIndicator(imageView2);
        tabHost.addTab(spec2);

        // Tab 3
        TabHost.TabSpec spec3 = tabHost.newTabSpec("Tab 3");
        spec3.setContent(R.id.tab3);
        ImageView imageView3 = new ImageView(this);
        imageView3.setImageResource(R.drawable.ic_star_48dp);
        spec3.setIndicator(imageView3);
        tabHost.addTab(spec3);
    }

    public void initListView1() {
        ArrayAdapter<Game> adapter = new ArrayAdapter<Game>(this, android.R.layout.simple_list_item_1, games);
        ListView lv = (ListView) findViewById(R.id.listViewGames);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Game listItem = (Game) parent.getAdapter().getItem(position);
//                Toast.makeText(MainActivity.this, listItem.getDescription(), Toast.LENGTH_SHORT).show();
                optionsGame(position);
            }
        });
        lv.setAdapter(adapter);
    }

    public void initListView2() {
        ArrayAdapter<Player> adapter = new ArrayAdapter<Player>(this, android.R.layout.simple_list_item_1, players);
        ListView lv = (ListView) findViewById(R.id.listViewPlayers);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Player listItem = (Player) parent.getAdapter().getItem(position);
//                Toast.makeText(MainActivity.this, listItem.getName(), Toast.LENGTH_SHORT).show();
                editPlayer(position);
            }
        });
        lv.setAdapter(adapter);
    }

    public void initListView3() {
        ArrayList<Player> p = new ArrayList<Player>();
        for (int i = 0; i < players.size(); i++) {
            p.add(players.get(i));
        }
        Collections.sort (p, Player.ComparatorPlayerQtd);
        ArrayAdapter<Player> adapter = new ArrayAdapter<Player>(this, android.R.layout.simple_list_item_1, p);
        ListView lv = (ListView) findViewById(R.id.listViewStatistic);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Player listItem = (Player) parent.getAdapter().getItem(position);
                StringBuffer responseText = new StringBuffer();
                responseText.append("Dados do Jogador...\n");
                responseText.append("\n" + listItem.getName());
                responseText.append("\nVitórias: " + listItem.getQtd_V());
                responseText.append("\nDerrotas: " + listItem.getQtd_D());
                responseText.append("\nEmpate: " + listItem.getQtd_E());
                responseText.append("\n\nPartidas jogadas: " + listItem.getSize_games());
                Toast.makeText(getApplicationContext(), responseText, Toast.LENGTH_LONG).show();
            }
        });
        lv.setAdapter(adapter);
    }

    public void editPlayer(int position) {
        //Call PlayerEdit activity passing info about the selected item
        Intent it = new Intent(this, PlayerEdit.class);
        it.putExtra("players", players);
        it.putExtra("position", position);
        startActivityForResult(it, ACTIVITY_EDIT_PLAYER);
    }

    public void optionsGame(int position) {
        //Call GameOptions activity passing info about the selected item
        Intent it = new Intent(this, GameOptions.class);
        it.putExtra("games", games);
        it.putExtra("players", players);
        it.putExtra("playerxgame", playerXGame);
        it.putExtra("position", position);
        startActivityForResult(it, ACTIVITY_OPTIONS_GAME);
    }

    public void initFloatingActionButton() {
        FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.fabAddGame);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addGame();
            }
        });

        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fabAddPlayer);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPlayer();
            }
        });
    }

    private void addGame() {
        Intent it = new Intent(this, GameAdd.class);
        it.putExtra("games", games);
        startActivityForResult(it, ACTIVITY_ADD_GAME);
    }

    private void addPlayer() {
        Intent it = new Intent(this, PlayerAdd.class);
        it.putExtra("players", players);
        startActivityForResult(it, ACTIVITY_ADD_PLAYER);
    }

    @Override
    public void onActivityResult(int code, int result, Intent it) {
        if (code == ACTIVITY_ADD_GAME) {
            if (it != null) {
                //Receive parameter(s) from called Activity
                games = it.getParcelableArrayListExtra("games");
                saveGames(games);
                initListView1();
            }
        } else if (code == ACTIVITY_OPTIONS_GAME) {
            if (it != null) {
                //Receive parameter(s) from called Activity
                games = it.getParcelableArrayListExtra("games");
                playerXGame = it.getParcelableArrayListExtra("playerxgame");
                players = it.getParcelableArrayListExtra("players");
                saveGames(games);
                savePlayersXGame(playerXGame);
                savePlayers(players);
                initListView1();
                initListView2();
                initListView3();
            }
        } else if (code == ACTIVITY_ADD_PLAYER || code == ACTIVITY_EDIT_PLAYER) {
            if (it != null) {
                //Receive parameter(s) from called Activity
                players = it.getParcelableArrayListExtra("players");
                Collections.sort (players, Player.ComparatorPlayerNome);
                savePlayers(players);
                initListView2();
            }
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

    public ArrayList<Game> loadGamesFile() {
        ArrayList<Game> games = new ArrayList<Game>();
        FileInputStream fis;
        try {
            fis = this.openFileInput(NAME_FILE_GAMES);
            ObjectInputStream ois = new ObjectInputStream(fis);
            games = (ArrayList<Game>) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return games;
    }

    public ArrayList<Player> loadPlayersFile() {
        ArrayList<Player> players = new ArrayList<Player>();
        FileInputStream fis;
        try {
            fis = this.openFileInput(NAME_FILE_PLAYERS);
            ObjectInputStream ois = new ObjectInputStream(fis);
            players = (ArrayList<Player>) ois.readObject();
            Collections.sort (players, Player.ComparatorPlayerNome);
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return players;
    }

    public ArrayList<PlayerXGame> loadPlayersXGameFile() {
        ArrayList<PlayerXGame> playersXgame = new ArrayList<PlayerXGame>();
        FileInputStream fis;
        try {
            fis = this.openFileInput(NAME_FILE_PLAYERSXGAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            playersXgame = (ArrayList<PlayerXGame>) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return playersXgame;
    }

    public boolean saveGames(ArrayList<Game> games) {
        FileOutputStream fos;
        try {
            fos = this.openFileOutput(NAME_FILE_GAMES, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(games);
            oos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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
