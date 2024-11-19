package com.github.yufiriamazenta.craftorithm.listener;

import com.github.yufiriamazenta.craftorithm.item.ItemManager;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeGroup;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeType;
import crypticlib.listener.EventListener;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.FurnaceStartSmeltEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@EventListener
public enum RandomSmeltHandler implements Listener {

    INSTANCE;

    private final Map<Block, YamlConfiguration> randomFurnaceBlockMap;
    private final Random random;

    RandomSmeltHandler() {
        random = new Random();
        randomFurnaceBlockMap = new HashMap<>();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onFurnaceStartSmelt(FurnaceStartSmeltEvent event) {
        NamespacedKey recipeKey = event.getRecipe().getKey();
        Map<String, RecipeGroup> randomCookingRecipeGroups = RecipeManager.INSTANCE.recipeMap().getOrDefault(RecipeType.RANDOM_COOKING, new HashMap<>());
        boolean isRandomCooking = false;
        for (RecipeGroup group : randomCookingRecipeGroups.values()) {
            if (group.contains(recipeKey)) {
                isRandomCooking = true;
                break;
            }
        }
        if (!isRandomCooking)
            return;
        YamlConfiguration config = RecipeManager.INSTANCE.getRecipeConfig(event.getRecipe().getKey());
        if (config == null)
            return;
        if (config.getString("type", "shaped.yml").equals("random_cooking")) {
            randomFurnaceBlockMap.put(event.getBlock(), config);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onFurnaceSmelt(FurnaceSmeltEvent event) {
        if (randomFurnaceBlockMap.containsKey(event.getBlock())) {
            List<String> resultList = randomFurnaceBlockMap.get(event.getBlock()).getStringList("result");
            List<Map.Entry<ItemStack, Integer>> weightList = getWeight(resultList);
            int randomNum = random.nextInt(weightList.get(weightList.size() - 1).getValue());
            for (Map.Entry<ItemStack, Integer> entry : weightList) {
                if (randomNum < entry.getValue()) {
                    event.setResult(entry.getKey());
                    break;
                }
            }
            randomFurnaceBlockMap.remove(event.getBlock());
        }
    }

    private List<Map.Entry<ItemStack, Integer>> getWeight(List<String> resultStr) {
        Map<ItemStack, Integer> weightMap = new HashMap<>();
        int sum = 0;
        for (String result : resultStr) {
            String item = result.substring(0, result.lastIndexOf(" "));
            int weight = Integer.parseInt(result.substring(result.lastIndexOf(" ") + 1));
            ItemStack itemStack = ItemManager.INSTANCE.matchItem(item);
            sum += weight;
            weightMap.put(itemStack, sum);
        }
        List<Map.Entry<ItemStack, Integer>> weightList = new ArrayList<>(weightMap.entrySet());
        weightList.sort(Map.Entry.comparingByValue());
        return weightList;
    }

}
