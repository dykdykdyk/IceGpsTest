package ice.rtk.utils.gpxparser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

class BaseGPX {

    final SimpleDateFormat xmlDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    final ArrayList<ice.rtk.utils.gpxparser.extension.IExtensionParser> extensionParsers = new ArrayList<>();

    BaseGPX() {
        // TF, 20170515: iso6801 dates are always in GMT timezone
        xmlDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    /**
     * Adds a new extension parser to be used when parsing a gpx steam
     *
     * @param parser an instance of a {@link ice.rtk.utils.gpxparser.extension.IExtensionParser} implementation
     */
    public void addExtensionParser(ice.rtk.utils.gpxparser.extension.IExtensionParser parser) {
        this.extensionParsers.add(parser);
    }

    /**
     * Removes an extension parser previously added
     *
     * @param parser an instance of a {@link ice.rtk.utils.gpxparser.extension.IExtensionParser} implementation
     */
    public void removeExtensionParser(ice.rtk.utils.gpxparser.extension.IExtensionParser parser) {
        this.extensionParsers.remove(parser);
    }
}
