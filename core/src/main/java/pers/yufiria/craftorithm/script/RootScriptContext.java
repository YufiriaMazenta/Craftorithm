package pers.yufiria.craftorithm.script;

import crypticlib.script.ScriptContext;
import crypticlib.script.ScriptValue;
import crypticlib.script.object.ReflectPropertyResolver;
import org.bukkit.Bukkit;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.recipe.RecipeManager;

public class RootScriptContext extends ScriptContext {

    public static final RootScriptContext INSTANCE = new RootScriptContext();

    private RootScriptContext() {
        ReflectPropertyResolver resolver = ReflectPropertyResolver.INSTANCE;
        setVariable("server", ScriptValue.of(Bukkit.getServer(), resolver));
        setVariable("recipe_manager", ScriptValue.of(RecipeManager.INSTANCE, resolver));
        setVariable("item_manager", ScriptValue.of(ItemManager.INSTANCE, resolver));
    }

}
