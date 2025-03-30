package io.enderdev.alchemistry

import io.enderdev.alchemistry.blocks.ModBlocks
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import java.io.File
import java.text.DecimalFormat

object Reference {

    const val MODID = Tags.MOD_ID
    const val MODNAME = Tags.MOD_NAME
    const val VERSION = Tags.VERSION
    const val DEPENDENCIES = "required-after:forgelin_continuous;after:crafttweaker;before:jei;"
    val DECIMAL_FORMAT = DecimalFormat("#0.00")

    lateinit var configPath: String
    lateinit var configDir: File

    val creativeTab: CreativeTabs = object : CreativeTabs(MODID) {
        override fun createIcon(): ItemStack = ItemStack(ModBlocks.chemical_combiner, 1)
    }
}