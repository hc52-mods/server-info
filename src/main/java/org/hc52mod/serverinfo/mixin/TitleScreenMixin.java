package org.hc52mod.serverinfo.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.hc52mod.serverinfo.ProblemsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.hc52mod.serverinfo.ProfileScreen;
import net.minecraft.client.MinecraftClient;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void addCustomButton(CallbackInfo ci) {
        int buttonWidth = 100;
        int buttonHeight = 20;
        int buttonX =  10; // 10 Pixel vom rechten Rand
        int buttonY =  10; // 10 Pixel vom oberen Rand

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Profile"), button -> openCustomScreen())
                .dimensions(buttonX, buttonY, buttonWidth, buttonHeight)
                .build());



        System.out.println("Adding custom button to Multiplayer Screen at position: " + buttonX + ", " + buttonY);
    }




    private void openCustomScreen() {
        MinecraftClient.getInstance().setScreen(new ProfileScreen(Text.literal("Profile")));
    }
}