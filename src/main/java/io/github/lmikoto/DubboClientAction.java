package io.github.lmikoto;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiElement;
import org.jf.dexlib2.immutable.util.ParamUtil;

import javax.swing.*;

public class DubboClientAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiElement psiElement = (PsiElement)e.getData(CommonDataKeys.PSI_ELEMENT);
        if (!(psiElement instanceof PsiMethod)) {
            Messages.showMessageDialog("only apply on method", "warn", (Icon)null);
        } else {
            PsiMethod psiMethod = (PsiMethod)psiElement;
            PsiParameterList parameterList = psiMethod.getParameterList();
            PsiJavaFile javaFile = (PsiJavaFile)psiMethod.getContainingFile();
            PsiClass psiClass = (PsiClass)psiElement.getParent();
            String interfaceName = String.format("%s.%s", javaFile.getPackageName(), psiClass.getName());
            String[] methodType = new String[parameterList.getParameters().length];

            String methodName;
            for(int i = 0; i < parameterList.getParameters().length; ++i) {
                methodName = parameterList.getParameters()[i].getType().getCanonicalText();
                methodType[i] = methodName;
            }

            Object[] initParamArray = ParamUtil.getInitParamArray(psiMethod.getParameterList(), psiMethod.getDocComment());
            methodName = psiMethod.getName();
            ToolWindow toolWindow = ToolWindowManager.getInstance(e.getProject()).getToolWindow("DubboTest");
            if (toolWindow != null) {
                toolWindow.show(() -> {
                });
                DubboPanel dubboPanel1 = (DubboPanel)toolWindow.getComponent().getComponent(0);
                DubboMethodEntity dubboMethodEntity = new DubboMethodEntity();
                dubboMethodEntity.setInterfaceName(interfaceName);
                dubboMethodEntity.setParamObj(initParamArray);
                dubboMethodEntity.setMethodType(methodType);
                dubboMethodEntity.setMethodName(methodName);
                DubboPanel.refreshUI(dubboPanel1, dubboMethodEntity);
            }

        }
    }
}
