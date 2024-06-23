package org.hc52mod.serverinfo.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.hc52mod.serverinfo.CustomScreen;
import org.hc52mod.serverinfo.ProblemsScreen;
import net.minecraft.client.MinecraftClient;

@Mixin(MultiplayerScreen.class)
public abstract class MultiplayerScreenMixin extends Screen {

    protected MultiplayerScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void addCustomButton(CallbackInfo ci) {
        int buttonWidth = 100;
        int buttonHeight = 20;
        int buttonX = this.width - buttonWidth - 10; // 10 Pixel vom rechten Rand
        int buttonY = 10; // 10 Pixel vom oberen Rand

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Check Server Status"), button -> openCustomScreen())
                .dimensions(buttonX, buttonY, buttonWidth, buttonHeight)
                .build());

        int leftButtonX = 10;
        int leftButtonY = 10;

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Issues"), button -> openLeftScreen())
                .dimensions(leftButtonX, leftButtonY, buttonWidth, buttonHeight)
                .build());


        System.out.println("Adding custom button to Multiplayer Screen at position: " + buttonX + ", " + buttonY);
    }

    private void openLeftScreen() {
        MinecraftClient.getInstance().setScreen(new ProblemsScreen(Text.literal("Problems")));
    }


    private void openCustomScreen() {
        MinecraftClient.getInstance().setScreen(new CustomScreen(Text.literal("Server Status")));
    }
}