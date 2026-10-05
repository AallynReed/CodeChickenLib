package codechicken.lib.model;

import codechicken.lib.util.TransformUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3fc;

/**
 * A simple model, with automatic handling of
 * {@link PerspectiveModelState}s.
 * <p>
 * Created by covers1624 on 9/7/22.
 *
 * @see TransformUtils
 */
public interface PerspectiveModel {

    /**
     * The {@link PerspectiveModelState} for this model.
     *
     * @return The state or {@code null} for vanilla behaviour.
     */
    @Nullable
    PerspectiveModelState getModelState();

    boolean useAmbientOcclusion();

    boolean isGui3d();

    boolean usesBlockLight();

    TextureAtlasSprite getParticleIcon();

    default void applyTransform(ItemDisplayContext context, PoseStack pStack) {
        PerspectiveModelState modelState = getModelState();
        if (modelState != null) {
            Transformation transform = getModelState().getTransform(context);

            Vector3fc trans = transform.translation();
            pStack.translate(trans.x(), trans.y(), trans.z());

            pStack.mulPose(transform.leftRotation());

            Vector3fc scale = transform.scale();
            pStack.scale(scale.x(), scale.y(), scale.z());

            pStack.mulPose(transform.rightRotation());
        }
    }
}
