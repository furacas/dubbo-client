package io.github.lmikoto;

import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
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

    static {
        application.setName("DubboClient");
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
                    Object invoke = genericService.$invoke(entity.getMethodName(), entity.getMethodType(), entity.getParam());
                    return invoke;
                } catch (Exception e) {
                    referenceConfig.destroy();
                    String key = address.name() + "-" + entity.getInterfaceName();
                    cacheReferenceMap.remove(key);
                    return e.getLocalizedMessage();
                }
            }
        }
    }

    private static ReferenceConfig<GenericService> getReferenceConfig(DubboEntity entity) {
        Thread.currentThread().setContextClassLoader(DubboEntity.class.getClassLoader());

        Address addressType = Address.getAddressType(entity.getAddress());

        String key = addressType.name() + "-" + entity.getInterfaceName();
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

            if (StringUtils.isNotBlank(entity.getVersion())) {
                reference.setVersion(entity.getVersion());
            }

            cacheReferenceMap.put(key, reference);
        }

        return reference;
    }
}
