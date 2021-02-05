package io.github.lmikoto;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.psi.PsiParameterList;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author liuyang
 * 2021/2/4 5:00 下午
 */
@State(
        name = "io.github.lmikoto.dubbo.client",
        storages = {@Storage("DubboClient.xml")}
)
public class Setting implements PersistentStateComponent<Setting> {

    @Setter
    public List<String> address;

    @Getter
    public Map<String,DubboEntity> entityCache;

    public DubboEntity getCache(String interfaceName, String methodName, String[] methodType){
        if(Objects.isNull(entityCache)){
            return null;
        }
        String key = cacheKey(interfaceName,methodName,methodType);
        DubboEntity cache = entityCache.get(key);
        if(Objects.isNull(cache)){
            return null;
        }
        return cache.clone();
    }

    public void addCache(DubboEntity entity){

        if(Objects.isNull(entityCache)){
            entityCache = new HashMap<>();
        }
        entityCache.put(cacheKey(entity.getInterfaceName(),entity.getMethodName(),entity.getMethodType()), entity.clone());
    }

    private String cacheKey(String interfaceName, String methodName, String[] methodType){
        StringBuilder builder = new StringBuilder();
        builder.append(interfaceName);
        builder.append(".");
        builder.append(methodName);
        builder.append("#");
        for (String type: methodType){
            builder.append(type);
            builder.append("-");
        }
        return builder.toString();
    }

    public List<String> getAddress(){
        if(CollectionUtils.isEmpty(address)){
            address = new ArrayList<>();
        }
        return address;
    }

    public static Setting getInstance() {
        return ServiceManager.getService(Setting.class);
    }

    @Override
    public @Nullable Setting getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull Setting state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
