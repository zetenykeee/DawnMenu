package io.github.zetenykeee.dawnmenu;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public final class DawnMenuClient implements ClientModInitializer {
    private static final List<Waypoint> WAYPOINTS = new ArrayList<>();

    private final KeyMapping.Category category = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath("dawnmenu", "main")
    );

    private final KeyMapping addWaypointKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key.dawnmenu.add_waypoint",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_K,
            category
    ));

    private final KeyMapping listWaypointsKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key.dawnmenu.list_waypoints",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_J,
            category
    ));

    private final KeyMapping openMenuKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key.dawnmenu.open_menu",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_RIGHT_SHIFT,
            category
    ));

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (addWaypointKey.consumeClick()) {
                addWaypoint(client);
            }
            while (listWaypointsKey.consumeClick()) {
                listWaypoints(client);
            }
            while (openMenuKey.consumeClick()) {
                if (client.player != null) {
                    client.setScreen(new DawnMenuScreen());
                }
            }
        });
    }

    private static void addWaypoint(Minecraft client) {
        if (client.player == null || client.level == null) {
            return;
        }

        var position = client.player.blockPosition();
        Waypoint waypoint = new Waypoint(
                "Waypoint " + (WAYPOINTS.size() + 1),
                position.getX(),
                position.getY(),
                position.getZ()
        );
        WAYPOINTS.add(waypoint);
        client.player.sendSystemMessage(Component.literal("[DawnMenu] Saved " + waypoint.name()
                + " at " + waypoint.x() + ", " + waypoint.y() + ", " + waypoint.z()));
    }

    private static void listWaypoints(Minecraft client) {
        if (client.player == null) {
            return;
        }
        if (WAYPOINTS.isEmpty()) {
            client.player.sendSystemMessage(Component.literal(
                    "[DawnMenu] No waypoints yet. Press K to save your position."
            ));
            return;
        }
        client.player.sendSystemMessage(Component.literal("[DawnMenu] Personal waypoints:"));
        for (Waypoint waypoint : WAYPOINTS) {
            client.player.sendSystemMessage(Component.literal("- " + waypoint.name() + ": "
                    + waypoint.x() + ", " + waypoint.y() + ", " + waypoint.z()));
        }
    }

    private static final class DawnMenuScreen extends Screen {
        private DawnMenuScreen() {
            super(Component.literal("DawnMenu"));
        }

        @Override
        protected void init() {
            int centerX = this.width / 2;
            int top = this.height / 2 - 55;
            addRenderableWidget(Button.builder(Component.literal("Save waypoint"), button ->
                    addWaypoint(Minecraft.getInstance()))
                    .bounds(centerX - 100, top, 200, 20).build());
            addRenderableWidget(Button.builder(Component.literal("List waypoints in chat"), button ->
                    listWaypoints(Minecraft.getInstance()))
                    .bounds(centerX - 100, top + 26, 200, 20).build());
            addRenderableWidget(Button.builder(Component.literal("Close"), button ->
                    Minecraft.getInstance().setScreen(null))
                    .bounds(centerX - 100, top + 52, 200, 20).build());
        }

        @Override
        public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            renderBackground(graphics, mouseX, mouseY, partialTick);
            graphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 82, 0xFFFFFF);
            graphics.drawCenteredString(this.font,
                    Component.literal("Right Shift opens this menu"),
                    this.width / 2, this.height / 2 - 68, 0xB8B8B8);
            super.render(graphics, mouseX, mouseY, partialTick);
        }
    }

    private record Waypoint(String name, int x, int y, int z) {}
}
