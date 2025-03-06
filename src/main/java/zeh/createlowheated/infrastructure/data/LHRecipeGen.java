package zeh.createlowheated.infrastructure.data;

import com.simibubi.create.foundation.data.recipe.ProcessingRecipeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.fluids.FluidType;
import net.minecraft.data.DataProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LHRecipeGen {
    protected static final List<ProcessingRecipeGen> GENERATORS = new ArrayList<>();
    protected static final int BUCKET = FluidType.BUCKET_VOLUME;
    protected static final int BOTTLE = 250;

    public static void registerAll(DataGenerator gen, PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        GENERATORS.add(new LHMixingRecipeGen(output, registries));

        gen.addProvider(true, new DataProvider() {

            @Override
            public String getName() {
                return "Create Low-Heated's Processing Recipes";
            }

            @Override
            public CompletableFuture<?> run(CachedOutput dc) {
                return CompletableFuture.allOf(GENERATORS.stream()
                        .map(gen -> gen.run(dc))
                        .toArray(CompletableFuture[]::new));
            }
        });
    }

}
