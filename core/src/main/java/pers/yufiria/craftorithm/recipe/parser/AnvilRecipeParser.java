package pers.yufiria.craftorithm.recipe.parser;

import crypticlib.util.MaterialHelper;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.ItemPack;
import pers.yufiria.craftorithm.item.NamespacedItemId;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.AnvilRecipe;
import pers.yufiria.craftorithm.recipe.RecipeParser;
import pers.yufiria.craftorithm.recipe.choice.RecipeChoiceParser;
import pers.yufiria.craftorithm.recipe.choice.StackableItemIdChoice;
import pers.yufiria.craftorithm.recipe.exception.RecipeLoadException;
import pers.yufiria.craftorithm.recipe.resultProcessor.ResultProcessorManager;
import pers.yufiria.craftorithm.util.RecipeUtils;

import java.util.List;
import java.util.Optional;

public enum AnvilRecipeParser implements RecipeParser<AnvilRecipe> {

    INSTANCE;

    @Override
    public @NotNull RecipeChoiceParser choiceParser() {
        return (choiceStr -> {
            if (choiceStr == null || choiceStr.isEmpty()) {
                throw new RecipeLoadException(choiceStr + " is not a valid ingredient ID.");
            }
            List<NamespacedItemIdStack> choices;
            if (!choiceStr.contains(":")) {
                Material material;
                if (choiceStr.contains(" ")) {
                    //如果包含数量，需要去除数量
                    material = MaterialHelper.matchMaterial(choiceStr.substring(0, choiceStr.indexOf(" ")));
                } else {
                    material = MaterialHelper.matchMaterial(choiceStr);
                }
                if (material == null) {
                    throw new RecipeLoadException(choiceStr + " is not a valid material");
                }
                choices = List.of(NamespacedItemIdStack.fromString(choiceStr));
                return new StackableItemIdChoice(choices);
            }
            int index = choiceStr.indexOf(":");
            String namespace = choiceStr.substring(0, index);
            namespace = namespace.toLowerCase();
            switch (namespace) {
                case "minecraft":
                    Material material;
                    if (choiceStr.contains(" ")) {
                        //如果包含数量，需要去除数量
                        material = MaterialHelper.matchMaterial(choiceStr.substring(0, choiceStr.indexOf(" ")));
                    } else {
                        material = MaterialHelper.matchMaterial(choiceStr);
                    }
                    if (material == null) {
                        throw new RecipeLoadException(choiceStr + " is not a valid material");
                    }
                    choices = List.of(NamespacedItemIdStack.fromString(choiceStr));
                    break;
                case "tag":
                    String tagKeyStr = choiceStr.substring(4);
                    int spaceIndex = tagKeyStr.lastIndexOf(' ');
                    if (spaceIndex == -1) {
                        Optional<Tag<Material>> tagOpt = RecipeUtils.getTag(tagKeyStr);
                        if (tagOpt.isEmpty()) {
                            throw new RecipeLoadException(tagKeyStr + " is not a valid tag");
                        }
                        Tag<Material> materialTag = tagOpt.get();
                        choices = materialTag.getValues().stream().map(it -> new NamespacedItemIdStack(NamespacedItemId.fromMaterial(it))).toList();
                    } else {
                        int amount = Integer.parseInt(tagKeyStr.substring(spaceIndex + 1));
                        tagKeyStr = tagKeyStr.substring(0, spaceIndex);
                        Optional<Tag<Material>> tagOpt = RecipeUtils.getTag(tagKeyStr);
                        if (tagOpt.isEmpty()) {
                            throw new RecipeLoadException(tagKeyStr + " is not a valid tag");
                        }
                        Tag<Material> materialTag = tagOpt.get();
                        choices = materialTag.getValues().stream().map(it -> new NamespacedItemIdStack(NamespacedItemId.fromMaterial(it), amount)).toList();
                    }
                    break;
                case "item_pack":
                    //是物品组
                    String packId = choiceStr.substring("item_pack:".length());
                    ItemPack itemPack = ItemManager.INSTANCE.getItemPack(packId);
                    if (itemPack == null) {
                        throw new RecipeLoadException(packId + " is not a valid item pack");
                    }
                    choices = itemPack.itemIds();
                    break;
                default:
                    choices = List.of(NamespacedItemIdStack.fromString(choiceStr));
                    break;
            }
            return new StackableItemIdChoice(choices);
        });
    }

    @Override
    public @NotNull AnvilRecipe parse(String recipeName, ConfigurationSection recipeConfig) {
        try {
            NamespacedKey recipeKey = new NamespacedKey(Craftorithm.instance(), recipeName);
            String resultId = recipeConfig.getString("result");
            NamespacedItemIdStack result = NamespacedItemIdStack.fromString(resultId);
            String baseId = recipeConfig.getString("base");
            StackableItemIdChoice base = (StackableItemIdChoice) choiceParser().parse(baseId);
            String additionId = recipeConfig.getString("addition");
            StackableItemIdChoice addition = (StackableItemIdChoice) choiceParser().parse(additionId);
            int costLevel = recipeConfig.getInt("cost_level", 0);
            if (recipeConfig.isConfigurationSection("result_processors")) {
                ConfigurationSection section = recipeConfig.getConfigurationSection("result_processors");
                ResultProcessorManager.INSTANCE.addRecipeProcessors(recipeKey, section);
            } else if (recipeConfig.isList("copy_components_rules")) {
                ResultProcessorManager.INSTANCE.addRecipeProcessorsLegacy(recipeKey, recipeConfig.getStringList("copy_components_rules"));
            }
            AnvilRecipe anvilRecipe = new AnvilRecipe(recipeKey, result, base, addition);
            anvilRecipe.setCostLevel(costLevel);
            return anvilRecipe;
        } catch (RecipeLoadException e) {
            throw e;
        } catch (Throwable throwable) {
            throw new RecipeLoadException(throwable);
        }
    }

}
