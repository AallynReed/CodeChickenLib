package codechicken.lib.gui.modular.lib;

import codechicken.lib.gui.modular.lib.geometry.Borders;
import codechicken.lib.gui.modular.lib.geometry.Rectangle;
import codechicken.lib.gui.modular.sprite.Material;
import codechicken.lib.gui.render.CCCustomRenderState;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Matrix4f;

import java.util.List;
import java.util.Optional;

/**
 * This class primarily based on GuiHelper from BrandonsCore
 * But its implementation is heavily inspired by the new GuiGraphics system in 1.20+
 * <p>
 * The purpose of this class is to provide most of the basic rendering functions required to render various GUI geometry.
 * This includes things like simple rectangles, textures, strings, etc.
 * <p>
 * On 26.1 it draws through {@link GuiGraphicsExtractor}. {@link #pose()} is still a {@link PoseStack}; its transform is
 * applied to every draw, without z (GUI layering now follows draw order).
 * <p>
 * Created by brandon3055 on 29/06/2023
 */
public class GuiRender {

    private final Minecraft mc;
    private final GuiGraphicsExtractor graphics;
    private final PoseStack pose;
    private Font fontOverride;

    public GuiRender(Minecraft mc, GuiGraphicsExtractor graphics, PoseStack poseStack) {
        this.mc = mc;
        this.graphics = graphics;
        this.pose = poseStack;
    }

    public GuiRender(Minecraft mc, GuiGraphicsExtractor graphics) {
        this(mc, graphics, new PoseStack());
    }

    public static GuiRender convert(GuiGraphicsExtractor graphics) {
        return new GuiRender(Minecraft.getInstance(), graphics);
    }

    public PoseStack pose() {
        return pose;
    }

    public GuiGraphicsExtractor guiGraphics() {
        return graphics;
    }

    public Minecraft mc() {
        return mc;
    }

    public Font font() {
        return fontOverride == null ? mc().font : fontOverride;
    }

    public int guiWidth() {
        return mc().getWindow().getGuiScaledWidth();
    }

    public int guiHeight() {
        return mc().getWindow().getGuiScaledHeight();
    }

    /**
     * Allows you to override the font renderer used for all text rendering.
     * Be sure to set the override back to null when you are finished using your custom font!
     *
     * @param font The font to use, or null to disable override.
     */
    public void overrideFont(@Nullable Font font) {
        this.fontOverride = font;
    }

    public void batchDraw(Runnable batch) {
        batch.run();
    }

    /**
     * GUI geometry is collected and drawn by Minecraft at the end of the frame, so there is nothing to flush.
     */
    public void flush() {
    }

    private void draw(Runnable draw) {
        Matrix4f m = pose.last().pose();
        graphics.pose().pushMatrix();
        graphics.pose().mul(new Matrix3x2f(m.m00(), m.m01(), m.m10(), m.m11(), m.m30(), m.m31()));
        draw.run();
        graphics.pose().popMatrix();
    }

    /**
     * Submits custom geometry, transformed by {@link #pose()}. This replaces drawing into {@code buffers()},
     * which 26.1 GUIs no longer have.
     */
    public void submitCustom(RenderPipeline pipeline, TextureSetup texture, double x0, double x1, double y0, double y1, CCCustomRenderState.VertBuilder builder) {
        draw(() -> graphics.cc$submitCustom(pipeline, texture, x0, x1, y0, y1, builder));
    }

    //=== Un-Textured geometry ===//

    public void rect(Rectangle rectangle, int colour) {
        rect(rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height(), colour);
    }

    public void rect(double x, double y, double width, double height, int colour) {
        fill(x, y, x + width, y + height, colour);
    }

    public void fill(double xMin, double yMin, double xMax, double yMax, int colour) {
        draw(() -> graphics.cc$fill(xMin, yMin, xMax, yMax, colour));
    }

    public void gradientFillV(double xMin, double yMin, double xMax, double yMax, int topColour, int bottomColour) {
        draw(() -> graphics.cc$fillGradientV(xMin, yMin, xMax, yMax, topColour, bottomColour));
    }

    public void gradientFillH(double xMin, double yMin, double xMax, double yMax, int leftColour, int rightColour) {
        draw(() -> graphics.cc$fillGradientH(xMin, yMin, xMax, yMax, leftColour, rightColour));
    }

    public void borderRect(Rectangle rectangle, double borderWidth, int fillColour, int borderColour) {
        borderRect(rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height(), borderWidth, fillColour, borderColour);
    }

    public void borderRect(double x, double y, double width, double height, double borderWidth, int fillColour, int borderColour) {
        draw(() -> graphics.cc$borderRect(x, y, width, height, borderWidth, fillColour, borderColour));
    }

    public void borderFill(double xMin, double yMin, double xMax, double yMax, double borderWidth, int fillColour, int borderColour) {
        draw(() -> graphics.cc$borderFill(xMin, yMin, xMax, yMax, borderWidth, fillColour, borderColour));
    }

    public void shadedRect(Rectangle rectangle, double borderWidth, int topLeftColour, int bottomRightColour, int fillColour) {
        shadedRect(rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height(), borderWidth, topLeftColour, bottomRightColour, fillColour);
    }

    public void shadedRect(double x, double y, double width, double height, double borderWidth, int topLeftColour, int bottomRightColour, int fillColour) {
        draw(() -> graphics.cc$shadedRect(x, y, width, height, borderWidth, topLeftColour, bottomRightColour, fillColour));
    }

    public void shadedRect(Rectangle rectangle, double borderWidth, int topLeftColour, int bottomRightColour, int cornerMixColour, int fillColour) {
        shadedRect(rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height(), borderWidth, topLeftColour, bottomRightColour, cornerMixColour, fillColour);
    }

    public void shadedRect(double x, double y, double width, double height, double borderWidth, int topLeftColour, int bottomRightColour, int cornerMixColour, int fillColour) {
        draw(() -> graphics.cc$shadedRect(x, y, width, height, borderWidth, topLeftColour, bottomRightColour, cornerMixColour, fillColour));
    }

    public void shadedFill(double xMin, double yMin, double xMax, double yMax, double borderWidth, int topLeftColour, int bottomRightColour, int fillColour) {
        draw(() -> graphics.cc$shadedFill(xMin, yMin, xMax, yMax, borderWidth, topLeftColour, bottomRightColour, fillColour));
    }

    public void shadedFill(double xMin, double yMin, double xMax, double yMax, double borderWidth, int topLeftColour, int bottomRightColour, int cornerMixColour, int fillColour) {
        draw(() -> graphics.cc$shadedFill(xMin, yMin, xMax, yMax, borderWidth, topLeftColour, bottomRightColour, cornerMixColour, fillColour));
    }

    public void toolTipBackground(double x, double y, double width, double height) {
        toolTipBackground(x, y, width, height, 0xF0100010, 0x505000FF, 0x5028007f);
    }

    public void toolTipBackground(double x, double y, double width, double height, int backgroundColour, int borderColourTop, int borderColourBottom) {
        toolTipBackground(x, y, width, height, backgroundColour, backgroundColour, borderColourTop, borderColourBottom, false);
    }

    public void toolTipBackground(double x, double y, double width, double height, int backgroundColourTop, int backgroundColourBottom, int borderColourTop, int borderColourBottom, boolean empty) {
        draw(() -> graphics.cc$tooltipBackground(x, y, width, height, backgroundColourTop, backgroundColourBottom, borderColourTop, borderColourBottom, empty));
    }

    //=== Sprites ===//

    public void partialSprite(double xMin, double yMin, double xMax, double yMax, TextureAtlasSprite sprite, float uMin, float vMin, float uMax, float vMax, int argb) {
        draw(() -> graphics.cc$blitPartialSprite(RenderPipelines.GUI_TEXTURED, xMin, yMin, xMax, yMax, sprite, uMin, vMin, uMax, vMax, argb));
    }

    public void partialSprite(double xMin, double yMin, double xMax, double yMax, TextureAtlasSprite sprite, float uMin, float vMin, float uMax, float vMax, float red, float green, float blue, float alpha) {
        partialSprite(xMin, yMin, xMax, yMax, sprite, uMin, vMin, uMax, vMax, ARGB.colorFromFloat(alpha, red, green, blue));
    }

    //=== Texture (Material) ===//

    public void texRect(Material material, Rectangle rectangle) {
        texRect(material, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height());
    }

    public void texRect(Material material, Rectangle rectangle, int argb) {
        texRect(material, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height(), argb);
    }

    public void texRect(Material material, Rectangle rectangle, float red, float green, float blue, float alpha) {
        texRect(material, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height(), red, green, blue, alpha);
    }

    public void texRect(Material material, double x, double y, double width, double height) {
        tex(material, x, y, x + width, y + height);
    }

    public void texRect(Material material, double x, double y, double width, double height, int argb) {
        tex(material, x, y, x + width, y + height, argb);
    }

    public void texRect(Material material, double x, double y, double width, double height, float red, float green, float blue, float alpha) {
        tex(material, x, y, x + width, y + height, red, green, blue, alpha);
    }

    /**
     * Draws a texture sprite derived from the provided material.
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     */
    public void tex(Material material, double xMin, double yMin, double xMax, double yMax) {
        tex(material, xMin, yMin, xMax, yMax, 0xFFFFFFFF);
    }

    public void tex(Material material, double xMin, double yMin, double xMax, double yMax, float red, float green, float blue, float alpha) {
        tex(material, xMin, yMin, xMax, yMax, ARGB.colorFromFloat(alpha, red, green, blue));
    }

    public void tex(Material material, double xMin, double yMin, double xMax, double yMax, int argb) {
        TextureAtlasSprite sprite = material.sprite();
        draw(() -> graphics.cc$blitSprite(RenderPipelines.GUI_TEXTURED, sprite, xMin, yMin, xMax - xMin, yMax - yMin, argb));
    }

    public void texRect(Material material, int rotation, Rectangle rectangle) {
        texRect(material, rotation, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height());
    }

    public void texRect(Material material, int rotation, Rectangle rectangle, int argb) {
        texRect(material, rotation, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height(), argb);
    }

    public void texRect(Material material, int rotation, Rectangle rectangle, float red, float green, float blue, float alpha) {
        texRect(material, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height(), rotation, red, green, blue, alpha);
    }

    public void texRect(Material material, int rotation, double x, double y, double width, double height) {
        tex(material, rotation, x, y, x + width, y + height);
    }

    public void texRect(Material material, int rotation, double x, double y, double width, double height, int argb) {
        tex(material, x, y, x + width, y + height, rotation, argb);
    }

    public void texRect(Material material, double x, double y, double width, double height, int rotation, float red, float green, float blue, float alpha) {
        tex(material, x, y, x + width, y + height, rotation, red, green, blue, alpha);
    }

    public void tex(Material material, int rotation, double xMin, double yMin, double xMax, double yMax) {
        tex(material, xMin, yMin, xMax, yMax, rotation, 0xFFFFFFFF);
    }

    public void tex(Material material, double xMin, double yMin, double xMax, double yMax, int rotation, float red, float green, float blue, float alpha) {
        tex(material, xMin, yMin, xMax, yMax, rotation, ARGB.colorFromFloat(alpha, red, green, blue));
    }

    public void tex(Material material, double xMin, double yMin, double xMax, double yMax, int rotation, int argb) {
        TextureAtlasSprite sprite = material.sprite();
        draw(() -> graphics.cc$blitSprite(RenderPipelines.GUI_TEXTURED, sprite, rotation, xMin, yMin, xMax - xMin, yMax - yMin, argb));
    }

    public void dynamicTex(Material material, Rectangle rectangle, Borders borders, int argb) {
        TextureAtlasSprite sprite = material.sprite();
        draw(() -> graphics.cc$blitDynamicSprite(RenderPipelines.GUI_TEXTURED, sprite, rectangle, borders, argb));
    }

    public void dynamicTex(Material material, Rectangle rectangle, int topBorder, int leftBorder, int bottomBorder, int rightBorder, int argb) {
        TextureAtlasSprite sprite = material.sprite();
        draw(() -> graphics.cc$blitDynamicSprite(RenderPipelines.GUI_TEXTURED, sprite, rectangle, topBorder, leftBorder, bottomBorder, rightBorder, argb));
    }

    public void dynamicTex(Material material, int x, int y, int width, int height, int topBorder, int leftBorder, int bottomBorder, int rightBorder, int argb) {
        TextureAtlasSprite sprite = material.sprite();
        draw(() -> graphics.cc$blitDynamicSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, width, height, topBorder, leftBorder, bottomBorder, rightBorder, argb));
    }

    public void dynamicTex(Material material, Rectangle rectangle, Borders borders) {
        dynamicTex(material, rectangle, borders, 0xFFFFFFFF);
    }

    public void dynamicTex(Material material, Rectangle rectangle, int topBorder, int leftBorder, int bottomBorder, int rightBorder) {
        dynamicTex(material, rectangle, topBorder, leftBorder, bottomBorder, rightBorder, 0xFFFFFFFF);
    }

    public void dynamicTex(Material material, int x, int y, int width, int height, int topBorder, int leftBorder, int bottomBorder, int rightBorder) {
        dynamicTex(material, x, y, width, height, topBorder, leftBorder, bottomBorder, rightBorder, 0xFFFFFFFF);
    }

    public void dynamicTex(Material material, int x, int y, int width, int height, int topBorder, int leftBorder, int bottomBorder, int rightBorder, float red, float green, float blue, float alpha) {
        dynamicTex(material, x, y, width, height, topBorder, leftBorder, bottomBorder, rightBorder, ARGB.colorFromFloat(alpha, red, green, blue));
    }

    //=== Strings ===//

    public int drawString(@Nullable String message, double x, double y, int colour) {
        return drawString(message, x, y, colour, true);
    }

    public int drawString(@Nullable String message, double x, double y, int colour, boolean shadow) {
        if (message == null) return 0;
        draw(() -> graphics.cc$drawString(font(), message, x, y, colour, shadow));
        return (int) x + font().width(message) + (shadow ? 1 : 0);
    }

    public int drawString(FormattedCharSequence message, double x, double y, int colour) {
        return drawString(message, x, y, colour, true);
    }

    public int drawString(FormattedCharSequence message, double x, double y, int colour, boolean shadow) {
        draw(() -> graphics.cc$drawString(font(), message, x, y, colour, shadow));
        return (int) x + font().width(message) + (shadow ? 1 : 0);
    }

    public int drawString(Component message, double x, double y, int colour) {
        return drawString(message, x, y, colour, true);
    }

    public int drawString(Component message, double x, double y, int colour, boolean shadow) {
        return drawString(message.getVisualOrderText(), x, y, colour, shadow);
    }

    public void drawWordWrap(FormattedText message, double x, double y, int width, int colour) {
        drawWordWrap(message, x, y, width, colour, false);
    }

    public void drawWordWrap(FormattedText message, double x, double y, int width, int colour, boolean shadow) {
        drawWordWrap(message, x, y, width, colour, shadow, font().lineHeight);
    }

    public void drawWordWrap(FormattedText message, double x, double y, int width, int colour, boolean shadow, double spacing) {
        for (FormattedCharSequence formattedcharsequence : font().split(message, width)) {
            drawString(formattedcharsequence, x, y, colour, shadow);
            y += spacing;
        }
    }

    public void drawCenteredString(String message, double x, double y, int colour) {
        drawCenteredString(message, x, y, colour, true);
    }

    public void drawCenteredString(String message, double x, double y, int colour, boolean shadow) {
        drawString(message, x - font().width(message) / 2D, y, colour, shadow);
    }

    public void drawCenteredString(Component message, double x, double y, int colour) {
        drawCenteredString(message, x, y, colour, true);
    }

    public void drawCenteredString(Component message, double x, double y, int colour, boolean shadow) {
        FormattedCharSequence formattedcharsequence = message.getVisualOrderText();
        drawString(formattedcharsequence, x - font().width(formattedcharsequence) / 2D, y, colour, shadow);
    }

    public void drawCenteredString(FormattedCharSequence message, double x, double y, int colour) {
        drawCenteredString(message, x, y, colour, true);
    }

    public void drawCenteredString(FormattedCharSequence message, double x, double y, int colour, boolean shadow) {
        drawString(message, x - font().width(message) / 2D, y, colour, shadow);
    }

    public void drawScrollingString(Component component, double x, double y, double xMax, int colour, boolean shadow) {
        drawScrollingString(component, x, y, xMax, colour, shadow, true);
    }

    public void drawScrollingString(Component component, double x, double y, double xMax, int colour, boolean shadow, boolean doScissor) {
        draw(() -> graphics.cc$drawScrollingString(font(), component, x, y, xMax, colour, shadow, doScissor));
    }

    //=== Tool Tips ===//

    public void renderTooltip(ItemStack stack, double mouseX, double mouseY) {
        graphics.setTooltipForNextFrame(font(), stack, (int) mouseX, (int) mouseY);
    }

    public void toolTipWithImage(List<Component> tooltips, Optional<TooltipComponent> tooltipImage, ItemStack stack, double mouseX, double mouseY) {
        graphics.setTooltipForNextFrame(font(), tooltips, tooltipImage, stack, (int) mouseX, (int) mouseY);
    }

    public void toolTipWithImage(List<Component> tooltip, Optional<TooltipComponent> tooltipImage, double mouseX, double mouseY) {
        graphics.setTooltipForNextFrame(font(), tooltip, tooltipImage, (int) mouseX, (int) mouseY);
    }

    public void renderTooltip(Component message, double mouseX, double mouseY) {
        graphics.setTooltipForNextFrame(font(), message, (int) mouseX, (int) mouseY);
    }

    public void componentTooltip(List<? extends FormattedText> tooltips, double mouseX, double mouseY, ItemStack stack) {
        graphics.setComponentTooltipForNextFrame(font(), tooltips, (int) mouseX, (int) mouseY, stack);
    }

    public void componentTooltip(List<? extends FormattedText> tooltips, double mouseX, double mouseY) {
        renderTooltip(Lists.transform(tooltips, Language.getInstance()::getVisualOrder), mouseX, mouseY);
    }

    public void renderTooltip(List<? extends FormattedCharSequence> tooltips, double mouseX, double mouseY) {
        graphics.setTooltipForNextFrame(font(), tooltips, (int) mouseX, (int) mouseY);
    }

    //=== Items ===//

    public void renderItem(ItemStack stack, double x, double y) {
        renderItem(stack, x, y, 16);
    }

    public void renderItem(ItemStack stack, double x, double y, double size) {
        renderItem(mc().player, mc().level, stack, x, y, size, 0);
    }

    public void renderItem(ItemStack stack, double x, double y, double size, int modelRand) {
        renderItem(mc().player, mc().level, stack, x, y, size, modelRand);
    }

    public void renderFakeItem(ItemStack stack, double x, double y) {
        renderFakeItem(stack, x, y, 16);
    }

    public void renderFakeItem(ItemStack stack, double x, double y, double size) {
        renderItem(null, mc().level, stack, x, y, size, 0);
    }

    public void renderItem(LivingEntity entity, ItemStack stack, double x, double y, int modelRand) {
        renderItem(entity, stack, x, y, 16, modelRand);
    }

    public void renderItem(LivingEntity entity, ItemStack stack, double x, double y, double size, int modelRand) {
        renderItem(entity, entity.level(), stack, x, y, size, modelRand);
    }

    public void renderItem(@Nullable LivingEntity entity, @Nullable Level level, ItemStack stack, double x, double y, double size, int modelRand) {
        if (stack.isEmpty()) return;
        draw(() -> {
            graphics.pose().pushMatrix();
            graphics.pose().translate((float) x, (float) y);
            graphics.pose().scale((float) size / 16F);
            if (entity == null) {
                graphics.fakeItem(stack, 0, 0, modelRand);
            } else {
                graphics.item(entity, stack, 0, 0, modelRand);
            }
            graphics.pose().popMatrix();
        });
    }

    public void renderItemDecorations(ItemStack stack, double x, double y) {
        renderItemDecorations(stack, x, y, 16);
    }

    public void renderItemDecorations(ItemStack stack, double x, double y, double size) {
        renderItemDecorations(stack, x, y, size, null);
    }

    public void renderItemDecorations(ItemStack stack, double x, double y, @Nullable String text) {
        renderItemDecorations(stack, x, y, 16, text);
    }

    public void renderItemDecorations(ItemStack stack, double x, double y, double size, @Nullable String text) {
        if (stack.isEmpty()) return;
        draw(() -> {
            graphics.pose().pushMatrix();
            graphics.pose().translate((float) x, (float) y);
            graphics.pose().scale((float) size / 16F);
            graphics.cc$renderItemDecorations(font(), stack, 0, 0, text);
            graphics.pose().popMatrix();
        });
    }

    //=== Render Utils ===//

    public void pushScissorRect(Rectangle rectangle) {
        pushScissorRect(rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height());
    }

    public void pushScissorRect(double x, double y, double width, double height) {
        pushScissor(x, y, x + width, y + height);
    }

    public void pushScissor(double xMin, double yMin, double xMax, double yMax) {
        draw(() -> graphics.cc$enableScissor(xMin, yMin, xMax, yMax));
    }

    public void popScissor() {
        graphics.cc$disableScissor();
    }

    //=== Static Utils ===//

    public static boolean isInRect(double minX, double minY, double width, double height, double testX, double testY) {
        return ((testX >= minX && testX < minX + width) && (testY >= minY && testY < minY + height));
    }

    public static boolean isInRect(int minX, int minY, int width, int height, double testX, double testY) {
        return ((testX >= minX && testX < minX + width) && (testY >= minY && testY < minY + height));
    }

    /**
     * Mixes the two input colours by adding up the R, G, B and A values of each input.
     */
    public static int mixColours(int colour1, int colour2) {
        return mixColours(colour1, colour2, false);
    }

    /**
     * Mixes the two input colours by adding up the R, G, B and A values of each input.
     *
     * @param subtract If true, subtract colour2 from colour1, otherwise add colour2 to colour1.
     */
    public static int mixColours(int colour1, int colour2, boolean subtract) {
        int alpha1 = colour1 >> 24 & 255;
        int alpha2 = colour2 >> 24 & 255;
        int red1 = colour1 >> 16 & 255;
        int red2 = colour2 >> 16 & 255;
        int green1 = colour1 >> 8 & 255;
        int green2 = colour2 >> 8 & 255;
        int blue1 = colour1 & 255;
        int blue2 = colour2 & 255;

        int alpha = Mth.clamp(alpha1 + (subtract ? -alpha2 : alpha2), 0, 255);
        int red = Mth.clamp(red1 + (subtract ? -red2 : red2), 0, 255);
        int green = Mth.clamp(green1 + (subtract ? -green2 : green2), 0, 255);
        int blue = Mth.clamp(blue1 + (subtract ? -blue2 : blue2), 0, 255);

        return (alpha & 0xFF) << 24 | (red & 0xFF) << 16 | (green & 0xFF) << 8 | blue & 0xFF;
    }

    /**
     * Returns a colour half-way between the two input colours.
     * The R, G, B and A channels are extracted from each input,
     * Then for each chanel, a midpoint is determined,
     * And a new colour is constructed based on the midpoint of each channel.
     */
    public static int midColour(int colour1, int colour2) {
        int alpha1 = colour1 >> 24 & 255;
        int alpha2 = colour2 >> 24 & 255;
        int red1 = colour1 >> 16 & 255;
        int red2 = colour2 >> 16 & 255;
        int green1 = colour1 >> 8 & 255;
        int green2 = colour2 >> 8 & 255;
        int blue1 = colour1 & 255;
        int blue2 = colour2 & 255;
        return (alpha2 + (alpha1 - alpha2) / 2 & 0xFF) << 24 | (red2 + (red1 - red2) / 2 & 0xFF) << 16 | (green2 + (green1 - green2) / 2 & 0xFF) << 8 | blue2 + (blue1 - blue2) / 2 & 0xFF;
    }
}
