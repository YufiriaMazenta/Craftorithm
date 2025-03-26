package pers.yufiria.craftorithm.command.recipe;

import crypticlib.lifecycle.AutoTask;
import crypticlib.lifecycle.BukkitLifeCycleTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.TaskRule;
import crypticlib.util.IOHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.config.PluginConfigs;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.RecipeType;
import pers.yufiria.craftorithm.recipe.RecipeTypeMap;
import pers.yufiria.craftorithm.recipe.SimpleRecipeTypes;
import pers.yufiria.craftorithm.recipe.extra.AnvilRecipe;
import pers.yufiria.craftorithm.ui.anvil.AnvilDisplayMenuManager;
import pers.yufiria.craftorithm.ui.vanillaShaped.VanillaShapedDisplayMenuManager;
import pers.yufiria.craftorithm.util.CommandUtils;
import crypticlib.command.BukkitSubcommand;
import crypticlib.command.CommandInfo;
import crypticlib.perm.PermInfo;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.*;
import java.util.function.BiConsumer;

@AutoTask(
    rules = {
        @TaskRule(lifeCycle = LifeCycle.ACTIVE)
    }
)
public class DisplayRecipeCommand extends BukkitSubcommand implements BukkitLifeCycleTask {

    public static final DisplayRecipeCommand INSTANCE = new DisplayRecipeCommand();
    private final Map<RecipeType, BiConsumer<Player, Recipe>> recipeDisplayMap = new RecipeTypeMap<>();

    protected DisplayRecipeCommand() {
        super(
            CommandInfo
                .builder("display")
                .permission(new PermInfo("craftorithm.command.display"))
                .usage("&r/craftorithm display <recipe_id>")
                .build()
        );
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if (args.isEmpty()) {
            sendDescriptions(sender);
            return;
        }
        Player target;
        if (args.size() >= 2) {
            String targetName = args.get(1);
            Player player = Bukkit.getPlayer(targetName);
            if (player == null) {
                //TODO 提示消息
                IOHelper.info("&cUnknown player: " + targetName);
                return;
            }
            target = player;
        } else {
            if (!CommandUtils.checkSenderIsPlayer(sender))
                return;
            target = (Player) sender;
        }
        NamespacedKey namespacedKey = NamespacedKey.fromString(args.get(0));
        Recipe recipe = RecipeManager.INSTANCE.getRecipe(namespacedKey);
        if (recipe == null) {
            return;
        }
        RecipeType recipeType = RecipeManager.INSTANCE.getRecipeType(recipe);
        recipeDisplayMap.getOrDefault(recipeType, (player, displayRecipe) -> {
            LangUtils.sendLang(sender, Languages.COMMAND_DISPLAY_UNSUPPORTED_RECIPE_TYPE);
        }).accept(target, recipe);
    }

    @Override
    public @Nullable List<String> tab(CommandSender sender, List<String> args) {
        if (args.size() <= 1) {
            Set<NamespacedKey> recipes = new LinkedHashSet<>(RecipeManager.INSTANCE.craftorithmRecipes().keySet());
            recipes.addAll(RecipeManager.INSTANCE.craftorithmRecipes().keySet());
            return recipes.stream().map(NamespacedKey::toString).toList();
        }
        return Collections.singletonList("");
    }

    public void addRecipeDisplay(RecipeType recipeType, BiConsumer<Player, Recipe> displayFunc) {
        recipeDisplayMap.put(recipeType, displayFunc);
    }

    public void removeRecipeDisplay(RecipeType recipeType) {
        recipeDisplayMap.remove(recipeType);
    }

    @Override
    public void run(Plugin plugin, LifeCycle lifeCycle) {
        registerDefRecipeDisplay();
    }

    private void registerDefRecipeDisplay() {
        addRecipeDisplay(SimpleRecipeTypes.VANILLA_SHAPED, (player, recipe) -> {
            VanillaShapedDisplayMenuManager.INSTANCE.openMenu(player, (ShapedRecipe) recipe);
        });
        if (PluginConfigs.ENABLE_ANVIL_RECIPE.value()) {
            addRecipeDisplay(SimpleRecipeTypes.ANVIL, (player, recipe) -> {
                AnvilDisplayMenuManager.INSTANCE.openMenu(player, (AnvilRecipe) recipe);
            });
        }
    }


}
