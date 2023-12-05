package com.github.alexthe666.iceandfire.client.model.util;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntitySeaSerpent;
import net.ilexiconn.llibrary.client.model.tabula.TabulaModelHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public enum EnumSeaSerpentAnimations {
    T_POSE(""),
    SWIM1("Swim1"),
    SWIM2("Swim2"),
    SWIM3("Swim3"),
    SWIM4("Swim4"),
    SWIM5("Swim5"),
    SWIM6("Swim6"),
    BITE1("Bite1"),
    BITE2("Bite2"),
    BITE3("Bite3"),
    ROAR1("Roar1"),
    ROAR2("Roar2"),
    ROAR3("Roar3"),
    DEAD("Dead"),
    JUMPING1("Jumping1"),
    JUMPING2("Jumping2");

    @SideOnly(Side.CLIENT)
    public IceAndFireTabulaModel<EntitySeaSerpent> seaserpent_model;
    private final String fileSuffix;

    EnumSeaSerpentAnimations(String fileSuffix) {
        this.fileSuffix = fileSuffix;
    }

    @SideOnly(Side.CLIENT)
    public static void initializeSerpentModels() {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            for (EnumSeaSerpentAnimations animation : values()) {
                try {
                    animation.seaserpent_model = new IceAndFireTabulaModel<EntitySeaSerpent>(TabulaModelHandler.INSTANCE.loadTabulaModel("/assets/iceandfire/models/tabula/seaserpent/seaserpent" + animation.fileSuffix));
                } catch (Exception e) {
                    IceAndFire.logger.warn("sea serpent model at: seaserpent" + animation.fileSuffix + ".tbl doesn't exist!");
                    e.printStackTrace();
                }
            }
        }

    }
}
