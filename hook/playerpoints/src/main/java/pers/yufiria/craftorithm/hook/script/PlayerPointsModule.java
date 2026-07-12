package pers.yufiria.craftorithm.hook.script;

import crypticlib.script.ScriptContext;
import crypticlib.script.ScriptValue;
import crypticlib.script.func.ScriptFunctionRegistry;
import crypticlib.script.func.ScriptModule;
import crypticlib.script.vm.ScriptVM;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import pers.yufiria.craftorithm.hook.PlayerPointsHook;

import java.util.Optional;
import java.util.UUID;

public enum PlayerPointsModule implements ScriptModule {

    INSTANCE;

    @Override
    public String moduleName() {
        return "playerpoints";
    }

    @Override
    public void register(ScriptFunctionRegistry registry) {
        String moduleName = moduleName();
        registry.register(moduleName, "points", this::points);
        registry.register(moduleName, "take_points", this::takePoints);
        registry.register(moduleName, "give_points", this::givePoints);
    }

    private ScriptValue points(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        if (!PlayerPointsHook.INSTANCE.isPlayerPointsHooked()) {
            return ScriptValue.of(0);
        }
        PlayerPointsAPI api = ((PlayerPoints) PlayerPointsHook.INSTANCE.playerPoints()).getAPI();
        Optional<UUID> playerIdOpt = ctx.playerId();
        if (playerIdOpt.isPresent()) {
            return ScriptValue.of(api.look(playerIdOpt.get()));
        }
        return ScriptValue.of(0);
    }

    private ScriptValue takePoints(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        if (args.length < 1) {
            return ScriptValue.of(false);
        }
        if (!PlayerPointsHook.INSTANCE.isPlayerPointsHooked()) {
            return ScriptValue.of(false);
        }
        PlayerPointsAPI api = ((PlayerPoints) PlayerPointsHook.INSTANCE.playerPoints()).getAPI();
        int amount = (int) args[0].asNumber();
        if (amount > 0) {
            Optional<UUID> playerId = ctx.playerId();
            if (playerId.isEmpty()) {
                return ScriptValue.of(false);
            } else {
                api.take(playerId.get(), amount);
            }
        }
        return ScriptValue.of(true);
    }

    private ScriptValue givePoints(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        if (args.length < 1) {
            return ScriptValue.of(false);
        }
        if (!PlayerPointsHook.INSTANCE.isPlayerPointsHooked()) {
            return ScriptValue.of(false);
        }
        PlayerPointsAPI api = ((PlayerPoints) PlayerPointsHook.INSTANCE.playerPoints()).getAPI();
        int amount = (int) args[0].asNumber();
        if (amount > 0) {
            Optional<UUID> playerId = ctx.playerId();
            if (playerId.isEmpty()) {
                return ScriptValue.of(false);
            } else {
                api.give(playerId.get(), amount);
            }
        }
        return ScriptValue.of(true);
    }

}
