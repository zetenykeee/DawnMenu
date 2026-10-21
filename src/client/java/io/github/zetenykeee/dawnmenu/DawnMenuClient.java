package io.github.zetenykeee.dawnmenu;

import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public final class DawnMenuClient implements ClientModInitializer {
    private static final List<Waypoint> WAYPOINTS = new ArrayList<>();
    private final KeyMapping.Category category = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath("dawnmenu", "main"));
    private final KeyMapping addWaypointKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key.dawnmenu.add_waypoint", InputConstants.Type.KEYSYM, InputConstants.KEY_K, category));
    private final KeyMapping listWaypointsKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key.dawnmenu.list_waypoints", InputConstants.Type.KEYSYM, InputConstants.KEY_J, category));
    private final KeyMapping openMenuKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key.dawnmenu.open_menu", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_CONTROL, category));

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (addWaypointKey.consumeClick()) addWaypoint(client);
            while (listWaypointsKey.consumeClick()) listWaypoints(client);
            while (openMenuKey.consumeClick()) {
                if (client.player != null) client.gui.setScreen(new DawnMenuScreen());
            }
        });
    }

    private static void addWaypoint(Minecraft client) {
        if (client.player == null || client.level == null) return;
        var p = client.player.blockPosition();
        Waypoint w = new Waypoint("Waypoint " + (WAYPOINTS.size() + 1), p.getX(), p.getY(), p.getZ());
        WAYPOINTS.add(w);
        client.player.sendSystemMessage(Component.literal("[DawnMenu] Saved " + w.name()
                + " at " + w.x() + ", " + w.y() + ", " + w.z()));
    }

    private static void listWaypoints(Minecraft client) {
        if (client.player == null) return;
        if (WAYPOINTS.isEmpty()) {
            client.player.sendSystemMessage(Component.literal("[DawnMenu] No waypoints yet. Press K to save one."));
            return;
        }
        client.player.sendSystemMessage(Component.literal("[DawnMenu] Personal waypoints:"));
        for (Waypoint w : WAYPOINTS) client.player.sendSystemMessage(Component.literal(
                "- " + w.name() + ": " + w.x() + ", " + w.y() + ", " + w.z()));
    }

    private static final class DawnMenuScreen extends Screen {
        private int selectedCategory = 0;
        private DawnMenuScreen() { super(Component.literal("DawnMenu")); }

        @Override
        protected void init() {
            clearWidgets();
            int panelWidth = Math.min(720, this.width - 32);
            int panelHeight = Math.min(430, this.height - 32);
            int left = (this.width - panelWidth) / 2;
            int top = (this.height - panelHeight) / 2;
            int sidebar = left + 18;
            String[] cats = {"Home", "Waypoints", "Settings"};
            for (int i = 0; i < cats.length; i++) {
                final int idx = i;
                addRenderableWidget(Button.builder(Component.literal(cats[i]), b -> {
                    selectedCategory = idx;
                    init();
                }).bounds(sidebar, top + 74 + i * 32, 145, 24).build());
            }
            int contentX = left + 185;
            int contentWidth = panelWidth - 205;
            if (selectedCategory == 0 || selectedCategory == 1) {
                addRenderableWidget(Button.builder(Component.literal("Save current position"), b ->
                        addWaypoint(Minecraft.getInstance()))
                        .bounds(contentX, top + 108, contentWidth, 28).build());
                addRenderableWidget(Button.builder(Component.literal("Print waypoints to chat"), b ->
                        listWaypoints(Minecraft.getInstance()))
                        .bounds(contentX, top + 146, contentWidth, 28).build());
            }
            addRenderableWidget(Button.builder(Component.literal("Close menu"), b ->
                    Minecraft.getInstance().gui.setScreen(null))
                    .bounds(left + panelWidth - 112, top + panelHeight - 38, 96, 24).build());
        }

        @Override
        public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
            extractBackground(g, mouseX, mouseY, partialTick);
            int panelWidth = Math.min(720, this.width - 32);
            int panelHeight = Math.min(430, this.height - 32);
            int left = (this.width - panelWidth) / 2;
            int top = (this.height - panelHeight) / 2;
            g.fill(0, 0, this.width, this.height, 0xB8000000);
            g.fill(left, top, left + panelWidth, top + panelHeight, 0xFF11141C);
            g.fill(left, top, left + panelWidth, top + 3, 0xFF9B5CFF);
            g.fill(left + 12, top + 54, left + 158, top + panelHeight - 12, 0xFF191D28);
            g.fill(left + 170, top + 54, left + panelWidth - 12, top + panelHeight - 48, 0xFF171B25);
            g.text(this.font, Component.literal("DAWN"), left + 20, top + 17, 0xFFFFFFFF, false);
            g.text(this.font, Component.literal("MENU  /  CLIENT"), left + 80, top + 18, 0xFFB6A0FF, false);
            g.text(this.font, Component.literal("Right Ctrl  •  Toggle menu"), left + 20, top + 38, 0xFF9BA3B7, false);
            String[] cats = {"HOME", "WAYPOINTS", "SETTINGS"};
            for (int i = 0; i < cats.length; i++) {
                g.text(this.font, Component.literal(cats[i]), left + 30, top + 82 + i * 32,
                        selectedCategory == i ? 0xFFFFFFFF : 0xFF9BA3B7, false);
            }
            String heading = selectedCategory == 0 ? "Dashboard" : selectedCategory == 1 ? "Waypoints" : "Settings";
            g.text(this.font, Component.literal(heading), left + 190, top + 70, 0xFFFFFFFF, false);
            g.text(this.font, Component.literal("PERSONAL TOOLS"), left + 190, top + 88, 0xFF9B5CFF, false);
            if (selectedCategory == 0 || selectedCategory == 1) {
                g.text(this.font, Component.literal("Save your current coordinates or list saved points in chat."),
                        left + 190, top + 196, 0xFFB7BECE, false);
                g.text(this.font, Component.literal("Saved in this session: " + WAYPOINTS.size()),
                        left + 190, top + 216, 0xFF9BA3B7, false);
            } else {
                g.text(this.font, Component.literal("Menu key: Right Ctrl"), left + 190, top + 120, 0xFFB7BECE, false);
                g.text(this.font, Component.literal("Waypoint keys: K to save, J to list"), left + 190, top + 142, 0xFFB7BECE, false);
            }
            super.extractRenderState(g, mouseX, mouseY, partialTick);
        }

        @Override
        public boolean isPauseScreen() { return false; }
    }

    private record Waypoint(String name, int x, int y, int z) {}
}
