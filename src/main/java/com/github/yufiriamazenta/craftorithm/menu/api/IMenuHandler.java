package com.github.yufiriamazenta.craftorithm.menu.api;

import java.util.Map;

/**
 * 菜单事件的处理器
 * @param <T> Slot的类型
 * @param <C> 点击事件的类型
 * @param <I> 菜单图标的类型
 */
public interface IMenuHandler<T, C, I extends IMenuIcon<?, ?>> {

    Map<T, I> getMenuIconMap();

    void click(T slot, C clickEvent);

}
