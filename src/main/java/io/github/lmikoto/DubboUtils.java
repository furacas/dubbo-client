package io.github.lmikoto;

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

    private static ApplicationConfig application = new ApplicationConfig();

    private static Map<String, ReferenceConfig<GenericService>> cacheReferenceMap = new ConcurrentHashMap();

    private static Map<String, RegistryConfig> registryConfigCache = new ConcurrentHashMap();

    static {
        application.setName(Const.NAME);
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
                    String key = address.name() + "-" + entity.getInterfaceName() + address.name();;
                    cacheReferenceMap.remove(key);
                    return e.getLocalizedMessage();
                }
            }
        }
    }

    private static ReferenceConfig<GenericService> getReferenceConfig(DubboEntity entity) {
        Thread.currentThread().setContextClassLoader(DubboEntity.class.getClassLoader());

        Address addressType = Address.getAddressType(entity.getAddress());

        String key = addressType.name() + "-" + entity.getInterfaceName() + addressType.name();
        ReferenceConfig<GenericService> reference = cacheReferenceMap.get(key);
        if (Objects.isNull(reference)) {
            reference = new ReferenceConfig<>();
            reference.setApplication(application);
            reference.setInterface(entity.getInterfaceName());
            reference.setCheck(false);
            reference.setGeneric(true);
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

            cacheReferenceMap.put(key, reference);
        }

        return reference;
    }

    private static RegistryConfig getRegistryConfig(String address, String version) {
        String key = address + "-" + version;
        RegistryConfig registryConfig = registryConfigCache.get(key);
        if (Objects.isNull(registryConfig)) {
            registryConfig = new RegistryConfig();
            if (StringUtils.isNotBlank(address)) {
                registryConfig.setAddress(address);
            }

            if (StringUtils.isNotBlank(version)) {
                registryConfig.setVersion(version);
            }

            registryConfigCache.put(key, registryConfig);
        }

        return registryConfig;
    }
}
