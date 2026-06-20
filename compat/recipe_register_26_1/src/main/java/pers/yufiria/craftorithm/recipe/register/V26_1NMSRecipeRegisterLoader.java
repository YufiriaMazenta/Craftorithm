package pers.yufiria.craftorithm.recipe.register;


import crypticlib.MinecraftVersion;
import crypticlib.lifecycle.BukkitLifeCycleTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.LifeCycleTaskSettings;
import crypticlib.lifecycle.TaskRule;
import org.bukkit.plugin.Plugin;

@LifeCycleTaskSettings(
    rules = {
        @TaskRule(lifeCycle = LifeCycle.LOAD)
    }
)
public enum V26_1NMSRecipeRegisterLoader implements BukkitLifeCycleTask {

    INSTANCE;

    @Override
    public void lifecycle(Plugin plugin, LifeCycle lifeCycle) {
        BukkitRecipeRegister.INSTANCE.nmsRecipeRegisterCompat().register(
            MinecraftVersion.V26_1.name(),
            V26_1NMSRecipeRegister::new
        );
    }

}
