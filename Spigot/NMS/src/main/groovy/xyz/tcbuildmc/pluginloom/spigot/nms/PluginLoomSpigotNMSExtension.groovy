package xyz.tcbuildmc.pluginloom.spigot.nms

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.bundling.AbstractArchiveTask
import xyz.tcbuildmc.pluginloom.common.annotation.Todo
class PluginLoomSpigotNMSExtension {
    private final Project project

    PluginLoomSpigotNMSExtension(Project project) {
        this.project = project
    }

    // Generate
    @Todo
    boolean mirror = false

    // Remap
    AbstractArchiveTask inputJarTask = project.tasks.named(JavaPlugin.JAR_TASK_NAME, AbstractArchiveTask).get()

    String obfJarClassifier = "remapped-obf"

    String spigotMappingsJarClassifier = "remapped-spigot"

    String spigotApiVersion = ""
}
