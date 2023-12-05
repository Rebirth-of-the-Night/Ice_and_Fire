package com.github.alexthe666.iceandfire.client.model.animator;

import com.github.alexthe666.iceandfire.client.model.util.*;
import com.github.alexthe666.iceandfire.entity.EntityLightningDragon;

import java.util.Arrays;

/*
 * Code directly taken from 1.16 IaF, I own no rights over it.
 */
@SuppressWarnings("deprecation")
public class LightningDragonTabulaModelAnimator extends DragonTabulaModelAnimator<EntityLightningDragon> {
	public LightningDragonTabulaModelAnimator() {
        super(EnumDragonAnimations.GROUND_POSE.lightningdragon_model);

        this.walkPoses = Arrays.asList(EnumDragonAnimations.WALK1.lightningdragon_model, EnumDragonAnimations.WALK2.lightningdragon_model, EnumDragonAnimations.WALK3.lightningdragon_model, EnumDragonAnimations.WALK4.lightningdragon_model);
        this.flyPoses = Arrays.asList(EnumDragonAnimations.FLIGHT1.lightningdragon_model, EnumDragonAnimations.FLIGHT2.lightningdragon_model, EnumDragonAnimations.FLIGHT3.lightningdragon_model, EnumDragonAnimations.FLIGHT4.lightningdragon_model, EnumDragonAnimations.FLIGHT5.lightningdragon_model, EnumDragonAnimations.FLIGHT6.lightningdragon_model);
        this.swimPoses = walkPoses;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected IceAndFireTabulaModel<EntityLightningDragon> getModel(EnumDragonPoses pose) {
        return DragonAnimationsLibrary.getModel(pose, EnumDragonModelTypes.LIGHTNING_DRAGON_MODEL);
	}
}
