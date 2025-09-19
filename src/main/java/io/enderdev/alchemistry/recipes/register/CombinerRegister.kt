package io.enderdev.alchemistry.recipes.register

import io.enderdev.alchemistry.chemistry.CompoundRegistry
import io.enderdev.alchemistry.items.ModItems
import io.enderdev.alchemistry.recipes.CombinerRecipe
import io.enderdev.alchemistry.utils.extensions.chemical
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagString
import org.ender_development.catalyx.utils.extensions.areStacksEqualIgnoreQuantity
import org.ender_development.catalyx.utils.extensions.firstOre
import org.ender_development.catalyx.utils.extensions.toDict
import org.ender_development.catalyx.utils.extensions.toStack

class CombinerRegister : AbstractRecipeRegister<CombinerRecipe>() {
	companion object {
		val INSTANCE = CombinerRegister()
	}

	override fun registerRecipes() {
		recipes.add(CombinerRecipe(Items.COAL.toStack(meta = 1), listOf(null, null, "carbon".chemical(8)))) // charcoal
		recipes.add(CombinerRecipe(Items.COAL.toStack(), listOf(null, "carbon".chemical(8))))
		recipes.add(CombinerRecipe(Blocks.GLOWSTONE.toStack(), listOf(null, "phosphorus".chemical(16))))

		// every quartz block variant
		(0..2).forEach {
			val input: MutableList<Any?> = (0..it).map { null }.toMutableList()
			input.add("barium".chemical(32))
			input.add("silicon_dioxide".chemical(64))
			recipes.add(
				CombinerRecipe(
					Blocks.QUARTZ_BLOCK.toStack(meta = it),
					input
				)
			)
		}

		metals.forEach { entry ->
			val metal = heathens.getOrElse(entry) { entry }.chemical(16)
			arrayOf("dust", "ingot").forEach { type ->
				val output = entry.toDict(type).firstOre()
				if(!output.isEmpty)
					recipes.add(CombinerRecipe(output, if(type == "dust") listOf(ItemStack.EMPTY, metal) else listOf(metal)))
			}
		}

		val saltOutputs = ArrayList<ItemStack>()
		listOf("lumpSalt", "materialSalt", "salt", "itemSalt", "dustSalt", "foodSalt")
			.forEachIndexed { i, name ->
				if(!oreNotEmpty(name))
					return@forEachIndexed
				val input: MutableList<ItemStack?> = (0..<i).map { null }.toMutableList()
				if(saltOutputs.none { it.areStacksEqualIgnoreQuantity(name.firstOre()) }) {
					recipes.add(
						CombinerRecipe(
							name.firstOre(),
							input.apply { add("sodium_chloride".chemical(8)) })
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
				if(saltpeterOutputs.none { it.areStacksEqualIgnoreQuantity(name.firstOre()) }) {
					recipes.add(
						CombinerRecipe(
							name.firstOre(),
							input.apply { add("potassium_nitrate".chemical(8)) })
					)
					saltpeterOutputs.add(name.firstOre())
				}
			}

		recipes.add(
			CombinerRecipe(
				"triglyceride".chemical(),
				listOf(
					null, null, "oxygen".chemical(2),
					null, "hydrogen".chemical(32), null,
					"carbon".chemical(18)
				)
			)
		)

		recipes.add(
			CombinerRecipe(
				"cucurbitacin".chemical(),
				listOf(
					null, null, null,
					null, "hydrogen".chemical(44), null,
					"carbon".chemical(32), null, "oxygen".chemical(8)
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

		val carbon = "carbon".chemical(64)
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
					null, null, "silicon_dioxide".chemical(4)
				)
			)
		)

		recipes.add(
			CombinerRecipe(
				Blocks.SAND.toStack(8, 1), //red sand
				listOf(
					null, null, null,
					"silicon_dioxide".chemical(32), "iron_oxide".chemical()
				)
			)
		)

		recipes.add(
			CombinerRecipe(
				Blocks.COBBLESTONE.toStack(2),
				listOf("silicon_dioxide".chemical())
			)
		)

		recipes.add(
			CombinerRecipe(
				Blocks.STONE.toStack(),
				listOf(null, "silicon_dioxide".chemical())
			)
		)

		recipes.add(
			CombinerRecipe(
				Blocks.OBSIDIAN.toStack(),
				listOf(
					"magnesium_oxide".chemical(8), "potassium_chloride".chemical(8), "aluminum_oxide".chemical(8),
					"silicon_dioxide".chemical(24)
				)
			)
		)

		recipes.add(
			CombinerRecipe(
				Blocks.CLAY.toStack(),
				listOf(null, "kaolinite".chemical(4))
			)
		)

		recipes.add(
			CombinerRecipe(
				Blocks.DIRT.toStack(4),
				listOf("water".chemical(), "cellulose".chemical(), "kaolinite".chemical())
			)
		)

		recipes.add(
			CombinerRecipe(
				Blocks.MYCELIUM.toStack(4),
				listOf(
					null, null, null,
					null, null, "psilocybin".chemical(),
					"water".chemical(), "cellulose".chemical(), "kaolinite".chemical()
				)
			)
		)

		recipes.add(
			CombinerRecipe(
				Items.FEATHER.toStack(),
				listOf(
					null, null, null,
					null, null, "protein".chemical(2)
				)
			)
		)

		recipes.add(
			CombinerRecipe(
				Items.SPIDER_EYE.toStack(),
				listOf(null, "beta_carotene".chemical(2), "protein".chemical(2))
			)
		)

		recipes.add(
			CombinerRecipe(
				Blocks.SPONGE.toStack(),
				listOf(null, "calcium_carbonate".chemical(8), "kaolinite".chemical(8))
			)
		)

		recipes.add(
			CombinerRecipe(
				Blocks.GRASS.toStack(4),
				listOf(
					null, null, null,
					"water".chemical(), "cellulose".chemical(), "kaolinite".chemical()
				)
			)
		)

		recipes.add(
			CombinerRecipe(
				Blocks.GRAVEL.toStack(),
				listOf(null, null, "silicon_dioxide".chemical())
			)
		)

		recipes.add(
			CombinerRecipe(
				Items.WATER_BUCKET.toStack(),
				listOf(
					null, null, null,
					null, "water".chemical(16), null,
					null, Items.BUCKET, null
				)
			)
		)


		recipes.add(
			CombinerRecipe(
				Items.MILK_BUCKET.toStack(),
				listOf(
					null, null, null,
					"protein".chemical(2), "water".chemical(16), "sucrose".chemical(),
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
					null, "water".chemical(16), null,
					null, Items.GLASS_BOTTLE, null
				)
			)
		)

		recipes.add(
			CombinerRecipe(
				Blocks.REDSTONE_BLOCK.toStack(),
				listOf(
					null, null, null,
					"iron_oxide".chemical(9), "strontium_carbonate".chemical(9)
				)
			)
		)

		recipes.add(
			CombinerRecipe(
				Items.STRING.toStack(4),
				listOf(null, "protein".chemical(2))
			)
		)

		recipes.add(
			CombinerRecipe(
				Blocks.WOOL.toStack(),
				listOf(
					null, null, null,
					null, null, null,
					"protein".chemical(1), "triglyceride".chemical(1)
				)
			)
		)

		recipes.add(
			CombinerRecipe(
				Items.CARROT.toStack(),
				listOf(
					null, null, null,
					"cellulose".chemical(), "beta_carotene".chemical()
				)
			)
		)

		recipes.add(
			CombinerRecipe(
				Items.REEDS.toStack(),
				listOf(
					null, null, null,
					"cellulose".chemical(), "sucrose".chemical()
				)
			)
		)

		recipes.add(
			CombinerRecipe(
				Blocks.STONE.toStack(meta = 1), //granite
				listOf(
					null, null, null,
					"silicon_dioxide".chemical(1)
				)
			)
		)

		recipes.add(
			CombinerRecipe(
				Blocks.STONE.toStack(meta = 3), //diorite
				listOf(
					null, null, null,
					null, "silicon_dioxide".chemical(1)
				)
			)
		)

		if(oreNotEmpty("itemSilicon"))
			recipes.add(
				CombinerRecipe(
					"itemSilicon".firstOre(),
					listOf(null, null, "silicon".chemical(16))
				)
			)

		recipes.add(
			CombinerRecipe(
				Blocks.STONE.toStack(meta = 5), //andesite
				listOf(
					null, null, null,
					null, null, "silicon_dioxide".chemical(1)
				)
			)
		)

		recipes.add(
			CombinerRecipe(
				Items.FLINT.toStack(),
				listOf(
					null, null, null,
					null, null, null,
					null, "silicon_dioxide".chemical(3), null
				)
			)
		)

		recipes.add(
			CombinerRecipe(
				Items.POTATO.toStack(),
				listOf("starch".chemical(), "potassium".chemical(4))
			)
		)

		recipes.add(
			CombinerRecipe(
				Items.APPLE.toStack(),
				listOf(
					null, "cellulose".chemical(), null,
					null, "sucrose".chemical(1), null
				)
			)
		)

		recipes.add(
			CombinerRecipe(
				ModItems.fertilizer.toStack(8),
				listOf(
					"urea".chemical(1),
					"diammonium_phosphate".chemical(1),
					"potassium_chloride".chemical(1)
				)
			)
		)

		if(oreNotEmpty("gemRuby"))
			recipes.add(
				CombinerRecipe(
					"gemRuby".firstOre(),
					listOf("aluminum_oxide".chemical(16), "chromium".chemical(8))
				)
			)

		if(oreNotEmpty("gemSapphire")) {
			recipes.add(
				CombinerRecipe(
					"gemSapphire".firstOre(),
					listOf(
						"aluminum_oxide".chemical(16),
						"iron".chemical(4),
						"titanium".chemical(4)
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
			val inputs = mutableListOf(null, "triglyceride".chemical(), null)
			inputs.addAll((0..<index).map { null })
			inputs.add("sucrose".chemical())
			if(stack.item == Items.BEETROOT_SEEDS)
				inputs.add("iron_oxide".chemical())
			recipes.add(CombinerRecipe(stack, inputs))
		}

		recipes.add(
			CombinerRecipe(
				Items.BEETROOT.toStack(), listOf(
					null, "sucrose".chemical(), "iron_oxide".chemical()
				)
			)
		)



		Item.getByNameOrId("forestry:iodine_capsule")?.let {
			recipes.add(
				CombinerRecipe(
					it.toStack(),
					listOf(
						null, null, null,
						"iodine".chemical(8), "iodine".chemical(8)
					)
				)
			)
		}

		// all saplings
		(0..5).forEach { i ->
			val input: MutableList<ItemStack?> = (0..<i).map { null }.toMutableList()
			input.add("oxygen".chemical())
			input.add("cellulose".chemical(2))
			recipes.add(CombinerRecipe(Blocks.SAPLING.toStack(4, i), input))
		}

		// all logs
		(0..5).forEach { i ->
			val input: MutableList<ItemStack?> = (0..<i).map { null }.toMutableList()
			input.add("cellulose".chemical())

			//y u gotta do dis mojang
			if(i < 4) recipes.add(CombinerRecipe(ItemStack(Blocks.LOG, 1, i), input))
			else recipes.add(CombinerRecipe(ItemStack(Blocks.LOG2, 1, i - 4), input))
		}


		recipes.add(CombinerRecipe(Items.DYE.toStack(meta = 0), listOf("titanium_oxide".chemical(4)))) // ink sac
		recipes.add(CombinerRecipe(Items.DYE.toStack(meta = 1), listOf("mercury_sulfide".chemical(4)))) // red dye
		recipes.add(CombinerRecipe(Items.DYE.toStack(meta = 2), listOf("nickel_chloride".chemical(4)))) // green dye
		recipes.add(CombinerRecipe(Items.DYE.toStack(meta = 3), listOf("caffeine".chemical(1), "cellulose".chemical(1)))) // cocoa beans
		// lapis lazuli
		recipes.add(CombinerRecipe(Items.DYE.toStack(meta = 5), listOf("potassium_permanganate".chemical(4)))) // purple dye
		recipes.add(CombinerRecipe(Items.DYE.toStack(meta = 6), listOf("copper_chloride".chemical(4)))) // cyan dye
		recipes.add(CombinerRecipe(Items.DYE.toStack(meta = 7), listOf("magnesium_sulfate".chemical(4)))) // light grey dye
		recipes.add(CombinerRecipe(Items.DYE.toStack(meta = 8), listOf("barium_sulfate".chemical(4)))) // grey dye
		recipes.add(CombinerRecipe(Items.DYE.toStack(meta = 9), listOf("arsenic_sulfide".chemical(4)))) // pink dye
		recipes.add(CombinerRecipe(Items.DYE.toStack(meta = 10), listOf("cadmium_sulfide".chemical(2), "chromium_oxide".chemical(2)))) // lime dye
		recipes.add(CombinerRecipe(Items.DYE.toStack(meta = 11), listOf("lead_iodide".chemical(4)))) // yellow dye
		recipes.add(CombinerRecipe(Items.DYE.toStack(meta = 12), listOf("cobalt_aluminate".chemical(2), "antimony_trioxide".chemical(2)))) // light blue dye
		recipes.add(CombinerRecipe(Items.DYE.toStack(meta = 13), listOf("han_purple".chemical(4)))) // magenta dye
		recipes.add(CombinerRecipe(Items.DYE.toStack(meta = 14), listOf("potassium_dichromate".chemical(4)))) // orange dye
		recipes.add(CombinerRecipe(Items.DYE.toStack(3, 15), listOf(null, null, "hydroxylapatite".chemical(2)))) // bone meal


		recipes.add(
			CombinerRecipe(
				Items.SNOWBALL.toStack(),
				listOf(
					null, null, null,
					null, null, null,
					"water".chemical(4)
				)
			)
		)

		recipes.add(
			CombinerRecipe(
				Blocks.SNOW.toStack(),
				listOf(
					null, null, null,
					null, null, null,
					null, "water".chemical(16)
				)
			)
		)

		recipes.add(
			CombinerRecipe(
				Blocks.ICE.toStack(),
				listOf(
					null, null, null,
					null, null, null,
					null, null, "water".chemical(16)
				)
			)
		)

		recipes.add(
			CombinerRecipe(
				Items.LEATHER.toStack(),
				listOf(
					null, null, null,
					null, "protein".chemical(3)
				)
			)
		)


		recipes.add(
			CombinerRecipe(
				Items.ROTTEN_FLESH.toStack(),
				listOf(
					null, null, null,
					null, null, null,
					null, "protein".chemical(3)
				)
			)
		)

		recipes.add(
			CombinerRecipe(
				Items.NETHER_STAR.toStack(),
				listOf(
					"lutetium".chemical(64), "hydrogen".chemical(64), "titanium".chemical(64),
					"hydrogen".chemical(64), "hydrogen".chemical(64), "hydrogen".chemical(64),
					"dysprosium".chemical(64), "hydrogen".chemical(64), "mendelevium".chemical(64)
				)
			)
		)
	}
}
