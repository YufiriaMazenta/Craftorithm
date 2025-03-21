package pers.yufiria.craftorithm.ui.icon;

import crypticlib.action.Action;
import crypticlib.chat.BukkitTextProcessor;
import crypticlib.lang.LangManager;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.Craftorithm;

import java.util.HashMap;
import java.util.Map;

public class ActionIcon extends Icon {

    protected final Action action;
    //用于替换文本内一些内容的map
    protected Map<String, String> textReplaceMap = new HashMap<>();

    public ActionIcon(@NotNull IconDisplay iconDisplay) {
        this(iconDisplay, null);
    }

    public ActionIcon(@NotNull IconDisplay iconDisplay, Action action) {
        super(iconDisplay);
        this.action = action;
    }

    @Override
    public String parseIconText(String originText) {
        Player iconParsePlayer = this.parsePlayer();
        String text = LangManager.INSTANCE.replaceLang(originText, iconParsePlayer);
        for (Map.Entry<String, String> entry : textReplaceMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            text = text.replace(key, value);
        }
        return BukkitTextProcessor.color(BukkitTextProcessor.placeholder(iconParsePlayer, text));
    }

    @Override
    public Icon onClick(InventoryClickEvent event) {
        if (action != null) {
            action.run(((Player) event.getWhoClicked()), Craftorithm.instance(), null);
        }
        return this;
    }

    public @NotNull Map<String, String> textReplaceMap() {
        return textReplaceMap;
    }

    public void setTextReplaceMap(@NotNull Map<String, String> textReplaceMap) {
        this.textReplaceMap = textReplaceMap;
    }

    public Action action() {
        return action;
    }

}
