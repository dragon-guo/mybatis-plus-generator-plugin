package com.mybatisplus.generator.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.mybatisplus.generator.ui.GeneratorDialog;
import org.jetbrains.annotations.NotNull;

/**
 * MyBatis Plus 代码生成器入口 Action
 *
 * @author generator
 */
public class GenerateCodeAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }

        // 获取当前选中的目录路径
        String basePath = project.getBasePath();
        if (e.getData(CommonDataKeys.VIRTUAL_FILE) != null) {
            basePath = e.getData(CommonDataKeys.VIRTUAL_FILE).getPath();
        }

        // 打开代码生成器对话框
        GeneratorDialog dialog = new GeneratorDialog(project, basePath);
        dialog.show();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        // 只有在项目打开时才启用此 Action
        Project project = e.getProject();
        e.getPresentation().setEnabledAndVisible(project != null);
    }
}
