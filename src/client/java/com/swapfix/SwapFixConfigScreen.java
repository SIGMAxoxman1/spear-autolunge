package com.swapfix;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class SwapFixConfigScreen extends Screen {

    private final Screen parent;
    private EditBox aspectSlotBox;
    private EditBox returnSlotBox;

    protected SwapFixConfigScreen(Screen parent) {
        super(Component.literal("Swap Fix - الإعدادات"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        SwapFixConfig config = SwapFixConfig.get();

        int centerX = this.width / 2;
        int boxWidth = 60;

        aspectSlotBox = new EditBox(this.font, centerX - boxWidth - 10, 80, boxWidth, 20,
                Component.literal("Aspect Slot"));
        aspectSlotBox.setValue(String.valueOf(config.aspectSlot));
        aspectSlotBox.setMaxLength(1);
        this.addRenderableWidget(aspectSlotBox);

        returnSlotBox = new EditBox(this.font, centerX + 10, 80, boxWidth, 20,
                Component.literal("Return Slot"));
        returnSlotBox.setValue(String.valueOf(config.returnSlot));
        returnSlotBox.setMaxLength(1);
        this.addRenderableWidget(returnSlotBox);

        this.addRenderableWidget(Button.builder(Component.literal("حفظ وإغلاق"), button -> {
            saveAndClose();
        }).bounds(centerX - 100, this.height - 40, 200, 20).build());
    }

    private void saveAndClose() {
        SwapFixConfig config = SwapFixConfig.get();
        config.aspectSlot = parseSlotOrDefault(aspectSlotBox.getValue(), config.aspectSlot);
        config.returnSlot = parseSlotOrDefault(returnSlotBox.getValue(), config.returnSlot);
        config.save();
        this.onClose();
    }

    private int parseSlotOrDefault(String text, int fallback) {
        try {
            int value = Integer.parseInt(text.trim());
            if (value >= 1 && value <= 9) {
                return value;
            }
        } catch (NumberFormatException ignored) {
            // نتجاهل ونرجع القيمة القديمة لو الرقم مش صحيح
        }
        return fallback;
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            this.minecraft.setScreen(parent);
        }
    }

    @Override
    public void render(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, "رقم سلوت الاسبير", this.width / 2 - 100, 65, 0xFFFFFF);
        guiGraphics.drawCenteredString(this.font, "رقم السلوت اللي ترجعله", this.width / 2 + 100, 65, 0xFFFFFF);
    }
}
