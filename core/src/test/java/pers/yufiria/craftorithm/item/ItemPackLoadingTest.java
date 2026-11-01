package pers.yufiria.craftorithm.item;

import crypticlib.CrypticLib;
import crypticlib.config.BukkitConfigWrapper;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ItemPackLoadingTest {

    private MockedStatic<CrypticLib> logger;
    private Object previousConfig;
    private Map<String, ItemPack> previousPacks;
    private final List<String> errors = new ArrayList<>();

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() throws Exception {
        logger = mockStatic(CrypticLib.class);
        logger.when(() -> CrypticLib.error(any(String.class), any(Throwable.class)))
            .thenAnswer(call -> { errors.add(call.getArgument(0)); return null; });
        previousConfig = configField().get(ItemManager.INSTANCE);
        previousPacks = new LinkedHashMap<>((Map<String, ItemPack>) packsField().get(ItemManager.INSTANCE));
        ItemProvider provider = mock(ItemProvider.class);
        when(provider.namespace()).thenReturn("pack_test");
        when(provider.matchItem(any(String.class))).thenAnswer(call -> {
            ItemStack item = mock(ItemStack.class);
            when(item.getAmount()).thenReturn(1);
            when(item.clone()).thenReturn(item);
            return item;
        });
        ItemManager.INSTANCE.regItemProvider(provider);
    }

    @AfterEach
    @SuppressWarnings("unchecked")
    void tearDown() throws Exception {
        try {
            configField().set(ItemManager.INSTANCE, previousConfig);
            Map<String, ItemPack> packs = (Map<String, ItemPack>) packsField().get(ItemManager.INSTANCE);
            packs.clear();
            packs.putAll(previousPacks);
            ItemManager.INSTANCE.removeItemProvider("pack_test");
        } finally {
            logger.close();
        }
    }

    @Test
    void resolvesForwardReferencesWithoutDependingOnYamlOrder() throws Exception {
        Map<String, List<String>> definitions = new LinkedHashMap<>();
        definitions.put("combined", List.of("item_pack:base"));
        definitions.put("base", List.of("pack_test:diamond"));
        reload(definitions);
        assertIds("combined", "pack_test:diamond");
        assertTrue(errors.isEmpty());
    }

    @Test
    void preservesOrderDuplicatesAndIgnoresQuantitySuffixesAcrossNestedReferences() throws Exception {
        Map<String, List<String>> definitions = new LinkedHashMap<>();
        definitions.put("all", List.of("pack_test:first 9", "item_pack:middle 2", "pack_test:last"));
        definitions.put("middle", List.of("item_pack:base", "pack_test:diamond"));
        definitions.put("base", List.of("pack_test:diamond 64"));
        reload(definitions);
        assertIds("all", "pack_test:first", "pack_test:diamond", "pack_test:diamond", "pack_test:last");
    }

    @Test
    void missingReferenceRejectsOnlyAffectedPacksAndReportsIt() throws Exception {
        reload(Map.of(
            "broken", List.of("pack_test:first", "item_pack:missing"),
            "independent", List.of("pack_test:diamond")
        ));
        assertNull(ItemManager.INSTANCE.getItemPack("broken"));
        assertIds("independent", "pack_test:diamond");
        assertTrue(errors.stream().anyMatch(message -> message.contains("broken")));
    }

    @Test
    void cycleAndItsDependentsFailWithoutDiscardingIndependentPacks() throws Exception {
        Map<String, List<String>> definitions = new LinkedHashMap<>();
        definitions.put("dependent", List.of("item_pack:a"));
        definitions.put("a", List.of("item_pack:b"));
        definitions.put("b", List.of("item_pack:a"));
        definitions.put("independent", List.of("pack_test:diamond"));
        reload(definitions);
        assertNull(ItemManager.INSTANCE.getItemPack("a"));
        assertNull(ItemManager.INSTANCE.getItemPack("b"));
        assertNull(ItemManager.INSTANCE.getItemPack("dependent"));
        assertIds("independent", "pack_test:diamond");
        assertEquals(3, errors.size());
    }

    @Test
    void selfReferenceFailsInsteadOfSilentlyCreatingAnEmptyPack() throws Exception {
        reload(Map.of("self", List.of("item_pack:self")));
        assertNull(ItemManager.INSTANCE.getItemPack("self"));
        assertEquals(1, errors.size());
    }

    @Test
    void reloadDoesNotResolveReferencesAgainstPreviousGeneration() throws Exception {
        reload(Map.of("base", List.of("pack_test:old")));
        Map<String, List<String>> definitions = new LinkedHashMap<>();
        definitions.put("combined", List.of("item_pack:base"));
        definitions.put("base", List.of("pack_test:new"));
        reload(definitions);
        assertIds("combined", "pack_test:new");
    }

    @Test
    void directConstructionReportsMissingReferenceInsteadOfDroppingIt() {
        assertThrows(pers.yufiria.craftorithm.item.exception.ItemNotFoundException.class,
            () -> new ItemPack("broken", List.of("item_pack:missing"), id -> null));
    }

    @Test
    void existingBackwardReferencesStillWork() throws Exception {
        Map<String, List<String>> definitions = new LinkedHashMap<>();
        definitions.put("base", List.of("pack_test:diamond"));
        definitions.put("combined", List.of("item_pack:base"));
        reload(definitions);
        assertIds("combined", "pack_test:diamond");
    }

    private void reload(Map<String, List<String>> definitions) throws Exception {
        YamlConfiguration yaml = mock(YamlConfiguration.class);
        when(yaml.getKeys(false)).thenReturn(new LinkedHashSet<>(definitions.keySet()));
        definitions.forEach((key, value) -> when(yaml.getStringList(key)).thenReturn(value));
        BukkitConfigWrapper wrapper = mock(BukkitConfigWrapper.class);
        when(wrapper.config()).thenReturn(yaml);
        configField().set(ItemManager.INSTANCE, wrapper);
        Method reload = ItemManager.class.getDeclaredMethod("reloadItemPacks");
        reload.setAccessible(true);
        reload.invoke(ItemManager.INSTANCE);
    }

    private void assertIds(String packId, String... ids) {
        ItemPack pack = ItemManager.INSTANCE.getItemPack(packId);
        assertNotNull(pack, "Expected pack " + packId);
        assertEquals(List.of(ids), pack.itemIds().stream().map(Object::toString).toList());
    }

    private static Field configField() throws Exception {
        Field field = ItemManager.class.getDeclaredField("itemPacksConfig");
        field.setAccessible(true);
        return field;
    }

    private static Field packsField() throws Exception {
        Field field = ItemManager.class.getDeclaredField("itemPacks");
        field.setAccessible(true);
        return field;
    }
}
