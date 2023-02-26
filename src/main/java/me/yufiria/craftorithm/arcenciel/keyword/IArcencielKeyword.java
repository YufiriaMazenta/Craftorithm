package me.yufiria.craftorithm.arcenciel.keyword;

import me.yufiria.craftorithm.arcenciel.obj.ReturnObj;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public interface IArcencielKeyword<T> {

    ReturnObj<T> exec(Player player, List<String> args);

    String getKeyword();

}
