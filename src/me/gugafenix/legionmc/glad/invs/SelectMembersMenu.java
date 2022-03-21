/*
 * 
 */
package me.gugafenix.legionmc.glad.invs;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import me.HClan.Objects.Jogador;
import me.HClan.Utils.Item;
import me.HClan.Utils.SkullCreator;
import me.gugafenix.legionmc.glad.main.Main;
import me.gugafenix.legionmc.glad.objects.Gladiator;
import me.gugafenix.legionmc.glad.utils.API;

public class SelectMembersMenu {
	private Inventory inventory;
	private static HashMap<Player, Integer> map = new HashMap<>();
	
	@SuppressWarnings({ "deprecation", "unchecked" })
	public void openInventory(Player p) {
		
		// Checking page
		List<Player> members = Jogador.check(p).getClan().getOnlinePlayers();
		members.remove(p);
		int page = map.containsKey(p) ? map.get(p) : 0;
		Inventory inv = getInventory();
		
		// Glasses
		Item pane = new Item(160, (byte) 15);
		pane.setGlow();
		pane.setDisplayName("§" + new Random().nextInt(9) + "Vazio");
		
		// Filling the menu
		for (int i = 0; i < (inv.getSize()) - 1; i++) inv.setItem(i, pane.build());
		
		List<Object> objects = (List<Object>) (API.getApi().getPagina(page, 6 * 9, members));
		
		// Setting the players
		objects.forEach(o -> {
			Player all = (Player) o;
			Item skull = new Item(SkullCreator.itemFromName(all.getName()));
			skull.setLore("", "§bNome", "  \n§5↪ " + all.getName(), "§aKDR", "  \n§d↪" + Jogador.check(all).getKDR());
			skull.setDisplayName("§6Selecionar " + all.getName());
			
			int slot = members.indexOf(all);
			if (slot > 9 * 6 - 9) slot++;
			
			inv.setItem(members.indexOf(all), skull.build());
		});
		
		{
			
			Item item = new Item(Material.STAINED_GLASS_PANE, 10);
			item.setGlow();
			item.setDisplayName("§b</>");
			for (int i = 9 * 6 - 9; i < 9 * 6 - 1; i++) inv.setItem(i, item.build());
			
		}
		
		{
			Item item = new Item(Material.ARROW);
			item.setDisplayName("§bPróxima Página");
			item.setAmount(page + 1);
			inv.setItem((6 * 9) - 1, item.build());
		}
		
		{
			Item item = new Item(Material.ARROW);
			item.setDisplayName("§bPágina Anterior");
			item.setAmount(page + 1);
			inv.setItem((6 * 9) - 9, item.build());
		}
		
		{
			Item ready = new Item(Material.BOOKSHELF);
			ready.setDisplayName("§APronto");
			ready.setLore("", "§bPressione este item se já estiver selecionado todos os guerreiros");
			ready.setAmount(page + 1);
			inv.setItem((6 * 9) - 2, ready.build());
		}
		
		{
			Item random = new Item(Material.BEACON);
			random.setDisplayName("§aAleatório");
			random.setLore("", "§aEscolher guerreiros aleatórios");
			random.setAmount(page + 1);
			inv.setItem((6 * 9) - 8, random.build());
		}
		
		p.openInventory(inv);
		p.updateInventory();
		
	}
	
	public static HashMap<Player, Integer> getMap() { return map; }
	
	public Inventory createInventory() {
		setInventory(Bukkit.createInventory(null, 9 * 6, "§8§lSelecionar Guerreiros"));
		return this.inventory;
	}
	
	public Inventory getInventory() { return inventory; }
	
	public void setInventory(Inventory inventory) { this.inventory = inventory; }
	
}
