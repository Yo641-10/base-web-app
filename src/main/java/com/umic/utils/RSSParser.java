package com.umic.utils;

import javafx.util.Pair;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.System.out;


public class RSSParser {
    public static List<List<String>> parse(String uri) throws ParserConfigurationException, IOException, SAXException {
        Pair<String, Long> pair = SimpleMemDB.getSimpleMemDB().get(uri);
        out.println(new Date().getTime());
        if (pair == null || pair.getValue() < new Date().getTime() - 60000) {
            if (pair != null) {
                SimpleMemDB.getSimpleMemDB().unset(uri);
            }
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();
            InnerSaxParserHandler innerSaxParserHandler = new InnerSaxParserHandler();
            saxParser.parse(uri, innerSaxParserHandler);
            String dataString = SimpleMemDB.pack(innerSaxParserHandler.itemList
                    .stream()
                    .map(item -> SimpleMemDB.pack(item, "" + (char) 1))
                    .collect(Collectors.toList()), "" + (char) 2);
            SimpleMemDB.getSimpleMemDB().set(uri, dataString);
            out.println("use Download.");
        } else {
            out.println("use Cache.");
        }
        pair = SimpleMemDB.getSimpleMemDB().get(uri);
        if (pair != null) {
            return SimpleMemDB.unpack(pair.getKey()).stream().map(SimpleMemDB::unpack).collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }

    }

    public static void main(String[] args) throws Exception {
        parse("http://rss.sina.com.cn/news/china/focus15.xml");
    }

    private static class InnerSaxParserHandler extends DefaultHandler {
        List<List<String>> itemList = new ArrayList<>();
        List<String> item = null;
        String key = null;
        String value = null;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            if (item != null) {
                key = qName;
                value = "";
            }
            if (qName.equals("item")) {
                item = new ArrayList<>();
            }

        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            if (key != null) {
                item.add(key + "#" + value.replaceAll("[\n\r\t]", "").trim());
                key = null;
                value = null;
            }
            if (qName.equals("item")) {
                itemList.add(item);
                item = null;
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            if (item != null && key != null) {
                value += new String(ch, start, length);
            }
        }
    }
}
