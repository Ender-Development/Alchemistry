package io.enderdev.alchemistry.compat.groovyscript

import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer
import io.enderdev.alchemistry.compat.groovyscript.register.Atomizer
import io.enderdev.alchemistry.compat.groovyscript.register.Electrolyzer
import io.enderdev.alchemistry.compat.groovyscript.register.Evaporator
import io.enderdev.alchemistry.compat.groovyscript.register.Liquifier

class GSContainer : GroovyPropertyContainer() {
    val atomizer: Atomizer = Atomizer()
    val electrolyzer: Electrolyzer = Electrolyzer()
    val evaporator: Evaporator = Evaporator()
    val liquifier: Liquifier = Liquifier()

    init {
        addProperty(atomizer)
        addProperty(electrolyzer)
        addProperty(evaporator)
        addProperty(liquifier)
    }
}