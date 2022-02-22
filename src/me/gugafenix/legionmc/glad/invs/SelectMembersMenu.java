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
import me.gugafenix.legionmc.glad.utils.API;

public class SelectMembersMenu {
	private Inventory inventory;
	private static HashMap<Player, Integer> map = new HashMap<>();
	
	public Inventory getInventory() { return inventory; }
	
	public void setInventory(Inventory inventory) { this.inventory = inventory; }
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public void openInventory(Player p) {
		
		List<Player> members = Jogador.check(p).getClan().getOnlinePlayers();
		members.remove(p);
		int page = map.containsKey(p) ? map.get(p) : 0;
		Inventory inv = getInventory();
		Item pane = new Item(160, (byte) 15);
		pane.setGlow();
		pane.setDisplayName("§" + new Random().nextInt(9) + "Vazio");
		
		for (int i = 0; i < (inv.getSize()) - 1; i++) inv.setItem(i, pane.build());
		
		List<Object> objects = (List<Object>) API.getApi().getPagina(page, 6 * 9, members);
		objects.forEach(o -> {
			Player all = (Player) o;
			Item skull = new Item(SkullCreator.itemFromName(all.getName()));
			skull.setLore("", "§bNome", "  \n§5↪ " + all.getName(), "§aKDR", "  \n§d↪" + Jogador.check(all).getKDR());
			skull.setDisplayName("§6Selecionar " + all.getName());
			int slot = members.indexOf(all);
			if (slot == (6 * 9) - 1 || slot == (6 * 9) - 5 || slot == (6 * 9) - 10) slot++;
			inv.setItem(members.indexOf(all), skull.build());
		});
		
		if (API.getApi().getPagina(page + 1, 6 * 9, members).size() > 0) {
			Item item = new Item(Material.ARROW);
			item.setDisplayName("Â§bPrÃ³xima PÃ¡gina");
			item.setAmount(page + 1);
			inv.setItem((6 * 9) - 1, item.build());
		}
		
		if (API.getApi().getPagina(page - 1, 6 * 9, members).size() > 0) {
			Item item = new Item(Material.ARROW);
			item.setDisplayName("§bPágina Anterior");
			item.setAmount(page + 1);
			inv.setItem((6 * 9) - 10, item.build());
		}
		
		Item ready = new Item(Material.EMERALD);
		ready.setDisplayName("§APronto");
		ready.setLore("", "§bPressione este item se já estiver esolhido todos os guerreiros");
		ready.setAmount(page + 1);
		inv.setItem((6 * 9) - 5, ready.build());
		
		p.openInventory(inv);
		p.updateInventory();
		
	}
	
	public static HashMap<Player, Integer> getMap() { return map; }
	
	public void createInventory() { setInventory(Bukkit.createInventory(null, 9 * 6, "§0Selecionar Guerreiros")); }
	
}
