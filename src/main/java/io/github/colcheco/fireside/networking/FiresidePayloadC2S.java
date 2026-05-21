package io.github.colcheco.fireside.networking;

import io.github.colcheco.fireside.Fireside;
import io.github.colcheco.fireside.entity.LogEntity;
import io.github.colcheco.fireside.entity.Sleeper;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record FiresidePayloadC2S(byte data) implements CustomPacketPayload {
    public static final Type<FiresidePayloadC2S> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(Fireside.MOD_ID, "payload"));

    public static final StreamCodec<RegistryFriendlyByteBuf, FiresidePayloadC2S> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.BYTE, FiresidePayloadC2S::data, FiresidePayloadC2S::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(FiresidePayloadC2S payload, ServerPlayNetworking.Context context) {
        if (context.player().getVehicle() instanceof LogEntity log && log.campfire()) {
            context.player().sendOverlayMessage(getMessage(payload.data(), (Sleeper) context.player()));
        } else {
            context.player().sendOverlayMessage(Component.literal("You are not sitting by a campfire"));
        }
    }

    private static Component getMessage(byte data, Sleeper sleeper) {
        Sleeper.WakeUpTime target = switch (data) {
            case 1 -> Sleeper.WakeUpTime.MIDNIGHT;
            case 2 -> Sleeper.WakeUpTime.MORNING;
            case 3 -> Sleeper.WakeUpTime.NOON;
            case 4 -> Sleeper.WakeUpTime.NIGHT;
            default -> Sleeper.WakeUpTime.NOT_SLEEPING;
        };
        sleeper.setSleeping(target);
        if (target == Sleeper.WakeUpTime.NOT_SLEEPING) {
            return Component.literal("Please make a selection and try again");
        }
        return Component.literal("Waiting by the fire " + ("until " + target).toLowerCase());
    }
}
