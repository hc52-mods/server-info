package org.hc52mod.serverinfo.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.*;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import org.hc52mod.serverinfo.ServerStatusFetcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AddServerScreen.class)
public class AddServerScreenMixin {

    @Shadow private TextFieldWidget addressField;

    private Text statusText = Text.empty();

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        if (this.addressField != null) {
            this.addressField.setChangedListener(this::onAddressChanged);
        }
    }

    private void onAddressChanged(String address) {
        if (!address.isEmpty()) {
            fetchServerStatus(address);
        } else {
            this.statusText = Text.empty();
        }
    }

    private void fetchServerStatus(String address) {
        ServerStatusFetcher.fetchServerStatus(address)
                .thenAccept(status -> {
                    this.statusText = formatServerStatus(status);
                    checkWhitelistAndBanStatus(address);
                })
                .exceptionally(e -> {
                    this.statusText = Text.literal("Error: " + e.getMessage())
                            .setStyle(Style.EMPTY.withColor(Formatting.RED));
                    return null;
                });
    }

    private Text formatServerStatus(Text status) {
        return Text.literal("Server Status:\n")
                .setStyle(Style.EMPTY.withColor(Formatting.GOLD))
                .append(status.copy().setStyle(Style.EMPTY.withColor(Formatting.WHITE)));
    }

    private void checkWhitelistAndBanStatus(String address) {
        // In einer realen Implementierung würden Sie hier eine API oder Datenbank abfragen
        // Dies ist nur ein Beispiel mit Dummy-Daten
        boolean isWhitelisted = address.contains("white");
        boolean isBanned = address.contains("ban");

        Text whitelistText = isWhitelisted
                ? Text.literal("Whitelisted").setStyle(Style.EMPTY.withColor(Formatting.GREEN))
                : Text.literal("Not whitelisted").setStyle(Style.EMPTY.withColor(Formatting.YELLOW));

        Text banText = isBanned
                ? Text.literal("Banned").setStyle(Style.EMPTY.withColor(Formatting.RED))
                : Text.literal("Not banned").setStyle(Style.EMPTY.withColor(Formatting.GREEN));

        /*this.statusText = this.statusText.copy()
                .append("\n\n")
                .append(Text.literal("Player Status:\n").setStyle(Style.EMPTY.withColor(Formatting.GOLD)))
                .append(whitelistText)
                .append("\n")
                .append(banText);*/
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int yOffset = 150;
        for (OrderedText line : textRenderer.wrapLines(this.statusText, 200)) {
            context.drawTextWithShadow(textRenderer, line, 50, yOffset, 0xFFFFFF);
            yOffset += textRenderer.fontHeight + 2;
        }
    }
}