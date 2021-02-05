package io.github.lmikoto;

import lombok.Data;
import lombok.SneakyThrows;

import java.io.Serializable;

/**
 * @author liuyang
 * 2021/2/4 2:04 下午
 */
@Data
public class DubboEntity implements Serializable,Cloneable {

    private String interfaceName;

    private String methodName;

    private String version;

    private String[] methodType;

    private Object[] param;

    private String address;

    private Integer timeout;

    @SneakyThrows
    @Override
    protected DubboEntity clone() {
        return (DubboEntity)super.clone();
    }
}
