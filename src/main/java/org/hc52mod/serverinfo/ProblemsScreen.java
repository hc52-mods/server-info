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

public class ProblemsScreen extends Screen {
    private List<ServerStatus> serverStatuses;

    public ProblemsScreen(Text title) {
        super(title);
        this.serverStatuses = new ArrayList<>();
    }

    @Override
    protected void init() {
        this.serverStatuses.clear();
        loadServerStatuses();

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Back to Multiplayer"), button -> this.client.setScreen(new MultiplayerScreen(this)))
                .dimensions(this.width / 2 - 100, this.height - 30, 200, 20)
                .build());
    }

    private void loadServerStatuses() {
        ServerList serverList = new ServerList(this.client);
        serverList.loadFile();
        for (int i = 0; i < serverList.size(); i++) {
            ServerInfo serverInfo = serverList.get(i);
            // Here you would typically check against a database or API to determine the player's status
            // For this example, we'll use some dummy data
            if (i % 3 == 0) {
                serverStatuses.add(new ServerStatus(serverInfo.name, "Not whitelisted"));
            } else if (i % 3 == 1) {
                serverStatuses.add(new ServerStatus(serverInfo.name, "Banned"));
            } else {
                serverStatuses.add(new ServerStatus(serverInfo.name, "Everything OK"));
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);

        int yOffset = 50;
        for (ServerStatus status : serverStatuses) {
            Text serverText = Text.literal(status.serverName + ": ")
                    .setStyle(Style.EMPTY.withColor(Formatting.WHITE));
            Text statusText = Text.literal(status.status)
                    .setStyle(Style.EMPTY.withColor(getStatusColor(status.status)));

            context.drawTextWithShadow(this.textRenderer, ((MutableText) serverText).append(statusText), this.width / 2 - 150, yOffset, 0xFFFFFF);
            yOffset += 20;
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