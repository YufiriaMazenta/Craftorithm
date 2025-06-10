package pers.yufiria.craftorithm.ui.icon;

import crypticlib.action.Action;
import crypticlib.chat.BukkitTextProcessor;
import crypticlib.lang.LangManager;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.Craftorithm;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ActionIcon extends Icon {

    protected final Map<ClickType, Action> actions;
    //用于替换文本内一些内容的map
    protected Map<String, String> textReplaceMap = new HashMap<>();

    public ActionIcon(@NotNull IconDisplay iconDisplay) {
        this(iconDisplay, new HashMap<>());
    }

    public ActionIcon(@NotNull IconDisplay iconDisplay, @NotNull Map<ClickType, Action> actions) {
        super(iconDisplay);
        this.actions = actions != null ? new ConcurrentHashMap<>(actions) : new ConcurrentHashMap<>();
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
        runActions(event, this.actions);
        return this;
    }

    public @NotNull Map<String, String> textReplaceMap() {
        return textReplaceMap;
    }

    public void setTextReplaceMap(@NotNull Map<String, String> textReplaceMap) {
        this.textReplaceMap = textReplaceMap;
    }

    public void runActions(@NotNull InventoryClickEvent event, @NotNull Map<ClickType, Action> actionsMap) {
        ClickType click = event.getClick();
        Action action = actionsMap.get(click);
        if (action != null) {
            action.run(((Player) event.getWhoClicked()), Craftorithm.instance(), null);
        }
    }

}
