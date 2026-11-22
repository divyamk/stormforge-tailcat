package com.tailscale.mclink;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public final class JoinRemoteScreen extends Screen {
    private final Screen parent;
    private EditBox invite;
    private Button connect;
    private Button modeButton;
    private JoinMode joinMode = JoinMode.defaultMode();
    private Component status = Component.empty();
    public JoinRemoteScreen(Screen parent) {
        super(Component.translatable("mclink.join"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        invite = new EditBox(font, width / 2 - 150, height / 2 - 48, 300, 20,
                Component.translatable("mclink.invite"));
        invite.setMaxLength(8192);
        invite.setHint(Component.literal("Paste a Tailcat invitation or address"));
        invite.setResponder(value -> {
            connect.active = JoinInput.accepts(value);
            modeButton.active = JoinInput.isPskAddress(value);
        });
        addRenderableWidget(invite);
        modeButton = addRenderableWidget(Button.builder(Component.literal(joinMode.label()), b -> {
            joinMode = joinMode.next();
            b.setMessage(Component.literal(joinMode.label()));
        }).bounds(width / 2 - 150, height / 2 - 16, 300, 20).build());
        modeButton.active = false;
        connect = addRenderableWidget(Button.builder(Component.translatable("mclink.connect"), b -> begin())
                .bounds(width / 2 - 102, height / 2 + 32, 100, 20).build());
        connect.active = false;
        addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), b -> onClose())
                .bounds(width / 2 + 2, height / 2 + 32, 100, 20).build());
        setInitialFocus(invite);
    }

    private void begin() {
        connect.active = false;
        modeButton.active = false;
        invite.setEditable(false);
        status = Component.translatable("mclink.starting");
        McLinkClient.state().join(minecraft, parent, invite.getValue(), joinMode).whenComplete((ignored, error) -> {
            if (error != null) {
                minecraft.execute(() -> {
                    status = Component.literal("Could not connect: " + ShareScreen.rootMessage(error));
                    invite.setEditable(true);
                    connect.active = JoinInput.accepts(invite.getValue());
                    modeButton.active = JoinInput.isPskAddress(invite.getValue());
                });
            }
        });
    }

    @Override
    public void onClose() {
        McLinkClient.state().stop();
        minecraft.setScreen(parent);
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredString(font, title, width / 2, height / 2 - 82, 0xffffff);
        String hint = invite.getValue().trim().startsWith("mcl1_")
                ? "Legacy invitations keep their original mode." : joinMode.hint();
        context.drawCenteredString(font, hint, width / 2, height / 2 + 10, 0xaaaaaa);
        context.drawCenteredString(font, status, width / 2, height / 2 + 64, 0xffaaaa);
    }
}
