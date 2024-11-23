package net.bagaja.colorinventory;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.components.Button;
import net.minecraft.util.Mth;

public class ColorPickerScreen extends Screen {
    private final Screen lastScreen;
    private ColorSlider redSlider;
    private ColorSlider greenSlider;
    private ColorSlider blueSlider;
    private int currentColor;
    private static final int DEFAULT_COLOR = 0xFF0000; // Default red color

    public ColorPickerScreen(Screen lastScreen) {
        super(Component.literal("Color Picker"));
        this.lastScreen = lastScreen;
        this.currentColor = ColorInventoryMod.getInventoryColor();
    }

    private class ColorSlider extends Button {
        private final String prefix;
        private double value;
        private final double min;
        private final double max;

        public ColorSlider(int x, int y, int width, int height, String prefix, double value, double min, double max) {
            super(Button.builder(Component.literal(""), button -> {})
                    .pos(x, y)
                    .size(width, height));
            this.prefix = prefix;
            this.value = value;
            this.min = min;
            this.max = max;
            updateMessage();
        }

        private void updateMessage() {
            setMessage(Component.literal(String.format("%s: %.0f", prefix, value)));
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            setValueFromMouse(mouseX);
        }

        @Override
        protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
            setValueFromMouse(mouseX);
        }

        private void setValueFromMouse(double mouseX) {
            double d0 = (mouseX - getX()) / (double)width;
            value = Mth.lerp(Mth.clamp(d0, 0.0D, 1.0D), min, max);
            updateMessage();
        }

        public double getValue() {
            return value;
        }

        public void setValue(double newValue) {
            this.value = Mth.clamp(newValue, min, max);
            updateMessage();
        }
    }

    @Override
    protected void init() {
        int sliderWidth = 200;
        int centerX = this.width / 2 - sliderWidth / 2;

        // Create RGB sliders using custom ColorSlider class
        this.redSlider = new ColorSlider(
                centerX, this.height / 2 - 40, sliderWidth, 20,
                "Red", (currentColor >> 16) & 0xFF, 0, 255
        );

        this.greenSlider = new ColorSlider(
                centerX, this.height / 2, sliderWidth, 20,
                "Green", (currentColor >> 8) & 0xFF, 0, 255
        );

        this.blueSlider = new ColorSlider(
                centerX, this.height / 2 + 40, sliderWidth, 20,
                "Blue", currentColor & 0xFF, 0, 255
        );

        this.addRenderableWidget(redSlider);
        this.addRenderableWidget(greenSlider);
        this.addRenderableWidget(blueSlider);

        // Center the buttons
        int buttonWidth = 98;
        int buttonSpacing = 8;
        int totalButtonsWidth = (buttonWidth * 3) + (buttonSpacing * 2);
        int buttonsStartX = this.width / 2 - totalButtonsWidth / 2;

        // Apply button
        this.addRenderableWidget(Button.builder(Component.literal("Apply"), button -> {
            updateColor();
            ColorInventoryMod.setInventoryColor(currentColor);
            this.minecraft.setScreen(lastScreen);
        }).pos(buttonsStartX, this.height / 2 + 80).size(buttonWidth, 20).build());

        // Reset button
        this.addRenderableWidget(Button.builder(Component.literal("Reset"), button -> {
            resetToDefault();
        }).pos(buttonsStartX + buttonWidth + buttonSpacing, this.height / 2 + 80).size(buttonWidth, 20).build());

        // Cancel button
        this.addRenderableWidget(Button.builder(Component.literal("Cancel"), button -> {
            this.minecraft.setScreen(lastScreen);
        }).pos(buttonsStartX + (buttonWidth + buttonSpacing) * 2, this.height / 2 + 80).size(buttonWidth, 20).build());
    }

    private void resetToDefault() {
        redSlider.setValue((DEFAULT_COLOR >> 16) & 0xFF);
        greenSlider.setValue((DEFAULT_COLOR >> 8) & 0xFF);
        blueSlider.setValue(DEFAULT_COLOR & 0xFF);
        currentColor = DEFAULT_COLOR;
    }

    private void updateColor() {
        int r = (int) redSlider.getValue();
        int g = (int) greenSlider.getValue();
        int b = (int) blueSlider.getValue();
        currentColor = (r << 16) | (g << 8) | b;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics, mouseX, mouseY, partialTicks);
        super.render(graphics, mouseX, mouseY, partialTicks);

        // Draw title
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);

        // Update color before preview
        updateColor();

        // Draw color preview
        int previewX = this.width / 2 - 50;
        int previewY = this.height / 2 - 90;

        // Draw white border
        graphics.fill(previewX - 1, previewY - 1, previewX + 101, previewY + 31, 0xFFFFFFFF);

        // Draw the current color with full opacity
        int colorWithAlpha = 0xFF000000 | currentColor; // Force full opacity
        graphics.fill(previewX, previewY, previewX + 100, previewY + 30, colorWithAlpha);
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }
}