package io.github.lmikoto;

import static io.github.lmikoto.Const.DUBBO_PREFIX;
import static io.github.lmikoto.Const.NACOS_PREFIX;
import static io.github.lmikoto.Const.ZK_PREFIX;

import com.google.common.collect.Sets;
import java.util.Set;

/**
 * @author liuyang
 * 2021/2/5 10:27 上午
 */
public enum Address {

    /**
     * dubbo url
     */
    DUBBO,
    /**
     * 注册中心 url
     */
    REGISTRY,
    /**
     * 未知
     */
    UNKNOWN;


    /**
     * todo 补上其他注册中心
     */
    private static final Set<String> REGISTRY_PREFIXES = Sets.newHashSet(ZK_PREFIX, NACOS_PREFIX);

    public static Address getAddressType(String address){
        if(address.startsWith(DUBBO_PREFIX)){
            return DUBBO;
        }
        for (String registryPrefix : REGISTRY_PREFIXES) {
            if (address.startsWith(registryPrefix)) {
                return REGISTRY;
            }
        }
        return UNKNOWN;
    }
}
