package pers.yufiria.craftorithm.api;

public enum CraftorithmApi {

    INSTANCE;

    public ItemApi item() {
        return ItemApi.INSTANCE;
    }

    public RecipeApi recipe() {
        return RecipeApi.INSTANCE;
    }

    public MenuApi menu() {
        return MenuApi.INSTANCE;
    }

    public TriggerApi trigger() {
        return TriggerApi.INSTANCE;
    }

    /**
     * 结果处理器的组件/行为扩展入口
     */
    public ResultProcessorApi resultProcessor() {
        return ResultProcessorApi.INSTANCE;
    }

}
