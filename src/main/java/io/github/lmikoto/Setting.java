package io.github.lmikoto;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Getter;
import lombok.Setter;
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

    public DubboEntity getCache(String interfaceName,String methodName){
        if(Objects.isNull(entityCache)){
            return null;
        }
        // 防止缓存错乱
        DubboEntity entity = entityCache.get(interfaceName + "." + methodName);
        if(!Objects.equals(entity.getInterfaceName(),interfaceName) || !Objects.equals(entity.getMethodName(),methodName)){
            return null;
        }
        return entityCache.get(interfaceName + "." + methodName);
    }

    public void addCache(DubboEntity entity){
        if(Objects.isNull(entityCache)){
            entityCache = new HashMap<>();
        }
        entityCache.put(entity.getInterfaceName() + "." + entity.getMethodName(),entity);
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
