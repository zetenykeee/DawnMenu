package io.github.zetenykeee.dawnmenu;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public final class DawnMenuClient implements ClientModInitializer {
    private static final List<Waypoint> WAYPOINTS = new ArrayList<>();
    private static KeyBinding addWaypointKey;
    private static KeyBinding listWaypointsKey;

    @Override
    public void onInitializeClient() {
        addWaypointKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.dawnmenu.add_waypoint",
                GLFW.GLFW_KEY_K,
                KeyBinding.Category.MISC
        ));
        listWaypointsKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.dawnmenu.list_waypoints",
                GLFW.GLFW_KEY_J,
                KeyBinding.Category.MISC
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (addWaypointKey.wasPressed()) {
                addWaypoint(client);
            }
            while (listWaypointsKey.wasPressed()) {
                listWaypoints(client);
            }
        });
    }

    private static void addWaypoint(MinecraftClient client) {
        if (client.player == null || client.world == null) {
            return;
        }

        Waypoint waypoint = new Waypoint(
                "Waypoint " + (WAYPOINTS.size() + 1),
                client.player.getBlockX(),
                client.player.getBlockY(),
                client.player.getBlockZ(),
                client.world.getRegistryKey().getValue().toString()
        );
        WAYPOINTS.add(waypoint);
        client.player.sendMessage(Text.literal("[DawnMenu] Saved " + waypoint.name()
                + " at " + waypoint.x() + ", " + waypoint.y() + ", " + waypoint.z()), false);
    }

    private static void listWaypoints(MinecraftClient client) {
        if (client.player == null) {
            return;
        }
        if (WAYPOINTS.isEmpty()) {
            client.player.sendMessage(Text.literal("[DawnMenu] No waypoints yet. Press K to save your position."), false);
            return;
        }
        client.player.sendMessage(Text.literal("[DawnMenu] Personal waypoints:"), false);
        for (Waypoint waypoint : WAYPOINTS) {
            client.player.sendMessage(Text.literal("- " + waypoint.name() + ": "
                    + waypoint.x() + ", " + waypoint.y() + ", " + waypoint.z()
                    + " (" + waypoint.dimension() + ")"), false);
        }
    }

    private record Waypoint(String name, int x, int y, int z, String dimension) {}
}
