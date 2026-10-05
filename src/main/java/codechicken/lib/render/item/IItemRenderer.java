package codechicken.lib.render.item;

import codechicken.lib.model.PerspectiveModel;
import codechicken.lib.texture.TextureUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface IItemRenderer extends PerspectiveModel {

    /**
     * Called to render your item with complete control. Bypasses all vanilla rendering of your model.
     *
     * @param stack         The {@link ItemStack} being rendered.
     * @param ctx           The {@link ItemDisplayContext} of where we are rendering.
     * @param mStack        The {@link PoseStack} to get / add transformations to.
     * @param collector     The {@link SubmitNodeCollector} to submit geometry to.
     * @param packedLight   The {@link LightCoordsUtil} packed coords.
     * @param packedOverlay The {@link OverlayTexture} packed coords.
     */
    void renderItem(ItemStack stack, ItemDisplayContext ctx, PoseStack mStack, SubmitNodeCollector collector, int packedLight, int packedOverlay);

    /**
     * Called before {@link #renderItem} with the level and entity the stack is rendered for.
     * Replaces resolving through {@code ItemOverrides}, which Minecraft removed.
     */
    default void resolve(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity) {
    }

    @Override
    default TextureAtlasSprite getParticleIcon() {
        return TextureUtils.getMissingSprite();
    }
}
