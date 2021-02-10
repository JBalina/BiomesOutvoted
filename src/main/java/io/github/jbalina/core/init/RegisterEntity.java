package io.github.jbalina.core.init;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.ObjectHolder;

import io.github.jbalina.BiomesOutvotedMod;
import io.github.jbalina.common.entities.OstrichEntity;

// The value here should match an entry in the META-INF/mods.toml file
@ObjectHolder(BiomesOutvotedMod.MODID)
public class RegisterEntity
{
	
	public static final EntityType<OstrichEntity> OSTRICH = null;
    
    @Mod.EventBusSubscriber(modid = BiomesOutvotedMod.MODID, bus = Bus.MOD)
	public static class RegistrationHandler {
    	public static void registerEntities(final RegistryEvent.Register<EntityType<?>> event) {
    		final EntityType<OstrichEntity> ostrich = build(
				"ostrich",
				EntityType.Builder.create(OstrichEntity::new, EntityClassification.CREATURE)
						.size(0.8f, 2.1f)
			);
	    	
	    	event.getRegistry().registerAll(
					ostrich
			);
	    	GlobalEntityTypeAttributes.put(ostrich, OstrichEntity.setCustomAttributes().create());
	    }
    	
    }
    
    private static <T extends Entity> EntityType<T> build(final String name, final EntityType.Builder<T> builder) {
		final ResourceLocation registryName = new ResourceLocation(BiomesOutvotedMod.MODID, name);

		final EntityType<T> entityType = builder
				.build(registryName.toString());

		entityType.setRegistryName(registryName);

		return entityType;
	}
    /*
    private static void addSpawn(final EntityType<? extends MobEntity> entityType, final int itemWeight, final int minGroupCount, final int maxGroupCount, final EntityClassification classification, final Biome... biomes) {
		for (final Biome biome : biomes) {
			final List<Biome.SpawnListEntry> spawns = biome.getSpawns(classification);

			// Try to find an existing entry for the entity type
			spawns.stream()
					.filter(entry -> entry.entityType == entityType)
					.findFirst()
					.ifPresent(spawns::remove); // If there is one, remove it

			// Add a new one
			spawns.add(new Biome.SpawnListEntry(entityType, itemWeight, minGroupCount, maxGroupCount));
		}
	}
    
    private static Biome[] getBiomes(final BiomeDictionary.Type type) {
		return BiomeDictionary.getBiomes(type).toArray(new Biome[0]);
	}*/
	

}
