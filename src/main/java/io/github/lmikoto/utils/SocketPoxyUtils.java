package io.github.lmikoto.utils;

/**
 * @author liuyang
 */
public class SocketPoxyUtils {

    public static void setProxy(String ip,String port){
        System.setProperty("socks.ProxyHost", ip);
        System.setProperty("socks.ProxyPort", port);
    }

    public static void cancelProxy(){
        System.clearProperty("socks.ProxyHost");
        System.clearProperty("socks.ProxyPort");
    }
}
