package com.github.alexthe666.iceandfire.item;

import java.util.List;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.enums.EnumToolEffect;
import com.github.alexthe666.iceandfire.util.ItemUtil;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemAlchemySword extends ItemSword {
	private final EnumToolEffect toolEffect;

    public ItemAlchemySword(ToolMaterial toolmaterial, String gameName, String name, EnumToolEffect toolEffect) {
        super(toolmaterial);
        this.setTranslationKey(name);
        this.setCreativeTab(IceAndFire.TAB_ITEMS);
        this.setRegistryName(IceAndFire.MODID, gameName); 
        this.toolEffect = toolEffect;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    	switch(this.toolEffect) {
    	case FIRE:
    		ItemUtil.getFireDragonsteelComment(tooltip, true);
    		break;
    	case ICE:
    		ItemUtil.getIceDragonsteelComment(tooltip, true);
    		break;
    	case LIGHTNING:
    		ItemUtil.getLightningDragonsteelComment(tooltip, true);
    		break;
        default:
            break;
    	}
    }

    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    public EnumToolEffect getToolEffect() {
        return this.toolEffect;
    }
}
