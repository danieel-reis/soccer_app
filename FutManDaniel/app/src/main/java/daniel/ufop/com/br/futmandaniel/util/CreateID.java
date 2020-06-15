package daniel.ufop.com.br.futmandaniel.util;

import java.util.ArrayList;

import daniel.ufop.com.br.futmandaniel.object.Game;
import daniel.ufop.com.br.futmandaniel.object.Player;
import daniel.ufop.com.br.futmandaniel.object.PlayerXGame;

/**
 * Created by daniel on 06/07/17.
 */

public class CreateID {

    public static int getProxIDGame(ArrayList<Game> games) {
        int game = 1;
        if(games != null) {
            if(games.size() > 0) {
                game = games.get(games.size() - 1).getGame() + 1;
            }
        }
        return game;
    }

    public static int getProxIDPlayer(ArrayList<Player> players) {
        int player = 1;
        if(players != null) {
            if(players.size() > 0) {
                player = players.get(players.size() - 1).getPlayer() + 1;
            }
        }
        return player;
    }

    public static int getProxIDPlayerXGame(ArrayList<PlayerXGame> playerXGame) {
        int playerXgame = 1;
        if (playerXGame != null) {
            if (playerXGame.size() > 0) {
                playerXgame = playerXGame.get(playerXGame.size() - 1).getPlayerXgame() + 1;
            }
        }
        return playerXgame;
    }
}
