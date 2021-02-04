package io.github.lmikoto;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public List<String> getAddress(){
        if(CollectionUtils.isEmpty(address)){
            address = new ArrayList<>();
            address.add("dubbo://127.0.0.1:26880");
        }
        return this.address;
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
