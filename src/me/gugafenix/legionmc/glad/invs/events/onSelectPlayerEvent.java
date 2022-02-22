package me.gugafenix.legionmc.glad.invs.events;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.HClan.Objects.Jogador;
import me.HClan.Utils.Item;
import me.HClan.Utils.SkullCreator;
import me.gugafenix.legionmc.glad.invs.SelectMembersMenu;
import me.gugafenix.legionmc.glad.main.Main;
import me.gugafenix.legionmc.glad.objects.Gladiator;
import me.gugafenix.legionmc.glad.player.GladPlayer;
import me.gugafenix.legionmc.glad.player.GladPlayer.SelectionStatus;

public class onSelectPlayerEvent implements Listener {
	
	public onSelectPlayerEvent() { Main.registerEvent(new onSelectPlayerEvent()); }
	
	@SuppressWarnings("deprecation")
	@EventHandler
	void onClickInInventoryEvent(InventoryClickEvent e) {
		
		HashMap<Player, Integer> map = SelectMembersMenu.getMap();
		
		// Verificações para ignorar
		if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
		if (!e.getInventory().getTitle().equalsIgnoreCase("§0Selecionar Guerreiros")) return;
		if (e.getCurrentItem().getType() == Material.STAINED_GLASS) {
			e.setCancelled(true);
			return;
		}
		
		// Evento cancelado
		e.setCancelled(true);
		
		if (e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().hasDisplayName()) {
			// Vars
			Player p = (Player) e.getWhoClicked();
			ItemStack item = e.getCurrentItem();
			String name = null;
			Player selected = null;
			
			if (item.getItemMeta().getDisplayName().contentEquals("§bPróxima Página")) {
				
				// Passar a página
				map.put(p, map.containsKey(p) ? map.get(p) + 1 : 0);
				SelectMembersMenu menu = new SelectMembersMenu();
				menu.createInventory();
				menu.openInventory(p);
				return;
				
			} else if (item.getItemMeta().getDisplayName().contentEquals("§bPágina Anterior")) {
				
				// Voltar a página
				map.put(p, map.containsKey(p) ? map.get(p) - 1 : 0);
				SelectMembersMenu menu = new SelectMembersMenu();
				menu.createInventory();
				menu.openInventory(p);
				
			} else if (item.getType() == Material.SKULL_ITEM) {
				
				name = item.getItemMeta().getDisplayName().replace("§" + item.getItemMeta().getDisplayName().charAt(1), "");
				selected = Bukkit.getPlayer(name);
				
				// Selecionar o player
				if (e.getClickedInventory().all(Material.BARRIER).size() == Gladiator.getGladRunning().getMaxClanMembers()) {
					p.sendMessage(Main.tag + " §cO máximo de players que poderão participar do gladiador foi alcançado");
					p.playSound(p.getLocation(), Sound.VILLAGER_NO, 10, 10);
					p.closeInventory();
					return;
				}
				
				// Substituindo o item
				Item barrier = new Item(Material.BARRIER);
				barrier.setDisplayName("§c" + name);
				p.sendMessage("§aJogador selecionado, clique novamente sobre ele para remover a seleção");
				e.getInventory().setItem(e.getSlot(), barrier.build());
				
				// Avisando o player
				selected.sendMessage(Main.tag + "§aVocê foi escolhido por §7" + p.getName() + " §apara lutar no gladiador");
				selected.sendMessage(
						Main.tag + "§bSerá teleportado para a arena de batalha em §d" + Gladiator.getGladRunning().getTimeToStart());
				
				// Configurando o selecionado
				Main.getPlayerManager().getPlayer(p).getSelectedPlayers().add(selected);
				GladPlayer gp = null;
				if (Main.getPlayerManager().getPlayer(selected) == null) gp = new GladPlayer(selected);
				gp.setInGladiator(true);
				Gladiator.getGladRunning().getPlayers().add(gp);
				
				gp.getPlayer().sendMessage(Main.tag
						+ "§bO evento gladiador será iniciado assim que todos os guerreiros estiverem selecionados, prepare-se para a batalha!");
				
			} else if (item.getType() == Material.BARRIER) {
				
				name = item.getItemMeta().getDisplayName().replace("§" + item.getItemMeta().getDisplayName().charAt(1), "");
				selected = Bukkit.getPlayer(name);
				
				// Resetando a skull
				Item skull = new Item(SkullCreator.itemFromName(name));
				skull.setLore("", "§bNome", "  §5↪ " + selected.getName(), "§aKDR", "  §d↪ " + Jogador.check(selected).getKDR());
				skull.setDisplayName("§a" + selected.getName());
				e.getInventory().setItem(e.getSlot(), skull.build());
				
				// Removendo o player
				Gladiator.getGladRunning().getPlayers().remove(Main.getPlayerManager().getPlayer(name));
				Main.getPlayerManager().getPlayer(p).getSelectedPlayers().remove(selected);
				
				// Avisando
				p.sendMessage("§aSeleção removida, clique novamente sobre ele para seleciona-lo");
				selected.sendMessage(
						Main.tag + "§cVocê foi removido da lista de players que participarão do gladiador por §7" + p.getName());
				
			} else {
				
				// Pronto
				p.closeInventory();
				p.sendMessage("§bJogadors selecionados com sucesso!");
				p.playSound(p.getLocation(), Sound.CAT_MEOW, 10f, 10f);
				Main.getPlayerManager().getPlayer(p).setSelectionStatus(SelectionStatus.READY);
			}
		}
	}
}
