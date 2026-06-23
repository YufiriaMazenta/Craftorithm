package pers.yufiria.craftorithm.hook.protocolLib;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.Craftorithm;

public class FakeResultPreviewPacketListener extends PacketAdapter implements Listener {

    public static final FakeResultPreviewPacketListener INSTANCE = new FakeResultPreviewPacketListener();
    
    private FakeResultPreviewPacketListener() {
        super(Craftorithm.instance(), PacketType.Play.Server.SET_SLOT, PacketType.Play.Server.WINDOW_ITEMS);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        
    }

    private record CacheRecipeData(
        @NotNull NamespacedKey recipeKey,
        int resultSlot
    ) {}
    
}
