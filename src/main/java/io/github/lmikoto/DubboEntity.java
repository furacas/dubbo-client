package io.github.lmikoto;

import lombok.Data;

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

    private String group;

    @Override
    protected DubboEntity clone() throws CloneNotSupportedException {
        return (DubboEntity)super.clone();
    }
}
