package io.github.lmikoto.ui;

import io.github.lmikoto.Setting;
import io.github.lmikoto.utils.SocketPoxyUtils;

import javax.swing.*;
import java.awt.event.*;

/**
 * @author liuyang
 */
public class ProxyDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField ip;
    private JTextField port;

    public ProxyDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        Setting setting = Setting.getInstance();
        ip.setText(setting.getProxyIp());
        port.setText(setting.getProxyPort());

        setTitle("设置socket代理");
        setSize(280,150);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void onOK() {
        Setting setting = Setting.getInstance();
        setting.setProxyIp(ip.getText());
        setting.setProxyPort(port.getText());
        SocketPoxyUtils.setProxy(ip.getText(),port.getText());
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    public static void main(String[] args) {
        ProxyDialog dialog = new ProxyDialog();
        dialog.pack();
        System.exit(0);
    }
}
