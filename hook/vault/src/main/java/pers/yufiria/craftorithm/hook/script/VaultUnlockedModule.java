package pers.yufiria.craftorithm.hook.script;

import crypticlib.script.ScriptContext;
import crypticlib.script.ScriptValue;
import crypticlib.script.func.ScriptFunctionRegistry;
import crypticlib.script.func.ScriptModule;
import crypticlib.script.vm.ScriptVM;
import net.milkbowl.vault2.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.hook.VaultHook;
import pers.yufiria.craftorithm.util.PlayerUtils;

import java.math.BigDecimal;
import java.util.UUID;

public enum VaultUnlockedModule implements ScriptModule {

    INSTANCE;

    @Override
    public String moduleName() {
        return "vault_unlocked";
    }

    @Override
    public void register(ScriptFunctionRegistry registry) {
        String moduleName = moduleName();
        registry.register(moduleName, "money", this::money);
        registry.register(moduleName, "take_money", this::takeMoney);
        registry.register(moduleName, "give_money", this::giveMoney);
    }

    private ScriptValue money(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        UUID playerId = PlayerUtils.getPlayerIdFromInvoker(ctx.invoker());
        if (playerId == null) {
            return ScriptValue.of(false);
        }
        Economy economy = (Economy) VaultHook.INSTANCE.economy();
        BigDecimal balance;
        if (args.length < 1) {
            balance = economy.balance(Craftorithm.instance().pluginName(), playerId);
        } else {
            String currency = args[0].asString();
            balance = economy.balance(Craftorithm.instance().pluginName(), playerId, currency);
        }
        return ScriptValue.of(balance);
    }

    private ScriptValue takeMoney(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        if (args.length < 1) return ScriptValue.of(false);
        UUID playerId = PlayerUtils.getPlayerIdFromInvoker(ctx.invoker());
        if (playerId == null) {
            return ScriptValue.of(false);
        }
        Economy economy = (Economy) VaultHook.INSTANCE.economy();
        BigDecimal amount = args[0].asBigDecimal();
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            return ScriptValue.of(false);
        }
        if (args.length >= 2) {
            String currency = args[1].asString();
            Player player = Bukkit.getPlayer(playerId);
            if (player == null) {
                return ScriptValue.of(false);
            }
            economy.withdraw(Craftorithm.instance().pluginName(), playerId, player.getWorld().getName(), currency, amount);
            return ScriptValue.of(true);
        } else {
            economy.withdraw(Craftorithm.instance().pluginName(), playerId, amount);
            return ScriptValue.of(true);
        }
    }

    private ScriptValue giveMoney(ScriptContext ctx, ScriptVM vm, ScriptValue... args) {
        if (args.length < 1) return ScriptValue.of(false);
        UUID playerId = PlayerUtils.getPlayerIdFromInvoker(ctx.invoker());
        if (playerId == null) {
            return ScriptValue.of(false);
        }
        Economy economy = (Economy) VaultHook.INSTANCE.economy();
        BigDecimal amount = args[0].asBigDecimal();
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            return ScriptValue.of(false);
        }
        if (args.length >= 2) {
            String currency = args[1].asString();
            Player player = Bukkit.getPlayer(playerId);
            if (player == null) {
                return ScriptValue.of(false);
            }
            economy.deposit(Craftorithm.instance().pluginName(), playerId, player.getWorld().getName(), currency, amount);
            return ScriptValue.of(true);
        } else {
            economy.deposit(Craftorithm.instance().pluginName(), playerId, amount);
            return ScriptValue.of(true);
        }
    }

}
