import com.sun.tools.doclint.HtmlTag;
import org.xml.sax.*;
import org.xml.sax.helpers.XMLReaderFactory;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.zip.ZipInputStream;

public class Model extends Observable implements Serializable {
    private EnumMap<WayType, List<Shape>> shapes = new EnumMap<>(WayType.class); {
        for (WayType type : WayType.values()) {
            shapes.put(type, new ArrayList<>());
        }
    }
    private float minlat, minlon, maxlat, maxlon;
    private long id;

    public Model(String filename) {
        load(filename);
    }

    public Model() {

    }

    public void add(WayType type, Shape shape) {
        shapes.get(type).add(shape);
        dirty();
    }

    private void dirty() {
        setChanged();
        notifyObservers();
    }

    public Iterable<Shape> get(WayType type) {
        return shapes.get(type);
    }

    public void save(String filename) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(shapes);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load(String filename) {
        if (filename.endsWith(".osm")) {
            loadOSM(new InputSource(filename));
        } else if (filename.endsWith(".zip")) {
            try {
                ZipInputStream zip = new ZipInputStream(new FileInputStream(filename));
                zip.getNextEntry();
                loadOSM(new InputSource(zip));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
                shapes = (EnumMap<WayType,List<Shape>>) in.readObject();
                dirty();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadOSM(InputSource source) {
        try {
            XMLReader reader = XMLReaderFactory.createXMLReader();
            reader.setContentHandler(new OSMHandler());
            reader.parse(source);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public float getMinLon() {
        return minlon;
    }

    public float getMaxLat() {
        return maxlat;
    }

    public float getMaxLon() {
        return maxlon;
    }

    private class OSMHandler implements ContentHandler {
        Map<Long,OSMNode> idToNode = new HashMap<>();
        Map<Long,OSMWay> idToWay = new HashMap<>();
        OSMWay way;
        OSMRelation relation;
        WayType type;
        private float lonfactor;
        private int minLon;

        @Override
        public void setDocumentLocator(Locator locator) {

        }

        @Override
        public void startDocument() throws SAXException {

        }

        @Override
        public void endDocument() throws SAXException {

        }

        @Override
        public void startPrefixMapping(String prefix, String uri) throws SAXException {

        }

        @Override
        public void endPrefixMapping(String prefix) throws SAXException {

        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
            switch(qName) {
                case "bounds":
                    parseBounds(atts);
                    break;
                case "node":
                    parseNodes(atts);
                    break;
                case "way":
                    parseWays(atts);
                    break;
                case "relation":
                    parseRelations(atts);
                    break;
                case "nd":
                    parseNds(atts);
                    break;
                case "tag":
                    parseTags(atts);
                    break;
                case "member":
                    parseMembers(atts);
                    break;
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            switch (qName) {
                case "way":
                    Path2D path = new Path2D.Float();
                    OSMNode node = way.get(0);
                    path.moveTo(node.getLon(), node.getLat());
                    for (int i = 1 ; i < way.size() ; i++) {
                        node = way.get(i);
                        path.lineTo(node.getLon(), node.getLat());
                    }
                    add(type, path);
                    break;
                case "relation":
                    path = new Path2D.Float();
                    path.setWindingRule(Path2D.WIND_EVEN_ODD);
                    for (OSMWay way : relation) if (way != null) {
                        node = way.get(0);
                        path.moveTo(node.getLon(), node.getLat());
                        for (int i = 1 ; i < way.size() ; i++) {
                            node = way.get(i);
                            path.lineTo(node.getLon(), node.getLat());
                        }
                    }
                    add(type, path);
                    break;
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {

        }

        @Override
        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {

        }

        @Override
        public void processingInstruction(String target, String data) throws SAXException {

        }

        @Override
        public void skippedEntity(String name) throws SAXException {

        }

        private void parseBounds(Attributes atts) {
            minlat = Float.parseFloat(atts.getValue("minlat"));
            minlon = Float.parseFloat(atts.getValue("minlon"));
            maxlat = Float.parseFloat(atts.getValue("maxlat"));
            maxlon = Float.parseFloat(atts.getValue("maxlon"));
            float avglat = minlat + (maxlat - minlat) / 2;
            lonfactor = (float) Math.cos(avglat / 180 * Math.PI);
            minlon *= lonfactor;
            maxlon *= lonfactor;
            minlat = -minlat;
            maxlat = -maxlat;
        }

        private void parseNodes(Attributes atts) {
            long id = Long.parseLong(atts.getValue("id"));
            float lat = Float.parseFloat(atts.getValue("lat"));
            float lon = Float.parseFloat(atts.getValue("lon"));
            idToNode.put(id, new OSMNode(lonfactor * lon, -lat));
        }

        private void parseWays(Attributes atts) {
            way = new OSMWay();
            id = Long.parseLong(atts.getValue("id"));
            type = WayType.UNKNOWN;
            idToWay.put(id, way);
        }

        private void parseRelations(Attributes atts) {
            relation = new OSMRelation();
            type = WayType.UNKNOWN;
        }

        private void parseNds(Attributes atts) {
            long ref = Long.parseLong(atts.getValue("ref"));
            way.add(idToNode.get(ref));
        }

        private void parseTags(Attributes atts) {
            String k = atts.getValue("k");
            String v = atts.getValue("v");
            switch (k) {
                case "highway":
                    type = WayType.ROAD;
                    if (v.equals("footway")) {
                        type = WayType.FOOTWAY;
                    }
                    if (v.equals("residential")) {
                        type = WayType.RESIDENTIAL_ROAD;
                    }
                    break;
                case "natural":
                    if (v.equals("water")) {
                        type = WayType.WATER;
                    }
                    if (v.equals("coastline")) {
                        type = WayType.COASTLINE;
                    }
                    break;
                case "leisure":
                    if (v.equals("park")) {
                        type = WayType.PARK;
                    }
                    break;
                case "building":
                    if (v.equals("yes")) {
                        type = WayType.BUILDING;
                    }
                    break;
                case "landuse":
                    if (v.equals("farmland")) {
                        type = WayType.FARMLAND;
                    }
                    if (v.equals("forest")) {
                        type = WayType.FOREST;
                    }
                    break;
            }
        }

        private void parseMembers(Attributes atts) {
            long ref = Long.parseLong(atts.getValue("ref"));
            relation.add(idToWay.get(ref));
        }
    }
}
