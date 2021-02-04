package io.github.lmikoto;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liuyang
 * 2021/2/4 5:46 下午
 */
public class ClientPanel extends JPanel {

    private JPanel mainPanel;

    private JButton button1;

    @Getter
    private JTextField interfaceNameTextField;

    private JPanel reqPane;

    private JPanel respPane;

    private JButton saveBtn;

    private JButton delBtn;

    private JComboBox<String> addressBox;

    @Getter
    private JTextField methodNameTextField;

    private JTextField versionTextField;

    @Getter
    private JsonEditor jsonEditorReq;

    private JsonEditor jsonEditorResp;

    @Getter
    private Project project;

    private DubboEntity entity;


    public ClientPanel(Project project, ToolWindow toolWindow){
        initUI(project);
        initListener();

    }

    private void initUI(Project project) {
        entity = new DubboEntity();
        this.project = project;
        setLayout(new BorderLayout());
        add(mainPanel,"Center",0);
        jsonEditorReq = new JsonEditor(project);
        jsonEditorResp = new JsonEditor(project);

        reqPane.add(jsonEditorReq,BorderLayout.CENTER,0);
        respPane.add(jsonEditorResp,BorderLayout.CENTER,0);

        Setting setting = Setting.getInstance();
        for (String address: setting.getAddress()){
            addressBox.addItem(address);
        }
    }

    private void initListener() {
        saveBtn.addActionListener((e) -> {
            String selectedItem = (String)addressBox.getSelectedItem();
            Setting.getInstance().getAddress().add(selectedItem);
            addressBox.addItem(selectedItem);
        });
    }

    public static void refreshUI(ClientPanel client, DubboEntity dubboEntity) {
        JTextField textField1 = client.getInterfaceNameTextField();
        JTextField textField2 = client.getMethodNameTextField();
        JsonEditor jsonEditorReq = client.getJsonEditorReq();
        textField1.setText(dubboEntity.getInterfaceName());
        textField2.setText(dubboEntity.getMethodName());
        Map<String, Object> map = new HashMap();
        map.put("param", dubboEntity.getParamObj());
        map.put("methodType", dubboEntity.getMethodType());
        writeDocument(client.getProject(), jsonEditorReq.getDocument(), JsonUtils.toPrettyJson(map));
        client.updateUI();
    }

    private static void writeDocument(Project project, Document document, String text) {
        WriteCommandAction.runWriteCommandAction(project, () -> document.setText(text));
    }
}
