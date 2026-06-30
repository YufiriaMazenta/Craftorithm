package pers.yufiria.craftorithm.hook.packetevents;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import pers.yufiria.craftorithm.recipe.RecipeManager;

import java.util.Set;

public enum RecipeUpdatePacketListener implements PacketListener {

    INSTANCE;

    private final Set<PacketTypeCommon> BLOCK_PACKET_TYPES = Set.of(
        //配方书添加
        PacketType.Play.Server.RECIPE_BOOK_ADD,
        //配方书删除
        PacketType.Play.Server.RECIPE_BOOK_REMOVE,
        //配方书设置
        PacketType.Play.Server.RECIPE_BOOK_SETTINGS,
        //更新配方
        PacketType.Play.Server.DECLARE_RECIPES,
        //更新成就
        PacketType.Play.Server.UPDATE_ADVANCEMENTS,
        //更新tag
        PacketType.Play.Server.TAGS
    );

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (!RecipeManager.INSTANCE.isReloadingRecipeManager()) {
            //如果当前不是正在重载配方，那么不进行拦截
            return;
        }
        if (BLOCK_PACKET_TYPES.contains(event.getPacketType())) {
            //拦截处于黑名单状态下的数据包发送类型
            event.setCancelled(true);
        }
    }
}
