package org.hc52mod.serverinfo;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.option.ServerList;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.network.ServerAddress;

public class CustomScreen extends Screen {
    private TextFieldWidget serverInputField;
    private ButtonWidget addServerButton;
    private ServerInfo serverInfo;
    private Text serverInfoText = Text.empty();

    public CustomScreen(Text title) {
        super(title);
    }

    @Override
    protected void init() {
        this.serverInputField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 50, 200, 20, Text.literal("Server IP"));
        this.serverInputField.setMaxLength(128);
        this.serverInputField.setChangedListener(this::onServerInputChanged);
        this.addDrawableChild(this.serverInputField);

        this.addServerButton = this.addDrawableChild(ButtonWidget.builder(Text.literal("Add Server"), this::addServer)
                .dimensions(this.width / 2 - 100, 80, 200, 20)
                .build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Back to Multiplayer"), button -> this.client.setScreen(new MultiplayerScreen(this)))
                .dimensions(this.width / 2 - 100, this.height - 30, 200, 20)
                .build());
    }

    private void onServerInputChanged(String newInput) {
        updateServerInfo(newInput);
    }

    private void updateServerInfo(String address) {
        this.serverInfoText = Text.literal("Fetching server info...")
                .setStyle(Style.EMPTY.withColor(Formatting.YELLOW));
        ServerStatusFetcher.fetchServerStatus(address)
                .thenAccept(status -> {
                    this.serverInfoText = status;
                    this.serverInfo = new ServerInfo("", address, ServerInfo.ServerType.OTHER);
                })
                .exceptionally(e -> {
                    this.serverInfoText = Text.literal("Error: " + e.getMessage())
                            .setStyle(Style.EMPTY.withColor(Formatting.RED));
                    return null;
                });
    }

    private void addServer(ButtonWidget button) {
        if (this.serverInputField != null && !this.serverInputField.getText().isEmpty()) {
            String address = this.serverInputField.getText();
            ServerInfo serverInfo = new ServerInfo("Server " + address, address, ServerInfo.ServerType.OTHER);

            ServerList serverList = new ServerList(this.client);
            serverList.loadFile();
            serverList.add(serverInfo, true);
            serverList.saveFile();

            this.serverInfoText = Text.literal("Server added: " + address)
                    .setStyle(Style.EMPTY.withColor(Formatting.GREEN));
            this.client.setScreen(new MultiplayerScreen(this));
        } else {
            this.serverInfoText = Text.literal("Please enter a server address")
                    .setStyle(Style.EMPTY.withColor(Formatting.RED));
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);

        // Render server info text
        int yOffset = 110;
        int maxWidth = this.width - 40;
        for (OrderedText line : this.textRenderer.wrapLines(this.serverInfoText, maxWidth)) {
            context.drawTextWithShadow(this.textRenderer, line, this.width / 2 - maxWidth / 2, yOffset, 0xFFFFFF);
            yOffset += this.textRenderer.fontHeight + 2;
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderBackground(DrawContext context) {
        context.fillGradient(0, 0, this.width, this.height, 0xC0101010, 0xD0101010);
    }
}