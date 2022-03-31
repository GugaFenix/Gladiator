/*
 * 
 */
package me.gugafenix.legionmc.glad.invs.events;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.HClan.Objects.Jogador;
import me.HClan.Utils.Item;
import me.HClan.Utils.SkullCreator;
import me.gugafenix.legionmc.glad.invs.SelectMembersMenu;
import me.gugafenix.legionmc.glad.main.Main;
import me.gugafenix.legionmc.glad.objects.Gladiator;
import me.gugafenix.legionmc.glad.player.GladPlayer;
import me.gugafenix.legionmc.glad.player.GladPlayer.SelectionStatus;
import me.gugafenix.legionmc.glad.tasks.Tasks;
import me.gugafenix.legionmc.glad.tasks.Tasks.TaskId;
import me.gugafenix.legionmc.glad.utils.API;

public class onSelectPlayerEvent implements Listener {
	
	@SuppressWarnings("deprecation")
	@EventHandler
	void onClickInInventoryEvent(InventoryClickEvent e) {
		
		HashMap<Player, Integer> map = SelectMembersMenu.getMap();
		
		// Verificações para ignorar
		if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
		if (e.getInventory().getTitle().equalsIgnoreCase("§8§lSelecionar Guerreiros")) e.setCancelled(true);
		
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
					p.sendMessage(Main.tag + " §cO máximo de players que poderão participar do gladiador por clã foi alcançado");
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
				GladPlayer gp = Main.getPlayerManager().getPlayer(selected);
				if (gp == null) gp = new GladPlayer(selected);
				gp.setInGladiator(true);
				Gladiator.getGladRunning().getPlayers().add(gp);
				
				gp.getPlayer().sendMessage(Main.tag
						+ "§bO evento gladiador será iniciado assim que todos os guerreiros estiverem selecionados por seus líderes, prepare-se para a batalha!");
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
				
			} else if (item.getType() == Material.BOOKSHELF) {
				
				// Pronto
				p.closeInventory();
				p.sendMessage(Main.tag + "§bJogadores selecionados com sucesso!");
				p.playSound(p.getLocation(), Sound.CAT_MEOW, 10f, 10f);
				Main.getPlayerManager().getPlayer(p).setSelectionStatus(SelectionStatus.READY);
				Tasks task = new Tasks(TaskId.START_BATTLE, Gladiator.getGladRunning());
				if (task.hasPlayerSelecting()) return;
				else Gladiator.getGladRunning().runTask(TaskId.START_BATTLE);
				
			} else {
				int slot = randomSlot(e.getInventory());
				
				if (slot == -1) {
					p.sendMessage(Main.tag + "§eVocê não tem jogadores no clã.");
					return;
				}
				
				Inventory inv = e.getInventory();
				ItemStack it = inv.getItem(slot);
				Player sl = Bukkit
						.getPlayer(it.getItemMeta().getDisplayName().replace("§" + it.getItemMeta().getDisplayName().charAt(1), ""));
				
				int j = 0;
				
				if (e.getInventory().all(Material.SKULL_ITEM).size() <= 12) j = e.getInventory().all(Material.SKULL_ITEM).size();
				else j = Gladiator.getGladRunning().getMaxClanMembers();
				
				for (int i = 0; i < j; i++) {
					
					// Substituindo o item
					Item barrier = new Item(Material.BARRIER);
					barrier.setDisplayName("§c" + name);
					e.getInventory().setItem(e.getSlot(), barrier.build());
					
					// Avisando o player
					sl.sendMessage(Main.tag + "§aVocê foi escolhido por §7" + p.getName() + " §apara lutar no gladiador");
					sl.sendMessage(
							Main.tag + "§bSerá teleportado para a arena de batalha em §d" + Gladiator.getGladRunning().getTimeToStart());
					
					// Configurando o selecionado
					GladPlayer gp = Main.getPlayerManager().getPlayer(sl);
					if (gp == null) gp = new GladPlayer(sl);
					gp.setInGladiator(true);
					Main.getPlayerManager().getPlayer(p).getSelectedPlayers().add(gp.getPlayer());
					Gladiator.getGladRunning().getPlayers().add(gp);
					
					gp.getPlayer().sendMessage(Main.tag
							+ "§bO evento gladiador será iniciado assim que todos os guerreiros estiverem selecionados por seus líderes, prepare-se para a batalha!");
					
				}
				
				p.sendMessage("§eJogadores aleatórios selecionados!");
				API.getApi().playSound(p, Sound.ANVIL_BREAK);
				
			}
		}
		Gladiator.getGladRunning().updateInfoFromAll();
	}
	
	public int randomSlot(Inventory inv) {
		int heads = inv.all(Material.SKULL_ITEM).size();
		if (heads <= 0) return -1;
		
		return new Random().nextInt(heads);
	}
}
