package io.github.lmikoto.utils;

import static org.apache.dubbo.common.constants.CommonConstants.GENERIC_SERIALIZATION_DEFAULT;

import io.github.lmikoto.Address;
import io.github.lmikoto.Const;
import io.github.lmikoto.DubboEntity;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.rpc.service.GenericService;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liuyang
 * 2021/2/5 10:05 上午
 */
public class DubboUtils {

    private static final ApplicationConfig APPLICATION = new ApplicationConfig();

    private static final Map<String, ReferenceConfig<GenericService>> CACHE_REFERENCE_MAP = new ConcurrentHashMap<>();

    private static final Map<String, RegistryConfig> REGISTRY_CONFIG_CACHE = new ConcurrentHashMap<>();

    static {
        APPLICATION.setName(Const.NAME);
    }

    public static Object invoke(DubboEntity entity){
        if(Objects.isNull(entity) || StringUtils.isBlank(entity.getAddress()) ||  StringUtils.isBlank(entity.getInterfaceName()) || StringUtils.isBlank(entity.getMethodName()) ){
            return "地址或接口为空";
        }

        Address address = Address.getAddressType(entity.getAddress());
        if (address.equals(Address.UNKNOWN)) {
            return "无效地址";
        }
        ReferenceConfig<GenericService> referenceConfig = getReferenceConfig(entity);
        if (referenceConfig == null) {
            return null;
        } else {
            GenericService genericService = referenceConfig.get();
            if (genericService == null) {
                return null;
            } else {
                try {
                    return genericService.$invoke(entity.getMethodName(), entity.getMethodType(), entity.getParam());
                } catch (Exception e) {
                    referenceConfig.destroy();
                    String key = getCacheKey(entity);
                    CACHE_REFERENCE_MAP.remove(key);
                    return e.getLocalizedMessage();
                }
            }
        }
    }

    private static String getCacheKey(DubboEntity entity){
        Address address = Address.getAddressType(entity.getAddress());
        return entity.getAddress() + "-" + address.name() + "-" + entity.getInterfaceName() + "-" + address.name() + '-' + entity.getVersion() + "-" + entity.getGroup();
    }

    private static ReferenceConfig<GenericService> getReferenceConfig(DubboEntity entity) {
        Thread.currentThread().setContextClassLoader(DubboEntity.class.getClassLoader());

        Address addressType = Address.getAddressType(entity.getAddress());

        String key = getCacheKey(entity);
        ReferenceConfig<GenericService> reference = CACHE_REFERENCE_MAP.get(key);

        if (Objects.isNull(reference)) {
            reference = new ReferenceConfig<>();
            reference.setApplication(APPLICATION);
            reference.setInterface(entity.getInterfaceName());
            reference.setCheck(false);
            reference.setGeneric(GENERIC_SERIALIZATION_DEFAULT);
            reference.setRetries(0);
            reference.setTimeout(entity.getTimeout());

            if (addressType.equals(Address.DUBBO)) {
                reference.setUrl(entity.getAddress());
            }

            if(addressType.equals(Address.REGISTRY)){
                RegistryConfig registryConfig = getRegistryConfig(entity.getAddress(), entity.getVersion());
                reference.setRegistry(registryConfig);
            }

            if (StringUtils.isNotBlank(entity.getVersion())) {
                reference.setVersion(entity.getVersion());
            }

            if(StringUtils.isNotEmpty(entity.getGroup())){
                reference.setGroup(entity.getGroup());
            }

            CACHE_REFERENCE_MAP.put(key, reference);
        }

        return reference;
    }

    private static RegistryConfig getRegistryConfig(String address, String version) {
        String key = address + "-" + version;
        RegistryConfig registryConfig = REGISTRY_CONFIG_CACHE.get(key);
        if (Objects.isNull(registryConfig)) {
            registryConfig = new RegistryConfig();
            if (StringUtils.isNotBlank(address)) {
                registryConfig.setAddress(address);
            }

            if (StringUtils.isNotBlank(version)) {
                registryConfig.setVersion(version);
            }

            REGISTRY_CONFIG_CACHE.put(key, registryConfig);
        }

        return registryConfig;
    }
}
