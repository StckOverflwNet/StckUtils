package de.stckoverflw.stckutils.config

import net.axay.kspigot.main.KSpigotMainInstance
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.IOException

abstract class AbstractConfig(name: String) {

    private val file: File
    private val dir: File = KSpigotMainInstance.dataFolder
    val yaml: YamlConfiguration

    init {
        if(!dir.exists()) {
            dir.mkdirs()
        }
        file = File(dir, name)
        if(!file.exists()) {
            KSpigotMainInstance.saveResource(name, false)
        }
        yaml = YamlConfiguration()
        try {
            yaml.load(file)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun save() {
        try {
            yaml.save(file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}