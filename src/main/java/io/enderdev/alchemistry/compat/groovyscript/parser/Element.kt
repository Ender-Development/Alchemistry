package io.enderdev.alchemistry.compat.groovyscript.parser

import com.cleanroommc.groovyscript.api.infocommand.InfoParserPackage
import com.cleanroommc.groovyscript.compat.vanilla.command.infoparser.GenericInfoParser
import com.cleanroommc.groovyscript.helper.ingredient.GroovyScriptCodeConverter
import io.enderdev.alchemistry.chemistry.ChemicalElement
import io.enderdev.alchemistry.chemistry.ElementRegistry
import net.minecraft.item.ItemStack

class Element: GenericInfoParser<ChemicalElement>() {
    companion object {
        val instance = Element()
    }

    override fun name(): String? {
        return "Element"
    }

    override fun text(
        entry: ChemicalElement,
        colored: Boolean,
        prettyNbt: Boolean
    ): String? {
        return asGroovyCode(entry, colored)
    }

    override fun parse(info: InfoParserPackage) {
        if (info.stack.isEmpty) return
        ElementRegistry.getAllElements().forEach { e ->
            if (ItemStack.areItemStacksEqual(e.toItemStack(1), info.stack)) {
                instance.add(info.messages, e, info.isPrettyNbt)
            }
        }
    }

    override fun id(): String? {
        return "element"
    }

    fun asGroovyCode(element: ChemicalElement, colored: Boolean): String {
        return GroovyScriptCodeConverter.formatGenericHandler("element", element.name, colored)
    }
}