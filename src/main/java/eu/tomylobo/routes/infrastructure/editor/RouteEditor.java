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

package eu.tomylobo.routes.infrastructure.editor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import eu.tomylobo.abstraction.Environment;
import eu.tomylobo.abstraction.entity.Player;
import eu.tomylobo.abstraction.event.Event;
import eu.tomylobo.abstraction.event.EventHandler;
import eu.tomylobo.abstraction.event.EventPriority;
import eu.tomylobo.abstraction.event.PlayerClickEvent;
import eu.tomylobo.routes.Routes;
import eu.tomylobo.routes.infrastructure.Route;

public class RouteEditor {
	private final Map<Player, RouteEditSession> routeEditSessions = new HashMap<Player, RouteEditSession>();
	private final Multimap<Route, Player> editedRoutes = ArrayListMultimap.create();

	private final Routes plugin;

	public RouteEditor(Routes plugin) {
		this.plugin = plugin;

		Environment.dispatcher().registerEvents(this, plugin);
		plugin.getCommandSystem().registerPlayerMap(RouteEditSession.class, routeEditSessions);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerClick(PlayerClickEvent event) {
		if (event.isCancelled())
			return;

		final Player player = event.getPlayer();

		final int inHand = player.getItemTypeInHand();
		if (inHand == plugin.config.editorTool) {
			final RouteEditSession routeEditSession = routeEditSessions.get(player);
			if (routeEditSession == null)
				return;

			routeEditSession.state.onPlayerClick(event.isRightClick());
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerMove(Event event) {
		if (event.isCancelled())
			return;

		final Player player = event.getPlayer();

		final int inHand = player.getItemTypeInHand();
		if (inHand == plugin.config.editorTool) {
			final RouteEditSession routeEditSession = routeEditSessions.get(player);
			if (routeEditSession == null)
				return;

			routeEditSession.state.onPlayerMove(event.getLocation());
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerQuit(Event event) {
		close(event.getPlayer());
	}

	/**
	 * Creates a new edit session for the player and returns it.<br />
	 * Closes the player's previous edit session.
	 *
	 * @param player
	 * @param route The route to edit
	 * @return the new edit session
	 */
	public RouteEditSession edit(Player player, Route route) {
		close(player);

		final RouteEditSession newSession = new RouteEditSession(player, route);
		routeEditSessions.put(player, newSession);
		editedRoutes.put(route, player);
		return newSession;
	}

	public Collection<Player> getPlayersEditing(Route route) {
		return editedRoutes.get(route);
	}

	/**
	 * Closes the player's current edit session.
	 *
	 * @param player
	 * @return
	 */
	public void close(Player player) {
		final RouteEditSession oldSession = routeEditSessions.remove(player);
		if (oldSession != null) {
			oldSession.close();
			editedRoutes.remove(oldSession.getRoute(), player);
		}
	}

	public void broadcastRefreshRouteSegment(Route route, int startIndex, int oldAmount, int newAmount) {
		for (Player player : editedRoutes.get(route)) {
			routeEditSessions.get(player).state.refreshSegment(startIndex, oldAmount, newAmount);
		}
	}
}
