package io.enderdev.alchemistry.utils.extensions

import io.enderdev.alchemistry.chemistry.ChemicalCompound
import io.enderdev.alchemistry.chemistry.ChemicalElement
import io.enderdev.alchemistry.chemistry.CompoundRegistry
import io.enderdev.alchemistry.chemistry.ElementRegistry
import net.minecraft.block.Block
import net.minecraft.client.resources.I18n
import net.minecraft.init.Blocks
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import net.minecraft.potion.Potion
import net.minecraft.util.ResourceLocation
import net.minecraftforge.oredict.OreDictionary
import net.minecraftforge.oredict.OreIngredient

/**
 * Created by al132 on 4/27/2017.
 */

fun String.toPotion(): Potion = Potion.getPotionFromResourceLocation(this)!!

fun String.toOre(): OreIngredient = OreIngredient(this)

fun String.toStack(quantity: Int = 1, meta: Int = 0): ItemStack {
    val actualMeta = this.split(":").last().toIntOrNull() ?: meta
    val resourceLocation =
            if (this.count { it == ':' } == 2) ResourceLocation(this.dropLastWhile { it != ':' }.dropLast(1))
            else ResourceLocation(this)
    var outputStack: ItemStack = ItemStack.EMPTY
    val outputItem: Item? = Item.REGISTRY.getObject(resourceLocation)
    val outputBlock: Block? = Block.REGISTRY.getObject(resourceLocation)
    val outputElement: ChemicalElement? = ElementRegistry[this]
    val outputCompound: ChemicalCompound? = CompoundRegistry[this.replace(" ", "_")]
    if (outputElement != null) {
        outputStack = outputElement.toItemStack(quantity = quantity)
    } else if (outputCompound != null) {
        outputStack = outputCompound.toItemStack(quantity = quantity)
    } else if (outputItem != null) {
        outputStack = ItemStack(outputItem, quantity, actualMeta)
    } else if (outputBlock != null && outputBlock != Blocks.AIR && outputBlock != Blocks.WATER) {
        outputStack = ItemStack(outputBlock, quantity, actualMeta)
    }
    return outputStack
}

fun String.toIngredient(quantity: Int = 1, meta: Int = 0): Ingredient = Ingredient.fromStacks(toStack(quantity, meta))

fun String.toDict(prefix: String) = "$prefix${replaceFirstChar(Char::uppercaseChar)}"

fun String.firstOre(): ItemStack = OreDictionary.getOres(this).firstOrNull() ?: ItemStack.EMPTY

fun String.translate(vararg format: Any): String = I18n.format(this, *format)