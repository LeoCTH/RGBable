package com.leocth.rgbable.client.gui;

import com.google.common.collect.Lists;
import com.leocth.rgbable.api.NetworkUtilities;
import com.leocth.rgbable.api.color.RgbColor3f;
import com.leocth.rgbable.common.screen.PaintbrushScreenHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.List;

public class PaintbrushScreen extends HandledScreen<PaintbrushScreenHandler> {

    public static final Identifier TEXTURE = new Identifier("rgbable:textures/gui/rgb_block.png");
    private final PaintbrushScreenHandler handler;
    private final List<SliderWidget> sliders = Lists.newArrayList();
    public RgbColor3f.Mutable color;

    public PaintbrushScreen(PaintbrushScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.handler = handler;
        this.backgroundWidth = 176;
        this.backgroundHeight = 91;
        color = RgbColor3f.fromRgb(handler.getRgb()).toMutable();
    }

    @Override
    protected void init() {
        super.init();
        color = RgbColor3f.fromRgb(handler.getRgb()).toMutable();

        int mid = width / 2;
        sliders.add(this.addButton(new SliderWidget(mid - 60, y + 15, 120, 20, LiteralText.EMPTY, color.getR()) {
            {
                updateMessage();
            }
            @Override
            protected void updateMessage() {
                setMessage(new TranslatableText(
                        "gui.rgbable.rgb_block.slider.r",
                        MathHelper.floor(color.getR() * 255f),
                        String.format("%.3f", color.getR())
                ));
            }

            @Override
            protected void applyValue() {
                PaintbrushScreen.this.color.setR((float)this.value);
                updateAndSyncValues();
            }
        }));
        sliders.add(this.addButton(new SliderWidget(mid - 60, y + 36, 120, 20, LiteralText.EMPTY, color.getG()) {
            {
                updateMessage();
            }
            @Override
            protected void updateMessage() {
                setMessage(new TranslatableText(
                        "gui.rgbable.rgb_block.slider.g",
                        MathHelper.floor(color.getG() * 255f),
                        String.format("%.3f", color.getG())
                ));
            }

            @Override
            protected void applyValue() {
                PaintbrushScreen.this.color.setG((float)this.value);
                updateAndSyncValues();
            }
        }));
        sliders.add(this.addButton(new SliderWidget(mid - 60, y + 57, 120, 20, LiteralText.EMPTY, color.getB()) {
            {
                updateMessage();
            }
            @Override
            protected void updateMessage() {
                setMessage(new TranslatableText(
                        "gui.rgbable.rgb_block.slider.b",
                        MathHelper.floor(color.getB() * 255f),
                        String.format("%.3f", color.getB())
                ));
            }

            @Override
            protected void applyValue() {
                PaintbrushScreen.this.color.setB((float)this.value);
                updateAndSyncValues();
            }
        }));
    }

    private void updateAndSyncValues() {
        int rgb = color.toPackedRgb();
        handler.setRgb(rgb);
        handler.sendContentUpdates();
        NetworkUtilities.sendRgbSyncScreenHandlerPacket(handler.syncId, rgb, 0);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 0.3F);
        assert this.client != null;
        this.client.getTextureManager().bindTexture(TEXTURE);
        this.drawTexture(matrices, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        this.textRenderer.draw(matrices, this.title, (float)this.titleX, (float)this.titleY, 0x404040);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        // functional programming hell yeah
        return sliders.stream().anyMatch(
                widget -> widget.isMouseOver(mouseX, mouseY) &&
                        widget.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
        ) || super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    /*

    private HsvColorPickerWidget colorPicker;

    public PaintbrushScreen(PaintbrushScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        colorPicker = this.addChild(new HsvColorPickerWidget(6, 6, 128, 3, 3));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        colorPicker.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {

    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {

    }

    */
}
