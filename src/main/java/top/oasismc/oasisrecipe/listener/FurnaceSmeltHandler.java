package top.oasismc.oasisrecipe.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.FurnaceStartSmeltEvent;
import org.bukkit.inventory.ItemStack;
import top.oasismc.oasisrecipe.item.ItemManager;
import top.oasismc.oasisrecipe.recipe.RecipeManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum FurnaceSmeltHandler implements Listener {

    INSTANCE;

    private final Map<Block, YamlConfiguration> furnaceMap;

    FurnaceSmeltHandler() {
        furnaceMap = new HashMap<>();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onFurnaceStartSmelt(FurnaceStartSmeltEvent event) {
        YamlConfiguration config = RecipeManager.getRecipeConfig(event.getRecipe());
        if (config == null)
            return;
        if (config.getString("type", "shaped").equals("random_cooking")) {
            furnaceMap.put(event.getBlock(), config);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onFurnaceSmelt(FurnaceSmeltEvent event) {
        if (furnaceMap.containsKey(event.getBlock())) {
            List<String> resultList = furnaceMap.get(event.getBlock()).getStringList("result");
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

    private List<Map.Entry<ItemStack, Double>> getProbability(List<String> resultStr) {
        Map<ItemStack, Double> probabilityMap = new HashMap<>();
        double sum = 0;
        for (String result : resultStr) {
            String item = result.substring(0, result.lastIndexOf(" "));
            double probability = Double.parseDouble(result.substring(result.lastIndexOf(" ") + 1));
            ItemStack itemStack = ItemManager.matchOasisRecipeItem(item);
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
