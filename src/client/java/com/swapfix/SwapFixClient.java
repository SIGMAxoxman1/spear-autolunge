package com.swapfix;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.ToggleKeyMapping;
import org.lwjgl.glfw.GLFW;

public class SwapFixClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // زرار تفعيل/تعطيل المود - يتظبط من قائمة Controls زي أي زرار عادي.
        // مفيش زرار افتراضي (المستخدم يحدده بنفسه من الإعدادات).
        KeyMapping enabledToggle = KeyBindingHelper.registerKeyBinding(new ToggleKeyMapping(
                "key.swapfix.enabled",
                GLFW.GLFW_KEY_UNKNOWN,
                KeyMapping.Category.MISC,
                () -> false,
                false
        ));

        SwapFixHandler.setEnabledToggle(enabledToggle);
        ClientTickEvents.END_CLIENT_TICK.register(SwapFixHandler::onEndClientTick);
    }
}
