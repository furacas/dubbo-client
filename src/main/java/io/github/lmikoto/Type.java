package io.github.lmikoto;

import com.intellij.psi.*;
import com.intellij.psi.impl.PsiClassImplUtil;
import com.intellij.psi.search.ProjectAndLibrariesScope;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author liuyang
 * 2021/2/4 1:41 下午
 */
public enum Type {

    SIMPLE,

    MAP{
        @Override
        public Object getDefaultValue(PsiVariable var) {
            return new HashMap<>();
        }
    },

    COMPLEX {
        @Override
        public Object getDefaultValue(PsiVariable var) {
            PsiClass psiClass = JavaPsiFacade.getInstance(var.getProject()).findClass(var.getType().getCanonicalText(), new ProjectAndLibrariesScope(var.getProject()));
            return convert2Json(psiClass);
        }

        private Object convert2Json(PsiClass psiClass) {
            PsiField[] allField = PsiClassImplUtil.getAllFields(psiClass);
            Map result = new LinkedHashMap();
            for(PsiField psiField: allField){
                // 忽略 static 和 final
                if(psiField.getModifierList().hasModifierProperty(Const.STATIC) || psiField.getModifierList().hasModifierProperty(Const.FINAL)){
                    continue;
                }

                Type type = Type.fromParam(psiField);
                if(type == COMPLEX){
                    PsiClass subPsiClass = JavaPsiFacade.getInstance(psiClass.getProject()).findClass(psiField.getType().getCanonicalText(), new ProjectAndLibrariesScope(psiClass.getProject()));
                    result.put(psiField.getName(),convert2Json(subPsiClass));
                }else{
                    result.put(psiField.getName(),type.getValue(psiField));
                }
            }
            result.put(Const.CLASS,psiClass.getQualifiedName());
            return result;
        }
    };

    public Object getValue(PsiVariable var){
        return getDefaultValue(var);
    }

    public Object getDefaultValue(PsiVariable var){
        return null;
    }

    public static Type fromParam(PsiVariable param){
        PsiType type = param.getType();
        // todo 其他基本类型的判断
        if(PsiType.BOOLEAN.isAssignableFrom(type) ||
                PsiType.INT.isAssignableFrom(type) ||
                PsiType.LONG.isAssignableFrom(type)
        ){
            return SIMPLE;
        }
        // String
        if(type.equalsToText(String.class.getCanonicalName())){
            return SIMPLE;
        }
        // Date
        if(type.equalsToText(Date.class.getCanonicalName())){
            return SIMPLE;
        }

        if (type.getCanonicalText().startsWith(Map.class.getCanonicalName())) {
            return MAP;
        }

        return COMPLEX;
    }
}
