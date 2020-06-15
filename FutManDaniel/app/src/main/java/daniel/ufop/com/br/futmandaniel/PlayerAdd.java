package daniel.ufop.com.br.futmandaniel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import daniel.ufop.com.br.futmandaniel.object.Player;
import daniel.ufop.com.br.futmandaniel.util.CreateID;

import static daniel.ufop.com.br.futmandaniel.util.Constants.AUSENTE;

/**
 * Created by daniel on 06/07/17.
 */

public class PlayerAdd extends Activity {

    public ArrayList<Player> players = new ArrayList<Player>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_add);

        Intent it = getIntent();
        Bundle params = it.getExtras();
        players = (ArrayList<Player>) params.get("players");
    }

    public void confirm(View view) {
        TextView tv1 = (TextView) findViewById(R.id.editTextName);
        TextView tv2 = (TextView) findViewById(R.id.editTextPhone);

        boolean erro = false;

        String name = tv1.getText().toString();
        if(tv1.getText().toString().equals("")) {
            Toast.makeText(this, "Nome inválido!", Toast.LENGTH_SHORT).show(); erro = true;
        }

        String phone = tv2.getText().toString();
        if(tv2.getText().toString().equals("")) {
            Toast.makeText(this, "Telefone inválido!", Toast.LENGTH_SHORT).show(); erro = true;
        }

        if(erro == false) {
            // Create id game
            int player = CreateID.getProxIDPlayer(players);

            // Parameters
            int qtd_V = 0;
            int qtd_D = 0;
            int qtd_E = 0;
            int size_games = 0;

            Player p = new Player(player, name, phone, qtd_V, qtd_D, qtd_E, size_games, AUSENTE);
            players.add(p);
            Toast.makeText(this, "Jogador " + name + " adicionado!", Toast.LENGTH_SHORT).show();

            //Clean fields
            tv1.setText("");
            tv2.setText("");
            tv1.requestFocus();
        }
    }

    @Override
    public void onBackPressed() {
        Intent it = new Intent();
        it.putExtra("players", players);
        setResult(RESULT_OK, it);
        finish();
    }
}
