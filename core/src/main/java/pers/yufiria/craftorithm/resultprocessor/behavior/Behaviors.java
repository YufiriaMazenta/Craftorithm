package pers.yufiria.craftorithm.resultprocessor.behavior;

/**
 * 内置行为标识。
 * <p>
 * 行为名是普通字符串, 第三方注册自己的行为时不需要命名空间。
 */
public final class Behaviors {

    /**
     * 从源物品复制该字段, 源物品没有该字段时跳过
     */
    public static final String COPY_FROM_SOURCE = "copy_from_source";

    /**
     * 合并源物品的字段, 保留结果物品已有内容
     */
    public static final String MERGE_SOURCE = "merge_source";

    /**
     * 写入配置里的值
     */
    public static final String ADD = "add";

    /**
     * 清空后写入配置里的值
     */
    public static final String SET = "set";

    /**
     * 清除该字段
     */
    public static final String REMOVE = "remove";

    public static String qualify(String componentName, String behaviorName) {
        return componentName + "/" + behaviorName;
    }

}
