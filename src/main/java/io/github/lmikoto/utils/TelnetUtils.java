package io.github.lmikoto.utils;

import io.github.lmikoto.Const;
import io.github.lmikoto.DubboEntity;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

public class TelnetUtils {

    private static final String INVOKE = "invoke";

    private static final String BLANK = " ";

    private static final String DOT = ".";

    private static final String BRACKETS_START = "(";

    private static final String BRACKETS_END = ")";

    private static final String COMMA = ",";

    public static void copy(DubboEntity entity) {
        StringBuffer invoke = new StringBuffer();
        invoke.append(INVOKE)
                .append(BLANK)
                .append(entity.getInterfaceName())
                .append(DOT)
                .append(entity.getMethodName())
                .append(BRACKETS_START);

        // 第一个不需要加逗号
        boolean flag = true;
        Object[] params = entity.getParam();
        for(Object param: params){
            if(!flag){
                invoke.append(COMMA);
            }else{
                flag = false;
            }
            invoke.append(JsonUtils.toJson(param));
        }

        invoke.append(BRACKETS_END);

        // 写剪切板
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable trans = new StringSelection(invoke.toString());
        clipboard.setContents(trans, null);
    }
}
