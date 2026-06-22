package net.zphyghtning.streetscape.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.world.World;
import net.zphyghtning.streetscape.block.ModBlocks;

import java.util.ArrayList;
import java.util.List;

public class RoadworksScreenHandler extends ScreenHandler {
    private final ScreenHandlerContext context;
    private final Property selectedRecipe = Property.create();
    private final World world;
    private final List<ItemStack> availableRecipes = new ArrayList<>();
    private final Inventory input = new SimpleInventory(2) {
        @Override
        public void markDirty() {
            super.markDirty();
            RoadworksScreenHandler.this.onContentChanged(this);
        }
    };
    private final CraftingResultInventory output = new CraftingResultInventory();

    public final Slot blockSlot;
    public final Slot dyeSlot;
    public final Slot outputSlot;

    public RoadworksScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
    }

    public RoadworksScreenHandler(int syncId, PlayerInventory playerInventory, final ScreenHandlerContext context) {
        super(ModScreenHandlers.ROADWORKS_SCREEN_HANDLER, syncId);
        this.context = context;
        this.world = playerInventory.player.getWorld();

        this.blockSlot = this.addSlot(new Slot(this.input, 0, 18, 22) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isOf(ModBlocks.ASPHALT.asItem());
            }
        });

        this.dyeSlot = this.addSlot(new Slot(this.input, 1, 18, 44) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.getItem() instanceof DyeItem;
            }
        });

        this.outputSlot = this.addSlot(new Slot(this.output, 2, 143, 33) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }

            @Override
            public void onTakeItem(PlayerEntity player, ItemStack stack) {
                stack.onCraftByPlayer(player.getWorld(), player, stack.getCount());
                ItemStack blockStack = RoadworksScreenHandler.this.blockSlot.takeStack(1);
                ItemStack dyeStack = RoadworksScreenHandler.this.dyeSlot.takeStack(1);

                if (!blockStack.isEmpty() && !dyeStack.isEmpty()) {
                    RoadworksScreenHandler.this.populateResult();
                }

                context.run((world, pos) -> {
                    world.playSound(null, pos, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                });
                super.onTakeItem(player, stack);
            }
        });

        // Player Inventory
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        // Player Hotbar
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }

        this.addProperty(this.selectedRecipe);
    }

    public int getSelectedRecipe() {
        return this.selectedRecipe.get();
    }

    public List<ItemStack> getAvailableRecipes() {
        return this.availableRecipes;
    }

    public int getAvailableRecipeCount() {
        return this.availableRecipes.size();
    }

    public boolean canCraft() {
        return this.blockSlot.hasStack() && this.dyeSlot.hasStack() && !this.availableRecipes.isEmpty();
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(this.context, player, ModBlocks.ROADWORKS_TABLE);
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (id >= 0 && id < this.availableRecipes.size()) {
            this.selectedRecipe.set(id);
            this.populateResult();
            return true;
        }
        return false;
    }

    private Runnable contentsChangedListener = () -> {};

    public void setContentsChangedListener(Runnable contentsChangedListener) {
        this.contentsChangedListener = contentsChangedListener;
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        ItemStack blockStack = this.blockSlot.getStack();
        ItemStack dyeStack = this.dyeSlot.getStack();
        if (blockStack.isEmpty() || dyeStack.isEmpty()) {
            this.availableRecipes.clear();
            this.selectedRecipe.set(-1);
            this.outputSlot.setStackNoCallbacks(ItemStack.EMPTY);
        } else {
            this.updateAvailableRecipes(blockStack, dyeStack);
        }
        this.contentsChangedListener.run();
        this.sendContentUpdates();
    }

    private void updateAvailableRecipes(ItemStack blockStack, ItemStack dyeStack) {
        this.availableRecipes.clear();
        if (blockStack.isOf(ModBlocks.ASPHALT.asItem()) && dyeStack.getItem() instanceof DyeItem dyeItem) {
            DyeColor color = dyeItem.getColor();
            if (color == DyeColor.WHITE) {
                this.availableRecipes.add(new ItemStack(ModBlocks.WHITE_DIAGONAL_STRIPE_MARKING));
                this.availableRecipes.add(new ItemStack(ModBlocks.WHITE_ARROW_MARKING));
                this.availableRecipes.add(new ItemStack(ModBlocks.WHITE_SINGLE_LINE_MARKING));
            } else if (color == DyeColor.YELLOW) {
                this.availableRecipes.add(new ItemStack(ModBlocks.YELLOW_DIAGONAL_STRIPE_MARKING));
                this.availableRecipes.add(new ItemStack(ModBlocks.YELLOW_ARROW_MARKING));
                this.availableRecipes.add(new ItemStack(ModBlocks.YELLOW_SINGLE_LINE_MARKING));
            }
        }

        int selected = this.selectedRecipe.get();
        if (selected >= 0 && selected < this.availableRecipes.size()) {
            this.populateResult();
        } else {
            this.selectedRecipe.set(-1);
            this.outputSlot.setStackNoCallbacks(ItemStack.EMPTY);
        }
    }

    private void populateResult() {
        int selected = this.selectedRecipe.get();
        if (selected >= 0 && selected < this.availableRecipes.size()) {
            ItemStack outputStack = this.availableRecipes.get(selected).copy();
            this.outputSlot.setStackNoCallbacks(outputStack);
        } else {
            this.outputSlot.setStackNoCallbacks(ItemStack.EMPTY);
        }
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.output.removeStack(2);
        this.context.run((world, pos) -> this.dropInventory(player, this.input));
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasStack()) {
            ItemStack slotStack = slot.getStack();
            Item item = slotStack.getItem();
            itemStack = slotStack.copy();

            if (slotIndex == 2) { // Output slot
                slotStack.onCraftByPlayer(player.getWorld(), player, slotStack.getCount());
                if (!this.insertItem(slotStack, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickTransfer(slotStack, itemStack);
            } else if (slotIndex == 0 || slotIndex == 1) { // Input slots
                if (!this.insertItem(slotStack, 3, 39, false)) {
                    return ItemStack.EMPTY;
                }
            } else { // Player inventory
                if (slotStack.isOf(ModBlocks.ASPHALT.asItem())) {
                    if (!this.insertItem(slotStack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (item instanceof DyeItem) {
                    if (!this.insertItem(slotStack, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (slotIndex >= 3 && slotIndex < 30) { // Main inventory
                    if (!this.insertItem(slotStack, 30, 39, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (slotIndex >= 30 && slotIndex < 39 && !this.insertItem(slotStack, 3, 30, false)) { // Hotbar
                    return ItemStack.EMPTY;
                }
            }

            if (slotStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            if (slotStack.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTakeItem(player, slotStack);
            this.sendContentUpdates();
        }

        return itemStack;
    }
}
