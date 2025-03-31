package io.enderdev.alchemistry.recipes.register

import io.enderdev.alchemistry.chemistry.CompoundRegistry
import io.enderdev.alchemistry.items.ModItems
import io.enderdev.alchemistry.recipes.CombinerRecipe
import io.enderdev.alchemistry.utils.extensions.areStacksEqualIgnoreQuantity
import io.enderdev.alchemistry.utils.extensions.firstOre
import io.enderdev.alchemistry.utils.extensions.toDict
import io.enderdev.alchemistry.utils.extensions.toStack
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagString
import net.minecraft.util.ResourceLocation

class CombinerRegister : AbstractRecipeRegister<CombinerRecipe>() {
    companion object {
        val INSTANCE = CombinerRegister()
    }

    override fun registerRecipes() {
        recipes.add(CombinerRecipe(Items.COAL.toStack(meta = 1), listOf(null, null, "carbon".toStack(8))))
        recipes.add(CombinerRecipe(Items.COAL.toStack(), listOf(null, "carbon".toStack(8))))
        recipes.add(CombinerRecipe(Blocks.GLOWSTONE.toStack(), listOf(null, "phosphorus".toStack(16))))

        // every quartz block variant
        (0..2).forEach {
            val input: MutableList<Any?> = (0..it).map { null }.toMutableList()
            input.add("barium".toStack(32))
            input.add("silicon_dioxide".toStack(64))
            recipes.add(
                CombinerRecipe(
                    Blocks.QUARTZ_BLOCK.toStack(meta = it),
                    input
                )
            )
        }

        metals.forEach { entry ->
            val dustOutput: ItemStack? = entry.toDict("dust").firstOre()
            if (dustOutput != null && !dustOutput.isEmpty) {
                recipes.add(
                    CombinerRecipe(
                        dustOutput,
                        listOf(
                            ItemStack.EMPTY,
                            if (entry in heathens.keys) {
                                heathens[entry]!!.toStack(16)
                            } else {
                                entry.toStack(16)
                            }
                        )
                    )
                )
            }

            val ingotOutput: ItemStack? = entry.toDict("ingot").firstOre()
            if (ingotOutput != null && !ingotOutput.isEmpty) {
                recipes.add(
                    CombinerRecipe(
                        ingotOutput,
                        listOf(
                            if (entry in heathens.keys) {
                                heathens[entry]!!.toStack(16)
                            } else {
                                entry.toStack(16)
                            }
                        )
                    )
                )
            }
        }

        val saltOutputs = ArrayList<ItemStack>()
        listOf("lumpSalt", "materialSalt", "salt", "itemSalt", "dustSalt", "foodSalt")
            .forEachIndexed { i, name ->
                if(!oreNotEmpty(name))
                    return@forEachIndexed
                val input: MutableList<ItemStack?> = (0..<i).map { null }.toMutableList()
                if (saltOutputs.none { it.areStacksEqualIgnoreQuantity(name.firstOre()) }) {
                    recipes.add(
                        CombinerRecipe(
                            name.firstOre(),
                            input.apply { add("sodium_chloride".toStack(8)) })
                    )
                    saltOutputs.add(name.firstOre())
                }
            }

        val saltpeterOutputs = ArrayList<ItemStack>()
        listOf("dustSaltpeter", "nitrate", "nitre")
            .forEachIndexed { i, name ->
                if(!oreNotEmpty(name))
                    return@forEachIndexed
                val input: MutableList<ItemStack?> = (0..<i).map { null }.toMutableList()
                if (saltpeterOutputs.none { it.areStacksEqualIgnoreQuantity(name.firstOre()) }) {
                    recipes.add(
                        CombinerRecipe(
                            name.firstOre(),
                            input.apply { add("potassium_nitrate".toStack(8)) })
                    )
                    saltpeterOutputs.add(name.firstOre())
                }
            }

        recipes.add(
            CombinerRecipe(
                "triglyceride".toStack(),
                listOf(
                    null, null, "oxygen".toStack(2),
                    null, "hydrogen".toStack(32), null,
                    "carbon".toStack(18)
                )
            )
        )

        recipes.add(
            CombinerRecipe(
                "cucurbitacin".toStack(),
                listOf(
                    null, null, null,
                    null, "hydrogen".toStack(44), null,
                    "carbon".toStack(32), null, "oxygen".toStack(8)
                )
            )
        )

        CompoundRegistry.compounds().forEach { compound ->
            if(!compound.autoCombinerRecipe)
                return@forEach

            var list = compound.toItemStackList()
            if(compound.shiftedSlots != 0) {
                list = list.toMutableList()
                list.addAll(0, (0..<compound.shiftedSlots).map { _ -> ItemStack.EMPTY })
            }

            recipes.add(CombinerRecipe(compound.toItemStack(1), list))
        }

        DissolverRegister.INSTANCE.recipes.forEach { recipe ->
            if(recipe.reversible && recipe.inputs.isNotEmpty())
                recipes.add(CombinerRecipe(recipe.inputs[0], recipe.outputs.toStackList()))
        }

        val carbon = "carbon".toStack(quantity = 64)
        recipes.add(
            CombinerRecipe(
                Items.DIAMOND.toStack(),
                listOf(
                    carbon, carbon, carbon,
                    carbon, null, carbon,
                    carbon, carbon, carbon
                )
            )
        )

        recipes.add(
            CombinerRecipe(
                Blocks.SAND.toStack(),
                listOf(
                    null, null, null,
                    null, null, null,
                    null, null, "silicon_dioxide".toStack(4)
                )
            )
        )

        recipes.add(
            CombinerRecipe(
                Blocks.SAND.toStack(quantity = 8, meta = 1), //red sand
                listOf(
                    null, null, null,
                    "silicon_dioxide".toStack(quantity = 32), "iron_oxide".toStack()
                )
            )
        )

        recipes.add(
            CombinerRecipe(
                Blocks.COBBLESTONE.toStack(quantity = 2),
                listOf("silicon_dioxide".toStack())
            )
        )

        recipes.add(
            CombinerRecipe(
                Blocks.STONE.toStack(),
                listOf(null, "silicon_dioxide".toStack())
            )
        )

        recipes.add(
            CombinerRecipe(
                Blocks.OBSIDIAN.toStack(),
                listOf(
                    "magnesium_oxide".toStack(8), "potassium_chloride".toStack(8), "aluminum_oxide".toStack(8),
                    "silicon_dioxide".toStack(24)
                )
            )
        )

        recipes.add(
            CombinerRecipe(
                Blocks.CLAY.toStack(),
                listOf(null, "kaolinite".toStack(4))
            )
        )

        recipes.add(
            CombinerRecipe(
                Blocks.DIRT.toStack(4),
                listOf("water".toStack(), "cellulose".toStack(), "kaolinite".toStack())
            )
        )

        recipes.add(
            CombinerRecipe(
                Blocks.MYCELIUM.toStack(4),
                listOf(
                    null, null, null,
                    null, null, "psilocybin".toStack(),
                    "water".toStack(), "cellulose".toStack(), "kaolinite".toStack()
                )
            )
        )

        recipes.add(
            CombinerRecipe(
                Items.FEATHER.toStack(),
                listOf(
                    null, null, null,
                    null, null, "protein".toStack(2)
                )
            )
        )

        recipes.add(
            CombinerRecipe(
                Items.SPIDER_EYE.toStack(),
                listOf(null, "beta_carotene".toStack(2), "protein".toStack(2))
            )
        )

        recipes.add(
            CombinerRecipe(
                Blocks.SPONGE.toStack(),
                listOf(null, "calcium_carbonate".toStack(8), "kaolinite".toStack(8))
            )
        )

        recipes.add(
            CombinerRecipe(
                Blocks.GRASS.toStack(4),
                listOf(
                    null, null, null,
                    "water".toStack(), "cellulose".toStack(), "kaolinite".toStack()
                )
            )
        )

        recipes.add(
            CombinerRecipe(
                Blocks.GRAVEL.toStack(),
                listOf(null, null, "silicon_dioxide".toStack())
            )
        )

        recipes.add(
            CombinerRecipe(
                Items.WATER_BUCKET.toStack(),
                listOf(
                    null, null, null,
                    null, "water".toStack(16), null,
                    null, Items.BUCKET, null
                )
            )
        )


        recipes.add(
            CombinerRecipe(
                Items.MILK_BUCKET.toStack(),
                listOf(
                    null, null, null,
                    "protein".toStack(2), "water".toStack(16), "sucrose".toStack(),
                    null, Items.BUCKET, null
                )
            )
        )

        recipes.add(
            CombinerRecipe(
                Items.POTIONITEM.toStack()
                    .apply { this.setTagInfo("Potion", NBTTagString("water")) },
                listOf(
                    null, null, null,
                    null, "water".toStack(16), null,
                    null, Items.GLASS_BOTTLE, null
                )
            )
        )

        recipes.add(
            CombinerRecipe(
                Blocks.REDSTONE_BLOCK.toStack(),
                listOf(
                    null, null, null,
                    "iron_oxide".toStack(9), "strontium_carbonate".toStack(9)
                )
            )
        )

        recipes.add(
            CombinerRecipe(
                Items.STRING.toStack(4),
                listOf(null, "protein".toStack(2))
            )
        )

        recipes.add(
            CombinerRecipe(
                Blocks.WOOL.toStack(),
                listOf(
                    null, null, null,
                    null, null, null,
                    "protein".toStack(1), "triglyceride".toStack(1)
                )
            )
        )

        recipes.add(
            CombinerRecipe(
                Items.CARROT.toStack(),
                listOf(
                    null, null, null,
                    "cellulose".toStack(), "beta_carotene".toStack()
                )
            )
        )

        recipes.add(
            CombinerRecipe(
                Items.REEDS.toStack(),
                listOf(
                    null, null, null,
                    "cellulose".toStack(), "sucrose".toStack()
                )
            )
        )

        recipes.add(
            CombinerRecipe(
                Blocks.STONE.toStack(meta = 1), //granite
                listOf(
                    null, null, null,
                    "silicon_dioxide".toStack(1)
                )
            )
        )

        recipes.add(
            CombinerRecipe(
                Blocks.STONE.toStack(meta = 3), //diorite
                listOf(
                    null, null, null,
                    null, "silicon_dioxide".toStack(1)
                )
            )
        )

        if(oreNotEmpty("itemSilicon"))
            recipes.add(
                CombinerRecipe(
                    "itemSilicon".firstOre(),
                    listOf(null, null, "silicon".toStack(16))
                )
            )

        recipes.add(
            CombinerRecipe(
                Blocks.STONE.toStack(meta = 5), //andesite
                listOf(
                    null, null, null,
                    null, null, "silicon_dioxide".toStack(1)
                )
            )
        )

        recipes.add(
            CombinerRecipe(
                Items.FLINT.toStack(),
                listOf(
                    null, null, null,
                    null, null, null,
                    null, "silicon_dioxide".toStack(3), null
                )
            )
        )

        recipes.add(
            CombinerRecipe(
                Items.POTATO.toStack(),
                listOf("starch".toStack(), "potassium".toStack(4))
            )
        )

        recipes.add(
            CombinerRecipe(
                Items.APPLE.toStack(),
                listOf(
                    null, "cellulose".toStack(), null,
                    null, "sucrose".toStack(1), null
                )
            )
        )

        recipes.add(
            CombinerRecipe(
                ModItems.fertilizer.toStack(8),
                listOf(
                    "urea".toStack(1),
                    "diammonium_phosphate".toStack(1),
                    "potassium_chloride".toStack(1)
                )
            )
        )

        if(oreNotEmpty("gemRuby"))
            recipes.add(
                CombinerRecipe(
                    "gemRuby".firstOre(),
                    listOf("aluminum_oxide".toStack(16), "chromium".toStack(8))
                )
            )

        if (oreNotEmpty("gemSapphire")) {
            recipes.add(
                CombinerRecipe(
                    "gemSapphire".firstOre(),
                    listOf(
                        "aluminum_oxide".toStack(16),
                        "iron".toStack(4),
                        "titanium".toStack(4)
                    )
                )
            )
        }

        val seeds = listOf(
            Items.WHEAT_SEEDS.toStack(),
            Items.PUMPKIN_SEEDS.toStack(),
            Items.MELON_SEEDS.toStack(),
            Items.BEETROOT_SEEDS.toStack()
        )

        seeds.forEachIndexed { index: Int, stack: ItemStack ->
            val inputs = mutableListOf(null, "triglyceride".toStack(), null)
            inputs.addAll((0..<index).map { null })
            inputs.add("sucrose".toStack())
            if (stack.item == Items.BEETROOT_SEEDS)
                inputs.add("iron_oxide".toStack())
            recipes.add(CombinerRecipe(stack, inputs))
        }

        recipes.add(
            CombinerRecipe(
                Items.BEETROOT.toStack(), listOf(
                    null, "sucrose".toStack(), "iron_oxide".toStack()
                )
            )
        )



        Item.REGISTRY.getObject(ResourceLocation("forestry", "iodine_capsule"))?.let {
            recipes.add(
                CombinerRecipe(
                    it.toStack(),
                    listOf(
                        null, null, null,
                        "iodine".toStack(8), "iodine".toStack(8)
                    )
                )
            )
        }


        // all saplings
        (0..5).forEach { i ->
            val input: MutableList<ItemStack?> = (0..<i).map { null }.toMutableList()
            input.add("oxygen".toStack())
            input.add("cellulose".toStack(2))
            recipes.add(CombinerRecipe(Blocks.SAPLING.toStack(quantity = 4, meta = i), input))
        }

        // all logs
        (0 ..5).forEach { i ->
            val input: MutableList<ItemStack?> = (0..<i).map { null }.toMutableList()
            input.add("cellulose".toStack())

            //y u gotta do dis mojang
            if (i < 4) recipes.add(CombinerRecipe(ItemStack(Blocks.LOG, 1, i), input))
            else recipes.add(CombinerRecipe(ItemStack(Blocks.LOG2, 1, i - 4), input))
        }


        recipes.add(CombinerRecipe(Items.DYE.toStack(meta = 0), listOf("titanium_oxide".toStack(4))))
        recipes.add(CombinerRecipe(Items.DYE.toStack(meta = 1), listOf("mercury_sulfide".toStack(4))))
        recipes.add(CombinerRecipe(Items.DYE.toStack(meta = 2), listOf("nickel_chloride".toStack(4))))
        recipes.add(
            CombinerRecipe(
                Items.DYE.toStack(meta = 3),
                listOf("caffeine".toStack(1), "cellulose".toStack(1))
            )
        )
        recipes.add(CombinerRecipe(Items.DYE.toStack(meta = 5), listOf("potassium_permanganate".toStack(4))))
        recipes.add(CombinerRecipe(Items.DYE.toStack(meta = 6), listOf("copper_chloride".toStack(4))))
        recipes.add(CombinerRecipe(Items.DYE.toStack(meta = 7), listOf("magnesium_sulfate".toStack(4))))
        recipes.add(CombinerRecipe(Items.DYE.toStack(meta = 8), listOf("barium_sulfate".toStack(4))))
        recipes.add(CombinerRecipe(Items.DYE.toStack(meta = 9), listOf("arsenic_sulfide".toStack(4))))
        recipes.add(
            CombinerRecipe(
                Items.DYE.toStack(meta = 10),
                listOf("cadmium_sulfide".toStack(2), "chromium_oxide".toStack(2))
            )
        )
        recipes.add(CombinerRecipe(Items.DYE.toStack(meta = 11), listOf("lead_iodide".toStack(4))))
        recipes.add(
            CombinerRecipe(
                Items.DYE.toStack(meta = 12),
                listOf("cobalt_aluminate".toStack(2), "antimony_trioxide".toStack(2))
            )
        )
        recipes.add(CombinerRecipe(Items.DYE.toStack(meta = 13), listOf("han_purple".toStack(4))))
        recipes.add(CombinerRecipe(Items.DYE.toStack(meta = 14), listOf("potassium_dichromate".toStack(4))))


        recipes.add(
            CombinerRecipe(
                Items.SNOWBALL.toStack(),
                listOf(
                    null, null, null,
                    null, null, null,
                    "water".toStack(4)
                )
            )
        )

        recipes.add(
            CombinerRecipe(
                Blocks.SNOW.toStack(),
                listOf(
                    null, null, null,
                    null, null, null,
                    null, "water".toStack(16)
                )
            )
        )

        recipes.add(
            CombinerRecipe(
                Blocks.ICE.toStack(),
                listOf(
                    null, null, null,
                    null, null, null,
                    null, null, "water".toStack(16)
                )
            )
        )


        recipes.add(
            CombinerRecipe(
                Items.DYE.toStack(quantity = 3, meta = 15),
                listOf(null, null, "hydroxylapatite".toStack(2))
            )
        )

        recipes.add(
            CombinerRecipe(
                Items.LEATHER.toStack(),
                listOf(
                    null, null, null,
                    null, "protein".toStack(3)
                )
            )
        )


        recipes.add(
            CombinerRecipe(
                Items.ROTTEN_FLESH.toStack(),
                listOf(
                    null, null, null,
                    null, null, null,
                    null, "protein".toStack(3)
                )
            )
        )

        recipes.add(
            CombinerRecipe(
                Items.NETHER_STAR.toStack(),
                listOf(
                    "lutetium".toStack(64), "hydrogen".toStack(64), "titanium".toStack(64),
                    "hydrogen".toStack(64), "hydrogen".toStack(64), "hydrogen".toStack(64),
                    "dysprosium".toStack(64), "hydrogen".toStack(64), "mendelevium".toStack(64)
                )
            )
        )
    }
}