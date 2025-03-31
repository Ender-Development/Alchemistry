package io.enderdev.alchemistry.items

import io.enderdev.alchemistry.Tags
import io.enderdev.alchemistry.utils.extensions.translate
import net.minecraft.client.Minecraft
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

object ModItems {
    val items = ArrayList<ItemBase>()

    var mineralSalt = ItemBase("mineral_salt")
    var condensedMilk = ItemBase("condensed_milk")
    var fertilizer = ItemFertilizer()
    var obsidianBreaker = object : ItemBase("obsidian_breaker") {
        override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
            tooltip.add("item.${Tags.MOD_ID}:obsidian_breaker.tooltip".translate())
        }
    }

    var elements = ItemElement("element")
    var compounds = ItemCompound("compound")
    var ingots = ItemElementIngot("ingot")
    val periodicDankMolecule = ItemPeriodicDiagram()


    fun registerItems(event: RegistryEvent.Register<Item>) = items.forEach { it.registerItem(event) }

    @SideOnly(Side.CLIENT)
    fun registerModels() = items.forEach { it.registerModel() }

    @SideOnly(Side.CLIENT)
    fun initColors() =
        Minecraft.getMinecraft().itemColors.registerItemColorHandler(ItemColorHandler(), compounds, ingots, elements)
}