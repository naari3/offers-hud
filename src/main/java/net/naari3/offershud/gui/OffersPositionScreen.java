package net.naari3.offershud.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import me.shedaniel.autoconfig.AutoConfig;
/*? if >= 1.21.11 {*/
import me.shedaniel.autoconfig.AutoConfigClient;
/*?}*/
import me.shedaniel.clothconfig2.gui.AbstractConfigScreen;
/*? if >= 26.1 {*/
import net.minecraft.client.gui.GuiGraphicsExtractor;
/*?} else {*/
/*import net.minecraft.client.gui.GuiGraphics;
*//*?}*/
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
/*? if >= 1.21.9 {*/
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
/*?}*/
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.naari3.offershud.MerchantInfo;
import net.naari3.offershud.OffersHUD;
import net.naari3.offershud.config.ModConfig;
import net.naari3.offershud.renderer.OffersHUDRenderer;

public class OffersPositionScreen extends Screen {
    // GLFW key codes
    private static final int KEY_RIGHT = 262;
    private static final int KEY_LEFT = 263;
    private static final int KEY_DOWN = 264;
    private static final int KEY_UP = 265;
    private static final int MOD_SHIFT = 0x0001;

    private final Screen parent;
    private final ModConfig config;
    private List<MerchantOffer> previewOffers;

    // working values (committed to config only on Done)
    private ModConfig.Alignment workAlignment;
    private int workOffsetX;
    private int workOffsetY;
    private float workScale;

    // current HUD top-left in scaled-gui pixels
    private float curX;
    private float curY;

    private boolean dragging = false;
    private double grabDX;
    private double grabDY;

    public OffersPositionScreen(Screen parent) {
        super(Component.translatable("offershud.screen.position.title"));
        this.parent = parent;
        this.config = OffersHUD.getConfig();
        this.workAlignment = config.alignment;
        this.workOffsetX = config.offsetX;
        this.workOffsetY = config.offsetY;
        this.workScale = config.scale;
    }

    @Override
    protected void init() {
        List<MerchantOffer> live = MerchantInfo.getInfo().getOffers();
        this.previewOffers = (live != null && !live.isEmpty()) ? live : sampleOffers();
        recomputeTopLeftFromWork();

        int cx = this.width / 2;
        int by = this.height - 28;

        this.addRenderableWidget(Button.builder(Component.literal("-"), b -> {
            workScale = Math.max(0.25f, Math.round((workScale - 0.25f) * 100f) / 100f);
            recomputeTopLeftFromWork();
        }).bounds(cx - 154, by, 20, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("+"), b -> {
            workScale = Math.min(3.0f, Math.round((workScale + 0.25f) * 100f) / 100f);
            recomputeTopLeftFromWork();
        }).bounds(cx - 132, by, 20, 20).build());

        this.addRenderableWidget(Button.builder(Component.translatable("offershud.screen.position.reset"), b -> {
            workAlignment = ModConfig.Alignment.TOP_LEFT;
            workOffsetX = 5;
            workOffsetY = 5;
            workScale = 1.0f;
            recomputeTopLeftFromWork();
        }).bounds(cx - 108, by, 100, 20).build());

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> {
            config.alignment = workAlignment;
            config.offsetX = workOffsetX;
            config.offsetY = workOffsetY;
            config.scale = workScale;
            AutoConfig.getConfigHolder(ModConfig.class).save();
            // The parent Cloth Config screen caches its entry widgets with the values
            // they had when built, so it would still show the old values after we save.
            // Rebuild a fresh config screen so the saved values are reflected immediately.
            Screen rebuilt = rebuiltConfigScreen(parent);
            /*? if >= 26.2 {*/
            this.minecraft.gui.setScreen(rebuilt != null ? rebuilt : parent);
            /*?} else {*/
            /*this.minecraft.setScreen(rebuilt != null ? rebuilt : parent);
            *//*?}*/
        }).bounds(cx - 4, by, 100, 20).build());

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, b -> onClose())
                .bounds(cx + 100, by, 100, 20).build());
    }

    private static List<MerchantOffer> sampleOffers() {
        List<MerchantOffer> list = new ArrayList<>();
        list.add(new MerchantOffer(new ItemCost(Items.EMERALD, 5), Optional.empty(),
                new ItemStack(Items.DIAMOND), 12, 5, 0.05f));
        list.add(new MerchantOffer(new ItemCost(Items.EMERALD, 20), Optional.of(new ItemCost(Items.BOOK)),
                new ItemStack(Items.ENCHANTED_BOOK), 3, 10, 0.2f));
        list.add(new MerchantOffer(new ItemCost(Items.EMERALD, 1), Optional.empty(),
                new ItemStack(Items.BREAD, 6), 16, 1, 0.05f));
        return list;
    }

    private float scaledW() {
        return OffersHUDRenderer.calcWidth(previewOffers, this.font) * workScale;
    }

    private float scaledH() {
        return OffersHUDRenderer.calcHeight(previewOffers) * workScale;
    }

    private void recomputeTopLeftFromWork() {
        float sW = scaledW();
        float sH = scaledH();
        curX = workAlignment.isRight() ? this.width - sW - workOffsetX : workOffsetX;
        curY = workAlignment.isBottom() ? this.height - sH - workOffsetY : workOffsetY;
    }

    private void snapToCorner() {
        float sW = scaledW();
        float sH = scaledH();
        float centerX = curX + sW / 2f;
        float centerY = curY + sH / 2f;
        boolean right = centerX >= this.width / 2.0f;
        boolean bottom = centerY >= this.height / 2.0f;
        workAlignment = right
                ? (bottom ? ModConfig.Alignment.BOTTOM_RIGHT : ModConfig.Alignment.TOP_RIGHT)
                : (bottom ? ModConfig.Alignment.BOTTOM_LEFT : ModConfig.Alignment.TOP_LEFT);
        workOffsetX = Math.max(0, Math.round(right ? this.width - sW - curX : curX));
        workOffsetY = Math.max(0, Math.round(bottom ? this.height - sH - curY : curY));
        recomputeTopLeftFromWork();
    }

    private boolean inBox(double mx, double my) {
        float sW = scaledW();
        float sH = scaledH();
        return mx >= curX && mx <= curX + sW && my >= curY && my <= curY + sH;
    }

    private int boxColor() {
        return dragging ? 0x6055FF55 : 0x3055FF55;
    }

    /*? if >= 26.1 {*/
    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float partialTick) {
        context.fill(0, 0, context.guiWidth(), context.guiHeight(), 0x88000000);
        super.extractRenderState(context, mouseX, mouseY, partialTick);
        context.centeredText(this.font, this.title.getString(), this.width / 2, 10, 0xFFFFFFFF);
        context.centeredText(this.font, Component.translatable("offershud.screen.position.hint").getString(),
                this.width / 2, 22, 0xFFAAAAAA);
        context.centeredText(this.font, String.format("scale: %.2f  /  %s", workScale, workAlignment.name()),
                this.width / 2, 34, 0xFFAAAAAA);
        float sW = scaledW();
        float sH = scaledH();
        context.fill(Math.round(curX) - 1, Math.round(curY) - 1, Math.round(curX + sW) + 1, Math.round(curY + sH) + 1,
                boxColor());
        OffersHUDRenderer.renderOffers(context, this.font, previewOffers, curX, curY, workScale,
                config.highlightExtremePrices);
    }
    /*?} else {*/
    /*@Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float partialTick) {
        context.fill(0, 0, this.width, this.height, 0x88000000);
        super.render(context, mouseX, mouseY, partialTick);
        context.drawCenteredString(this.font, this.title.getString(), this.width / 2, 10, 0xFFFFFFFF);
        context.drawCenteredString(this.font, Component.translatable("offershud.screen.position.hint").getString(),
                this.width / 2, 22, 0xFFAAAAAA);
        context.drawCenteredString(this.font, String.format("scale: %.2f  /  %s", workScale, workAlignment.name()),
                this.width / 2, 34, 0xFFAAAAAA);
        float sW = scaledW();
        float sH = scaledH();
        context.fill(Math.round(curX) - 1, Math.round(curY) - 1, Math.round(curX + sW) + 1, Math.round(curY + sH) + 1,
                boxColor());
        OffersHUDRenderer.renderOffers(context, this.font, previewOffers, curX, curY, workScale,
                config.highlightExtremePrices);
    }
    *//*?}*/

    /*? if >= 1.21.9 {*/
    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubled) {
        if (super.mouseClicked(event, doubled)) {
            return true;
        }
        if (event.button() == 0 && inBox(event.x(), event.y())) {
            dragging = true;
            grabDX = event.x() - curX;
            grabDY = event.y() - curY;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (dragging) {
            curX = (float) (event.x() - grabDX);
            curY = (float) (event.y() - grabDY);
            return true;
        }
        return super.mouseDragged(event, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (dragging && event.button() == 0) {
            dragging = false;
            snapToCorner();
            return true;
        }
        return super.mouseReleased(event);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        int step = (event.modifiers() & MOD_SHIFT) != 0 ? 10 : 1;
        boolean handled = true;
        switch (event.key()) {
            case KEY_RIGHT -> curX += step;
            case KEY_LEFT -> curX -= step;
            case KEY_DOWN -> curY += step;
            case KEY_UP -> curY -= step;
            default -> handled = false;
        }
        if (handled) {
            snapToCorner();
            return true;
        }
        return super.keyPressed(event);
    }
    /*?} else {*/
    /*@Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (button == 0 && inBox(mouseX, mouseY)) {
            dragging = true;
            grabDX = mouseX - curX;
            grabDY = mouseY - curY;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (dragging) {
            curX = (float) (mouseX - grabDX);
            curY = (float) (mouseY - grabDY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (dragging && button == 0) {
            dragging = false;
            snapToCorner();
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        int step = (modifiers & MOD_SHIFT) != 0 ? 10 : 1;
        boolean handled = true;
        switch (keyCode) {
            case KEY_RIGHT -> curX += step;
            case KEY_LEFT -> curX -= step;
            case KEY_DOWN -> curY += step;
            case KEY_UP -> curY -= step;
            default -> handled = false;
        }
        if (handled) {
            snapToCorner();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    *//*?}*/

    @Override
    public void onClose() {
        /*? if >= 26.2 {*/
        this.minecraft.gui.setScreen(parent);
        /*?} else {*/
        /*this.minecraft.setScreen(parent);
        *//*?}*/
    }

    /**
     * If this editor was opened from a Cloth Config screen, build a fresh instance of it
     * so that just-saved config values are shown (Cloth caches entry widgets and does not
     * react to external config changes). Returns {@code null} when the parent is not a
     * Cloth Config screen or its parent reference can't be read, so the caller falls back
     * to reusing the existing (stale) parent instead of breaking navigation.
     */
    private Screen rebuiltConfigScreen(Screen current) {
        if (!(current instanceof AbstractConfigScreen)) {
            return null;
        }
        try {
            java.lang.reflect.Field parentField = AbstractConfigScreen.class.getDeclaredField("parent");
            parentField.setAccessible(true);
            Screen grandparent = (Screen) parentField.get(current);
            /*? if >= 1.21.11 {*/
            return AutoConfigClient.getConfigScreen(ModConfig.class, grandparent).get();
            /*?} else {*/
            /*return AutoConfig.getConfigScreen(ModConfig.class, grandparent).get();
            *//*?}*/
        } catch (Exception e) {
            OffersHUD.logger.warn("Failed to rebuild config screen after position edit", e);
            return null;
        }
    }
}
