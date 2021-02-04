package io.github.lmikoto;

import com.intellij.psi.PsiType;
import com.intellij.psi.PsiVariable;

import java.util.Map;

/**
 * @author liuyang
 * 2021/2/4 1:41 下午
 */
public enum Type {

    BOOLEAN{
        @Override
        public Object getValue(PsiVariable var) {
            return null;
        }

        @Override
        public Object getDefaultValue(PsiVariable var) {
            return null;
        }
    };

    public abstract Object getValue(PsiVariable var);

    public abstract Object getDefaultValue(PsiVariable var);

    public static Type fromParam(PsiVariable param){
        PsiType type = param.getType();
        if(PsiType.BOOLEAN.isAssignableFrom(type)){
            return BOOLEAN;
        }
        return null;
    }
}
