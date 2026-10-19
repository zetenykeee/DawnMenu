package io.github.zetenykeee.dawnmenu;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public final class DawnMenuClient implements ClientModInitializer {
    private static final List<Waypoint> WAYPOINTS = new ArrayList<>();

    private final KeyMapping.Category category = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath("dawnmenu", "waypoints")
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

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (addWaypointKey.consumeClick()) {
                addWaypoint(client);
            }
            while (listWaypointsKey.consumeClick()) {
                listWaypoints(client);
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

    private record Waypoint(String name, int x, int y, int z) {}
}
