package com.github.alexthe666.iceandfire.client.model.animator;

import com.github.alexthe666.iceandfire.client.model.util.*;
import com.github.alexthe666.iceandfire.entity.EntityIceDragon;

import java.util.Arrays;

/*
 * Code directly taken from 1.16 IaF, I own no rights over it.
 */
@SuppressWarnings("deprecation")
public class IceDragonTabulaModelAnimator extends DragonTabulaModelAnimator<EntityIceDragon>{
	public IceDragonTabulaModelAnimator() {
		super(EnumDragonAnimations.GROUND_POSE.icedragon_model);
		
		this.walkPoses = Arrays.asList(EnumDragonAnimations.WALK1.icedragon_model, EnumDragonAnimations.WALK2.icedragon_model, EnumDragonAnimations.WALK3.icedragon_model, EnumDragonAnimations.WALK4.icedragon_model );
		this.flyPoses = Arrays.asList(EnumDragonAnimations.FLIGHT1.icedragon_model, EnumDragonAnimations.FLIGHT2.icedragon_model, EnumDragonAnimations.FLIGHT3.icedragon_model, EnumDragonAnimations.FLIGHT4.icedragon_model, EnumDragonAnimations.FLIGHT5.icedragon_model, EnumDragonAnimations.FLIGHT6.icedragon_model);
		this.swimPoses = Arrays.asList(EnumDragonAnimations.SWIM1.icedragon_model, EnumDragonAnimations.SWIM2.icedragon_model, EnumDragonAnimations.SWIM3.icedragon_model, EnumDragonAnimations.SWIM4.icedragon_model, EnumDragonAnimations.SWIM5.icedragon_model);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected IceAndFireTabulaModel<EntityIceDragon> getModel(EnumDragonPoses pose) {
		return DragonAnimationsLibrary.getModel(pose, EnumDragonModelTypes.ICE_DRAGON_MODEL);
	}
}
