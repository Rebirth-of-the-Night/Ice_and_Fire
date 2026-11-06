package com.github.alexthe666.iceandfire.client.render.tile;

import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.client.render.item.RenderDeathWormGauntlet;
import com.github.alexthe666.iceandfire.client.render.item.RenderTrollWeapon;
import com.github.alexthe666.iceandfire.item.ItemDeathwormGauntlet;
import com.github.alexthe666.iceandfire.item.ItemTrollWeapon;
import com.github.alexthe666.iceandfire.item.ItemTideTrident;
import com.github.alexthe666.iceandfire.client.model.ModelTideTrident;
import com.github.alexthe666.iceandfire.client.render.entity.RenderTideTrident;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class IceAndFireTEISR extends TileEntityItemStackRenderer {

    private final RenderTrollWeapon renderTrollWeapon = new RenderTrollWeapon();
    private final RenderDeathWormGauntlet renderDeathWormGauntlet = new RenderDeathWormGauntlet();
    private final RenderDreadPortal renderDreadPortal = new RenderDreadPortal();
    private final ModelTideTrident trident = new ModelTideTrident();

    @Override
    public void renderByItem(ItemStack itemStackIn) {
        if (itemStackIn.getItem() instanceof ItemTideTrident && !ItemTideTrident.isEmpty(itemStackIn)) {
            boolean culling = GL11.glIsEnabled(GL11.GL_CULL_FACE);
            GlStateManager.pushMatrix();
            try {
                // The fin is a zero-thickness plane, visible from either side.
                GlStateManager.disableCull();
                Minecraft.getMinecraft().getTextureManager().bindTexture(RenderTideTrident.TEXTURE);
                GlStateManager.translate(0.5F, 0.5F, 0.5F);
                GlStateManager.rotate(180, 1, 0, 0);
                // Put the grip at the item origin; retain the projectile's full length.
                GlStateManager.translate(0, -14.0F / 16.0F, 0);
                trident.render(null, 0, 0, 0, 0, 0, 0.0625F);
            } finally {
                if (culling) GlStateManager.enableCull();
                else GlStateManager.disableCull();
                GlStateManager.popMatrix();
            }
        }
        if (itemStackIn.getItem() instanceof ItemTrollWeapon) {
            ItemTrollWeapon weaponItem = (ItemTrollWeapon) itemStackIn.getItem();
            renderTrollWeapon.renderItem(weaponItem.weapon, 0, 0, 0, 0.0F, 0, 0.0F);
        }
        if (itemStackIn.getItem() instanceof ItemDeathwormGauntlet) {
            renderDeathWormGauntlet.renderItem(itemStackIn, 0, 0, 0, 0.0F, 0, 0.0F);
        }
        if (itemStackIn.getItem() == Item.getItemFromBlock(IafBlockRegistry.dread_portal)) {
            renderDreadPortal.render(null, 0, 0, 0, 0.0F, 0, 0.0F);
        }
    }
}
