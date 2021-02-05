package io.github.lmikoto;

import lombok.Data;

/**
 * @author liuyang
 * 2021/2/4 2:04 下午
 */
@Data
public class DubboEntity {

    private String interfaceName;

    private String methodName;

    private String version;

    private String[] methodType;

    private Object[] param;

    private String address;

    private Integer timeout;
}
