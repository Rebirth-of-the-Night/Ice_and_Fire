package com.lycanitesmobs.client.obj;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector4f;

public class VBOModel extends TessellatorModel
{

    public VBOModel(ResourceLocation resourceLocation) {
        super(resourceLocation);
    }

    @Override
    public void renderGroupImpl(ObjObject obj, Vector4f color, Vector2f textureOffset, VertexFormat vertexFormat) {
        GlStateManager.color(color.x, color.y, color.z, color.w);

        boolean hasTextureOffset = textureOffset.x != 0.0F || textureOffset.y != 0.0F; 
        if (hasTextureOffset) {
            GlStateManager.matrixMode(GL11.GL_TEXTURE);
            GlStateManager.pushMatrix();
            GlStateManager.translate(textureOffset.x * 0.01D, -textureOffset.y * 0.01D, 0.0D);
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        }

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, obj.mesh.getVbo());
        GL11.glVertexPointer(3, GL11.GL_FLOAT, 24, 0);
        GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 24, 12);
        GL11.glNormalPointer(GL11.GL_BYTE, 24, 20);
        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);

        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, obj.mesh.indices.length);

        GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        if (hasTextureOffset) {
            GlStateManager.matrixMode(GL11.GL_TEXTURE);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

}
