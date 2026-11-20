package com.tailscale.mclink;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
@Mod("tailcat_for_minecraft")
public final class McLinkClient {
    private static final ScreenState STATE = new ScreenState();
    public McLinkClient() { Runtime.getRuntime().addShutdownHook(new Thread(STATE::close, "tailcat-shutdown")); }
    public static ScreenState state() { return STATE; }
    @Mod.EventBusSubscriber(modid="tailcat_for_minecraft", value=Dist.CLIENT)
    public static class Events {
        @SubscribeEvent public static void init(ScreenEvent.Init.Post event) {
            var client = Minecraft.getInstance();
            var screen = event.getScreen();
            boolean share = screen instanceof PauseScreen && client.hasSingleplayerServer();
            if (!share && !(screen instanceof JoinMultiplayerScreen)) return;
            event.addListener(Button.builder(Component.translatable(share ? "mclink.share" : "mclink.join"),
                b -> client.setScreen(share ? new ShareScreen(screen) : new JoinRemoteScreen(screen)))
                .bounds(screen.width - 154, 6, 148, 20).build());
        }
        @SubscribeEvent public static void tick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) return;
            var client = Minecraft.getInstance();
            STATE.tick(client);
            String error = STATE.takeError();
            if (error != null) SystemToast.add(client.getToasts(), SystemToast.SystemToastIds.PERIODIC_NOTIFICATION,
                Component.literal("Remote LAN connection failed"), Component.literal(error));
        }
    }
}
