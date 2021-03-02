package io.github.lmikoto.ui;

import com.google.common.collect.Maps;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import io.github.lmikoto.*;
import io.github.lmikoto.utils.DubboUtils;
import io.github.lmikoto.utils.JsonUtils;
import io.github.lmikoto.utils.SocketPoxyUtils;
import io.github.lmikoto.utils.TelnetUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.github.lmikoto.utils.JsonUtils.TypeReference;

/**
 * @author liuyang
 * 2021/2/4 5:46 下午
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class ClientPanel extends JPanel {

    private JPanel mainPanel;

    private JButton runBtn;

    private JTextField interfaceName;

    private JPanel reqPane;

    private JPanel respPane;

    private JButton saveBtn;

    private JButton delBtn;

    private JComboBox<String> addressBox;

    private JTextField methodName;

    private JTextField version;

    private JLabel tips;

    private JTextField timeout;

    private JCheckBox useProxy;

    private JButton copyTelnet;

    private JsonEditor jsonEditorReq;

    private JsonEditor jsonEditorResp;

    private Project project;

    private DubboEntity entity;


    public ClientPanel(Project project){
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
        version.setText(Const.DEFAULT_VERSION);

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

            saveCache();

            // 清空返回
            writeDocument(project, jsonEditorResp.getDocument(), "");

            // 开一个线程去跑防止ui卡死
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.submit(() -> {
                try {
                    tips.setText("正在请求...");
                    tips.updateUI();
                    long start = System.currentTimeMillis();
                    Object result = DubboUtils.invoke(entity);
                    writeDocument(project, this.jsonEditorResp.getDocument(), JsonUtils.toPrettyJson(result));
                    long end = System.currentTimeMillis();
                    tips.setText("耗时:" + (end - start) + "ms");
                    tips.updateUI();
                    return result;
                } catch (Exception ex) {
                    return ex.getMessage();
                }
            });
        });

        useProxy.addActionListener(e->{
            JCheckBox checkBox = (JCheckBox)e.getSource();
            if(checkBox.isSelected()){
                new ProxyDialog();
            }else{
                SocketPoxyUtils.cancelProxy();
            }
        });

        copyTelnet.addActionListener(e->{
            refreshEntity();
            saveCache();
            TelnetUtils.copy(entity);

            tips.setText("复制成功");
        });
    }

    private void saveCache() {
        Setting instance = Setting.getInstance();
        instance.addCache(entity);
    }

    @SuppressWarnings("unchecked")
    private void refreshEntity() {
        JsonEditor jsonEditorReq = this.getJsonEditorReq();
        entity.setMethodName(methodName.getText());
        entity.setInterfaceName(interfaceName.getText());
        entity.setAddress((String)addressBox.getSelectedItem());
        entity.setVersion(version.getText());
        entity.setTimeout(StringUtils.isBlank(timeout.getText()) ? null : Integer.valueOf(timeout.getText()));
        if (jsonEditorReq.getDocumentText() != null && jsonEditorReq.getDocumentText().length() > 0) {
            Map<String, Object> map = JsonUtils.fromJson(jsonEditorReq.getDocumentText(),
                new TypeReference<Map<String, Object>>() {});
            List<String> methodTypeList = (List<String>)map.get(Const.METHOD_TYPE);
            if (CollectionUtils.isNotEmpty(methodTypeList)) {
                entity.setMethodType(methodTypeList.toArray(new String[0]));
            } else {
                entity.setMethodType(new String[0]);
            }

            List<Object> paramList = (List<Object>) map.get(Const.PARAM);
            if (CollectionUtils.isNotEmpty(paramList)) {
                entity.setParam(paramList.toArray());
            } else {
                entity.setParam(new Object[0]);
            }
        } else {
            entity.setParam(new Object[0]);
            entity.setMethodType(new String[0]);
        }
    }

    public static void refreshUserInterface(ClientPanel client, DubboEntity entity) {
        entity2UI(client,entity);
        JsonEditor jsonEditorReq = client.getJsonEditorReq();
        Map<String, Object> map = Maps.newHashMapWithExpectedSize(2);
        map.put(Const.PARAM, entity.getParam());
        map.put(Const.METHOD_TYPE, entity.getMethodType());
        writeDocument(client.getProject(), jsonEditorReq.getDocument(), JsonUtils.toPrettyJson(map));
        client.updateUI();
    }

    private static void entity2UI(ClientPanel client, DubboEntity entity) {
        client.getInterfaceName().setText(entity.getInterfaceName());
        client.getMethodName().setText(entity.getMethodName());
        client.getVersion().setText(entity.getVersion());
        client.getTimeout().setText(entity.getTimeout().toString());
        client.getAddressBox().setSelectedItem(entity.getAddress());
    }


    private static void writeDocument(Project project, Document document, String text) {
        WriteCommandAction.runWriteCommandAction(project, () -> document.setText(text.replace("\r\n","\n")));
    }

}
