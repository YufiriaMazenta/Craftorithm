package pers.yufiria.craftorithm.command.recipe;

import crypticlib.lifecycle.AutoTask;
import crypticlib.lifecycle.BukkitLifeCycleTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.TaskRule;
import org.bukkit.plugin.Plugin;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.RecipeType;
import pers.yufiria.craftorithm.recipe.RecipeTypeMap;
import pers.yufiria.craftorithm.util.CommandUtils;
import pers.yufiria.craftorithm.util.LangUtils;
import crypticlib.command.BukkitSubcommand;
import crypticlib.command.CommandInfo;
import crypticlib.perm.PermInfo;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AutoTask(
    rules = {
        @TaskRule(lifeCycle = LifeCycle.ACTIVE)
    }
)
public final class CreateRecipeCommand extends BukkitSubcommand implements BukkitLifeCycleTask {

    public static final CreateRecipeCommand INSTANCE = new CreateRecipeCommand();
    private final Pattern recipeNamePattern = Pattern.compile("^[a-z0-9._-]+$");
    private final Map<RecipeType, BiConsumer<Player, String>> recipeCreatorMap = new RecipeTypeMap<>();

    private CreateRecipeCommand() {
        super(CommandInfo
            .builder("create")
            .permission(new PermInfo("craftorithm.command.create"))
            .usage("&r/craftorithm create <recipe_type> [recipe_name]")
            .build()
        );
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if (!CommandUtils.checkSenderIsPlayer(sender))
            return;
        if (args.isEmpty()) {
            sendDescriptions(sender);
            return;
        }
        String recipeTypeStr = args.get(0);
        String recipeName;
        if (args.size() < 2)
            recipeName = UUID.randomUUID().toString();
        else
            recipeName = args.get(1);

        Matcher matcher = recipeNamePattern.matcher(recipeName);
        if (!matcher.matches()) {
            LangUtils.sendLang(sender, Languages.COMMAND_CREATE_UNSUPPORTED_RECIPE_NAME);
            return;
        }
        if (RecipeManager.INSTANCE.containsRecipe(recipeName)) {
            LangUtils.sendLang(sender, Languages.COMMAND_CREATE_NAME_USED);
            return;
        }
        RecipeType recipeType = RecipeManager.INSTANCE.getRecipeType(recipeTypeStr);
        if (recipeType == null) {
            LangUtils.sendLang(sender, Languages.COMMAND_CREATE_UNSUPPORTED_RECIPE_TYPE);
            return;
        }
        BiConsumer<Player, String> creatorConsumer = recipeCreatorMap.get(recipeType);
        if (creatorConsumer == null) {
            LangUtils.sendLang(sender, Languages.COMMAND_CREATE_UNSUPPORTED_RECIPE_TYPE);
            return;
        }
        Player player = (Player) sender;
        creatorConsumer.accept(player, recipeName);
    }

    @Override
    public List<String> tab(@NotNull CommandSender sender, List<String> args) {
        if (args.size() <= 1) {
            return recipeCreatorMap.keySet().stream().map(RecipeType::typeKey).toList();
        }
        return Collections.singletonList("<recipe_name>");
    }

    private void registerDefRecipeCreators() {
        //TODO 配方创建器
    }

    public void addRecipeCreator(RecipeType recipeType, BiConsumer<Player, String> creatorFunc) {
        recipeCreatorMap.put(recipeType, creatorFunc);
    }

    public void removeRecipeCreator(RecipeType recipeType) {
        recipeCreatorMap.remove(recipeType);
    }

    @Override
    public void run(Plugin plugin, LifeCycle lifeCycle) {
        registerDefRecipeCreators();
    }

}
