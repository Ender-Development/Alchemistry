package io.enderdev.alchemistry.items

import io.enderdev.alchemistry.chemistry.ChemicalElement
import io.enderdev.alchemistry.chemistry.ElementRegistry
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.resources.I18n
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraft.world.World
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.*

/**
 * Created by al132 on 1/16/2017.
 */
class ItemElement(name: String) : ItemMetaBase(name) {

    @SideOnly(Side.CLIENT)
    override fun registerModel() {
        ElementRegistry.keys().forEach {
            val element = ElementRegistry[it]
            val elementName = element?.name?.lowercase(Locale.getDefault()) ?: ""
            val elementNumber = element?.meta ?: 0
            ModelLoader.setCustomModelResourceLocation(this, it,
                    ModelResourceLocation(if (elementNumber <= 118) registryName.toString() + "_" + elementName else registryName.toString(), "inventory"))
        }
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, playerIn: World?, tooltip: List<String>, advanced: ITooltipFlag) {
        val element: ChemicalElement? = ElementRegistry[stack.itemDamage]
        element?.let {
            (tooltip as MutableList).add(element.abbreviation + " - " + element.meta)
        }
    }

    @SideOnly(Side.CLIENT)
    override fun getSubItems(tab: CreativeTabs, items: NonNullList<ItemStack>) {
        if (!isInCreativeTab(tab)) return;
        ElementRegistry.keys().forEach { items.add(ItemStack(this, 1, it)) }
    }

    override fun getItemStackDisplayName(stack: ItemStack): String {
        return if (stack.metadata > 118 && stack.item == ModItems.elements) {
            I18n.format(getTranslationKey(stack) + ".name")
        } else super.getItemStackDisplayName(stack)
    }

    override fun getTranslationKey(stack: ItemStack): String {
        var i = stack.itemDamage
        if (!ElementRegistry.keys().contains(i)) i = 1
        try {
            return super.getTranslationKey() + "_" + ElementRegistry[i]!!.name.lowercase(Locale.getDefault())
        } catch (e: NullPointerException) {
            throw NullPointerException("Unable to find translation key for element #[$i]")
        }
    }
}