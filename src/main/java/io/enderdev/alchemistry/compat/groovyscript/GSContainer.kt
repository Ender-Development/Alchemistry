package io.enderdev.alchemistry.compat.groovyscript

import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer

class GSContainer : GroovyPropertyContainer() {
    val atomizer: Atomizer = Atomizer()

    init {
        addProperty(atomizer)
    }
}