package pers.yufiria.craftorithm.arcenciel.token;

import pers.yufiria.craftorithm.arcenciel.obj.ReturnObj;
import org.bukkit.entity.Player;

import java.util.List;

public interface IArcencielToken<T> {

    ReturnObj<T> exec(Player player, List<String> args);

    String tokenStr();

}
