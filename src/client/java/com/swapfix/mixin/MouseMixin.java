package com.swapfix.mixin;

import com.swapfix.SwapFixHandler;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * بنمسك ضغطة الماوس (بما فيها left click) وهي لسه خام من GLFW،
 * عشان نعرف فورًا لو المستخدم ضرب left click وهو ماسك سلوت الرجوع.
 */
@Mixin(MouseHandler.class)
public class MouseMixin {

    @Inject(method = "onButton", at = @At("HEAD"))
    private void swapfix$onButton(long window, MouseButtonInfo input, int action, CallbackInfo ci) {
        SwapFixHandler.onRawMouseButton(input.button(), action);
    }
}
