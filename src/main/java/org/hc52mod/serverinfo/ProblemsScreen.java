package org.hc52mod.serverinfo;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ProblemsScreen extends Screen {
    private List<ServerStatus> serverStatuses;
    private boolean isLoading;

    public ProblemsScreen(Text title) {
        super(title);
        this.serverStatuses = new ArrayList<>();
        this.isLoading = true;
    }

    @Override
    protected void init() {
        this.serverStatuses.clear();
        this.isLoading = true;
        loadServerStatuses();

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Back to Multiplayer"), button -> this.client.setScreen(new MultiplayerScreen(this)))
                .dimensions(this.width / 2 - 100, this.height - 30, 200, 20)
                .build());
    }

    private void loadServerStatuses() {
        ServerList serverList = new ServerList(this.client);
        serverList.loadFile();
        List<CompletableFuture<ServerStatus>> futures = new ArrayList<>();

        for (int i = 0; i < serverList.size(); i++) {
            ServerInfo serverInfo = serverList.get(i);
            futures.add(checkServerStatus(serverInfo));
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenRun(() -> {
                    for (CompletableFuture<ServerStatus> future : futures) {
                        serverStatuses.add(future.join());
                    }
                    isLoading = false;
                });
    }

    private CompletableFuture<ServerStatus> checkServerStatus(ServerInfo serverInfo) {
        return CompletableFuture.supplyAsync(() -> {
            // Here you would implement the actual status check
            // This might involve pinging the server, checking against a whitelist API, etc.
            // For this example, we'll use a dummy check based on the server name
            String status;
            if (serverInfo.name.contains("1")) {
                status = "Not whitelisted";
            } else if (serverInfo.name.contains("2")) {
                status = "Banned";
            } else {
                status = "Everything OK";
            }
            return new ServerStatus(serverInfo.name, status);
        });
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);

        if (isLoading) {
            context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Loading server statuses..."), this.width / 2, this.height / 2, 0xFFFFFF);
        } else {
            int yOffset = 50;
            for (ServerStatus status : serverStatuses) {
                Text serverText = Text.literal(status.serverName + ": ")
                        .setStyle(Style.EMPTY.withColor(Formatting.WHITE));
                Text statusText = Text.literal(status.status)
                        .setStyle(Style.EMPTY.withColor(getStatusColor(status.status)));

                context.drawTextWithShadow(this.textRenderer, ((MutableText) serverText).append(statusText), this.width / 2 - 150, yOffset, 0xFFFFFF);
                yOffset += 20;
            }
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private Formatting getStatusColor(String status) {
        switch (status) {
            case "Banned":
                return Formatting.RED;
            case "Not whitelisted":
                return Formatting.YELLOW;
            case "Everything OK":
                return Formatting.GREEN;
            default:
                return Formatting.WHITE;
        }
    }

    private void renderBackground(DrawContext context) {
        context.fillGradient(0, 0, this.width, this.height, 0xC0101010, 0xD0101010);
    }

    private static class ServerStatus {
        String serverName;
        String status;

        ServerStatus(String serverName, String status) {
            this.serverName = serverName;
            this.status = status;
        }
    }
}