package net.naari3.offershud.config;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
/*? if >= 26.1 {*/
import net.minecraft.client.gui.GuiGraphicsExtractor;
/*?} else {*/
/*import net.minecraft.client.gui.GuiGraphics;
*//*?}*/
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.network.chat.Component;
import net.naari3.offershud.gui.OffersPositionScreen;

/**
 * A Cloth Config list entry that renders a single button which opens the
 * graphical HUD position editor ({@link OffersPositionScreen}).
 */
public class PositionButtonEntry extends AbstractConfigListEntry<Object> {
    private final Button button;
    private final List<Button> widgets;

    public PositionButtonEntry(Component buttonText) {
        super(Component.literal("editPosition"), false);
        this.button = Button.builder(buttonText, b -> {
            Minecraft mc = Minecraft.getInstance();
            /*? if >= 26.2 {*/
            /*mc.gui.setScreen(new OffersPositionScreen(mc.gui.screen()));
            *//*?} else {*/
            mc.setScreen(new OffersPositionScreen(mc.screen));
            /*?}*/
        }).bounds(0, 0, 150, 20).build();
        this.widgets = Collections.singletonList(button);
    }

    @Override
    public int getItemHeight() {
        return 24;
    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public Optional<Object> getDefaultValue() {
        return Optional.empty();
    }

    @Override
    public Iterator<String> getSearchTags() {
        return Collections.emptyIterator();
    }

    @Override
    @Nullable
    public ComponentPath nextFocusPath(FocusNavigationEvent event) {
        return button.nextFocusPath(event);
    }

    @Override
    public List<? extends NarratableEntry> narratables() {
        return widgets;
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return widgets;
    }

    /*? if >= 26.1 {*/
    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int index, int y, int x, int entryWidth,
            int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        super.extractRenderState(graphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
        button.setX(x);
        button.setY(y);
        button.setWidth(Math.min(entryWidth, 200));
        button.extractRenderState(graphics, mouseX, mouseY, delta);
    }
    /*?} else {*/
    /*@Override
    public void render(GuiGraphics graphics, int index, int y, int x, int entryWidth,
            int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        super.render(graphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
        button.setX(x);
        button.setY(y);
        button.setWidth(Math.min(entryWidth, 200));
        button.render(graphics, mouseX, mouseY, delta);
    }
    *//*?}*/
}
