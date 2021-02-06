package io.github.lmikoto;

import org.omg.CORBA.UNKNOWN;

/**
 * @author liuyang
 * 2021/2/5 10:27 上午
 */
public enum Address {

    DUBBO,
    REGISTRY,
    UNKNOWN;

    public static Address getAddressType(String address){
        if(address.startsWith("dubbo")){
            return DUBBO;
        }
        // todo 补上其他注册中心
        if (address.startsWith("zookeeper") ||
                address.startsWith("nacos")
        ){
            return REGISTRY;
        }
        return UNKNOWN;
    }
}
