package pers.yufiria.craftorithm.script.func;

/**
 * 脚本函数模块接口
 * 用于将相关函数组织为模块，批量注册
 */
public interface ScriptModule {

    /**
     * 模块名称
     */
    String moduleName();

    /**
     * 将本模块中的所有函数注册到注册中心
     * @param registry 函数注册中心
     */
    void register(ScriptFunctionRegistry registry);
}
