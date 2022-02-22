package me.gugafenix.legionmc.glad.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;
import me.gugafenix.legionmc.glad.main.Main;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;

public class API {
	
	public void sendTitle(String title, String subtitle, Player player) {
		IChatBaseComponent chatTitle = ChatSerializer.a("{\"text\": \"" + PlaceholderAPI.setPlaceholders(player, title) + "\"}");
		IChatBaseComponent chatSubtitle = ChatSerializer.a("{\"text\": \"" + PlaceholderAPI.setPlaceholders(player, subtitle) + "\"}");
		
		PacketPlayOutTitle packettitle = new PacketPlayOutTitle(EnumTitleAction.TITLE, chatTitle);
		PacketPlayOutTitle packetsubtitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, chatSubtitle);
		PacketPlayOutTitle length = new PacketPlayOutTitle(5, 20, 5);
		
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packettitle);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetsubtitle);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(length);
		
	}
	
	public List<?> getPagina(int paginaPlayer, int amountSlots, List<?> lista) {
		int pagina = paginaPlayer;
		int total = (lista.size() / amountSlots) + 1;
		
		List<Object> objects = new ArrayList<>(lista);
		
		HashMap<Integer, List<Object>> paginas = new HashMap<>();
		List<Integer> ids = new ArrayList<>();
		for (int i = 0; i < total; i++) {
			List<Object> list = new ArrayList<>();
			if (objects.size() > 0) {
				for (int j2 = 0; j2 < objects.size(); j2++) {
					if (!ids.contains(j2) && list.size() < amountSlots) {
						ids.add(j2);
						list.add(objects.get(j2));
					}
				}
			}
			paginas.put(i, list);
		}
		return paginas.containsKey(pagina) ? paginas.get(pagina) : new ArrayList<>();
	}
	
	public void playSound(Player player, Sound... sound) {
		
		for (Sound s : sound) player.playSound(player.getLocation(), s, 1.0f, 1.0f);
	}
	
	public static API getApi() { return Main.getApi(); }
	
}
