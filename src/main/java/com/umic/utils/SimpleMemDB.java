package com.umic.utils;

import javafx.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.System.out;

/**
 * Created by umic_ on 2018/3/31.
 */
public class SimpleMemDB {
    /*Simple HashMap Memory DB*/
    private static final SimpleMemDB simpleMemDB = new SimpleMemDB();

    public static SimpleMemDB getSimpleMemDB() {
        return simpleMemDB;
    }

    private static final HashMap<String, Pair<String, Long>> mem = new HashMap<>();

    private synchronized HashMap<String, Pair<String, Long>> getHandler() {
        return mem;
    }

    public void set(String k, String v) {
        getHandler().put(k, new Pair<>(v, new Date().getTime()));
    }

    public Pair<String, Long> get(String k) {
        return getHandler().getOrDefault(k, null);
    }

    public boolean unset(String k) {
        return getHandler().remove(k) != null;
    }

    public static String pack(final List<String> strList, final String div) {
        return div + strList.stream().reduce((a, b) -> a + div + b).orElse("");
    }

    public static List<String> unpack(String listString) {
        String div = listString.substring(0, 1);
        listString = listString.substring(1);
        return Arrays.asList(listString.split(div));
    }

    public static void main(String[] args) throws Exception {

        ArrayList<ArrayList<String>> listArrayList = new ArrayList<>();
        for (int i = 0; i < 5; ++i) {
            ArrayList<String> strings = new ArrayList<>();
            strings.add("2ad");
            strings.add("2dasd");
            listArrayList.add(strings);
        }
        String dataString = pack(listArrayList
                .stream()
                .map(item -> pack(item, "" + (char) 1))
                .collect(Collectors.toList()), "" + (char) 2);
        out.println(dataString);
        unpack(dataString).forEach(item -> {
            out.println(unpack(item));
        });

    }
}
