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

package eu.tomylobo.routes.trace;

import eu.tomylobo.math.Location;
import eu.tomylobo.math.Vector;

public interface Shape {
	/**
	 * Traces from the given <code>location</code> along the direction described by it.
	 *
	 * @param location
	 * @return
	 */
	public TraceResult trace(Location location);

	/**
	 * Traces from <code>start</code> along the given <code>direction</code>.
	 *
	 * @param start
	 * @param orientation
	 * @return
	 */
	public TraceResult trace(Vector start, Vector direction);

	/**
	 * Traces from <code>start</code> towards <code>end</code>.
	 *
	 * @param start
	 * @param end
	 * @return
	 */
	public TraceResult traceToPoint(Vector start, Vector end);
}
