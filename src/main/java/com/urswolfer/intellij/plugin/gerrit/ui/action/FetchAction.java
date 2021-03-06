/*
 * Copyright 2013 Urs Wolfer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.urswolfer.intellij.plugin.gerrit.ui.action;

import com.google.common.base.Optional;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.urswolfer.intellij.plugin.gerrit.git.GerritGitUtil;
import com.urswolfer.intellij.plugin.gerrit.rest.GerritUtil;
import com.urswolfer.intellij.plugin.gerrit.rest.bean.ChangeInfo;
import git4idea.repo.GitRepository;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Callable;

/**
 * @author Urs Wolfer
 */
public class FetchAction extends AbstractChangeAction {

    @Nullable
    private final Callable<Void> mySuccessCallable;

    public FetchAction() {
        this(null);
    }

    public FetchAction(@Nullable Callable<Void> successCallable) {
        super("Fetch", "Fetch change", AllIcons.Actions.Download);
        this.mySuccessCallable = successCallable;
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Optional<ChangeInfo> selectedChange = getSelectedChange(anActionEvent);
        if (!selectedChange.isPresent()) {
            return;
        }
        final Project project = anActionEvent.getData(PlatformDataKeys.PROJECT);

        final Optional<ChangeInfo> changeDetails = getChangeDetail(selectedChange.get(), project);
        if (!changeDetails.isPresent()) return;

        String ref = GerritUtil.getRef(changeDetails.get());

        Optional<GitRepository> gitRepository = GerritGitUtil.getRepositoryForGerritProject(project, changeDetails.get().getProject());
        if (!gitRepository.isPresent()) return;

        GerritGitUtil.fetchChange(project, gitRepository.get(), ref, mySuccessCallable);
    }
}
