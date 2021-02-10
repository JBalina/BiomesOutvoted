package io.github.jbalina.core.init;

import io.github.jbalina.BiomesOutvotedMod;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModSounds {
    public static DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, BiomesOutvotedMod.MODID);
    
    public static final RegistryObject<SoundEvent> OSTRICH_AMBIENT = SOUNDS.register("ostrich_ambient",
    		() -> new SoundEvent(new ResourceLocation(BiomesOutvotedMod.MODID, "ostrich_ambient")));
    public static final RegistryObject<SoundEvent> OSTRICH_HURT = SOUNDS.register("ostrich_hurt",
    		() -> new SoundEvent(new ResourceLocation(BiomesOutvotedMod.MODID, "ostrich_hurt")));
}
