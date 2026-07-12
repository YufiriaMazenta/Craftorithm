package pers.yufiria.craftorithm.hook.script;

import crypticlib.script.ScriptContext;
import crypticlib.script.ScriptValue;
import crypticlib.script.func.ScriptFunctionRegistry;
import crypticlib.script.func.ScriptModule;
import crypticlib.script.vm.ScriptVM;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import pers.yufiria.craftorithm.hook.VaultHook;
import pers.yufiria.craftorithm.util.PlayerUtils;

import java.util.Optional;

public enum VaultModule implements ScriptModule {

    INSTANCE;

    @Override
    public String moduleName() {
        return "vault";
    }

    @Override
    public void register(ScriptFunctionRegistry registry) {
        String moduleName = moduleName();
        registry.register(moduleName, "money", this::money);
        registry.register(moduleName, "take_money", this::takeMoney);
        registry.register(moduleName, "give_money", this::giveMoney);
    }

    private ScriptValue money(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        Optional<Player> playerOpt = PlayerUtils.getPlayerOpt(PlayerUtils.getPlayerIdFromInvoker(ctx.invoker()));
        if (playerOpt.isEmpty()) {
            return ScriptValue.of(false);
        }
        Player player = playerOpt.get();
        Economy economy = (Economy) VaultHook.INSTANCE.economy();
        return ScriptValue.of(economy.getBalance(player));
    }

    private ScriptValue takeMoney(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        if (args.length < 1) return ScriptValue.of(false);
        Optional<Player> playerOpt = PlayerUtils.getPlayerOpt(PlayerUtils.getPlayerIdFromInvoker(ctx.invoker()));
        if (playerOpt.isEmpty()) {
            return ScriptValue.of(false);
        }
        Player player = playerOpt.get();
        Economy economy = (Economy) VaultHook.INSTANCE.economy();
        double amount = args[0].asNumber();
        if (amount > 0) {
            economy.withdrawPlayer(player, amount);
        }
        return ScriptValue.of(true);
    }

    private ScriptValue giveMoney(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        if (args.length < 1) {
            return ScriptValue.of(false);
        }
        Optional<Player> playerOpt = PlayerUtils.getPlayerOpt(PlayerUtils.getPlayerIdFromInvoker(ctx.invoker()));
        if (playerOpt.isEmpty()) {
            return ScriptValue.of(false);
        }
        Player player = playerOpt.get();
        Economy economy = (Economy) VaultHook.INSTANCE.economy();
        double amount = args[0].asNumber();
        if (amount > 0) {
            economy.depositPlayer(player, amount);
        }
        return ScriptValue.of(true);
    }

}
