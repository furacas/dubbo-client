package io.github.lmikoto;

/**
 * @author liuyang
 * 2021/2/5 10:27 上午
 */
public enum Address {

    DUBBO,
    ZOOKEEPER,
    NACOS,
    UNKNOWN;

    public static Address getAddressType(String address){
        for (Address item: Address.values()){
            if(address.startsWith(item.name().toLowerCase())){
                return item;
            }
        }
        return UNKNOWN;
    }
}
