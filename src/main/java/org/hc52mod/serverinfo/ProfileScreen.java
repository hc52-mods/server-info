package org.hc52mod.serverinfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import java.net.URL;
import java.io.InputStream;

public class ProfileScreen extends Screen {
    private static final String SKIN_API_URL = "https://mineskin.eu/helm/";
    private Identifier playerHeadTexture;
    private static final int HEAD_SIZE = 100;

    public ProfileScreen(Text title) {
        super(title);
    }

    @Override
    protected void init() {
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Back to Multiplayer"), button -> this.client.setScreen(new TitleScreen(false)))
                .dimensions(this.width / 2 - 100, this.height - 30, 200, 20)
                .build());

        // Spielername aus der aktuellen Session abrufen
        String playerName = MinecraftClient.getInstance().getSession().getUsername();
        fetchPlayerHead(playerName);
    }

    private void fetchPlayerHead(String playerName) {
        String url = "https://mineskin.eu/helm/" + playerName + "/";

        try {
            URL imageUrl = new URL(url);
            InputStream stream = imageUrl.openStream();
            NativeImage image = NativeImage.read(stream);
            NativeImageBackedTexture texture = new NativeImageBackedTexture(image);
            playerHeadTexture = this.client.getTextureManager().registerDynamicTexture("player_head", texture);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);

        if (playerHeadTexture != null) {
            int x = (this.width - HEAD_SIZE) / 2;
            int y = (this.height - HEAD_SIZE) / 2;
            context.drawTexture(playerHeadTexture, x, y, 0, 0, HEAD_SIZE, HEAD_SIZE, HEAD_SIZE, HEAD_SIZE);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderBackground(DrawContext context) {
        context.fillGradient(0, 0, this.width, this.height, 0xC0101010, 0xD0101010);
    }
}
