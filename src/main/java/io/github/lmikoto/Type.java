package io.github.lmikoto;

import com.intellij.psi.*;
import com.intellij.psi.impl.PsiClassImplUtil;
import com.intellij.psi.search.ProjectAndLibrariesScope;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author liuyang
 * 2021/2/4 1:41 下午
 */
public enum Type {

    /**
     * simple type
     */
    SIMPLE,
    /**
     * map type
     */
    MAP{
        @Override
        public Object getDefaultValue(PsiVariable var) {
            return new HashMap<>();
        }
    },

    /**
     * collection type
     */
    COLLECTION{
        @Override
        public Object getDefaultValue(PsiVariable var) {
            String canonicalText = var.getType().getCanonicalText();
            if (canonicalText.indexOf(Const.GENERIC_PREFIX) > 0) {
                canonicalText = canonicalText.substring(canonicalText.indexOf(Const.GENERIC_PREFIX) + 1, canonicalText.length() - 1);
            }
            PsiClass psiClass = JavaPsiFacade.getInstance(var.getProject()).findClass(canonicalText, new ProjectAndLibrariesScope(var.getProject()));

            if (psiClass == null) {
                return new ArrayList<>();
            }
            Type type = fromParam(psiClass.getQualifiedName());
            if(type == SIMPLE){
                return new ArrayList<>();
            }
            ArrayList<Object> arrayList = new ArrayList<>();
            arrayList.add(convert2Json(psiClass));
            return arrayList;
        }
    },
    /**
     * complex type
     */
    COMPLEX {
        @Override
        public Object getDefaultValue(PsiVariable var) {
            PsiClass psiClass = JavaPsiFacade.getInstance(var.getProject()).findClass(var.getType().getCanonicalText(), new ProjectAndLibrariesScope(var.getProject()));
            return convert2Json(psiClass);
        }
    };

    private static Object convert2Json(PsiClass psiClass) {
        PsiField[] allField = PsiClassImplUtil.getAllFields(psiClass);
        Map<String, Object> result = new LinkedHashMap<>();
        for(PsiField psiField: allField){
            // 忽略 static 和 final
            PsiModifierList modifierList = psiField.getModifierList();
            boolean isStaticOrFinal = modifierList != null && (modifierList.hasModifierProperty(PsiModifier.STATIC)
                    || modifierList.hasModifierProperty(PsiModifier.FINAL));
            if(isStaticOrFinal){
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

    public Object getValue(PsiVariable var){
        return getDefaultValue(var);
    }

    public Object getDefaultValue(PsiVariable var){
        return null;
    }

    public static Type fromParam(String type){
        if(isBaseType(type)){
            return SIMPLE;
        }
        // Date
        if(type.equals(Date.class.getCanonicalName())){
            return SIMPLE;
        }

        if (type.startsWith(Map.class.getCanonicalName())) {
            return MAP;
        }

        if(isCollection(type)){
            return COLLECTION;
        }

        return COMPLEX;
    }

    public static Type fromParam(PsiVariable param){
        return fromParam(param.getType().getCanonicalText());
    }

    private static boolean isCollection(String type) {
        return type.startsWith(List.class.getCanonicalName())
                || type.startsWith(Set.class.getCanonicalName())
                || type.startsWith(HashSet.class.getCanonicalName())
                || type.startsWith(ArrayList.class.getCanonicalName());
    }

    private static boolean isBaseType(String type){
        return String.class.getCanonicalName().equals(type) ||
                Long.class.getCanonicalName().equals(type) ||
                Integer.class.getCanonicalName().equals(type) ||
                Float.class.getCanonicalName().equals(type) ||
                Byte.class.getCanonicalName().equals(type) ||
                BigDecimal.class.getCanonicalName().equals(type) ||
                Double.class.getCanonicalName().equals(type) ||
                "int".equals(type) ||
                "long".equals(type) ||
                "float".equals(type) ||
                "double".equals(type) ||
                "byte".equals(type);

    }
}
