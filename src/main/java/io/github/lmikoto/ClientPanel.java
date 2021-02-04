package io.github.lmikoto;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import lombok.Getter;
import org.apache.commons.collections.CollectionUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author liuyang
 * 2021/2/4 5:46 下午
 */
public class ClientPanel extends JPanel {

    private JPanel mainPanel;

    private JButton runBtn;

    @Getter
    private JTextField interfaceName;

    private JPanel reqPane;

    private JPanel respPane;

    private JButton saveBtn;

    private JButton delBtn;

    private JComboBox<String> addressBox;

    @Getter
    private JTextField methodName;

    private JTextField version;

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
        add(mainPanel,BorderLayout.CENTER,0);
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

        delBtn.addActionListener((e) -> {
            String selectedItem = (String)this.addressBox.getSelectedItem();
            Setting.getInstance().getAddress().remove(selectedItem);
            this.addressBox.removeItem(selectedItem);
        });

        runBtn.addActionListener((e) -> {

            refreshEntity();

            // 清空返回
            writeDocument(project, jsonEditorResp.getDocument(), "");

            ExecutorService executorService = Executors.newSingleThreadExecutor();
        });
    }

    private void refreshEntity() {
        JsonEditor jsonEditorReq = this.getJsonEditorReq();
        entity.setMethodName(methodName.getText());
        entity.setInterfaceName(interfaceName.getText());
        entity.setAddress((String)addressBox.getSelectedItem());
        entity.setVersion(version.getText());
        if (jsonEditorReq.getDocumentText() != null && jsonEditorReq.getDocumentText().length() > 0) {
            Map<String,Object> map = JsonUtils.fromJson(jsonEditorReq.getDocumentText(),Map.class);
            List<String> methodTypeList = (List<String>)map.get(Const.METHOD_TYPE);
            if (CollectionUtils.isNotEmpty(methodTypeList)) {
                entity.setMethodType((String[]) methodTypeList.toArray());
            } else {
                entity.setMethodType(new String[0]);
            }

//            JSONArray paramArray = map.getJSONArray("param");
//            if (paramArray != null) {
//                entity.setParam(paramArray.toArray());
//            } else {
//                entity.setParam(new Object[0]);
//            }
        } else {
            entity.setParam(new Object[0]);
            entity.setMethodType(new String[0]);
        }
    }

    public static void refreshUI(ClientPanel client, DubboEntity dubboEntity) {
        JTextField textField1 = client.getInterfaceName();
        JTextField textField2 = client.getMethodName();
        JsonEditor jsonEditorReq = client.getJsonEditorReq();
        textField1.setText(dubboEntity.getInterfaceName());
        textField2.setText(dubboEntity.getMethodName());
        Map<String, Object> map = new HashMap();
        map.put(Const.PARAM, dubboEntity.getParam());
        map.put(Const.METHOD_TYPE, dubboEntity.getMethodType());
        writeDocument(client.getProject(), jsonEditorReq.getDocument(), JsonUtils.toPrettyJson(map));
        client.updateUI();
    }

    private static void writeDocument(Project project, Document document, String text) {
        WriteCommandAction.runWriteCommandAction(project, () -> document.setText(text));
    }
}
