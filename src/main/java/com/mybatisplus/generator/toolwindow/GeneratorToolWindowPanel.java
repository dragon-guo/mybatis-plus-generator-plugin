package com.mybatisplus.generator.toolwindow;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import com.mybatisplus.generator.ui.GeneratorDialog;

import javax.swing.*;
import java.awt.*;

public class GeneratorToolWindowPanel extends JPanel {

    private final Project project;
    private final ToolWindow toolWindow;

    public GeneratorToolWindowPanel(Project project, ToolWindow toolWindow) {
        this.project = project;
        this.toolWindow = toolWindow;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBorder(JBUI.Borders.empty(10));

        JBLabel titleLabel = new JBLabel("MyBatis Plus Generator");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 14f));
        titleLabel.setBorder(JBUI.Borders.emptyBottom(10));

        JTextArea descArea = new JTextArea();
        descArea.setText("Generate MyBatis Plus code:\n" +
                "- Entity\n" +
                "- Mapper\n" +
                "- Service\n" +
                "- Controller\n" +
                "- XML");
        descArea.setEditable(false);
        descArea.setOpaque(false);
        descArea.setBorder(JBUI.Borders.emptyBottom(15));

        JButton generateButton = new JButton("Open Generator");
        generateButton.addActionListener(e -> openGeneratorDialog());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(descArea, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(generateButton);

        add(topPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
    }

    private void openGeneratorDialog() {
        String basePath = project.getBasePath();
        if (basePath == null) {
            basePath = "";
        }
        GeneratorDialog dialog = new GeneratorDialog(project, basePath);
        dialog.show();
    }
}
