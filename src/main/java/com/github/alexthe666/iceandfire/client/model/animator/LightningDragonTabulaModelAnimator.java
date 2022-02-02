package com.github.alexthe666.iceandfire.client.model.animator;

import java.util.Arrays;

import com.github.alexthe666.iceandfire.client.model.util.DragonAnimationsLibrary;
import com.github.alexthe666.iceandfire.client.model.util.EnumDragonAnimations;
import com.github.alexthe666.iceandfire.client.model.util.EnumDragonModelTypes;
import com.github.alexthe666.iceandfire.client.model.util.EnumDragonPoses;
import com.github.alexthe666.iceandfire.client.model.util.IceAndFireTabulaModel;
import com.github.alexthe666.iceandfire.entity.EntityLightningDragon;

/*
 * Code directly taken from 1.16 IaF, I own no rights over it.
 */
public class LightningDragonTabulaModelAnimator extends DragonTabulaModelAnimator<EntityLightningDragon> {
	public LightningDragonTabulaModelAnimator() {
        super(EnumDragonAnimations.GROUND_POSE.lightningdragon_model);

        this.walkPoses = Arrays.asList(EnumDragonAnimations.WALK1.lightningdragon_model, EnumDragonAnimations.WALK2.lightningdragon_model, EnumDragonAnimations.WALK3.lightningdragon_model, EnumDragonAnimations.WALK4.lightningdragon_model);
        this.flyPoses = Arrays.asList(EnumDragonAnimations.FLIGHT1.lightningdragon_model, EnumDragonAnimations.FLIGHT2.lightningdragon_model, EnumDragonAnimations.FLIGHT3.lightningdragon_model, EnumDragonAnimations.FLIGHT4.lightningdragon_model, EnumDragonAnimations.FLIGHT5.lightningdragon_model, EnumDragonAnimations.FLIGHT6.lightningdragon_model);
        this.swimPoses = walkPoses;
	}

	@Override
	protected IceAndFireTabulaModel getModel(EnumDragonPoses pose) {
        return DragonAnimationsLibrary.getModel(pose, EnumDragonModelTypes.LIGHTNING_DRAGON_MODEL);
	}
}
