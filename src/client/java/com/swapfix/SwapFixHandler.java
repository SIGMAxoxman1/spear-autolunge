package com.swapfix;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;

/**
 * المنطق الأساسي كله هنا.
 *
 * طول ما المود مفعّل (زرار التفعيل من Controls):
 * 1) لو ماسك زرار "سلوت الرجوع" (returnSlot من الإعدادات) وضغطت
 *    left click، بنبدل فورًا لـ "سلوت الاسبير" (aspectSlot) عشان
 *    الضربة تروح عليه.
 * 2) في نهاية نفس الـ tick، نرجع تلقائيًا لـ returnSlot - مضمون
 *    ومش معتمد على إعادة التحقق من حالة الزرار (عشان مشكلة الـ
 *    keyboard ghosting اللي بتحصل لما تضغط أزرار كتير مع بعض).
 */
public final class SwapFixHandler {

    private static KeyMapping enabledToggle;

    private static boolean returnSlotHeld = false;
    private static boolean pendingReturn = false;
    private static int pendingReturnIndex = -1;

    private SwapFixHandler() {}

    public static void setEnabledToggle(KeyMapping toggle) {
        enabledToggle = toggle;
    }

    private static boolean isEnabled() {
        return enabledToggle != null && enabledToggle.isDown();
    }

    /** بتتنادى من KeyboardMixin - بنتابع بس حالة زرار سلوت الرجوع. */
    public static void onRawKey(int action, KeyEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options == null) return;

        int returnIndex = SwapFixConfig.get().returnSlot - 1;
        KeyMapping[] hotbar = mc.options.keyHotbarSlots;
        if (hotbar == null || returnIndex < 0 || returnIndex >= hotbar.length) return;

        if (hotbar[returnIndex].matches(event)) {
            if (action == 1) { // GLFW_PRESS
                returnSlotHeld = true;
            } else if (action == 0) { // GLFW_RELEASE
                returnSlotHeld = false;
            }
        }
    }

    /** بتتنادى من MouseMixin - هنا بيحصل السواب الفعلي. */
    public static void onRawMouseButton(int button, int action) {
        if (button != 0 || action != 1) return; // زرار الماوس الشمال بس، وعند الضغط بس
        if (!isEnabled() || !returnSlotHeld) return;

        SwapFixConfig config = SwapFixConfig.get();
        int aspectIndex = config.aspectSlot - 1;
        int returnIndex = config.returnSlot - 1;

        switchSlot(aspectIndex);
        pendingReturn = true;
        pendingReturnIndex = returnIndex;
    }

    /** بتتنادى في نهاية كل client tick - الرجوع المضمون. */
    public static void onEndClientTick(Minecraft mc) {
        if (pendingReturn) {
            switchSlot(pendingReturnIndex);
            pendingReturn = false;
        }
    }

    private static void switchSlot(int index) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.getConnection() == null) return;
        if (index < 0 || index > 8) return;

        mc.player.getInventory().setSelectedSlot(index);
        mc.getConnection().send(new ServerboundSetCarriedItemPacket(index));
    }
}
