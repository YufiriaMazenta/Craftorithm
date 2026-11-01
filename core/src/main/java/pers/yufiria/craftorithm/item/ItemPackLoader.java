package pers.yufiria.craftorithm.item;

import pers.yufiria.craftorithm.item.exception.ItemNotFoundException;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

/** Resolves one generation of item packs without consulting the previous live registry. */
final class ItemPackLoader {

    private final Map<String, List<String>> definitions;
    private final Map<String, ItemPack> resolved = new LinkedHashMap<>();
    private final Map<String, RuntimeException> failed = new HashMap<>();
    private final Set<String> resolving = new LinkedHashSet<>();

    private ItemPackLoader(Map<String, List<String>> definitions) {
        this.definitions = definitions;
    }

    static Map<String, ItemPack> loadAll(
        Map<String, List<String>> definitions,
        BiConsumer<String, Throwable> reportFailure
    ) {
        ItemPackLoader loader = new ItemPackLoader(definitions);
        for (String packId : definitions.keySet()) {
            try {
                loader.resolve(packId);
            } catch (RuntimeException exception) {
                // A bad definition also invalidates packs depending on it, but not unrelated packs.
                reportFailure.accept(packId, exception);
            }
        }
        return loader.resolved;
    }

    private ItemPack resolve(String packId) {
        ItemPack existing = resolved.get(packId);
        if (existing != null) {
            return existing;
        }
        RuntimeException previousFailure = failed.get(packId);
        if (previousFailure != null) {
            throw previousFailure;
        }
        List<String> definition = definitions.get(packId);
        if (definition == null) {
            throw new ItemNotFoundException("Unknown item pack reference: " + packId);
        }
        if (!resolving.add(packId)) {
            throw new ItemNotFoundException("Circular item pack reference: "
                + String.join(" -> ", resolving) + " -> " + packId);
        }
        try {
            ItemPack pack = new ItemPack(packId, definition, this::resolve);
            resolved.put(packId, pack);
            return pack;
        } catch (RuntimeException exception) {
            failed.put(packId, exception);
            throw exception;
        } finally {
            resolving.remove(packId);
        }
    }
}
