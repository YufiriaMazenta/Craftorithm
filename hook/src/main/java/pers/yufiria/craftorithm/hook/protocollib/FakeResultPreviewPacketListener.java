package pers.yufiria.craftorithm.hook.protocollib;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.injector.temporary.TemporaryPlayer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.fakeresult.FakeResultDataHandler;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FakeResultPreviewPacketListener extends PacketAdapter {

    public static final FakeResultPreviewPacketListener INSTANCE = new FakeResultPreviewPacketListener();

    private FakeResultPreviewPacketListener() {
        super(Craftorithm.instance(), PacketType.Play.Server.SET_SLOT, PacketType.Play.Server.WINDOW_ITEMS);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        Player player = event.getPlayer();
        if (player instanceof TemporaryPlayer) {
            return;
        }
        UUID uuid = player.getUniqueId();
        Optional<FakeResultDataHandler.CacheRecipeData> preparingRecipeOpt = FakeResultDataHandler.INSTANCE.getPlayerPreparingRecipe(uuid);
        if (preparingRecipeOpt.isEmpty()) {
            return;
        }
        FakeResultDataHandler.CacheRecipeData cacheRecipeData = preparingRecipeOpt.get();
        NamespacedKey recipeKey = cacheRecipeData.recipeKey();

        PacketType packetType = event.getPacketType();
        Optional<ItemStack> fakeResultOpt = FakeResultDataHandler.INSTANCE.getRecipeFakeResult(recipeKey);
        fakeResultOpt.ifPresent(fakeResult -> {
            if (packetType.equals(PacketType.Play.Server.SET_SLOT)) {
                int slot = event.getPacket().getIntegers().read(2);
                if (slot != cacheRecipeData.resultSlot()) {
                    return;
                }
                event.getPacket().getItemModifier().write(0, fakeResult);
            } else if (packetType.equals(PacketType.Play.Server.WINDOW_ITEMS)) {
                List<ItemStack> items = event.getPacket().getItemListModifier().read(0);
                if (items != null && !items.isEmpty()) {
                    items.set(cacheRecipeData.resultSlot(), fakeResult);
                    event.getPacket().getItemListModifier().write(0, items);
                }
            }
        });

    }

}
