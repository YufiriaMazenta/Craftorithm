package pers.yufiria.craftorithm.hook.packetevents;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import pers.yufiria.craftorithm.fakeresult.FakeResultDataHandler;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public enum FakeResultPreviewPacketListener implements PacketListener {

    INSTANCE;

    @Override
    public void onPacketSend(PacketSendEvent event) {
        User user = event.getUser();
        UUID uuid = user.getUUID();
        if (uuid == null) {
            //这个地方千万不能去掉,不然启动时因为没有玩家会报错
            return;
        }

        Optional<FakeResultDataHandler.CacheRecipeData> preparingRecipeOpt = FakeResultDataHandler.INSTANCE.getPlayerPreparingRecipe(uuid);
        if (preparingRecipeOpt.isEmpty()) {
            return;
        }
        FakeResultDataHandler.CacheRecipeData cacheRecipeData = preparingRecipeOpt.get();
        NamespacedKey recipeKey = cacheRecipeData.recipeKey();

        final Optional<ItemStack> fakeResultOpt = FakeResultDataHandler.INSTANCE.getRecipeFakeResult(recipeKey);
        fakeResultOpt.ifPresent(fakeResult -> {
            switch (event.getPacketType()) {
                case PacketType.Play.Server.SET_SLOT -> {
                    WrapperPlayServerSetSlot packet = new WrapperPlayServerSetSlot(event);
                    if (packet.getSlot() != cacheRecipeData.resultSlot()) {
                        return;
                    }
                    packet.setItem(SpigotConversionUtil.fromBukkitItemStack(fakeResult));
                }
                case PacketType.Play.Server.WINDOW_ITEMS -> {
                    WrapperPlayServerWindowItems packet = new WrapperPlayServerWindowItems(event);
                    List<com.github.retrooper.packetevents.protocol.item.ItemStack> items = packet.getItems();
                    if (!items.isEmpty()) {
                        items.set(cacheRecipeData.resultSlot(), SpigotConversionUtil.fromBukkitItemStack(fakeResult));
                        packet.setItems(items);
                    }
                }
                default -> {
                    return;
                }
            }
            event.markForReEncode(true);
        });

    }
}
