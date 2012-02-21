/*
 * Copyright (C) 2012 TomyLobo
 *
 * This file is part of Routes.
 *
 * Routes is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.tomylobo.routes.commands;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import eu.tomylobo.routes.Route;
import eu.tomylobo.routes.commands.system.Command;
import eu.tomylobo.routes.commands.system.CommandContainer;
import eu.tomylobo.routes.commands.system.NestedCommand;

/**
 * Contains all commands connected to route management.
 *
 * @author TomyLobo
 *
 */
public class RoutesCommands extends CommandContainer implements Listener {
	public RoutesCommands() {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@NestedCommand
	public void routes(CommandSender sender, String commandName, String label, String[] args) {
		if (args.length < 1) {
			sender.sendMessage("/"+label+" expects a sub-command.");
		}
		else {
			sender.sendMessage("Could not find the specified /"+label+" sub-command.");
		}
	}

	Material toolMaterial = Material.GOLD_SPADE;
	@Command
	public void routes_add(CommandSender sender, String commandName, String label, String[] args) {
		if (!(sender instanceof Player))
			return;

		final Player player = (Player) sender;

		final String routeName = args[0];

		final Route route = new Route();
		plugin.transportSystem.addRoute(routeName, route);

		editedRoutes.put(player, route);

		sender.sendMessage("Starting a route named '"+routeName+"' here. Right-click with "+toolMaterial+" to add a waypoint.");
	}

	Map<Player, Route> editedRoutes = new HashMap<Player, Route>();

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		switch (event.getAction()) {
		case RIGHT_CLICK_AIR:
		case RIGHT_CLICK_BLOCK:
			final Player player = event.getPlayer();

			if (player.getItemInHand().getType() != toolMaterial)
				return;

			final Route route = editedRoutes.get(player);
			if (route == null)
				return;

			route.addNodes(player.getLocation());
			route.visualize(1.0);

			break;
		}
	}
}
