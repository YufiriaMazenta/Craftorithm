package top.oasismc.oasisrecipe.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.FurnaceStartSmeltEvent;
import org.bukkit.inventory.ItemStack;
import top.oasismc.oasisrecipe.item.ItemLoader;
import top.oasismc.oasisrecipe.recipe.RecipeManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum FurnaceSmeltListener implements Listener {

    INSTANCE;

    private final Map<Block, String> furnaceMap;

    FurnaceSmeltListener() {
        furnaceMap = new HashMap<>();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFurnaceStartSmelt(FurnaceStartSmeltEvent event) {
        String recipeName = RecipeManager.INSTANCE.getRecipeName(event.getRecipe());
        if (RecipeManager.INSTANCE.getRecipeFile().getConfig().getString(recipeName + ".type", "shaped").startsWith("random_")) {
            furnaceMap.put(event.getBlock(), recipeName);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFurnaceSmelt(FurnaceSmeltEvent event) {
        if (furnaceMap.containsKey(event.getBlock())) {
            List<String> resultList = RecipeManager.INSTANCE.getRecipeFile().getConfig().getStringList(furnaceMap.get(event.getBlock()) + ".result");
            List<Map.Entry<ItemStack, Double>> probabilityList = getProbability(resultList);
            double random = Math.random();
            for (Map.Entry<ItemStack, Double> entry : probabilityList) {
                if (random < entry.getValue()) {
                    event.setResult(entry.getKey());
                    break;
                }
            }
            furnaceMap.remove(event.getBlock());
        }
    }

    private List<Map.Entry<ItemStack, Double>> getProbability(List<String> results) {
        Map<ItemStack, Double> probabilityMap = new HashMap<>();
        double sum = 0;
        for (String result : results) {
            String item = result.substring(0, result.indexOf(" "));
            double probability = Double.parseDouble(result.substring(result.indexOf(" ") + 1));
            ItemStack itemStack = ItemLoader.getItemFromConfig(item);
            sum += probability;
            probabilityMap.put(itemStack, sum);
        }
        if (sum < 1.0) {
            probabilityMap.put(new ItemStack(Material.AIR), 1.0);
        }
        List<Map.Entry<ItemStack, Double>> probabilityList = new ArrayList<>(probabilityMap.entrySet());
        probabilityList.sort(Map.Entry.comparingByValue());
        return probabilityList;
    }

}
