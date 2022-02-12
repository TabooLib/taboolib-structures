package taboolib.structure

object TabooLib {

    val main = Group {
        add("common-adapter") {
            platform()
            dependency("common-core")
        }
        add("common-command") {
            platform()
            dependency("common-adapter")
        }
        add("common-command-annotation") {
            dependency("common-command")
        }
        add("common-core") {
            platform()
        }
        add("common-core-impl") {
            dependency("common-core")
        }
        add("common-environment") {
            dependency("common-core")
        }
        add("common-event") {
            platform()
            dependency("common-core")
        }
        add("common-listener") {
            platform()
            dependency("common-core")
        }
        add("common-openapi") {
            platform()
            dependency("common-core")
        }
        add("common-plugin") {
            platform()
            dependency("common-core")
        }
        add("common-scheduler") {
            platform()
            dependency("common-core")
        }
        add("common-util") {
            dependency("common-environment")
            dependency("common-plugin").optional()
            dependency("common-adapter").optional()
            library("com.google.guava:guava:21.0")
            library("com.google.code.gson:gson:2.8.9")
            library("org.apache.commons:commons-lang3:3.5")
        }
        add("module-chat") {
            dependency("common-environment")
            dependency("common-adapter")
            library("net.md-5:bungeecord-chat:1.17")
        }
        add("module-configuration-annotation") {
            dependency("common-plugin")
            dependency("common-util")
            dependency("module-configuration-core")
        }
        add("module-configuration-core") {
            dependency("common-environment")
            dependency("common-util")
            dependency("common-adapter").optional()
            dependency("module-chat").optional()
            library("org.yaml:snakeyaml:1.28")
            library("com.typesafe:config:1.4.1")
            library("com.electronwill.night-config:core:3.6.5")
            library("com.electronwill.night-config:toml:3.6.5")
            library("com.electronwill.night-config:json:3.6.5")
            library("com.electronwill.night-config:hocon:3.6.5")
        }
        add("module-configuration-data") {
            dependency("common-plugin")
            dependency("common-scheduler")
            dependency("module-configuration-core")
        }
    }
}

class Group(init: Group.() -> Unit) {

    val modules = ArrayList<Module>()

    init {
        init()
    }

    fun add(name: String, init: Module.() -> Unit = {}) {
        modules.add(Module(name, this).also(init))
    }

    fun get(name: String): Module? {
        return modules.find { it.name == name }
    }

    class Module(val name: String, val group: Group) {

        val dependencies = ArrayList<Dependency>()
        var platform = false

        fun platform(): Module {
            platform = true
            return this
        }

        fun library(name: String): Dependency {
            return Dependency(name).library().apply { dependencies.add(this) }
        }

        fun dependency(name: String): Dependency {
            return Dependency(name).apply { dependencies.add(this) }
        }

        fun collect(): Set<Dependency> {
            val result = HashSet<Dependency>()
            result.addAll(dependencies)
            dependencies.forEach { group.get(it.name)?.collect()?.apply { result.addAll(this) } }
            return result
        }
    }
}

class Dependency(val name: String) {

    var isLibrary = false
    var isOptional = false

    fun library(): Dependency {
        isLibrary = true
        return this
    }

    fun optional(): Dependency {
        isOptional = true
        return this
    }
}