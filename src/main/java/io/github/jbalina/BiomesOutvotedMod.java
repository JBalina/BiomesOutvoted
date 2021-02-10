package io.github.jbalina;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.github.jbalina.client.renderer.entity.OstrichRenderer;
import io.github.jbalina.common.entities.OstrichEntity;
import io.github.jbalina.common.world.gen.MobSpawnRates;
import io.github.jbalina.core.init.ModSounds;
import io.github.jbalina.core.init.RegisterEntity;


import java.util.List;
import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(BiomesOutvotedMod.MODID)
@Mod.EventBusSubscriber(modid = BiomesOutvotedMod.MODID, bus = Bus.MOD)
public class BiomesOutvotedMod
{
	public static final String MODID = "biomesoutvotedmod";
	public static final Logger LOGGER = LogManager.getLogger();

	public BiomesOutvotedMod() {
    	IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    	bus.addListener(this::setup);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new MobSpawnRates());
        ModSounds.SOUNDS.register(bus);


        
    }
	
	@SubscribeEvent
    public static void onEntityRegistry(RegistryEvent.Register<EntityType<?>> evt) {
        RegisterEntity.RegistrationHandler.registerEntities(evt);
    }
	/*
	@SubscribeEvent(priority = EventPriority.HIGH)
    public static void spawnEntities(BiomeLoadingEvent event) {
        String biomename = event.getName().toString();
        if (event.getCategory() == Biome.Category.SAVANNA) {
            event.getSpawns().withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(RegisterEntity.OSTRICH, 7, 2, 5));
        }
    }*/

    private void setup(final FMLCommonSetupEvent event)
    {

    }
    
    @EventBusSubscriber(value = Dist.CLIENT, bus = Bus.MOD)
    public static class RegistryClient {
        @SubscribeEvent
        public static void onRendererRegistry(FMLClientSetupEvent clientSetupEvent) {
            RenderingRegistry.registerEntityRenderingHandler(RegisterEntity.OSTRICH, OstrichRenderer::new);
        }
    }
    

    

}
