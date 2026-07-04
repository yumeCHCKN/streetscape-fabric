package net.zphyghtning.streetscape.client;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.zphyghtning.streetscape.Streetscape;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Function;

public class StreetscapeModelLoadingPlugin implements ModelLoadingPlugin {
    @Override
    public void onInitializeModelLoader(Context pluginContext) {
        pluginContext.modifyModelOnLoad().register((original, context) -> {
            ModelIdentifier modelId = context.topLevelId();
            if (modelId != null) {
                Identifier id = modelId.id();
                if (id.getNamespace().equals(Streetscape.MOD_ID) && id.getPath().equals("attached_road_sign")) {
                    return new MimicUnbakedModel(original);
                }
            }
            return original;
        });
    }

    public static class MimicUnbakedModel implements UnbakedModel {
        private final UnbakedModel original;

        public MimicUnbakedModel(UnbakedModel original) {
            this.original = original;
        }

        @Override
        public Collection<Identifier> getModelDependencies() {
            return original.getModelDependencies();
        }

        @Override
        public void setParents(Function<Identifier, UnbakedModel> modelLoader) {
            original.setParents(modelLoader);
        }

        @Nullable
        @Override
        public BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer) {
            BakedModel baked = original.bake(baker, textureGetter, rotationContainer);
            if (baked == null) return null;
            return new MimicBakedModel(baked);
        }
    }
}
