package pers.yufiria.craftorithm.ui.icon;

import crypticlib.chat.BukkitTextProcessor;
import crypticlib.lang.LangManager;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class TranslatableIcon extends Icon {

    //用于替换文本内一些内容的map
    protected Map<String, String> textReplaceMap = new HashMap<>();

    public TranslatableIcon(@NotNull IconDisplay iconDisplay) {
        super(iconDisplay);
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

    public void setTextReplaceMap(@NotNull Map<String, String> textReplaceMap) {
        this.textReplaceMap = textReplaceMap;
    }

    public @NotNull Map<String, String> textReplaceMap() {
        return textReplaceMap;
    }
}
