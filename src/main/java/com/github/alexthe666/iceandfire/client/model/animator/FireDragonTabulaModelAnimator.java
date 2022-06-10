package com.github.alexthe666.iceandfire.client.model.animator;

import java.util.Arrays;

import com.github.alexthe666.iceandfire.client.model.util.DragonAnimationsLibrary;
import com.github.alexthe666.iceandfire.client.model.util.EnumDragonAnimations;
import com.github.alexthe666.iceandfire.client.model.util.EnumDragonModelTypes;
import com.github.alexthe666.iceandfire.client.model.util.EnumDragonPoses;
import com.github.alexthe666.iceandfire.client.model.util.IceAndFireTabulaModel;
import com.github.alexthe666.iceandfire.entity.EntityFireDragon;

/*
 * Code directly taken from 1.16 IaF, I own no rights over it.
 */
@SuppressWarnings("deprecation")
public class FireDragonTabulaModelAnimator extends DragonTabulaModelAnimator<EntityFireDragon> {
	public FireDragonTabulaModelAnimator() {
		super(EnumDragonAnimations.GROUND_POSE.firedragon_model);
		
		this.walkPoses = Arrays.asList(EnumDragonAnimations.WALK1.firedragon_model, EnumDragonAnimations.WALK2.firedragon_model, EnumDragonAnimations.WALK3.firedragon_model, EnumDragonAnimations.WALK4.firedragon_model);
		this.flyPoses = Arrays.asList(EnumDragonAnimations.FLIGHT1.firedragon_model, EnumDragonAnimations.FLIGHT2.firedragon_model, EnumDragonAnimations.FLIGHT3.firedragon_model, EnumDragonAnimations.FLIGHT4.firedragon_model, EnumDragonAnimations.FLIGHT5.firedragon_model, EnumDragonAnimations.FLIGHT6.firedragon_model);
		this.swimPoses = walkPoses;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected IceAndFireTabulaModel<EntityFireDragon> getModel(EnumDragonPoses pose) {
		return DragonAnimationsLibrary.getModel(pose, EnumDragonModelTypes.FIRE_DRAGON_MODEL);
	}
}
