package me.gugafenix.legionmc.glad.protocol;

import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.gugafenix.legionmc.glad.main.Main;
import net.minecraft.server.v1_8_R3.Packet;

public class FastProtocol {
	
	private CraftServer server;
	
	public FastProtocol(Server server) { this.server = (CraftServer) server; }
	
	public PacketContainer createPacketContainer() { return new PacketContainer(); }
	
	public void sendPacketContainer(PacketContainer container, Player p) {
		new BukkitRunnable() {
			
			int i = 0;
			
			@Override
			public void run() {
				
				if (i >= container.size()) {
					this.cancel();
					return;
				}
				
				sendPacket(container.get(i), p);
				i++;
			}
		}.runTaskTimerAsynchronously(Main.getMain(), 0, 1);
	}
	
	public void sendPacket(Packet<?> packet, Player player) { ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet); }
	
	public CraftServer getServer() { return server; }
	
}
