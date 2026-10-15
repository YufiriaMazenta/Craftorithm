package pers.yufiria.craftorithm.ui;

import crypticlib.CrypticLibBukkit;
import crypticlib.chat.BukkitTextProcessor;
import crypticlib.conversation.Conversation;
import crypticlib.conversation.NumberPrompt;
import crypticlib.conversation.Prompt;
import crypticlib.lang.node.StringLang;
import crypticlib.ui.menu.Menu;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;
import pers.yufiria.craftorithm.Craftorithm;

import java.util.Map;
import java.util.function.Consumer;

/**
 * 数字输入对话的公共逻辑
 */
public final class ConversationUtils {

    private ConversationUtils() {}

    /**
     * 发起一次数字输入对话, 对话结束后刷新并重新打开菜单。
     * 输入合法数字时会回调 {@code onInput}, 非法输入由对话框架处理。
     * @param player 进行输入的玩家
     * @param menu 对话结束后需要重新打开的菜单
     * @param hint 输入提示的语言节点
     * @param onInput 输入合法数字后的回调
     */
    public static void startNumberInput(
        Player player,
        Menu menu,
        StringLang hint,
        Consumer<Number> onInput
    ) {
        new Conversation(Craftorithm.instance(), player,
            new NumberPrompt() {
                @Override
                public Prompt acceptValidatedInput(Map<Object, Object> conversationData, Number input) {
                    onInput.accept(input);
                    return null; // 结束对话
                }

                @Override
                public BaseComponent promptText(Map<Object, Object> conversationData) {
                    return BukkitTextProcessor.toComponent(
                        BukkitTextProcessor.color(hint.value(player))
                    );
                }
            },
            data -> {
                // 对话结束后重新打开菜单
                menu.updateMenu();
                CrypticLibBukkit.scheduler().sync(() -> menu.openMenu());
            }
        ).start();
    }

}
