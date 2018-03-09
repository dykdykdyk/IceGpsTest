package ice.rtk.utils.gpxparser.modal;

import java.util.ArrayList;

/**
 * This class holds gpx information from a &lt;gpx&gt; node. <br>
 * <p>
 * GPX specification for this tag:
 * </p>
 * <code>
 * &lt;gpx version="1.1" creator=""xsd:string [1]"&gt;<br>
 * &nbsp;&nbsp;&nbsp;&lt;metadata&gt; xsd:string &lt;/metadata&gt; [0..1]<br>
 * &nbsp;&nbsp;&nbsp;&lt;wpt&gt; xsd:string &lt;/wpt&gt; [0..1]<br>
 * &nbsp;&nbsp;&nbsp;&lt;rte&gt; xsd:string &lt;/rte&gt; [0..1]<br>
 * &nbsp;&nbsp;&nbsp;&lt;trk&gt; xsd:string &lt;/trk&gt; [0..1]<br>
 * &nbsp;&nbsp;&nbsp;&lt;extensions&gt; extensionsType &lt;/extensions&gt; [0..1]<br>
 * &lt;/gpx&gt;<br>
 * </code>
 */
public class GPX extends Extension {

	// Attributes
	private String creator;
	private String version = "1.1";

	// Nodes
	private ice.rtk.utils.gpxparser.modal.Metadata metadata;
	private ArrayList<ice.rtk.utils.gpxparser.modal.Route> routes;
	private ArrayList<ice.rtk.utils.gpxparser.modal.Track> tracks;
	private ArrayList<ice.rtk.utils.gpxparser.modal.Waypoint> waypoints;

	public GPX() {
		this.waypoints = new ArrayList<>();
		this.tracks = new ArrayList<ice.rtk.utils.gpxparser.modal.Track>();
		this.routes = new ArrayList<ice.rtk.utils.gpxparser.modal.Route>();
	}

	/**
	 * Adds a new Route to a gpx object
	 *
	 * @param route
	 *            a {@link ice.rtk.utils.gpxparser.modal.Route}
	 */
	public void addRoute(ice.rtk.utils.gpxparser.modal.Route route) {
		if (this.routes == null) {
			this.routes = new ArrayList<ice.rtk.utils.gpxparser.modal.Route>();
		}
		this.routes.add(route);
	}

	/**
	 * Adds a new track to a gpx object
	 *
	 * @param track
	 *            a {@link ice.rtk.utils.gpxparser.modal.Track}
	 */
	public void addTrack(ice.rtk.utils.gpxparser.modal.Track track) {
		if (this.tracks == null) {
			this.tracks = new ArrayList<ice.rtk.utils.gpxparser.modal.Track>();
		}
		this.tracks.add(track);
	}

	/**
	 * Adds a new waypoint to a gpx object
	 *
	 * @param waypoint
	 *            a {@link ice.rtk.utils.gpxparser.modal.Waypoint}
	 */
	public void addWaypoint(ice.rtk.utils.gpxparser.modal.Waypoint waypoint) {
		if (this.waypoints == null) {
			this.waypoints = new ArrayList<ice.rtk.utils.gpxparser.modal.Waypoint>();
		}
		this.waypoints.add(waypoint);

	}

	/**
	 * Returns the creator of this gpx object
	 *
	 * @return A String representing the creator of a gpx object
	 */
	public String getCreator() {
		return this.creator;
	}

	/**
	 * Getter for the list of routes from a gpx object
	 *
	 * @return a HashSet of {@link ice.rtk.utils.gpxparser.modal.Route}
	 */
	public ArrayList<ice.rtk.utils.gpxparser.modal.Route> getRoutes() {
		return this.routes;
	}

	/**
	 * Getter for the list of Tracks from a gpx objecty
	 *
	 * @return a HashSet of {@link ice.rtk.utils.gpxparser.modal.Track}
	 */
	public ArrayList<ice.rtk.utils.gpxparser.modal.Track> getTracks() {
		return this.tracks;
	}

	/**
	 * Returns the version of a gpx object
	 *
	 * @return A String representing the version of this gpx object
	 */
	public String getVersion() {
		return this.version;
	}

	/**
	 * Getter for the list of waypoints from a gpx objecty
	 *
	 * @return a HashSet of {@link ice.rtk.utils.gpxparser.modal.Waypoint}
	 */
	public ArrayList<ice.rtk.utils.gpxparser.modal.Waypoint> getWaypoints() {
		return this.waypoints;
	}

	/**
	 * Setter for gpx creator property. This maps to <i>creator</i> attribute
	 * value.
	 *
	 * @param creator
	 *            A String representing the creator of a gpx file.
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}

	/**
	 * Setter for the list of routes from a gpx object
	 *
	 * @param routes
	 *            a HashSet of {@link ice.rtk.utils.gpxparser.modal.Route}
	 */
	public void setRoutes(ArrayList<ice.rtk.utils.gpxparser.modal.Route> routes) {
		this.routes = routes;
	}

	/**
	 * Setter for the list of tracks from a gpx object
	 *
	 * @param tracks
	 *            a HashSet of {@link ice.rtk.utils.gpxparser.modal.Track}
	 */
	public void setTracks(ArrayList<ice.rtk.utils.gpxparser.modal.Track> tracks) {
		this.tracks = tracks;
	}

	/**
	 * Setter for the list of waypoints from a gpx object
	 *
	 * @param waypoints
	 *            a HashSet of {@link ice.rtk.utils.gpxparser.modal.Waypoint}
	 */
	public void setWaypoints(ArrayList<ice.rtk.utils.gpxparser.modal.Waypoint> waypoints) {
		this.waypoints = waypoints;
	}

	public ice.rtk.utils.gpxparser.modal.Metadata getMetadata() {
		return metadata;
	}

	public void setMetadata(ice.rtk.utils.gpxparser.modal.Metadata metadata) {
		this.metadata = metadata;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}
