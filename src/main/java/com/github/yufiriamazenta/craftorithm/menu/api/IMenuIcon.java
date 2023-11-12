package com.github.yufiriamazenta.craftorithm.menu.api;

/**
 * 菜单图标
 * @param <T> 图标展示元素的类型
 * @param <C> 图标点击的事件
 */
public interface IMenuIcon<T, C> {

    Object getValue(String key);

    void setValue(String name, Object value);

    void onClick(C clickEvent);

    T getDisplay();

    void setDisplay(T display);

}
