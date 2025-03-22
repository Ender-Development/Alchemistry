package io.enderdev.alchemistry.compat.groovyscript

import com.cleanroommc.groovyscript.api.Result
import com.cleanroommc.groovyscript.api.infocommand.InfoParserRegistry
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer
import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer
import io.enderdev.alchemistry.chemistry.ChemicalCompound
import io.enderdev.alchemistry.chemistry.ChemicalElement
import io.enderdev.alchemistry.chemistry.CompoundRegistry
import io.enderdev.alchemistry.chemistry.ElementRegistry
import io.enderdev.alchemistry.compat.groovyscript.content.Content
import io.enderdev.alchemistry.compat.groovyscript.parser.Compound
import io.enderdev.alchemistry.compat.groovyscript.parser.Element
import io.enderdev.alchemistry.compat.groovyscript.register.Atomizer
import io.enderdev.alchemistry.compat.groovyscript.register.Combiner
import io.enderdev.alchemistry.compat.groovyscript.register.Dissolver
import io.enderdev.alchemistry.compat.groovyscript.register.Electrolyzer
import io.enderdev.alchemistry.compat.groovyscript.register.Evaporator
import io.enderdev.alchemistry.compat.groovyscript.register.Liquifier
import net.minecraft.item.ItemStack

class GSContainer : GroovyPropertyContainer() {
    val atomizer: Atomizer = Atomizer()
    val combiner: Combiner = Combiner()
    val dissolver: Dissolver = Dissolver()
    val electrolyzer: Electrolyzer = Electrolyzer()
    val evaporator: Evaporator = Evaporator()
    val liquifier: Liquifier = Liquifier()
    val content: Content = Content()

    init {
        addProperty(atomizer)
        addProperty(combiner)
        addProperty(dissolver)
        addProperty(electrolyzer)
        addProperty(evaporator)
        addProperty(liquifier)
        addProperty(content)
    }

    override fun initialize(owner: GroovyContainer<*>) {
        owner.objectMapperBuilder("element", ItemStack::class.java)
            .parser { s, args ->
                val parsedName = s.trim().lowercase().replace(" ", "_")
                val compound: ChemicalCompound? = CompoundRegistry[parsedName]
                if (compound == null || compound.toItemStack(1).isEmpty) {
                    val element: ChemicalElement? = ElementRegistry[parsedName]
                    if (element == null || element.toItemStack(1).isEmpty) {
                        return@parser Result.error()
                    }
                    return@parser Result.some(element.toItemStack(1))
                }
                return@parser Result.some(compound.toItemStack(1))
            }
            .defaultValue { ItemStack.EMPTY }
            .completerOfNamed(CompoundRegistry::compounds, ChemicalCompound::name)
            .completerOfNamed(ElementRegistry::getAllElements, ChemicalElement::name)
            .docOfType("chemical element or compound as item stack")
            .register()

        InfoParserRegistry.addInfoParser(Compound.instance)
        InfoParserRegistry.addInfoParser(Element.instance)
    }
}