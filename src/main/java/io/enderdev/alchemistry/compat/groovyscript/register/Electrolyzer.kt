package io.enderdev.alchemistry.compat.groovyscript.register

import com.cleanroommc.groovyscript.api.GroovyBlacklist
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry
import io.enderdev.alchemistry.recipes.ElectrolyzerRecipe
import io.enderdev.alchemistry.recipes.register.ElectrolyzerRegister

class Electrolyzer: VirtualizedRegistry<ElectrolyzerRecipe>() {
    @GroovyBlacklist
    override fun onReload() {
        ElectrolyzerRegister.INSTANCE.recipes.removeAll(removeScripted())
        ElectrolyzerRegister.INSTANCE.recipes.addAll(restoreFromBackup())
    }


}