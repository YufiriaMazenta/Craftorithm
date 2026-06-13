package pers.yufiria.craftorithm.ui.creator.tag;

import com.destroystokyo.paper.MaterialSetTag;
import crypticlib.lifecycle.BukkitLifeCycleTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.LifeCycleTaskSettings;
import crypticlib.lifecycle.TaskRule;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@LifeCycleTaskSettings(
    rules = @TaskRule(lifeCycle = LifeCycle.LOAD)
)
public enum Tags implements BukkitLifeCycleTask {

    INSTANCE;

    private final List<Tag<Material>> vanillaTags = new ArrayList<>();

    @Override
    @SuppressWarnings("unchecked")
    public void lifecycle(Plugin plugin, LifeCycle lifeCycle) {
        vanillaTags.clear();
        Field[] fields = MaterialSetTag.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                if (!Modifier.isStatic(field.getModifiers())) continue;

                Object obj = field.get(null);
                if (!(obj instanceof Tag<?> tag)) continue;

                // 判断是否为 Material tag
                Set<?> values = tag.getValues();
                if (!values.isEmpty() && values.iterator().next() instanceof Material) {
                    vanillaTags.add((Tag<Material>) tag);
                }
            } catch (Throwable ignored) {}
        }
    }

    public @Unmodifiable List<Tag<Material>> vanillaTags() {
        return Collections.unmodifiableList(vanillaTags);
    }

}
