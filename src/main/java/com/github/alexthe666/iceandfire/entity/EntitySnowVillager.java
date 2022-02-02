package com.github.alexthe666.iceandfire.entity;

import net.ilexiconn.llibrary.server.entity.EntityPropertiesHandler;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Objects;

public class EntitySnowVillager extends EntityVillager {

    private net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession prof;

    public EntitySnowVillager(World worldIn) {
        super(worldIn);
    }

    public EntitySnowVillager(World worldIn, int profession) {
        super(worldIn, profession);
    }

    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        StoneEntityProperties properties = EntityPropertiesHandler.INSTANCE.getProperties(this, StoneEntityProperties.class);
        if (properties != null && properties.isStone) {
            return false;
        }
        return super.processInteract(player, hand);
    }

    public void setProfession(int professionId) {
        if (professionId > 2) {
            professionId = 2;
        }
        this.dataManager.set(PROFESSION, professionId);
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        if (HAND_STATES.equals(key) && this.world.isRemote) {
            if (this.isHandActive() && this.activeItemStack.isEmpty()) {
                this.activeItemStack = this.getHeldItem(this.getActiveHand());

                if (!this.activeItemStack.isEmpty()) {
                    this.activeItemStackUseCount = this.activeItemStack.getMaxItemUseDuration();
                }
            } else if (!this.isHandActive() && !this.activeItemStack.isEmpty()) {
                this.activeItemStack = ItemStack.EMPTY;
                this.activeItemStackUseCount = 0;
            }
        }

        if (Objects.equals(BABY, key)) {
            this.setScaleForAge(this.isChild());
        }
    }

    public void onDeath(DamageSource cause) {
        if (cause.getTrueSource() != null && cause.getTrueSource() instanceof EntityZombie && (this.world.getDifficulty() == EnumDifficulty.NORMAL || this.world.getDifficulty() == EnumDifficulty.HARD)) {
            return;
        } else {
            super.onDeath(cause);
        }
    }

    @SuppressWarnings("deprecation")
    public void setProfession(net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession prof) {
        if (IafVillagerRegistry.INSTANCE.professions.containsValue(prof)) {
            this.setProfession(net.minecraftforge.fml.common.registry.VillagerRegistry.getId(prof));
        } else {
            IafVillagerRegistry.INSTANCE.setRandomProfession(this, this.world.rand);
        }
    }

    public EntityVillager createChild(EntityAgeable ageable) {
        EntitySnowVillager entityvillager = new EntitySnowVillager(this.world);
        entityvillager.onInitialSpawn(this.world.getDifficultyForLocation(new BlockPos(entityvillager)), null);
        return entityvillager;
    }

    public ITextComponent getDisplayName() {
        Team team = this.getTeam();
        String s = this.getCustomNameTag();

        if (s != null && !s.isEmpty()) {
            TextComponentString textcomponentstring = new TextComponentString(ScorePlayerTeam.formatPlayerName(team, s));
            textcomponentstring.getStyle().setHoverEvent(this.getHoverEvent());
            textcomponentstring.getStyle().setInsertion(this.getCachedUniqueIdString());
            return textcomponentstring;
        } else {
            String s1 = this.getProfessionForge().getCareer(0).getName();
            {
                ITextComponent itextcomponent = new TextComponentTranslation("entity.Villager." + s1);
                itextcomponent.getStyle().setHoverEvent(this.getHoverEvent());
                itextcomponent.getStyle().setInsertion(this.getCachedUniqueIdString());

                if (team != null) {
                    itextcomponent.getStyle().setColor(team.getColor());
                }

                return itextcomponent;
            }
        }
    }

    public net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession getProfessionForge() {
        if (this.prof == null) {
            String p = this.getEntityData().getString("ProfessionName");
            if (p.isEmpty()) {
                this.prof = IafVillagerRegistry.INSTANCE.professions.get(this.getRNG().nextInt(3));
            } else {
                this.prof = IafVillagerRegistry.INSTANCE.professions.get(intFromProfesion(p));
            }
            
            this.careerId = 1;
        }
        return this.prof;
    }

    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.setProfession(compound.getInteger("Profession"));
        if (compound.hasKey("ProfessionName")) {
            net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession p =
                    IafVillagerRegistry.INSTANCE.professions.get(intFromProfesion(compound.getString("ProfessionName")));
            if (p == null)
                p = IafVillagerRegistry.INSTANCE.professions.get(0);
            this.setProfession(p);
        }

    }

    public IEntityLivingData finalizeMobSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData data, boolean forgeCheck) {
        this.prof = IafVillagerRegistry.INSTANCE.professions.get(this.getRNG().nextInt(3));
        return data;
    }


    private int intFromProfesion(String prof) {
        if (prof.contains("fisherman")) {
            return 0;
        }
        if (prof.contains("craftsman")) {
            return 1;
        }
        if (prof.contains("shaman")) {
            return 2;
        }
        return 0;
    }

}
