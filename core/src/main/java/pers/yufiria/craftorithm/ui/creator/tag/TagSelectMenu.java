package pers.yufiria.craftorithm.ui.creator.tag;

import crypticlib.CrypticLibBukkit;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.display.MenuLayout;
import crypticlib.ui.menu.Multipage;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.config.menu.creator.TagSelectConfig;
import pers.yufiria.craftorithm.ui.TranslatableMenu;
import pers.yufiria.craftorithm.ui.creator.CreatorIconParser;
import pers.yufiria.craftorithm.ui.creator.RecipeCreator;
import pers.yufiria.craftorithm.ui.icon.TranslatableIcon;

import java.util.*;
import java.util.function.Supplier;

/**
 * 材料Tag选择菜单
 * 展示所有原版材料Tag，玩家点击选择后返回配方创建器
 */
public class TagSelectMenu extends TranslatableMenu implements Multipage {

    private Tag<Material> selectedTag;
    private final RecipeCreator parentMenu;
    private int page = 0;
    private final List<Tag<Material>> TAGS = Tags.INSTANCE.vanillaTags();
    private final int maxPage;

    // 每页展示的tag数量 (5行 * 7列 = 35)
    private static final int TAGS_PER_PAGE = 35;

    public TagSelectMenu(@NotNull RecipeCreator parentMenu) {
        super(Objects.requireNonNull(parentMenu.player()));
        this.parentMenu = parentMenu;
        this.maxPage = Math.max(1, (TAGS.size() + TAGS_PER_PAGE - 1) / TAGS_PER_PAGE);
        this.display = new MenuDisplay(
            TagSelectConfig.TITLE.value(),
            new MenuLayout(
                List.of(
                    "---------",
                    "---------",
                    "---------",
                    "---------",
                    "---------",
                    "##<###>##"
                ),
                () -> {
                    Map<Character, Supplier<Icon>> iconMap = new HashMap<>();
                    iconMap.put('<', () -> new TranslatableIcon(
                        CreatorIconParser.INSTANCE.parseIconDisplay(
                            TagSelectConfig.PREVIOUS_ICON.value()
                        )
                    ) {
                        @Override
                        public Icon onClick(InventoryClickEvent event) {
                            previousPage();
                            return this;
                        }
                    });
                    iconMap.put('>', () -> new TranslatableIcon(
                        CreatorIconParser.INSTANCE.parseIconDisplay(
                            TagSelectConfig.NEXT_ICON.value()
                        )
                    ) {
                        @Override
                        public Icon onClick(InventoryClickEvent event) {
                            nextPage();
                            return this;
                        }
                    });
                    iconMap.put('#', CreatorIconParser.INSTANCE.parse(TagSelectConfig.FRAME_ICON.value()));
                    iconMap.put('-', this::createTagIcon);
                    return iconMap;
                }
            )
        );
    }

    /**
     * 为 '-' 位置创建Tag图标
     * 根据当前页面和槽位计算对应的Tag
     */
    private Icon createTagIcon() {
        return new TagIcon(new IconDisplay(Material.PAPER)) {
            @Override
            public ItemStack display() {
                Tag<Material> tag = getTagForSlot(this);
                if (tag == null) {
                    return new ItemStack(Material.AIR);
                }
                ItemStack item = new ItemStack(Material.PAPER);
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    // 使用tag的key作为显示名
                    String tagName = tag.getKey().toString();
                    meta.setDisplayName("§e" + tagName);
                    // 显示包含的材料数量
                    meta.setLore(List.of(
                        "§7材料数量: §f" + tag.getValues().size(),
                        "§a点击选择此Tag"
                    ));
                    item.setItemMeta(meta);
                }
                return item;
            }

            @Override
            public Icon onClick(InventoryClickEvent event) {
                Tag<Material> tag = getTagForSlot(this);
                if (tag == null) {
                    return this;
                }
                selectedTag = tag;
                event.getWhoClicked().closeInventory();
                return this;
            }
        };
    }

    /**
     * 根据TagIcon实例获取其对应的Tag
     * 通过布局槽位 '-' 的位置计算出对应的Tag索引
     */
    private Tag<Material> getTagForSlot(TagIcon icon) {
        // 获取 '-' 字符对应的所有槽位
        List<Integer> slots = getSlots('-');
        if (slots == null) return null;

        // 找到此icon在slotMap中的位置
        int iconSlot = -1;
        for (Map.Entry<Integer, Icon> entry : slotMap().entrySet()) {
            if (entry.getValue() == icon) {
                iconSlot = entry.getKey();
                break;
            }
        }
        if (iconSlot == -1) return null;

        int indexInSlots = slots.indexOf(iconSlot);
        if (indexInSlots == -1) return null;

        int tagIndex = page * TAGS_PER_PAGE + indexInSlots;
        if (tagIndex < 0 || tagIndex >= TAGS.size()) return null;

        return TAGS.get(tagIndex);
    }

    @Override
    public void onOpen(org.bukkit.event.inventory.InventoryOpenEvent event) {
        super.onOpen(event);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        if (selectedTag != null) {
            // 将选中的Tag设置到配方创建器的材料中
            applyTagToIngredient(selectedTag);
        }
        // 返回配方创建器
        CrypticLibBukkit.scheduler().sync(parentMenu::openMenu);
    }

    /**
     * 将选中的Tag应用到配方创建器的材料中
     * 通过Bukkit的Tag API创建基于Tag的RecipeChoice
     */
    private void applyTagToIngredient(Tag<Material> tag) {
        // 将Tag对应的材料设置到配方创建器的存储物品中
        // 选择Tag中第一个材料作为代表物展示
        Material firstMaterial = tag.getValues().stream().findFirst().orElse(Material.STONE);
        ItemStack tagItem = new ItemStack(firstMaterial);
        ItemMeta meta = tagItem.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§e[Tag] " + tag.getKey().toString());
            tagItem.setItemMeta(meta);
        }
        // 将代表物品放到配方创建器的第一个空材料槽
        for (int slot : new int[]{10, 11, 12, 19, 20, 21, 28, 29, 30}) {
            ItemStack existing = parentMenu.storedItems().get(slot);
            if (existing == null || existing.getType() == Material.AIR) {
                parentMenu.storedItems().put(slot, tagItem);
                break;
            }
        }
    }

    @Override
    public void nextPage() {
        page = Math.min(page + 1, maxPage - 1);
        updateMenu();
    }

    @Override
    public void previousPage() {
        page = Math.max(page - 1, 0);
        updateMenu();
    }

    @Override
    public Integer page() {
        return page;
    }

    @Override
    public void page(int i) {
        if (i == this.page) return;
        this.page = Math.max(0, Math.min(i, maxPage - 1));
        updateMenu();
    }

    @Override
    public Integer maxPage() {
        return maxPage;
    }
}
