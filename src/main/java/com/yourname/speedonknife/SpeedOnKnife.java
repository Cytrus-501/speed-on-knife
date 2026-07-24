package com.yourname.speedonknife;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class ClientEventHandler implements ClientModInitializer {
    private static int lastSlot = -1; // 记录上一次的槽位，防止每帧重复发包

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            
            int currentSlot = client.player.getInventory().selectedSlot;
            // 只有槽位发生变化时才发包
            if (currentSlot != lastSlot) {
                lastSlot = currentSlot;
                
                // 构建数据包并发送给服务端
                PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                buf.writeInt(currentSlot);
                ClientPlayNetworking.send(new Identifier(SpeedOnKnifeMod.MOD_ID, "switch_slot"), buf);
            }
        });
    }
}