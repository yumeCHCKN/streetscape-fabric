package net.zphyghtning.streetscape.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.zphyghtning.streetscape.Streetscape;
import net.zphyghtning.streetscape.screen.RoadworksScreenHandler;

import java.util.List;

@Environment(EnvType.CLIENT)
public class RoadworksScreen extends HandledScreen<RoadworksScreenHandler> {
    private static final Identifier SCROLLER_TEXTURE = Identifier.ofVanilla("container/stonecutter/scroller");
    private static final Identifier SCROLLER_DISABLED_TEXTURE = Identifier.ofVanilla("container/stonecutter/scroller_disabled");
    private static final Identifier RECIPE_SELECTED_TEXTURE = Identifier.ofVanilla("container/stonecutter/recipe_selected");
    private static final Identifier RECIPE_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla("container/stonecutter/recipe_highlighted");
    private static final Identifier RECIPE_TEXTURE = Identifier.ofVanilla("container/stonecutter/recipe");

    private static final Identifier TEXTURE = Identifier.of(Streetscape.MOD_ID, "textures/gui/container/roadworks_table.png");

    private static final int RECIPE_LIST_COLUMNS = 4;
    private static final int RECIPE_LIST_ROWS = 3;
    private static final int RECIPE_ENTRY_WIDTH = 16;
    private static final int RECIPE_ENTRY_HEIGHT = 18;
    private static final int SCROLLBAR_AREA_HEIGHT = 54;
    private static final int RECIPE_LIST_OFFSET_X = 52;
    private static final int RECIPE_LIST_OFFSET_Y = 14;

    private float scrollAmount;
    private boolean mouseClicked;
    private int scrollOffset;
    private boolean canCraft;

    public RoadworksScreen(RoadworksScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 166;
        this.titleY = 4;
        this.playerInventoryTitleY = 72;
        handler.setContentsChangedListener(this::onInventoryChange);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = this.x;
        int y = this.y;
        context.drawTexture(TEXTURE, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);

        int k = (int)(41.0F * this.scrollAmount);
        Identifier identifier = this.shouldScroll() ? SCROLLER_TEXTURE : SCROLLER_DISABLED_TEXTURE;
        context.drawGuiTexture(identifier, x + 119, y + 15 + k, 12, 15);

        int l = x + RECIPE_LIST_OFFSET_X;
        int m = y + RECIPE_LIST_OFFSET_Y;
        int n = this.scrollOffset + 12;
        this.renderRecipeBackground(context, mouseX, mouseY, l, m, n);
        this.renderRecipeIcons(context, l, m, n);
    }

    @Override
    protected void drawMouseoverTooltip(DrawContext context, int x, int y) {
        super.drawMouseoverTooltip(context, x, y);
        if (this.canCraft) {
            int i = this.x + RECIPE_LIST_OFFSET_X;
            int j = this.y + RECIPE_LIST_OFFSET_Y;
            int k = this.scrollOffset + 12;
            List<ItemStack> list = this.handler.getAvailableRecipes();

            for (int l = this.scrollOffset; l < k && l < list.size(); ++l) {
                int m = l - this.scrollOffset;
                int n = i + (m % RECIPE_LIST_COLUMNS) * RECIPE_ENTRY_WIDTH;
                int o = j + (m / RECIPE_LIST_COLUMNS) * RECIPE_ENTRY_HEIGHT + 2;
                if (x >= n && x < n + 16 && y >= o && y < o + 18) {
                    context.drawItemTooltip(this.textRenderer, list.get(l), x, y);
                }
            }
        }
    }

    private void renderRecipeBackground(DrawContext context, int mouseX, int mouseY, int x, int y, int scrollOffset) {
        for (int i = this.scrollOffset; i < scrollOffset && i < this.handler.getAvailableRecipeCount(); ++i) {
            int j = i - this.scrollOffset;
            int k = x + (j % RECIPE_LIST_COLUMNS) * RECIPE_ENTRY_WIDTH;
            int l = j / RECIPE_LIST_COLUMNS;
            int m = y + l * RECIPE_ENTRY_HEIGHT + 2;
            Identifier identifier;
            if (i == this.handler.getSelectedRecipe()) {
                identifier = RECIPE_SELECTED_TEXTURE;
            } else if (mouseX >= k && mouseY >= m && mouseX < k + 16 && mouseY < m + 18) {
                identifier = RECIPE_HIGHLIGHTED_TEXTURE;
            } else {
                identifier = RECIPE_TEXTURE;
            }
            context.drawGuiTexture(identifier, k, m - 1, 16, 18);
        }
    }

    private void renderRecipeIcons(DrawContext context, int x, int y, int scrollOffset) {
        List<ItemStack> list = this.handler.getAvailableRecipes();
        for (int i = this.scrollOffset; i < scrollOffset && i < list.size(); ++i) {
            int j = i - this.scrollOffset;
            int k = x + (j % RECIPE_LIST_COLUMNS) * RECIPE_ENTRY_WIDTH;
            int l = j / RECIPE_LIST_COLUMNS;
            int m = y + l * RECIPE_ENTRY_HEIGHT + 2;
            context.drawItem(list.get(i), k, m);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.mouseClicked = false;
        if (this.canCraft) {
            int i = this.x + RECIPE_LIST_OFFSET_X;
            int j = this.y + RECIPE_LIST_OFFSET_Y;
            int k = this.scrollOffset + 12;

            for (int l = this.scrollOffset; l < k && l < this.handler.getAvailableRecipeCount(); ++l) {
                int m = l - this.scrollOffset;
                double d = mouseX - (double)(i + (m % RECIPE_LIST_COLUMNS) * RECIPE_ENTRY_WIDTH);
                double e = mouseY - (double)(j + (m / RECIPE_LIST_COLUMNS) * RECIPE_ENTRY_HEIGHT);
                if (d >= 0.0 && e >= 0.0 && d < 16.0 && e < 18.0 && this.handler.onButtonClick(this.client.player, l)) {
                    this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
                    this.client.interactionManager.clickButton(this.handler.syncId, l);
                    return true;
                }
            }

            i = this.x + 119;
            j = this.y + 9;
            if (mouseX >= (double)i && mouseX < (double)(i + 12) && mouseY >= (double)j && mouseY < (double)(j + SCROLLBAR_AREA_HEIGHT)) {
                this.mouseClicked = true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.mouseClicked && this.shouldScroll()) {
            int i = this.y + 14;
            int j = i + SCROLLBAR_AREA_HEIGHT;
            this.scrollAmount = ((float)mouseY - (float)i - 7.5F) / ((float)(j - i) - 15.0F);
            this.scrollAmount = MathHelper.clamp(this.scrollAmount, 0.0F, 1.0F);
            this.scrollOffset = (int)((double)(this.scrollAmount * (float)this.getMaxScroll()) + 0.5) * RECIPE_LIST_COLUMNS;
            return true;
        } else {
            return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (this.shouldScroll()) {
            int i = this.getMaxScroll();
            float f = (float)verticalAmount / (float)i;
            this.scrollAmount = MathHelper.clamp(this.scrollAmount - f, 0.0F, 1.0F);
            this.scrollOffset = (int)((double)(this.scrollAmount * (float)i) + 0.5) * RECIPE_LIST_COLUMNS;
        }
        return true;
    }

    private boolean shouldScroll() {
        return this.canCraft && this.handler.getAvailableRecipeCount() > 12;
    }

    protected int getMaxScroll() {
        return (this.handler.getAvailableRecipeCount() + RECIPE_LIST_COLUMNS - 1) / RECIPE_LIST_COLUMNS - RECIPE_LIST_ROWS;
    }

    private void onInventoryChange() {
        this.canCraft = this.handler.canCraft();
        if (!this.canCraft) {
            this.scrollAmount = 0.0F;
            this.scrollOffset = 0;
        }
    }
}
