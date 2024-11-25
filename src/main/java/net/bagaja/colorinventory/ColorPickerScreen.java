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
    private ColorSlider alphaSlider;  // New alpha slider
    private int currentColor;
    private float currentAlpha;  // Default alpha value
    private static final int DEFAULT_COLOR = 0xFF0000;
    private static final float DEFAULT_ALPHA = 0.7f;

    public ColorPickerScreen(Screen lastScreen) {
        super(Component.literal("Color Picker"));
        this.lastScreen = lastScreen;
        this.currentColor = ColorInventoryMod.getInventoryColor();
        this.currentAlpha = ColorInventoryMod.getInventoryAlpha();
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
            if (prefix.equals("Alpha")) {
                setMessage(Component.literal(String.format("%s: %.2f", prefix, value)));
            } else {
                setMessage(Component.literal(String.format("%s: %.0f", prefix, value)));
            }
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
                centerX, this.height / 2 - 45, sliderWidth, 20,
                "Red", (currentColor >> 16) & 0xFF, 0, 255
        );

        this.greenSlider = new ColorSlider(
                centerX, this.height / 2 - 15, sliderWidth, 20,
                "Green", (currentColor >> 8) & 0xFF, 0, 255
        );

        this.blueSlider = new ColorSlider(
                centerX, this.height / 2 + 15, sliderWidth, 20,
                "Blue", currentColor & 0xFF, 0, 255
        );

        this.alphaSlider = new ColorSlider(
                centerX, this.height / 2 + 45, sliderWidth, 20,
                "Alpha", currentAlpha, 0, 1
        );

        this.addRenderableWidget(redSlider);
        this.addRenderableWidget(greenSlider);
        this.addRenderableWidget(blueSlider);
        this.addRenderableWidget(alphaSlider);

        // Center the buttons
        int buttonWidth = 75;
        int buttonSpacing = 8;
        int totalButtonsWidth = (buttonWidth * 4) + (buttonSpacing * 3); // Adjusted for 4 buttons
        int buttonsStartX = this.width / 2 - totalButtonsWidth / 2;

        // Apply button
        this.addRenderableWidget(Button.builder(Component.literal("Apply"), button -> {
            updateColor();
            ColorInventoryMod.setInventoryColor(currentColor);
            ColorInventoryMod.setInventoryAlpha((float)alphaSlider.getValue());
            ColorInventoryMod.setOverlayEnabled(true);
            this.minecraft.setScreen(lastScreen);
        }).pos(buttonsStartX, this.height / 2 + 85).size(buttonWidth, 20).build());

        // Reset button
        this.addRenderableWidget(Button.builder(Component.literal("Reset"), button -> {
            resetToDefault();
        }).pos(buttonsStartX + buttonWidth + buttonSpacing, this.height / 2 + 85).size(buttonWidth, 20).build());

        // Toggle button
        this.addRenderableWidget(Button.builder(
                Component.literal(ColorInventoryMod.isOverlayEnabled() ? "Disable" : "Enable"),
                button -> {
                    ColorInventoryMod.toggleOverlay();
                    button.setMessage(Component.literal(ColorInventoryMod.isOverlayEnabled() ? "Disable" : "Enable"));
                }
        ).pos(buttonsStartX + (buttonWidth + buttonSpacing) * 2, this.height / 2 + 85).size(buttonWidth, 20).build());


        // Add the transparent inventory toggle button
        // Here was the transparency removed and is at the bottom of these file

        
        // Move the Cancel button to the end
        this.addRenderableWidget(Button.builder(Component.literal("Cancel"), button -> {
                    this.minecraft.setScreen(lastScreen);
                }).pos(buttonsStartX + (buttonWidth + buttonSpacing) * 3, this.height / 2 + 85)
                .size(buttonWidth, 20)
                .build());
    }

    private void resetToDefault() {
        redSlider.setValue((DEFAULT_COLOR >> 16) & 0xFF);
        greenSlider.setValue((DEFAULT_COLOR >> 8) & 0xFF);
        blueSlider.setValue(DEFAULT_COLOR & 0xFF);
        alphaSlider.setValue(DEFAULT_ALPHA);
        currentColor = DEFAULT_COLOR;
        currentAlpha = DEFAULT_ALPHA;
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
        int previewY = this.height / 2 - 110;

        // Draw white border
        graphics.fill(previewX - 1, previewY - 1, previewX + 101, previewY + 31, 0xFFFFFFFF);

        // Draw checkerboard pattern to show transparency
        drawCheckerboardPattern(graphics, previewX, previewY, 100, 30);

        // Draw the current color with current alpha
        int alpha = (int)(alphaSlider.getValue() * 255);
        int colorWithAlpha = (alpha << 24) | currentColor;
        graphics.fill(previewX, previewY, previewX + 100, previewY + 30, colorWithAlpha);
    }

    private void drawCheckerboardPattern(GuiGraphics graphics, int x, int y, int width, int height) {
        int squareSize = 8;
        boolean isWhite = true;

        for (int i = 0; i < width; i += squareSize) {
            for (int j = 0; j < height; j += squareSize) {
                int color = isWhite ? 0xFFFFFFFF : 0xFFCCCCCC;
                graphics.fill(x + i, y + j,
                        Math.min(x + i + squareSize, x + width),
                        Math.min(y + j + squareSize, y + height),
                        color);
                isWhite = !isWhite;
            }
            isWhite = !isWhite;
        }
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }
}

//   this.addRenderableWidget(Button.builder(
//                        Component.literal(ColorInventoryMod.isUsingTransparentInventory() ?
//                                "Solid Inv" : "Trans Inv"),
//                        button -> {
//                            boolean newState = !ColorInventoryMod.isUsingTransparentInventory();
//                            ColorInventoryMod.setUseTransparentInventory(newState);
//                            button.setMessage(Component.literal(newState ?
//                                    "Solid Inv" : "Trans Inv"));
//                        }
//                ).pos(buttonsStartX + (buttonWidth + buttonSpacing) * 3, this.height / 2 + 100)
//                .size(buttonWidth, 20)
//                .build());