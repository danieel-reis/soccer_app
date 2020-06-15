package daniel.ufop.com.br.futmandaniel;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import daniel.ufop.com.br.futmandaniel.object.Player;

/**
 * Created by daniel on 06/07/17.
 */

public class PlayerEdit extends Activity {

    private ArrayList<Player> players = new ArrayList<Player>();
    private int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent it = getIntent();
        Bundle params = it.getExtras();
        players = (ArrayList<Player>) params.get("players");
        position = (int) params.get("position");
        setContentView(R.layout.player_edit);

        //Fill fields with game's data
        EditText et1 = (EditText) findViewById(R.id.editTextName);
        et1.setText("" + players.get(position).getName());
        EditText et2 = (EditText) findViewById(R.id.editTextPhone);
        et2.setText("" + players.get(position).getPhone());
    }

    public void confirm(View view) {
        TextView tv1 = (TextView) findViewById(R.id.editTextName);
        String name = tv1.getText().toString();
        TextView tv2 = (TextView) findViewById(R.id.editTextPhone);
        String phone = tv2.getText().toString();

        players.get(position).setName(name);
        players.get(position).setPhone(phone);

        Toast.makeText(this, "Jogador " + name + " editado!", Toast.LENGTH_SHORT).show();

        Intent it = new Intent();
        it.putExtra("players", players);
        setResult(RESULT_OK, it);
        finish();
    }

    public void delete(View view) {
        Toast.makeText(this, "Jogador " + players.get(position).getName() + " removido!", Toast.LENGTH_SHORT).show();
        players.remove(position);

        Intent it = new Intent();
        it.putExtra("players", players);
        setResult(RESULT_OK, it);
        finish();
    }

    public void chamar(View view) {
        makeCall(players.get(position).getPhone());
    }

    private void makeCall(String phone) {
        Uri uri = Uri.parse("tel:" + phone);
        Intent it = new Intent(Intent.ACTION_CALL, uri);

        //Check if app has permission to perform intent and starts if so
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(it);
        }
        finish();
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

}