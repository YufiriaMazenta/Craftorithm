package com.github.yufiriamazenta.craftorithm.arcenciel.token;

import com.github.yufiriamazenta.craftorithm.arcenciel.obj.ReturnObj;
import org.bukkit.entity.Player;

import java.util.List;

public interface IArcencielToken<T> {

    ReturnObj<T> exec(Player player, List<String> args);

    String getTokenStr();

}
