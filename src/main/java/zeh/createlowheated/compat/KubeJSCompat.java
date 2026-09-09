package zeh.createlowheated.compat;

import com.simibubi.create.content.processing.recipe.HeatCondition;

import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaRegistry;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaType;
import zeh.createlowheated.CreateLowHeated;

public class KubeJSCompat implements KubeJSPlugin {
    @Override
    public void registerRecipeSchemas(RecipeSchemaRegistry registry) {
        RecipeSchemaType schemaType = registry.namespace("create").get("base/processing");
        if (schemaType == null) {
            CreateLowHeated.LOGGER.warn("KubeJS is installed, but KubeJS Create is not. Skipping KubeJS Low-Heated integration.");
            return;
        }
        RecipeSchema schema = schemaType.schema;
        RecipeKey<HeatCondition> heatRequirement = schema.getKey("heat_requirement");
        schema.setOpFunction("lowheated", heatRequirement, HeatCondition.valueOf("LOWHEATED"));
    }
}
