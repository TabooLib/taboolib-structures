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
            dependency("common-plugin") // optional
            dependency("common-adapter") // optional
            dependency("common-environment")
            dependency("com.google.guava:guava:21.0").library()
            dependency("com.google.code.gson:gson:2.8.9").library()
            dependency("org.apache.commons:commons-lang3:3.5").library()
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

    fun library(): Dependency {
        isLibrary = true
        return this
    }
}