package io.github.colcheco.fireside.client;

import io.github.colcheco.fireside.entity.LogEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Environment(EnvType.CLIENT)
public class LogEntityRenderer extends EntityRenderer<LogEntity, EntityRenderState> {
    protected LogEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
    @Override
    public boolean shouldRender(LogEntity entity, Frustum culler, double x, double y, double z) {
        return true;
    }
    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }
}
