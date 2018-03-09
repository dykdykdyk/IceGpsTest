package ice.rtk.utils.gpxparser;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.OutputStream;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import ice.rtk.utils.gpxparser.modal.Extension;
import ice.rtk.utils.gpxparser.modal.Metadata;
import ice.rtk.utils.gpxparser.modal.TrackSegment;

public class GPXWriter extends BaseGPX {

	public void writeGPX(ice.rtk.utils.gpxparser.modal.GPX gpx, OutputStream out) throws ParserConfigurationException, TransformerException {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.newDocument();
		Node gpxNode = doc.createElement(GPXConstants.NODE_GPX);

		NamedNodeMap attrs = gpxNode.getAttributes();
		if (gpx.getVersion() != null) {
			Node verNode = doc.createAttribute(GPXConstants.ATTR_VERSION);
			verNode.setNodeValue(gpx.getVersion());
			attrs.setNamedItem(verNode);
		}
		if (gpx.getCreator() != null) {
			Node creatorNode = doc.createAttribute(GPXConstants.ATTR_CREATOR);
			creatorNode.setNodeValue(gpx.getCreator());
			attrs.setNamedItem(creatorNode);
		}
		if (gpx.getMetadata() != null) {
			this.addMetadataToNode(gpx.getMetadata(), gpxNode, doc);
		}
		if (gpx.getWaypoints() != null) {
			for (ice.rtk.utils.gpxparser.modal.Waypoint wp : gpx.getWaypoints()) {
				this.addWaypointToNode(GPXConstants.NODE_WPT, wp, gpxNode, doc);
			}
		}
		if (gpx.getRoutes() != null) {
			for (ice.rtk.utils.gpxparser.modal.Route route : gpx.getRoutes()) {
				this.addRouteToNode(route, gpxNode, doc);
			}
		}

		if (gpx.getTracks() != null) {
			for (ice.rtk.utils.gpxparser.modal.Track track : gpx.getTracks()) {
				this.addTrackToNode(track, gpxNode, doc);
			}
		}

		doc.appendChild(gpxNode);

		// Use a Transformer for output
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer();

		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(out);
		transformer.transform(source, result);
	}

	private void addTrackToNode(ice.rtk.utils.gpxparser.modal.Track trk, Node n, Document doc) {
		Node trkNode = doc.createElement(GPXConstants.NODE_TRK);

		if (trk.getName() != null) {
			Node node = doc.createElement(GPXConstants.NODE_NAME);
			node.appendChild(doc.createTextNode(trk.getName()));
			trkNode.appendChild(node);
		}
		if (trk.getComment() != null) {
			Node node = doc.createElement(GPXConstants.NODE_CMT);
			node.appendChild(doc.createTextNode(trk.getComment()));
			trkNode.appendChild(node);
		}
		if (trk.getDescription() != null) {
			Node node = doc.createElement(GPXConstants.NODE_DESC);
			node.appendChild(doc.createTextNode(trk.getDescription()));
			trkNode.appendChild(node);
		}
		if (trk.getSrc() != null) {
			Node node = doc.createElement(GPXConstants.NODE_SRC);
			node.appendChild(doc.createTextNode(trk.getSrc()));
			trkNode.appendChild(node);
		}
		if (trk.getLinks() != null) {
			for (ice.rtk.utils.gpxparser.modal.Link l : trk.getLinks()) {
				this.addLinkToNode(l, trkNode, doc);
			}
		}
		if (trk.getNumber() != null) {
			Node node = doc.createElement(GPXConstants.NODE_NUMBER);
			node.appendChild(doc.createTextNode(trk.getNumber().toString()));
			trkNode.appendChild(node);
		}
		if (trk.getType() != null) {
			Node node = doc.createElement(GPXConstants.NODE_TYPE);
			node.appendChild(doc.createTextNode(trk.getType()));
			trkNode.appendChild(node);
		}
		this.addExtensionToNode(trk, trkNode, doc);

		if (trk.getTrackSegments() != null) {
			for (TrackSegment ts : trk.getTrackSegments()) {
				this.addTrackSegmentToNode(ts, trkNode, doc);
			}
		}
		n.appendChild(trkNode);
	}

	private void addTrackSegmentToNode(TrackSegment ts, Node n, Document doc) {
		Node tsNode = doc.createElement(GPXConstants.NODE_TRKSEG);

		for (ice.rtk.utils.gpxparser.modal.Waypoint wp : ts.getWaypoints()) {
			this.addWaypointToNode(GPXConstants.NODE_TRKPT, wp, tsNode, doc);
		}
		this.addExtensionToNode(ts, tsNode, doc);

		n.appendChild(tsNode);

	}

	private void addRouteToNode(ice.rtk.utils.gpxparser.modal.Route rte, Node gpxNode, Document doc) {
		Node rteNode = doc.createElement(GPXConstants.NODE_RTE);

		if (rte.getName() != null) {
			Node node = doc.createElement(GPXConstants.NODE_NAME);
			node.appendChild(doc.createTextNode(rte.getName()));
			rteNode.appendChild(node);
		}
		if (rte.getComment() != null) {
			Node node = doc.createElement(GPXConstants.NODE_CMT);
			node.appendChild(doc.createTextNode(rte.getComment()));
			rteNode.appendChild(node);
		}
		if (rte.getDescription() != null) {
			Node node = doc.createElement(GPXConstants.NODE_DESC);
			node.appendChild(doc.createTextNode(rte.getDescription()));
			rteNode.appendChild(node);
		}
		if (rte.getSrc() != null) {
			Node node = doc.createElement(GPXConstants.NODE_SRC);
			node.appendChild(doc.createTextNode(rte.getSrc()));
			rteNode.appendChild(node);
		}
		if (rte.getLinks() != null) {
			for (ice.rtk.utils.gpxparser.modal.Link l : rte.getLinks()) {
				this.addLinkToNode(l, rteNode, doc);
			}
		}
		if (rte.getNumber() != null) {
			Node node = doc.createElement(GPXConstants.NODE_NUMBER);
			node.appendChild(doc.createTextNode(rte.getNumber().toString()));
			rteNode.appendChild(node);
		}
		if (rte.getType() != null) {
			Node node = doc.createElement(GPXConstants.NODE_TYPE);
			node.appendChild(doc.createTextNode(rte.getType()));
			rteNode.appendChild(node);
		}
		this.addExtensionToNode(rte, rteNode, doc);

		if (rte.getRoutePoints() != null) {
			Iterator<ice.rtk.utils.gpxparser.modal.Waypoint> it = rte.getRoutePoints().iterator();
			while (it.hasNext()) {
				this.addWaypointToNode(GPXConstants.NODE_RTEPT, it.next(), rteNode, doc);
			}
		}
		gpxNode.appendChild(rteNode);
	}

	private void addWaypointToNode(String tag, ice.rtk.utils.gpxparser.modal.Waypoint wpt, Node n, Document doc) {
		Node wptNode = doc.createElement(tag);
		NamedNodeMap attrs = wptNode.getAttributes();
		if (wpt.getLatitude() != 0) {
			Node latNode = doc.createAttribute(GPXConstants.ATTR_LAT);
			latNode.setNodeValue(String.valueOf(wpt.getLatitude()));
			attrs.setNamedItem(latNode);
		}
		if (wpt.getLongitude() != 0) {
			Node longNode = doc.createAttribute(GPXConstants.ATTR_LON);
			longNode.setNodeValue(String.valueOf(wpt.getLongitude()));
			attrs.setNamedItem(longNode);
		}
		if (wpt.getElevation() != 0) {
			Node node = doc.createElement(GPXConstants.NODE_ELE);
			node.appendChild(doc.createTextNode(String.valueOf(wpt.getElevation())));
			wptNode.appendChild(node);
		}
		if (wpt.getTime() != null) {
			Node node = doc.createElement(GPXConstants.NODE_TIME);
			node.appendChild(doc.createTextNode(xmlDateFormat.format(wpt.getTime())));
			wptNode.appendChild(node);
		}
		if (wpt.getMagneticVariation() != 0) {
			Node node = doc.createElement(GPXConstants.NODE_MAGVAR);
			node.appendChild(doc.createTextNode(String.valueOf(wpt.getMagneticVariation())));
			wptNode.appendChild(node);
		}
		if (wpt.getGeoIdHeight() != 0) {
			Node node = doc.createElement(GPXConstants.NODE_GEOIDHEIGHT);
			node.appendChild(doc.createTextNode(String.valueOf(wpt.getGeoIdHeight())));
			wptNode.appendChild(node);
		}
		if (wpt.getName() != null) {
			Node node = doc.createElement(GPXConstants.NODE_NAME);
			node.appendChild(doc.createTextNode(wpt.getName()));
			wptNode.appendChild(node);
		}
		if (wpt.getComment() != null) {
			Node node = doc.createElement(GPXConstants.NODE_CMT);
			node.appendChild(doc.createTextNode(wpt.getComment()));
			wptNode.appendChild(node);
		}
		if (wpt.getDescription() != null) {
			Node node = doc.createElement(GPXConstants.NODE_DESC);
			node.appendChild(doc.createTextNode(wpt.getDescription()));
			wptNode.appendChild(node);
		}
		if (wpt.getSrc() != null) {
			Node node = doc.createElement(GPXConstants.NODE_SRC);
			node.appendChild(doc.createTextNode(wpt.getSrc()));
			wptNode.appendChild(node);
		}
		if (wpt.getLinks() != null) {
			for (ice.rtk.utils.gpxparser.modal.Link l : wpt.getLinks()) {
				this.addLinkToNode(l, wptNode, doc);
			}
		}
		if (wpt.getSym() != null) {
			Node node = doc.createElement(GPXConstants.NODE_SYM);
			node.appendChild(doc.createTextNode(wpt.getSym()));
			wptNode.appendChild(node);
		}
		if (wpt.getType() != null) {
			Node node = doc.createElement(GPXConstants.NODE_TYPE);
			node.appendChild(doc.createTextNode(wpt.getType()));
			wptNode.appendChild(node);
		}
		if (wpt.getFix() != null) {
			Node node = doc.createElement(GPXConstants.NODE_FIX);
			node.appendChild(doc.createTextNode(wpt.getFix().toString()));
			wptNode.appendChild(node);
		}
		if (wpt.getSat() != 0) {
			Node node = doc.createElement(GPXConstants.NODE_SAT);
			node.appendChild(doc.createTextNode(String.valueOf(wpt.getSat())));
			wptNode.appendChild(node);
		}
		if (wpt.getHdop() != 0) {
			Node node = doc.createElement(GPXConstants.NODE_HDOP);
			node.appendChild(doc.createTextNode(String.valueOf(wpt.getHdop())));
			wptNode.appendChild(node);
		}
		if (wpt.getVdop() != 0) {
			Node node = doc.createElement(GPXConstants.NODE_VDOP);
			node.appendChild(doc.createTextNode(String.valueOf(wpt.getVdop())));
			wptNode.appendChild(node);
		}
		if (wpt.getPdop() != 0) {
			Node node = doc.createElement(GPXConstants.NODE_PDOP);
			node.appendChild(doc.createTextNode(String.valueOf(wpt.getPdop())));
			wptNode.appendChild(node);
		}
		if (wpt.getAgeOfGPSData() != 0) {
			Node node = doc.createElement(GPXConstants.NODE_AGEOFGPSDATA);
			node.appendChild(doc.createTextNode(String.valueOf(wpt.getAgeOfGPSData())));
			wptNode.appendChild(node);
		}
		if (wpt.getdGpsStationId() != 0) {
			Node node = doc.createElement(GPXConstants.NODE_DGPSID);
			node.appendChild(doc.createTextNode(String.valueOf(wpt.getdGpsStationId())));
			wptNode.appendChild(node);
		}
		this.addExtensionToNode(wpt, wptNode, doc);
		n.appendChild(wptNode);
	}

	private void addMetadataToNode(Metadata m, Node n, Document doc) {
		Node matadataNode = doc.createElement(GPXConstants.NODE_METADATA);

		if (m.getName() != null) {
			Node node = doc.createElement(GPXConstants.NODE_NAME);
			node.appendChild(doc.createTextNode(m.getName()));
			matadataNode.appendChild(node);
		}

		if (m.getDesc() != null) {
			Node node = doc.createElement(GPXConstants.NODE_DESC);
			node.appendChild(doc.createTextNode(m.getDesc()));
			matadataNode.appendChild(node);
		}

		if (m.getAuthor() != null) {
			this.addAuthorToNode(m.getAuthor(), matadataNode, doc);
		}

		if (m.getCopyright() != null) {
			this.addCopyrightToNode(m.getCopyright(), matadataNode, doc);
		}

		if (m.getLinks() != null) {
			for (ice.rtk.utils.gpxparser.modal.Link l : m.getLinks()) {
				this.addLinkToNode(l, matadataNode, doc);
			}
		}

		if (m.getTime() != null) {
			Node node = doc.createElement(GPXConstants.NODE_TIME);
			node.appendChild(doc.createTextNode(xmlDateFormat.format(m.getTime())));
			matadataNode.appendChild(node);
		}

		if (m.getKeywords() != null) {
			Node node = doc.createElement(GPXConstants.NODE_KEYWORDS);
			node.appendChild(doc.createTextNode(m.getKeywords()));
			matadataNode.appendChild(node);
		}

		if (m.getBounds() != null) {
			this.addBoundsToNode(m.getBounds(), matadataNode, doc);
		}

		this.addExtensionToNode(m, matadataNode, doc);

		n.appendChild(matadataNode);
	}

	private void addBoundsToNode(ice.rtk.utils.gpxparser.modal.Bounds bounds, Node n, Document doc) {
		Node bonundsNode = doc.createElement(GPXConstants.NODE_BOUNDS);
		NamedNodeMap attributes = bonundsNode.getAttributes();

		Node node = doc.createAttribute(GPXConstants.ATTR_MINLAT);
		node.setNodeValue(String.valueOf(bounds.getMinLat()));
		attributes.setNamedItem(node);

		node = doc.createAttribute(GPXConstants.ATTR_MINLON);
		node.setNodeValue(String.valueOf(bounds.getMinLon()));
		attributes.setNamedItem(node);

		node = doc.createAttribute(GPXConstants.ATTR_MAXLAT);
		node.setNodeValue(String.valueOf(bounds.getMaxLat()));
		attributes.setNamedItem(node);

		node = doc.createAttribute(GPXConstants.ATTR_MAXLON);
		node.setNodeValue(String.valueOf(bounds.getMaxLon()));
		attributes.setNamedItem(node);
	}

	private void addCopyrightToNode(ice.rtk.utils.gpxparser.modal.Copyright copyright, Node n, Document doc) {
		Node copyrightNode = doc.createElement(GPXConstants.NODE_COPYRIGHT);
		NamedNodeMap attributes = copyrightNode.getAttributes();
		if (copyright.getAuthor() != null) {
			Node node = doc.createAttribute(GPXConstants.ATTR_AUTHOR);
			node.setNodeValue(copyright.getAuthor());
			attributes.setNamedItem(node);
		}

		if (copyright.getYear() != null) {
			Node node = doc.createElement(GPXConstants.NODE_YEAR);
			node.appendChild(doc.createTextNode(copyright.getYear()));
			n.appendChild(node);
		}
		if (copyright.getLicense() != null) {
			Node node = doc.createElement(GPXConstants.NODE_LICENSE);
			node.appendChild(doc.createTextNode(copyright.getLicense()));
			n.appendChild(node);
		}

		n.appendChild(copyrightNode);

	}

	private void addAuthorToNode(ice.rtk.utils.gpxparser.modal.Person author, Node n, Document doc) {
		Node authorNode = doc.createElement(GPXConstants.NODE_AUTHOR);
		if (author.getName() != null) {
			Node node = doc.createElement(GPXConstants.NODE_NAME);
			node.appendChild(doc.createTextNode(author.getName()));
			n.appendChild(node);
		}
		if (author.getEmail() != null) {
			this.addEmailToNode(author.getEmail(), authorNode, doc);
		}

		if (author.getLink() != null) {
			this.addLinkToNode(author.getLink(), authorNode, doc);
		}
		n.appendChild(authorNode);
	}

	private void addLinkToNode(ice.rtk.utils.gpxparser.modal.Link link, Node n, Document doc) {
		Node linkNode = doc.createElement(GPXConstants.NODE_LINK);
		NamedNodeMap attributes = linkNode.getAttributes();
		if (link.getHref() != null) {
			Node node = doc.createAttribute(GPXConstants.ATTR_HREF);
			node.setNodeValue(link.getHref());
			attributes.setNamedItem(node);
		}

		if (link.getText() != null) {
			Node node = doc.createElement(GPXConstants.NODE_TEXT);
			node.appendChild(doc.createTextNode(link.getText()));
			n.appendChild(node);
		}
		if (link.getType() != null) {
			Node node = doc.createElement(GPXConstants.NODE_TYPE);
			node.appendChild(doc.createTextNode(link.getType()));
			n.appendChild(node);
		}

		n.appendChild(linkNode);
	}

	private void addEmailToNode(ice.rtk.utils.gpxparser.modal.Email email, Node n, Document doc) {
		Node emailNode = doc.createElement(GPXConstants.NODE_EMAIL);
		NamedNodeMap attributes = emailNode.getAttributes();
		if (email.getId() != null) {
			Node node = doc.createAttribute(GPXConstants.ATTR_ID);
			node.setNodeValue(email.getId());
			attributes.setNamedItem(node);
		}
		if (email.getDomain() != null) {
			Node node = doc.createAttribute(GPXConstants.ATTR_DOMAIN);
			node.setNodeValue(email.getDomain());
			attributes.setNamedItem(node);
		}

	}

	private void addExtensionToNode(Extension e, Node n, Document doc) {
		if (e.getExtensionsParsed() > 0) {
			Node node = doc.createElement(GPXConstants.NODE_EXTENSIONS);
			for (ice.rtk.utils.gpxparser.extension.IExtensionParser parser : this.extensionParsers) {
				parser.writeExtensions(node, doc);
			}
			n.appendChild(node);
		}
	}
}