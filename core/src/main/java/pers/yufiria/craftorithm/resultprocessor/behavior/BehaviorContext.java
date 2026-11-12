package pers.yufiria.craftorithm.resultprocessor.behavior;

import crypticlib.util.TriConsumer;
import org.bukkit.entity.Player;
import pers.yufiria.craftorithm.resultprocessor.ItemWrapper;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessor;

/**
 * 创建行为时的上下文。
 * <p>
 * 独立成接口, 后续增加方法(配方类型/物品/日志器等)时不破坏第三方实现。
 */
public interface BehaviorContext {

    /**
     * 当前行为所属的组件名
     */
    String componentName();

    /**
     * 当前行为的名字
     */
    String behaviorName();

    /**
     * 处理器名, 形如 {@code damage/add}
     */
    default String qualifiedName() {
        return Behaviors.qualify(componentName(), behaviorName());
    }

    /**
     * 由该行为生成一个处理器, 参数固定为(源物品, 结果物品, 玩家)
     * <p>
     * 两个物品都是 {@link ItemWrapper}, 源物品那个是只读的。
     */
    default ResultProcessor processor(TriConsumer<ItemWrapper, ItemWrapper, Player> action) {
        return BehaviorProcessors.of(qualifiedName(), action);
    }

    /**
     * 与 {@link #processor} 相同, 只是源物品不存在时(如工作台配方)直接跳过
     */
    default ResultProcessor requireSource(TriConsumer<ItemWrapper, ItemWrapper, Player> action) {
        return BehaviorProcessors.requireSource(qualifiedName(), action);
    }

}
