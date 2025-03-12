package io.enderdev.alchemistry.compat.groovyscript.parser

import com.cleanroommc.groovyscript.api.infocommand.InfoParserPackage
import com.cleanroommc.groovyscript.compat.vanilla.command.infoparser.GenericInfoParser
import com.cleanroommc.groovyscript.helper.ingredient.GroovyScriptCodeConverter
import io.enderdev.alchemistry.chemistry.ChemicalCompound
import io.enderdev.alchemistry.chemistry.CompoundRegistry
import net.minecraft.item.ItemStack

class Compound: GenericInfoParser<ChemicalCompound>() {
    companion object {
        val instance = Compound()
    }

    override fun name(): String? {
        return "Compound"
    }

    override fun text(
        entry: ChemicalCompound,
        colored: Boolean,
        prettyNbt: Boolean
    ): String? {
        return asGroovyCode(entry, colored)
    }

    override fun parse(info: InfoParserPackage) {
        if (info.stack.isEmpty) return
        CompoundRegistry.compounds().forEach { c ->
            if (ItemStack.areItemStacksEqual(c.toItemStack(1), info.stack)) {
                instance.add(info.messages, c, info.isPrettyNbt)
            }
        }
    }

    override fun id(): String? {
        return "compound"
    }

    fun asGroovyCode(compound: ChemicalCompound, colored: Boolean): String {
        return GroovyScriptCodeConverter.formatGenericHandler("compound", compound.name, colored)
    }
}