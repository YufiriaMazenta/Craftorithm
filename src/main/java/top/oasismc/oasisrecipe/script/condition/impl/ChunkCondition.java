package top.oasismc.oasisrecipe.script.condition.impl;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import top.oasismc.oasisrecipe.api.script.condition.IRecipeCondition;

public enum ChunkCondition implements IRecipeCondition {

    INSTANCE;

    @Override
    public Boolean exec(String arg, Player player) {
        arg = arg.replaceAll("\\s", "");
        Chunk chunk = player.getLocation().getChunk();
        switch (arg) {
            case "slime":
                return chunk.isSlimeChunk();
            case "forceLoaded":
                return chunk.isForceLoaded();
            default:
                return false;
        }
    }

    @Override
    public String getStatement() {
        return "chunk";
    }

}
