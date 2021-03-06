/*
 * 
 */
package me.gugafenix.legionmc.glad.spawnselection;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import me.HClan.Utils.Item;
import me.gugafenix.legionmc.glad.file.File;
import me.gugafenix.legionmc.glad.main.Main;
import me.gugafenix.legionmc.glad.protocol.FastProtocol;
import me.gugafenix.legionmc.glad.utils.API;
import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.IBlockData;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockChange;

public class SpawnSelect {
	private Player p;
	private World world;
	private File file;
	private List<Location> locations;
	private Location fromLocation;
	
	public SpawnSelect(Player p, World world, File file) {
		this.p = p;
		this.world = world;
		this.file = file;
		this.locations = new ArrayList<>();
		SpawnSelectManager.getManager().getSelects().add(this);
	}
	
	public void startSelection() {
		this.fromLocation = p.getLocation();
		p.teleport(world.getSpawnLocation());
		
		for (int i = 0; i < 100; i++) p.sendMessage("");
		
		p.sendMessage(Main.tag
				+ "§bColoque os portais do end onde quer adicionar um spawnpoint. E caso queira remove-lo, basta quebrar o bloco.");
		p.sendMessage("§aPara finalizar a seleção e salvar as localizacões digite §3§lPRONTO"
				+ " \n§aPara cancelar o processo digite §c§lCANCELAR");
		API.getApi().playSound(p, Sound.LEVEL_UP);
		
		p.getInventory().clear();
		
		Item portal = new Item(Material.ENDER_PORTAL_FRAME);
		portal.setDisplayName("§aAdicionar spawn point");
		portal.setGlow();
		portal.setLore("", "§bColoque este bloco onde deseja adicionar um spawn point do gladiador");
		
		p.getInventory().setItem(4, portal.build());
		
	}
	
	public void cancelSelection() {
		
		p.sendMessage(Main.tag + "§cSeleção de spawn points cancelada");
		API.getApi().playSound(p, Sound.LEVEL_UP);
		SpawnSelectManager.getManager().getSelects().remove(this);
		
		for (Location loc : locations) {
			
			PacketPlayOutBlockChange pktblock = new PacketPlayOutBlockChange(((CraftWorld) loc.getWorld()).getHandle(),
					new BlockPosition(loc.getX(), loc.getY(), loc.getZ()));
			pktblock.block = Block.getById(loc.getBlock().getTypeId()).getBlockData();
			
			new FastProtocol(Bukkit.getServer()).sendPacket(pktblock, p);
		}
		
		p.teleport(fromLocation);
		locations.clear();
	}
	
	public void finishSelection() {
		saveLocations();
		p.sendMessage(Main.tag + "§aLocalizações salvas com êxito!");
		p.getInventory().clear();
		API.getApi().playSound(p, Sound.LEVEL_UP);
		p.teleport(fromLocation);
		SpawnSelectManager.getManager().getSelects().remove(this);
	}
	
	private void saveLocations() {
		List<String> stringlist = new ArrayList<>();
		for (Location loc : locations) {
			stringlist.add(serialize(loc));
			
			PacketPlayOutBlockChange pktblock = new PacketPlayOutBlockChange(((CraftWorld) loc.getWorld()).getHandle(),
					new BlockPosition(loc.getX(), loc.getY(), loc.getZ()));
			
			net.minecraft.server.v1_8_R3.Block nmsblock = net.minecraft.server.v1_8_R3.Block.getById(loc.getBlock().getTypeId());
			IBlockData data = nmsblock.getBlockData();
			pktblock.block = data;
			
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(pktblock);
		}
		
		file.getConfig().set("Spawns", new ArrayList<>(stringlist));
		file.save();
		locations.clear();
		stringlist.clear();
	}
	
	private String serialize(Location loc) {
		
		double x = loc.getBlockX();
		double y = loc.getBlockY();
		double z = loc.getBlockZ();
		
		return ( x + ":" + y + ":" + z ).replace(".0", "");
	}
	
	public Player getPlayer() { return p; }
	
	public Player getP() { return p; }
	
	public void setP(Player p) { this.p = p; }
	
	public File getFile() { return file; }
	
	public void setFile(File file) { this.file = file; }
	
	public void setWorld(World world) { this.world = world; }
	
	public World getWorld() { return world; }
	
	public List<Location> getLocations() { return locations; }
	
	public void setLocations(List<Location> locations) { this.locations = locations; }
	
	public Location getFromLocation() { return fromLocation; }
	
}
