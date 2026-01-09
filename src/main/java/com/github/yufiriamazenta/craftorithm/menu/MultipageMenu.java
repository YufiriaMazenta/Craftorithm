package com.github.yufiriamazenta.craftorithm.menu;

import crypticlib.ui.display.Icon;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.display.MenuLayout;
import crypticlib.ui.menu.Menu;
import crypticlib.ui.menu.Multipage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

public class MultipageMenu extends Menu implements Multipage {

    protected List<Icon> elements = new CopyOnWriteArrayList<>();
    protected Integer page = 0;
    protected Integer maxPage;
    protected Integer maxElementNumPerPage;
    protected Character elementKey;
    protected List<Integer> elementSlots = new ArrayList<>();

    public MultipageMenu(@NotNull Player player) {
        this(player, new MenuDisplay());
    }

    public MultipageMenu(@NotNull Player player, @NotNull Supplier<MenuDisplay> displaySupplier) {
        this(player, displaySupplier.get(), null, new ArrayList<>());
    }

    public MultipageMenu(@NotNull Player player, @NotNull Supplier<MenuDisplay> displaySupplier, Character elementKey, Supplier<List<Icon>> elementsSupplier) {
        this(player, displaySupplier.get(), elementKey, elementsSupplier.get());
    }

    public MultipageMenu(@NotNull Player player, @NotNull Supplier<MenuDisplay> displaySupplier, Character elementKey, List<Icon> elements) {
        this(player, displaySupplier.get(), elementKey, elements);
    }

    public MultipageMenu(@NotNull Player player, @NotNull MenuDisplay menuDisplay) {
        this(player, menuDisplay, null, new ArrayList<>());
    }

    public MultipageMenu(@NotNull Player player, @NotNull MenuDisplay display, Character elementKey, Supplier<List<Icon>> elementsSupplier) {
        this(player, display, elementKey, elementsSupplier.get());
    }

    public MultipageMenu(@NotNull Player player, @NotNull MenuDisplay display, Character elementKey, List<Icon> elements) {
        super(player, display);
        this.elementKey = elementKey;
        if (elements != null)
            this.elements.addAll(elements);
    }

    @Override
    public void updateLayout() {
        slotMap.clear();
        layoutSlotMap.clear();
        elementSlots.clear();

        //解析除了自动生成图标以外的所有图标
        MenuLayout layout = display.layout();
        for (int x = 0; x < layout.layout().size(); x++) {
            String line = layout.layout().get(x);
            for (int y = 0; y < Math.min(line.length(), 9); y++) {
                Character key = line.charAt(y);
                int slot = x * 9 + y;
                if (key.equals(elementKey)) {
                    elementSlots.add(slot);
                    continue;
                }
                if (!layout.layoutMap().containsKey(key)) {
                    continue;
                }
                if (layoutSlotMap.get(key) == null) {
                    layoutSlotMap.put(key, new ArrayList<>(Collections.singletonList(slot)));
                } else {
                    layoutSlotMap.get(key).add(slot);
                }
                slotMap.put(slot, layout.layoutMap().get(key).get());
            }
        }

        parseElements();
    }

    protected void updateMaxPage() {
        maxElementNumPerPage = elementSlots.size();
        if (maxElementNumPerPage == 0) {
            maxPage = 1;
            return;
        }
        if (elements.size() % maxElementNumPerPage == 0)
            maxPage = elements.size() / maxElementNumPerPage;
        else
            maxPage = elements.size() / maxElementNumPerPage + 1;
    }

    //解析自动生成图标
    protected void parseElements() {
        updateMaxPage();
        for (Integer slot : elementSlots) {
            slotMap.remove(slot);
        }
        int start = page * maxElementNumPerPage;
        List<Icon> pageElements = elements.subList(start, Math.min(elements.size(), start + maxElementNumPerPage));
        for (int i = 0; i < elementSlots.size() && i < pageElements.size(); i++) {
            int slot = elementSlots.get(i);
            slotMap.put(slot, pageElements.get(i));
        }
    }

    @Override
    public void nextPage() {
        page(page + 1);
    }

    @Override
    public void previousPage() {
        page(page - 1);
    }

    @Override
    public Integer page() {
        return page;
    }

    @Override
    public void page(int page) {
        if (page < 0 || page >= maxPage)
            return;
        this.page = page;
        parseElements();
        updateMenuIcons();
    }

    @Override
    public Integer maxPage() {
        return maxPage;
    }

    public List<Icon> elements() {
        return new ArrayList<>(elements);
    }

    public MultipageMenu setElements(List<Icon> elements) {
        this.elements.clear();
        this.elements.addAll(elements);
        parseElements();
        updateMenuIcons();
        return this;
    }

    public Character elementKey() {
        return elementKey;
    }

    public MultipageMenu setElementKey(Character elementKey) {
        this.elementKey = elementKey;
        return this;
    }

    @Override
    public MultipageMenu openMenu() {
        return (MultipageMenu) super.openMenu();
    }

    @Override
    public MultipageMenu setDisplay(@NotNull MenuDisplay display) {
        return (MultipageMenu) super.setDisplay(display);
    }

}