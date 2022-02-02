package com.github.alexthe666.iceandfire.client.model.util;

/*
 * Code directly taken from 1.16 IaF, I own no rights over it.
 */
public enum EnumDragonModelTypes implements IEnumDragonModelTypes {
    FIRE_DRAGON_MODEL("fire"),
    ICE_DRAGON_MODEL("ice"),
    LIGHTNING_DRAGON_MODEL("lightning");

    String modelType;
    EnumDragonModelTypes(String modelType) {
        this.modelType = modelType;
    }
    @Override
    public String getModelType() {
        return modelType;
    }
}