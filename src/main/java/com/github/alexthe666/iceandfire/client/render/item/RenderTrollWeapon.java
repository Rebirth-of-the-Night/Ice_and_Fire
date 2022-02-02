package com.github.alexthe666.iceandfire.client.render.item;

import com.github.alexthe666.iceandfire.client.model.ModelTrollWeapon;
import com.github.alexthe666.iceandfire.enums.EnumTroll;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import org.lwjgl.opengl.GL11;

public class RenderTrollWeapon {
    private static final ModelBase MODEL = new ModelTrollWeapon();

    public void renderItem(EnumTroll.Weapon weapon, double x, double y, double z, float f, int f1, float alpha) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y - 0.75F, (float) z + 0.5F);
        GL11.glPushMatrix();
        Minecraft.getMinecraft().getTextureManager().bindTexture(weapon.TEXTURE);
        GL11.glPushMatrix();
        MODEL.render(null, 0, 0, 0, 0, 0, 0.0625F);
        GL11.glPopMatrix();
        GL11.glPopMatrix();
        GL11.glPopMatrix();
    }
}
