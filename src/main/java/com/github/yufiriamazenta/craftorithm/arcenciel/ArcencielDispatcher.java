package com.github.yufiriamazenta.craftorithm.arcenciel;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.arcenciel.block.ListArcencielBlock;
import com.github.yufiriamazenta.craftorithm.arcenciel.block.StringArcencielBlock;
import com.github.yufiriamazenta.craftorithm.arcenciel.obj.ArcencielSignal;
import com.github.yufiriamazenta.craftorithm.arcenciel.obj.ReturnObj;
import com.github.yufiriamazenta.craftorithm.arcenciel.token.*;
import com.github.yufiriamazenta.craftorithm.hook.impl.PlayerPointsHooker;
import com.github.yufiriamazenta.craftorithm.hook.impl.VaultHooker;
import crypticlib.config.BukkitConfigWrapper;
import crypticlib.lifecycle.AutoTask;
import crypticlib.lifecycle.BukkitLifeCycleTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.TaskRule;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;

@AutoTask(
    rules = {
        @TaskRule(lifeCycle = LifeCycle.ENABLE, priority = 1)
    }
)
public enum ArcencielDispatcher implements IArcencielDispatcher, BukkitLifeCycleTask {

    INSTANCE;
    private BukkitConfigWrapper functionFile;

    ArcencielDispatcher() {
    }

    @Override
    public ReturnObj<Object> dispatchArcencielBlock(Player player, String arcencielBlockBody) {
        if (arcencielBlockBody.contains("\n"))
            return new ListArcencielBlock(arcencielBlockBody).exec(player);
        else
            return new StringArcencielBlock(arcencielBlockBody).exec(player);
    }

    @Override
    public ReturnObj<Object> dispatchArcencielFunc(Player player, List<String> arcencielFuncBody) {
        ReturnObj<Object> returnObj = new ReturnObj<>();
        for (int i = 0; i < arcencielFuncBody.size(); i++) {
            returnObj = dispatchArcencielBlock(player, arcencielFuncBody.get(i));
            if (returnObj.obj() instanceof Boolean && returnObj.signal().equals(ArcencielSignal.IF)) {
                if (returnObj.obj().equals(false) && i + 1 < arcencielFuncBody.size())
                    i ++;
            }
            if (returnObj.signal().equals(ArcencielSignal.END))
                break;
        }
        return returnObj;
    }

    private void regDefScriptKeyword() {
        StringArcencielBlock.regScriptKeyword(TokenIf.INSTANCE);
        StringArcencielBlock.regScriptKeyword(TokenHasPerm.INSTANCE);
        StringArcencielBlock.regScriptKeyword(TokenRunCmd.INSTANCE);
        StringArcencielBlock.regScriptKeyword(TokenConsole.INSTANCE);
        StringArcencielBlock.regScriptKeyword(TokenReturn.INSTANCE);
        StringArcencielBlock.regScriptKeyword(TokenAll.INSTANCE);
        StringArcencielBlock.regScriptKeyword(TokenAny.INSTANCE);
        StringArcencielBlock.regScriptKeyword(TokenLevel.INSTANCE);
        StringArcencielBlock.regScriptKeyword(TokenTakeLevel.INSTANCE);
        StringArcencielBlock.regScriptKeyword(TokenPapi.INSTANCE);
        if (VaultHooker.INSTANCE.isEconomyHooked()) {
            StringArcencielBlock.regScriptKeyword(TokenMoney.INSTANCE);
            StringArcencielBlock.regScriptKeyword(TokenTakeMoney.INSTANCE);
        }
        if (PlayerPointsHooker.INSTANCE.isPlayerPointsHooked()) {
            StringArcencielBlock.regScriptKeyword(TokenPoints.INSTANCE);
            StringArcencielBlock.regScriptKeyword(TokenTakePoints.INSTANCE);
        }
    }

    public BukkitConfigWrapper functionFile() {
        return functionFile;
    }

    public List<String> getFunc(String funcName) {
        return functionFile.config().getStringList(funcName);
    }

    @Override
    public void run(Plugin plugin, LifeCycle lifeCycle) {
        functionFile = new BukkitConfigWrapper(Craftorithm.instance(), "function.yml");
        regDefScriptKeyword();
    }

}
