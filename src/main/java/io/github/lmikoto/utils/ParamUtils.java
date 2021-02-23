package io.github.lmikoto.utils;

import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import io.github.lmikoto.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liuyang
 * 2021/2/4 1:30 下午
 */
public class ParamUtils {
    public static Object[] getInitParamArray(PsiParameterList parameterList) {
        List<Object> paramList = new ArrayList<>();
        for(PsiParameter parameter: parameterList.getParameters()){
            Type type = Type.fromParam(parameter);
            Object value = type.getValue(parameter);
            paramList.add(value);
        }
        return paramList.toArray();
    }
}
