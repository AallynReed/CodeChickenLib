package codechicken.lib.datagen.recipe;

import net.minecraft.core.HolderGetter;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Recipe;

/**
 * Created by covers1624 on 28/12/20.
 */
public abstract class AbstractItemStackRecipeBuilder<T extends AbstractRecipeBuilder<ItemStackTemplate, T>> extends AbstractRecipeBuilder<ItemStackTemplate, T> {

    protected AbstractItemStackRecipeBuilder(Identifier id, HolderGetter<Item> items, ItemStackTemplate result) {
        super(id, items, result);
    }

    @Override
    public abstract Recipe<?> _build();
}
