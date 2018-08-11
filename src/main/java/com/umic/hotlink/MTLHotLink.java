package com.umic.hotlink;

import io.netty.handler.codec.base64.Base64Encoder;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;
import java.util.Optional;

public class MTLHotLink {
    private static MTLHotLink ourInstance = new MTLHotLink();

    public static MTLHotLink getInstance() {
        return ourInstance;
    }

    private MTLHotLink() {
    }

    private Optional<String> getPictureBase64(String id, String page) {
        try {
            URL url = new URL(String.format("https://mtl.ttsqgs.com/images/img/%s/%s.jpg", id, page));
            URLConnection connection = url.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
            httpURLConnection.setRequestProperty("Referer", "https://www.meitulu.com/item/");
            httpURLConnection.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                            "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.62 Safari/537.36");
            InputStream inputStream = httpURLConnection.getInputStream();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] bytes = new byte[1024];
            while (inputStream.read(bytes) > 0) {
                byteArrayOutputStream.write(bytes);
            }
            byte[] base64Bytes = Base64.getEncoder().encode(byteArrayOutputStream.toByteArray());
            return Optional.of(new String(base64Bytes));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public String getPicture(String id, String page) {
        String prefix = "data:image/gif;base64,";
        Optional<String> picString = getPictureBase64(id, page);
        if (picString.isPresent()) {
            return prefix + picString.get();
        } else {
            return "";
        }
    }

    public static void main(String[] args) throws Exception {
        Optional<String> base64 = MTLHotLink.getInstance().getPictureBase64("2876", "1");
        System.out.println(base64.orElse("NULL"));
    }
}
