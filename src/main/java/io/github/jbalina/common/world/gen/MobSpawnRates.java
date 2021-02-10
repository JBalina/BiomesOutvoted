package io.github.jbalina.common.world.gen;

import io.github.jbalina.core.init.RegisterEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import io.github.jbalina.BiomesOutvotedMod;

@Mod.EventBusSubscriber(modid = BiomesOutvotedMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MobSpawnRates {
	@SubscribeEvent(priority = EventPriority.HIGH)
    public static void spawnEntities(BiomeLoadingEvent event) {
        //String biomename = event.getName().toString();
        if (event.getCategory() == Biome.Category.SAVANNA) {
            event.getSpawns().withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(RegisterEntity.OSTRICH, 7, 2, 5));
        }
    }
}

