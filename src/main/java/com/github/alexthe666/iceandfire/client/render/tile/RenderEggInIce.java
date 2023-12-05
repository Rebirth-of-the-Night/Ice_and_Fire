package com.github.alexthe666.iceandfire.client.render.tile;

import com.github.alexthe666.iceandfire.client.model.ModelDragonEgg;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityEggInIce;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import org.lwjgl.opengl.GL11;

public class RenderEggInIce extends TileEntitySpecialRenderer<TileEntityEggInIce> {

    @Override
    public void render(TileEntityEggInIce egg, double x, double y, double z, float f, int f1, float alpha) {
        ModelDragonEgg model = new ModelDragonEgg();
        if (egg.type != null) {
            GL11.glPushMatrix();
            GL11.glTranslatef((float) x + 0.5F, (float) y - 0.75F, (float) z + 0.5F);
            GL11.glPushMatrix();
            this.bindTexture(RenderPodium.getEggTexture(egg.type));
            GL11.glPushMatrix();
            model.renderFrozen(egg);
            GL11.glPopMatrix();
            GL11.glPopMatrix();
            GL11.glPopMatrix();
        }
    }

}
