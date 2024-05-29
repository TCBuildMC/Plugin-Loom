package xyz.tcbuildmc.pluginloom.paper.nms

import org.gradle.api.Plugin
import org.gradle.api.Project
import xyz.tcbuildmc.pluginloom.paper.PluginLoomPaper
import xyz.tcbuildmc.pluginloom.paper.PluginLoomPaperExtension

class PluginLoomPaperNMS implements Plugin<Project> {
    @Override
    void apply(Project project) {
        if (!project.plugins.hasPlugin(PluginLoomPaper)) {
            throw new IllegalArgumentException()
        }

        def loomCache = "${project.projectDir.canonicalPath}/.gradle/pluginloom"

        def baseExt = project.extensions.getByType(PluginLoomPaperExtension)
        def ext = project.extensions.create("pluginloomNMS", PluginLoomPaperNMSExtension, project)

        project.afterEvaluate {
            prepareNMS(project, baseExt, loomCache)
        }
    }

    void prepareNMS(final Project project, final PluginLoomPaperExtension ext, final String loomCache) {

    }
}
