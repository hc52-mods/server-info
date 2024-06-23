package org.hc52mod.serverinfo;

import com.google.gson.Gson;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class ServerStatusFetcher {

    private static final String API_URL = "https://api.mcsrvstat.us/2/";

    public static CompletableFuture<Text> fetchServerStatus(String address) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(API_URL + address))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                String jsonResponse = response.body();

                Gson gson = new Gson();
                ServerStatusResponse status = gson.fromJson(jsonResponse, ServerStatusResponse.class);
                return formatServerStatus(status);
            } catch (Exception e) {
                return Text.literal("Error fetching server info: " + e.getMessage())
                        .setStyle(Style.EMPTY.withColor(Formatting.RED));
            }
        });
    }

    private static Text formatServerStatus(ServerStatusResponse status) {
        if (status.online) {
            Text infoText = Text.literal("")
                    .append(Text.literal("╔═══ Server Info ═══╗\n").setStyle(Style.EMPTY.withColor(Formatting.GOLD)))
                    .append(Text.literal("║ ").setStyle(Style.EMPTY.withColor(Formatting.GOLD)))
                    .append(Text.literal("Status: ").setStyle(Style.EMPTY.withColor(Formatting.AQUA)))
                    .append(Text.literal("Online\n").setStyle(Style.EMPTY.withColor(Formatting.GREEN)))
                    .append(Text.literal("║ ").setStyle(Style.EMPTY.withColor(Formatting.GOLD)))
                    .append(Text.literal("Address: ").setStyle(Style.EMPTY.withColor(Formatting.AQUA)))
                    .append(Text.literal(status.hostname + "\n").setStyle(Style.EMPTY.withColor(Formatting.WHITE)))
                    .append(Text.literal("║ ").setStyle(Style.EMPTY.withColor(Formatting.GOLD)))
                    .append(Text.literal("Players: ").setStyle(Style.EMPTY.withColor(Formatting.AQUA)))
                    .append(Text.literal(status.players.online + "/" + status.players.max + "\n").setStyle(Style.EMPTY.withColor(Formatting.WHITE)))
                    .append(Text.literal("║ ").setStyle(Style.EMPTY.withColor(Formatting.GOLD)))
                    .append(Text.literal("Version: ").setStyle(Style.EMPTY.withColor(Formatting.AQUA)))
                    .append(Text.literal(status.version + "\n").setStyle(Style.EMPTY.withColor(Formatting.WHITE)));

            if (status.motd != null && status.motd.clean != null && !status.motd.clean.isEmpty()) {
                infoText = ((MutableText) infoText)
                        .append(Text.literal("║ ").setStyle(Style.EMPTY.withColor(Formatting.GOLD)))
                        .append(Text.literal("Message: ").setStyle(Style.EMPTY.withColor(Formatting.AQUA)))
                        .append(Text.literal(status.motd.clean.get(0) + "\n").setStyle(Style.EMPTY.withColor(Formatting.YELLOW)));
            }

            infoText = ((MutableText) infoText).append(Text.literal("╚═══════════════════╝").setStyle(Style.EMPTY.withColor(Formatting.GOLD)));
            return infoText;
        } else {
            return Text.literal("")
                    .append(Text.literal("╔═══ Server Info ═══╗\n").setStyle(Style.EMPTY.withColor(Formatting.GOLD)))
                    .append(Text.literal("║ ").setStyle(Style.EMPTY.withColor(Formatting.GOLD)))
                    .append(Text.literal("Status: ").setStyle(Style.EMPTY.withColor(Formatting.AQUA)))
                    .append(Text.literal("Offline\n").setStyle(Style.EMPTY.withColor(Formatting.RED)))
                    .append(Text.literal("╚═══════════════════╝").setStyle(Style.EMPTY.withColor(Formatting.GOLD)));
        }
    }

    public static class ServerStatusResponse {
        public boolean online;
        public String hostname;
        public String version;
        public Players players;
        public Motd motd;

        public static class Players {
            public int online;
            public int max;
        }

        public static class Motd {
            public java.util.List<String> clean;
        }
    }
}