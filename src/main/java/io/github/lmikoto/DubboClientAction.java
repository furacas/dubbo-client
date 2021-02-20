package io.github.lmikoto;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.*;

import java.util.Optional;
import java.util.Objects;

/**
 * @author liuyang
 */
public class DubboClientAction extends AnAction {

    @SuppressWarnings("DialogTitleCapitalization")
    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);
        if (!(psiElement instanceof PsiMethod)) {
            Messages.showMessageDialog("只适用于dubbo方法", "warn", null);
        } else {
            PsiMethod psiMethod = (PsiMethod)psiElement;
            PsiParameterList parameterList = psiMethod.getParameterList();
            PsiJavaFile javaFile = (PsiJavaFile)psiMethod.getContainingFile();
            PsiClass psiClass = (PsiClass)psiElement.getParent();
            String interfaceName = String.format("%s.%s", javaFile.getPackageName(), psiClass.getName());
            String[] methodType = new String[parameterList.getParameters().length];

            for(int i = 0; i < parameterList.getParameters().length; ++i) {
                methodType[i] = parameterList.getParameters()[i].getType().getCanonicalText();
            }

            String methodName = psiMethod.getName();
            Optional.ofNullable(e.getProject())
                .map(ToolWindowManager::getInstance)
                .map(it -> it.getToolWindow(Const.NAME))
                .ifPresent(toolWindow -> {
                    toolWindow.show(() -> {
                    });
                    ClientPanel client = (ClientPanel)toolWindow.getComponent().getComponent(0);

                    Setting instance = Setting.getInstance();

                    DubboEntity entity = instance.getCache(interfaceName, methodName, methodType);
                    if (Objects.isNull(entity)) {
                        Object[] initParamArray = ParamUtils.getInitParamArray(psiMethod.getParameterList());
                        entity = new DubboEntity();
                        entity.setInterfaceName(interfaceName);
                        entity.setParam(initParamArray);
                        entity.setMethodType(methodType);
                        entity.setMethodName(methodName);
                        entity.setVersion(Const.DEFAULT_VERSION);
                        entity.setTimeout(10000);
                        entity.setAddress(Const.DEFAULT_DUBBO_ADDRESS);
                    }

                    ClientPanel.refreshUserInterface(client, entity);
                });
        }
    }
}
