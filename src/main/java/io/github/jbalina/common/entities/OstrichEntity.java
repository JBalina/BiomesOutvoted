package io.github.jbalina.common.entities;

import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;

import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

import io.github.jbalina.BiomesOutvotedMod;
import io.github.jbalina.core.init.ModSounds;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effects;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.TransportationHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class OstrichEntity extends AnimalEntity{
	private static final DataParameter<Boolean> SADDLED = EntityDataManager.createKey(OstrichEntity.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Byte> STATUS = EntityDataManager.createKey(OstrichEntity.class, DataSerializers.BYTE);
	private static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager.createKey(OstrichEntity.class, DataSerializers.OPTIONAL_UNIQUE_ID);
    private static final Ingredient BREEDING_ITEMS = Ingredient.fromItems(Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.CARROT, Items.BEETROOT);
    protected boolean ostrichJumping;
    protected int temper;
    
    
    public OstrichEntity(EntityType<? extends OstrichEntity> type, World worldIn)
    {
    	super(type, worldIn);
    	this.stepHeight = 1.0F;
    }
    
	protected void registerGoals() {
		this.goalSelector.addGoal(1, new PanicGoal(this, 1.2D));
		//this.goalSelector.addGoal(1, new RunAroundLikeCrazyGoal(this, 1.2D));
		this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D, OstrichEntity.class));
		this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.0D));
		this.goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 0.7D));
		this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
		this.initExtraAI();
    }

	protected void initExtraAI() {
		this.goalSelector.addGoal(0, new SwimGoal(this));
	}
	
	public static AttributeModifierMap.MutableAttribute setCustomAttributes() {
    	return MobEntity.func_233666_p_()
    			.createMutableAttribute(Attributes.MAX_HEALTH, 10.0D)
    			.createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.3D)
    			.createMutableAttribute(Attributes.ATTACK_DAMAGE, 5.0D);
    }
	
	protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
		return sizeIn.height * 0.95F;
	}
	
	@Nullable
	protected SoundEvent getAmbientSound() {
	    return !this.isInWater() && this.onGround && !this.isChild() ? ModSounds.OSTRICH_AMBIENT.get() : super.getAmbientSound();
	}
	
	@Nullable
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return ModSounds.OSTRICH_HURT.get();
	}
	
	
	protected void registerData() {
		super.registerData();
		this.dataManager.register(STATUS, (byte)0);
		this.dataManager.register(OWNER_UNIQUE_ID, Optional.empty());
		this.dataManager.register(SADDLED, false);
	}
	
	//2 - tame
	//4 - saddled
	//8 - breeding
	//16 - eating
	protected boolean getWatchableBoolean(int p_110233_1_) {
		return (this.dataManager.get(STATUS) & p_110233_1_) != 0;
	}
	
	protected void setWatchableBoolean(int p_110208_1_, boolean p_110208_2_) {
		byte b0 = this.dataManager.get(STATUS);
		if (p_110208_2_) {
			this.dataManager.set(STATUS, (byte)(b0 | p_110208_1_));
		} else {
			this.dataManager.set(STATUS, (byte)(b0 & ~p_110208_1_));
		}
	
	}
	
	public boolean isTame() {
		return this.getWatchableBoolean(2);
	}
	
	@Nullable
	public UUID getOwnerUniqueId() {
		return this.dataManager.get(OWNER_UNIQUE_ID).orElse((UUID)null);
	}
	
	public void setOwnerUniqueId(@Nullable UUID uniqueId) {
		this.dataManager.set(OWNER_UNIQUE_ID, Optional.ofNullable(uniqueId));
	}

	public boolean isOstrichJumping() {
		return this.ostrichJumping;
	}
	
	public void setOstrichTamed(boolean tamed) {
		this.setWatchableBoolean(2, tamed);
	}

	public void setOstrichJumping(boolean jumping) {
		this.ostrichJumping = jumping;
	}
	
	public boolean isBreeding() {
		return this.getWatchableBoolean(8);
	}
	
	public void setBreeding(boolean breeding) {
		this.setWatchableBoolean(8, breeding);
	}
	
	public boolean func_230264_L__() {
		return this.isAlive() && !this.isChild() && this.isTame();
	}
	
	public boolean isSaddled() {
		return this.dataManager.get(SADDLED);
		//return this.getWatchableBoolean(4);
	}
	
	public void setSaddled(boolean value) {
		this.dataManager.set(SADDLED, value);
	}
	
	public int getTemper() {
		return this.temper;
	}

	public void setTemper(int temperIn) {
		this.temper = temperIn;
	}
	
	public int getMaxTemper() {
		return 100;
	}

	public int increaseTemper(int p_110198_1_) {
		int i = MathHelper.clamp(this.getTemper() + p_110198_1_, 0, this.getMaxTemper());
		this.setTemper(i);
		return i;
	}
	
	public boolean canBePushed() {
		return !this.isBeingRidden();
	}
	
	protected void mountTo(PlayerEntity player) {
		//this.setEatingHaystack(false);
		//this.setRearing(false);
		if (!this.world.isRemote) {
			player.rotationYaw = this.rotationYaw;
			player.rotationPitch = this.rotationPitch;
			player.startRiding(this);
		}

	}
	
	protected boolean handleEating(PlayerEntity player, ItemStack stack) {
		boolean flag = false;
		float f = 0.0F;
		int i = 0;
		int j = 0;
		Item item = stack.getItem();
		if (item == Items.WHEAT_SEEDS || item == Items.BEETROOT_SEEDS || item == Items.PUMPKIN_SEEDS || item == Items.MELON_SEEDS) {
			f = 2.0F;
			i = 20;
			j = 3;
		}
		else if (item == Items.BEETROOT || item == Items.CARROT) {
			f = 3.0F;
			i = 60;
			j = 3;
		}
		else if (item == Items.GOLDEN_CARROT) {
			f = 4.0F;
			i = 60;
			j = 5;
			if (!this.world.isRemote && this.isTame() && this.getGrowingAge() == 0 && !this.isInLove()) {
				flag = true;
	            this.setInLove(player);
			}
		}
		
		if (this.getHealth() < this.getMaxHealth() && f > 0.0F) {
			this.heal(f);
			flag = true;
		}
		
		if (this.isChild() && i > 0) {
			this.world.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getPosXRandom(1.0D), this.getPosYRandom() + 0.5D, this.getPosZRandom(1.0D), 0.0D, 0.0D, 0.0D);
			if (!this.world.isRemote) {
				this.addGrowth(i);
			}

			flag = true;
		}

		if (j > 0 && (flag || !this.isTame()) && this.getTemper() < this.getMaxTemper()) {
			flag = true;
			if (!this.world.isRemote) {
				this.increaseTemper(j);
			}
		}

		if (flag) {
			this.eating();
		}

		return flag;
	}	
	
	public ActionResultType func_241395_b_(PlayerEntity p_241395_1_, ItemStack p_241395_2_) {
		boolean flag = this.handleEating(p_241395_1_, p_241395_2_);
		if (!p_241395_1_.abilities.isCreativeMode) {
			p_241395_2_.shrink(1);
		}

		if (this.world.isRemote) {
			return ActionResultType.CONSUME;
		} else {
			return flag ? ActionResultType.SUCCESS : ActionResultType.PASS;
		}
	}
	
	public ActionResultType func_230254_b_(PlayerEntity p_230254_1_, Hand p_230254_2_) {
		ItemStack itemstack = p_230254_1_.getHeldItem(p_230254_2_);
		if (!this.isChild()) {
	         if (this.isTame() && p_230254_1_.isSecondaryUseActive()) {
	            return ActionResultType.func_233537_a_(this.world.isRemote);
	         }

	         if (this.isBeingRidden()) {
	            return super.func_230254_b_(p_230254_1_, p_230254_2_);
	         }
	      }
		if (!itemstack.isEmpty()) {
			if (this.isBreedingItem(itemstack)) {
				return this.func_241395_b_(p_230254_1_, itemstack);
			}

			ActionResultType actionresulttype = itemstack.interactWithEntity(p_230254_1_, this, p_230254_2_);
			if (actionresulttype.isSuccessOrConsume()) {
				return actionresulttype;
			}
			/*
			if (!this.isTame()) {
				//this.makeMad();
            	this.world.addParticle(ParticleTypes.ANGRY_VILLAGER, this.getPosXRandom(1.0D), this.getPosYRandom() + 0.5D, this.getPosZRandom(1.0D), 0.0D, 0.0D, 0.0D);
	            return ActionResultType.func_233537_a_(this.world.isRemote);
			}*/
            boolean flag = !this.isChild() && !this.isSaddled() && itemstack.getItem() == Items.SADDLE;
            if (flag) {
            	//this.setWatchableBoolean(4, true);
            	this.setSaddled(true);
            	if (!p_230254_1_.abilities.isCreativeMode) {
        			itemstack.shrink(1);
        		}
            	//this.getEntityTexture();
            	this.world.addParticle(ParticleTypes.HEART, this.getPosXRandom(1.0D), this.getPosYRandom() + 0.5D, this.getPosZRandom(1.0D), 0.0D, 0.0D, 0.0D);
            	return ActionResultType.func_233537_a_(this.world.isRemote);
            }
		}
	    
        if (this.isChild()) {
        	return super.func_230254_b_(p_230254_1_, p_230254_2_);
        } 
        else {
        	if(this.isSaddled()) {
        		this.mountTo(p_230254_1_);
        	}
        	return ActionResultType.func_233537_a_(this.world.isRemote);
        }

	}
	
	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		//compound.putBoolean("EatingHaystack", this.isEating());
		compound.putBoolean("Bred", this.isBreeding());
		compound.putInt("Temper", this.getTemper());
		compound.putBoolean("Tame", this.isTame());
		compound.putBoolean("Saddled", this.isSaddled());
		if (this.getOwnerUniqueId() != null) {
			compound.putUniqueId("Owner", this.getOwnerUniqueId());
		}

	}

	   /**
	    * (abstract) Protected helper method to read subclass entity data from NBT.
	    */
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		//this.setEating(compound.getBoolean("EatingHaystack"));
		this.setBreeding(compound.getBoolean("Bred"));
		this.setTemper(compound.getInt("Temper"));
		this.setOstrichTamed(compound.getBoolean("Tame"));
		this.setSaddled(compound.getBoolean("Saddled"));
		UUID uuid;
		if (compound.hasUniqueId("Owner")) {
			uuid = compound.getUniqueId("Owner");
		} else {
			String s = compound.getString("Owner");
			uuid = PreYggdrasilConverter.convertMobOwnerIfNeeded(this.getServer(), s);
		}

		if (uuid != null) {
			this.setOwnerUniqueId(uuid);
      	}
		/*
		if (compound.contains("SaddleItem", 10)) {
			ItemStack itemstack = ItemStack.read(compound.getCompound("SaddleItem"));
			if (itemstack.getItem() == Items.SADDLE) {
				
			}
		}*/

		//this.func_230275_fc_();
	}

	
	
	private void eating() {
		if (!this.isSilent()) {
			//SoundEvent soundevent = this.func_230274_fe_();
			//if (soundevent != null) {
			//	this.world.playSound((PlayerEntity)null, this.getPosX(), this.getPosY(), this.getPosZ(), soundevent, this.getSoundCategory(), 1.0F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
			//}
		}

	}
	
    
    
	public boolean onLivingFall(float distance, float damageMultiplier) {
		if (distance > 1.0F) {
			//change!
			this.playSound(SoundEvents.ENTITY_HORSE_LAND, 0.4F, 1.0F);
		}

		int i = this.calculateFallDamage(distance, damageMultiplier);
		if (i <= 0) {
			return false;
		} else {
			this.attackEntityFrom(DamageSource.FALL, (float)i);
			if (this.isBeingRidden()) {
				for(Entity entity : this.getRecursivePassengers()) {
					entity.attackEntityFrom(DamageSource.FALL, (float)i);
				}
			}

			this.playFallSound();
			return true;
		}
	}

	protected int calculateFallDamage(float distance, float damageMultiplier) {
		return MathHelper.ceil((distance * 0.5F - 3.0F) * damageMultiplier);
	}

    public int getMaxSpawnedInChunk() {
    	return 6;
    }
    
    protected boolean isMovementBlocked() {
    	return super.isMovementBlocked() && this.isBeingRidden() && this.isSaddled();
    }
    
    public boolean isBreedingItem(ItemStack stack) {
    	return BREEDING_ITEMS.test(stack);
    }
    

    
    public void livingTick() {
    	super.livingTick();
    	if (!this.world.isRemote && this.isAlive()) {
    		if (this.rand.nextInt(900) == 0 && this.deathTime == 0) {
    			this.heal(1.0F);
    		}

    		/*if (this.canEatGrass()) {
    			if (!this.isEating() && !this.isBeingRidden() && this.rand.nextInt(300) == 0 && this.world.getBlockState(this.getPosition().down()).isIn(Blocks.GRASS_BLOCK)) {
    				this.setEating(true);
    			}

    			if (this.isEating() && ++this.eatingCounter > 50) {
    				this.eatingCounter = 0;
    				this.setEating(false);
    			}
    		}*/

    		//this.followMother();
        }
    }
    

    public boolean setTamedBy(PlayerEntity player) {
    	this.setOwnerUniqueId(player.getUniqueID());
    	this.setOstrichTamed(true);
    	if (player instanceof ServerPlayerEntity) {
    		CriteriaTriggers.TAME_ANIMAL.trigger((ServerPlayerEntity)player, this);
    	}

    	this.world.setEntityState(this, (byte)7);
    	return true;
    }
	
	public Entity getControllingPassenger() {
		return this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
	}
	
	public boolean canBeSteered() {
		//return true;
		return this.getControllingPassenger() instanceof LivingEntity;
	}
	
	public void travel(Vector3d travelVector) {
		if (this.isAlive()) {
			if (this.isBeingRidden() && this.canBeSteered() && this.isSaddled()) {
				LivingEntity livingentity = (LivingEntity)this.getControllingPassenger();
	            this.rotationYaw = livingentity.rotationYaw;
	            this.prevRotationYaw = this.rotationYaw;
	            this.rotationPitch = livingentity.rotationPitch * 0.5F;
	            this.setRotation(this.rotationYaw, this.rotationPitch);
	            this.renderYawOffset = this.rotationYaw;
	            this.rotationYawHead = this.renderYawOffset;
	            float f = livingentity.moveStrafing * 0.5F;
	            float f1 = livingentity.moveForward;
	            if (f1 <= 0.0F) {
	            	f1 *= 0.25F;
	            }

	            //if (this.onGround && this.jumpPower == 0.0F && this.isRearing() && !this.allowStandSliding) {
	            //   	f = 0.0F;
	            //   	f1 = 0.0F;
	            //}
	            
	            /*
	            if (!this.isOstrichJumping() && this.onGround) {
	               	double d0 = 1.0d;


	               	Vector3d vector3d = this.getMotion();
	               	this.setMotion(vector3d.x, d0, vector3d.z);
	               	//this.setHorseJumping(true);
	               	this.isAirBorne = true;
	               	net.minecraftforge.common.ForgeHooks.onLivingJump(this);
	               	if (f1 > 0.0F) {
						float f2 = MathHelper.sin(this.rotationYaw * ((float)Math.PI / 180F));
	                  	float f3 = MathHelper.cos(this.rotationYaw * ((float)Math.PI / 180F));
	                  	this.setMotion(this.getMotion().add((double)(-0.4F * f2 * d0), 0.0D, (double)(0.4F * f3 * d0)));
	               	}

	               	//this.jumpPower = 0.0F;
				}
				*/
	            this.jumpMovementFactor = this.getAIMoveSpeed() * 0.1F;
	            if (this.canPassengerSteer()) {
	            	this.setAIMoveSpeed((float)this.getAttributeValue(Attributes.MOVEMENT_SPEED));
	            	super.travel(new Vector3d((double)f, travelVector.y, (double)f1));
	            } else if (livingentity instanceof PlayerEntity) {
	            	this.setMotion(Vector3d.ZERO);
	            }

	            if (this.onGround) {
	            	this.setOstrichJumping(false);
	            }

	            this.func_233629_a_(this, false);
	         }
	         else {
	            this.jumpMovementFactor = 0.02F;
	            super.travel(travelVector);
	         }
		}
	}
	
	@Nullable
	private Vector3d func_234236_a_(Vector3d p_234236_1_, LivingEntity p_234236_2_) {
		double d0 = this.getPosX() + p_234236_1_.x;
		double d1 = this.getBoundingBox().minY;
		double d2 = this.getPosZ() + p_234236_1_.z;
		BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
		
		for(Pose pose : p_234236_2_.getAvailablePoses()) {
			blockpos$mutable.setPos(d0, d1, d2);
			double d3 = this.getBoundingBox().maxY + 0.75D;

			while(true) {
				double d4 = this.world.func_242403_h(blockpos$mutable);
				if ((double)blockpos$mutable.getY() + d4 > d3) {
					break;
	            }

				if (TransportationHelper.func_234630_a_(d4)) {
					AxisAlignedBB axisalignedbb = p_234236_2_.getPoseAABB(pose);
					Vector3d vector3d = new Vector3d(d0, (double)blockpos$mutable.getY() + d4, d2);
					if (TransportationHelper.func_234631_a_(this.world, p_234236_2_, axisalignedbb.offset(vector3d))) {
						p_234236_2_.setPose(pose);
						return vector3d;
					}
				}

	            blockpos$mutable.move(Direction.UP);
	            if (!((double)blockpos$mutable.getY() < d3)) {
	            	break;
	            }
			}
		}

		return null;
	}
	

	public Vector3d func_230268_c_(LivingEntity livingEntity) {
		Vector3d vector3d = func_233559_a_((double)this.getWidth(), (double)livingEntity.getWidth(), this.rotationYaw + (livingEntity.getPrimaryHand() == HandSide.RIGHT ? 90.0F : -90.0F));
		Vector3d vector3d1 = this.func_234236_a_(vector3d, livingEntity);
		if (vector3d1 != null) {
			return vector3d1;
		} else {
			Vector3d vector3d2 = func_233559_a_((double)this.getWidth(), (double)livingEntity.getWidth(), this.rotationYaw + (livingEntity.getPrimaryHand() == HandSide.LEFT ? 90.0F : -90.0F));
			Vector3d vector3d3 = this.func_234236_a_(vector3d2, livingEntity);
			return vector3d3 != null ? vector3d3 : this.getPositionVec();
		}
	}

	public void updatePassenger(Entity passenger) {
        if (this.isPassenger(passenger)) {
            float f = MathHelper.cos(this.renderYawOffset * 0.017453292F);
            float f1 = MathHelper.sin(this.renderYawOffset * 0.017453292F);
            passenger.setPosition(this.getPosX() + (double) (0.3F * f1), this.getPosY() + this.getMountedYOffset() + -0.5D + passenger.getYOffset(),
                    this.getPosZ() - (double) (0.3F * f));
        }
    }
	/*
	static class MateGoal extends BreedGoal {
		
	}*/
	
	@Override
	public AgeableEntity func_241840_a(ServerWorld arg0, AgeableEntity arg1) {
		// TODO Auto-generated method stub
		return (AgeableEntity) getType().create(world);
	}
	
	


}
