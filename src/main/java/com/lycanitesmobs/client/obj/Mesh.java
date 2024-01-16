package com.lycanitesmobs.client.obj;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import javax.vecmath.Vector3f;

public class Mesh {

    public int[] indices;
    public Vertex[] vertices;
    public Vector3f[] normals;
    private int vbo = -1;

    public int getVbo() {
        if (this.vbo != -1) {
            return this.vbo;
        }

        if (this.normals == null) {
            this.normals = new Vector3f[this.indices.length];
            for (int i = 0; i < this.normals.length / 3; i++) {
                Vector3f v1 = this.vertices[this.indices[i * 3]].getPos();
                Vector3f v2 = this.vertices[this.indices[i * 3 + 1]].getPos();
                Vector3f v3 = this.vertices[this.indices[i * 3 + 2]].getPos();
                Vector3f normal = calcNormal(v1, v2, v3);
                this.normals[i * 3] = normal;
                this.normals[i * 3 + 1] = normal;
                this.normals[i * 3 + 2] = normal;
            }
        }

        Tessellator tesselator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuffer();

        bufferBuilder.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX_NORMAL);
        for (int i = 0; i < this.indices.length; i++) {
            Vertex vertex = this.vertices[this.indices[i]];
            Vector3f normal = this.normals[i];
            bufferBuilder.pos(vertex.getPos().x, vertex.getPos().y, vertex.getPos().z);
            bufferBuilder.tex(vertex.getTexCoords().x, 1.0F - vertex.getTexCoords().y);
            bufferBuilder.normal(normal.x, normal.y, normal.z);
            bufferBuilder.endVertex();
        }
        bufferBuilder.finishDrawing();

        this.vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, bufferBuilder.getByteBuffer(), GL15.GL_STATIC_DRAW);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        bufferBuilder.reset();
        return this.vbo;
    }

    private static Vector3f calcNormal(Vector3f v1, Vector3f v2, Vector3f v3) {
        Vector3f output = new Vector3f();

        // Calculate Edges:
        Vector3f calU = new Vector3f(v2.x - v1.x, v2.y - v1.y, v2.z - v1.z);
        Vector3f calV = new Vector3f(v3.x - v1.x, v3.y - v1.y, v3.z - v1.z);

        // Cross Edges
        output.x = calU.y * calV.z - calU.z * calV.y;
        output.y = calU.z * calV.x - calU.x * calV.z;
        output.z = calU.x * calV.y - calU.y * calV.x;

        output.normalize();
        return output;
    }

    public void delete() {
        GL15.glDeleteBuffers(this.vbo);
        this.vbo = -1;
    }

}
