package org.cardboardpowered.mixin.resources;

import java.util.*;
import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.serialization.Decoder;

import io.papermc.paper.registry.PaperRegistryAccess;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
//import net.minecraft.registry.RegistryLoader.Loader;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.RegistryLoadTask;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.RegistryOps.RegistryInfoLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;

@Mixin(RegistryDataLoader.class)
public class RegistryDataLoaderMixin {

	/*
    @Inject(
    		at = @At(value = "RETURN"),
    		method = "loadContentsFromManager(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/resources/RegistryOps$RegistryInfoLookup;Lnet/minecraft/core/WritableRegistry;Lcom/mojang/serialization/Decoder;Ljava/util/Map;)V",
    		locals = LocalCapture.CAPTURE_FAILHARD
    )
    private static void cardboard$reg_lock_reference_holders (
    		ResourceManager resourceManager,
    		RegistryOps.RegistryInfoLookup infoGetter,
    		WritableRegistry registry,
    		Decoder elementDecoder, Map<ResourceKey<?>, Exception> errors,
    		CallbackInfo ci) {

    	 PaperRegistryAccess.instance().lockReferenceHolders(registry.key());
         // PaperRegistryListenerManager.INSTANCE.runFreezeListeners(registry.getKey(), conversions);
    }
    */
    
    /**
     * @author Cardboard
     * @reason Paper: add method to get the value for pre-filling builders in the reg mod API
     */
    @Overwrite
    private static RegistryInfoLookup createContext(final List<RegistryLookup<?>> registries,
            final List<RegistryLoadTask<?>> additionalRegistries) {
        final Map<ResourceKey<? extends Registry<?>>, HolderGetter<?>> getters = new HashMap<>();
        registries.forEach(registry -> getters.put(registry.key(), registry));
        additionalRegistries.forEach(task -> getters.put(task.registryKey(), task.registry));

        final HolderLookup.Provider providerForBuilders = HolderLookup.Provider.create(
            Stream.concat(registries.stream(), additionalRegistries.stream().map(task -> task.registry))
        );

        return new RegistryInfoLookup() {
            @Override
            @SuppressWarnings("unchecked")
            public <T> Optional<HolderGetter<T>> lookup(ResourceKey<? extends Registry<? extends T>> registryRef) {
                return Optional.ofNullable((HolderGetter<T>) getters.get(registryRef));
            }

            public HolderLookup.Provider lookupForValueCopyViaBuilders() {
                return providerForBuilders;
            }
        };
    }
}
