package io.enderdev.alchemistry.recipes.register

import io.enderdev.alchemistry.chemistry.CompoundRegistry
import io.enderdev.alchemistry.chemistry.ElementRegistry
import io.enderdev.alchemistry.items.ModItems
import io.enderdev.alchemistry.recipes.DissolverRecipe
import io.enderdev.alchemistry.recipes.dissolverRecipe
import io.enderdev.alchemistry.utils.extensions.toIngredient
import io.enderdev.alchemistry.utils.extensions.toOre
import io.enderdev.alchemistry.utils.extensions.toStack
import net.minecraft.block.Block
import net.minecraft.block.BlockTallGrass
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary

class DissolverRegister : AbstractRecipeRegister<DissolverRecipe>() {
	companion object {
		val INSTANCE = DissolverRegister()
	}

	val metalOreData: List<DissolverOreData> = listOf(
		DissolverOreData("ingot", 16, metals),
		DissolverOreData("ore", 32, metals),
		DissolverOreData("dust", 16, metals),
		DissolverOreData("block", 144, metals),
		DissolverOreData("nugget", 1, metals),
		DissolverOreData("plate", 16, metals)
	)

	override fun registerRecipes() {
		CompoundRegistry.compounds().forEach { compound ->
			if(compound.autoDissolverRecipe)
				recipes.add(dissolverRecipe {
					input = compound.toItemStack(1).toIngredient()
					output {
						addGroup {
							compound.components.forEach { component ->
								addStack { component.compound.toItemStack(component.quantity) }
							}
						}
					}
				})
		}

		(0..BlockTallGrass.EnumType.entries.size).forEach { meta ->
			recipes.add(dissolverRecipe {
				input = Blocks.TALLGRASS.toIngredient(meta = BlockTallGrass.EnumType.byMetadata(meta).ordinal)
				output {
					relativeProbability = false
					addGroup {
						probability = 25.0
						addStack { "cellulose".toStack() }
					}
				}
			})
		}

		listOf("ingotChrome", "plateChrome", "dustChrome").forEach { ore ->
			if(oreNotEmpty(ore))
				recipes.add(dissolverRecipe {
					input = ore.toOre()
					output {
						addStack { "chromium".toStack(16) }
					}
				})
		}

		if(oreNotEmpty("blockChrome")) {
			recipes.add(dissolverRecipe {
				input = "blockChrome".toOre()
				output {
					addStack { "chromium".toStack(16 * 9) }
				}
			})
		}

		if(oreNotEmpty("oreChrome")) {
			recipes.add(dissolverRecipe {
				input = "oreChrome".toOre()
				output {
					addStack { "chromium".toStack(16 * 2) }
				}
			})
		}

		if(oreNotEmpty("dustAsh")) {
			recipes.add(dissolverRecipe {
				input = "dustAsh".toOre()
				reversible = true
				output {
					addStack { "potassium_carbonate".toStack(4) }
				}
			})
		}

		recipes.add(dissolverRecipe {
			input = Items.FLINT.toIngredient()
			output {
				addStack { "silicon_dioxide".toStack(3) }
			}
		})

		listOf("lumpSalt", "materialSalt", "salt", "itemSalt", "dustSalt", "foodSalt").forEach {
			if(oreNotEmpty(it))
				recipes.add(dissolverRecipe {
					input = it.toOre()
					output {
						addStack { "sodium_chloride".toStack(8) }
					}
				})
		}

		recipes.add(dissolverRecipe {
			input = Items.DYE.toIngredient(meta = 3)
			output {
				relativeProbability = false
				addGroup {
					probability = 50.0
					addStack { "cellulose".toStack() }
				}
				addGroup {
					probability = 100.0
					addStack { "caffeine".toStack() }
				}
			}
		})

		recipes.add(dissolverRecipe {
			input = Items.APPLE.toIngredient()
			output {
				addGroup {
					addStack { "cellulose".toStack() }
					addStack { "sucrose".toStack() }
				}
			}
		})

		listOf("dustSaltpeter", "nitrate", "nitre").forEach {
			if(oreNotEmpty(it)) {
				recipes.add(dissolverRecipe {
					input = it.toOre()
					output {
						addStack { "potassium_nitrate".toStack(8) }
					}
				})
			}
		}


		recipes.add(dissolverRecipe {
			input = Blocks.COAL_ORE.toIngredient()
			output {
				addGroup {
					addStack { "carbon".toStack(32) }
					addStack { "sulfur".toStack(8) }
				}
			}
		})


		recipes.add(dissolverRecipe {
			input = Blocks.COAL_BLOCK.toIngredient()
			output {
				addStack { "carbon".toStack(9 * 8) }
			}
		})

		recipes.add(dissolverRecipe {
			input = Items.WHEAT_SEEDS.toIngredient()
			output {
				relativeProbability = false
				addGroup {
					probability = 10.0
					addStack { "cellulose".toStack() }
				}
			}
		})

		recipes.add(dissolverRecipe {
			input = Blocks.NETHERRACK.toIngredient()
			output {
				addGroup { addStack { ItemStack.EMPTY }; probability = 15.0 }
				addGroup { addStack { "zinc_oxide".toStack() }; probability = 2.0 }
				addGroup { addStack { "gold".toStack() }; probability = 1.0 }
				addGroup { addStack { "phosphorus".toStack() }; probability = 1.0 }
				addGroup { addStack { "sulfur".toStack() }; probability = 3.0 }
				addGroup { addStack { "germanium".toStack() }; probability = 1.0 }
				addGroup { addStack { "silicon".toStack() }; probability = 4.0 }
			}
		})

		listOf(Items.NETHERBRICK, Blocks.NETHER_BRICK).forEach {
			recipes.add(dissolverRecipe {
				input = if(it == Items.NETHERBRICK) (it as Item).toIngredient() else (it as Block).toIngredient()
				output {
					rolls = if(it == Blocks.NETHER_BRICK) 4 else 1
					addGroup { addStack { ItemStack.EMPTY }; probability = 5.0 }
					addGroup { addStack { "zinc_oxide".toStack() }; probability = 2.0 }
					addGroup { addStack { "gold".toStack() }; probability = 1.0 }
					addGroup { addStack { "phosphorus".toStack() }; probability = 1.0 }
					addGroup { addStack { "sulfur".toStack() }; probability = 4.0 }
					addGroup { addStack { "germanium".toStack() }; probability = 1.0 }
					addGroup { addStack { "silicon".toStack() }; probability = 4.0 }
				}
			})
		}

		recipes.add(dissolverRecipe {
			input = Items.SPIDER_EYE.toIngredient()
			output {
				addGroup {
					addStack { "beta_carotene".toStack(2) }
					addStack { "protein".toStack(2) }
				}
			}
		})

		recipes.add(dissolverRecipe {
			input = Items.IRON_HORSE_ARMOR.toIngredient()
			output {
				addStack { "iron".toStack(64) }
			}
		})

		recipes.add(dissolverRecipe {
			input = Items.DIAMOND_HORSE_ARMOR.toIngredient()
			output {
				addStack { "carbon".toStack(4 * (64 * 8)) }
			}
		})

		recipes.add(dissolverRecipe {
			input = Blocks.ANVIL.toIngredient()
			output {
				addStack { "iron".toStack((144 * 3) + (16 * 4)) }
			}
		})

		recipes.add(dissolverRecipe {
			input = Items.IRON_DOOR.toIngredient()
			output {
				addStack { "iron".toStack(32) }
			}
		})

		recipes.add(dissolverRecipe {
			input = Blocks.IRON_TRAPDOOR.toIngredient()
			output {
				addStack { "iron".toStack(64) }
			}
		})

		recipes.add(dissolverRecipe {
			input = Blocks.CHEST.toIngredient()
			output {
				addStack { "cellulose".toStack(2) }
			}
		})

		recipes.add(dissolverRecipe {
			input = Blocks.CRAFTING_TABLE.toIngredient()
			output {
				addStack { "cellulose".toStack() }
			}
		})

		recipes.add(dissolverRecipe {
			input = Blocks.WEB.toIngredient()
			output {
				addStack { "protein".toStack(2) }
			}
		})

		recipes.add(dissolverRecipe {
			input = Items.GOLDEN_HORSE_ARMOR.toIngredient()
			output {
				addStack { "gold".toStack(64) }
			}
		})

		recipes.add(dissolverRecipe {
			input = "wool".toOre()
			output {
				addGroup {
					addStack { "protein".toStack() }
					addStack { "triglyceride".toStack() }
				}
			}
		})

		(0..15).forEach { index ->
			recipes.add(dissolverRecipe {
				input = Blocks.CARPET.toIngredient(meta = index)
				output {
					relativeProbability = false
					addGroup {
						probability = 200.0 / 3
						addStack { "protein".toStack() }
						addStack { "triglyceride".toStack() }
					}
				}
			})
		}

		recipes.add(dissolverRecipe {
			input = Items.EMERALD.toIngredient()
			output {
				reversible = true
				addGroup {
					addStack { "beryl".toStack(8) }
					addStack { "chromium".toStack(8) }
					addStack { "vanadium".toStack(4) }
				}
			}
		})


		recipes.add(dissolverRecipe {
			input = Blocks.EMERALD_ORE.toIngredient()
			output {
				addGroup {
					addStack { "beryl".toStack(8 * 2) }
					addStack { "chromium".toStack(8 * 2) }
					addStack { "vanadium".toStack(4 * 2) }
				}
			}
		})

		listOf(Blocks.END_STONE, Blocks.END_BRICKS).forEach {
			recipes.add(dissolverRecipe {
				input = it.toIngredient()
				output {
					addGroup { addStack { "mercury".toStack() }; probability = 50.0 }
					addGroup { addStack { "neodymium".toStack() }; probability = 5.0 }
					addGroup { addStack { "silicon_dioxide".toStack(2) }; probability = 250.0 }
					addGroup { addStack { "lithium".toStack() }; probability = 50.0 }
					addGroup { addStack { "thorium".toStack() }; probability = 2.0 }
				}
			})
		}

		listOf(Blocks.SNOW, Blocks.ICE).forEach {
			recipes.add(dissolverRecipe {
				input = it.toIngredient()
				output {
					addStack { "water".toStack(16) }
				}
			})
		}


		recipes.add(dissolverRecipe {
			input = "record".toOre()
			output {
				addGroup {
					addStack { "polyvinyl_chloride".toStack(64) }
					addStack { "lead".toStack(16) }
					addStack { "cadmium".toStack(16) }
				}
			}
		})

		recipes.add(dissolverRecipe {
			input = Blocks.JUKEBOX.toIngredient()
			output {
				addGroup {
					addStack { "carbon".toStack(64 * 8) }
					addStack { "cellulose".toStack(2) }
				}
			}
		})

		(0..15).forEach { i ->
			recipes.add(dissolverRecipe {
				input = Blocks.CONCRETE_POWDER.toIngredient(2, meta = i)
				output {
					addStack { "silicon_dioxide".toStack(5) }
				}
			})
			recipes.add(dissolverRecipe {
				input = Blocks.CONCRETE.toIngredient(2, meta = i)
				output {
					addStack { "silicon_dioxide".toStack(5) }
				}
			})
		}

		listOf(
			Blocks.GRASS.toStack(),
			Blocks.DIRT.toStack(),
			Blocks.DIRT.toStack(meta = 1), // coarse dirt
			Blocks.DIRT.toStack(meta = 2) // podzol
		).forEach {
			recipes.add(dissolverRecipe {
				input = it.toIngredient()
				output {
					addGroup { addStack { "water".toStack() }; probability = 30.0 }
					addGroup { addStack { "silicon_dioxide".toStack() }; probability = 50.0 }
					addGroup { addStack { "cellulose".toStack() }; probability = 10.0 }
					addGroup { addStack { "kaolinite".toStack() }; probability = 10.0 }
				}
			})
		}

		recipes.add(dissolverRecipe {
			input = Blocks.EMERALD_BLOCK.toIngredient()
			output {
				addGroup {
					addStack { "beryl".toStack(8 * 9) }
					addStack { "chromium".toStack(8 * 9) }
					addStack { "vanadium".toStack(4 * 9) }
				}
			}
		})

		recipes.add(dissolverRecipe {
			input = "blockGlass".toOre()
			output {
				addStack { "silicon_dioxide".toStack(4) }
			}
		})

		listOf(
			"treeSapling".toOre(),
			Blocks.DEADBUSH.toIngredient(),
			Blocks.VINE.toIngredient(),
			Blocks.WATERLILY.toIngredient()
		).forEach {
			recipes.add(dissolverRecipe {
				input = it
				output {
					relativeProbability = false
					addGroup {
						probability = 25.0
						addStack { "cellulose".toStack() }
					}
				}
			})
		}

		recipes.add(dissolverRecipe {
			input = Blocks.PUMPKIN.toIngredient()
			output {
				relativeProbability = false
				addGroup {
					probability = 50.0
					addStack { "cucurbitacin".toStack() }
				}
			}
		})

		recipes.add(dissolverRecipe {
			input = Items.QUARTZ.toIngredient()
			reversible = true
			output {
				addGroup {
					addStack { "barium".toStack(8) }
					addStack { "silicon_dioxide".toStack(16) }
				}
			}
		})


		recipes.add(dissolverRecipe {
			input = Blocks.QUARTZ_ORE.toIngredient()
			output {
				addGroup {
					addStack { "barium".toStack(8 * 2) }
					addStack { "silicon_dioxide".toStack(16 * 2) }
				}
			}
		})

		(0..2).forEach {
			recipes.add(dissolverRecipe {
				input = Blocks.QUARTZ_BLOCK.toIngredient(meta = it)
				output {
					addGroup {
						addStack { "barium".toStack(8 * 4) }
						addStack { "silicon_dioxide".toStack(16 * 4) }
					}
				}
			})
		}
		recipes.add(dissolverRecipe {
			input = Blocks.BROWN_MUSHROOM.toIngredient()
			reversible = true
			output {
				addGroup {
					addStack { "psilocybin".toStack() }
					addStack { "cellulose".toStack() }
				}
			}
		})

		recipes.add(dissolverRecipe {
			input = Blocks.RED_MUSHROOM.toIngredient()
			reversible = true
			output {
				addGroup {
					addStack { "cellulose".toStack() }
					addStack { "psilocybin".toStack() }
				}
			}
		})

		recipes.add(dissolverRecipe {
			input = Blocks.SOUL_SAND.toIngredient()
			output {
				reversible = true
				addGroup {
					addStack { "thulium".toStack() }
					addStack { "silicon_dioxide".toStack(4) }
				}
			}
		})

		recipes.add(dissolverRecipe {
			input = Items.REEDS.toIngredient()
			output {
				addStack { "sucrose".toStack() }
			}
		})

		recipes.add(dissolverRecipe {
			input = Items.DYE.toIngredient(3, 4)
			output {
				reversible = true
				addGroup {
					addStack { "sodium".toStack(6) }
					addStack { "calcium".toStack(2) }
					addStack { "aluminum".toStack(6) }
					addStack { "silicon".toStack(6) }
					addStack { "oxygen".toStack(24) }
					addStack { "sulfur".toStack(2) }
				}
			}
		})

		recipes.add(dissolverRecipe {
			input = Blocks.LAPIS_ORE.toIngredient()
			output {
				addGroup {
					addStack { "sodium".toStack(6 * 2) }
					addStack { "calcium".toStack(2 * 2) }
					addStack { "aluminum".toStack(6 * 2) }
					addStack { "silicon".toStack(6 * 2) }
					addStack { "oxygen".toStack(24 * 2) }
					addStack { "sulfur".toStack(2 * 2) }
				}
			}
		})

		recipes.add(
			dissolverRecipe {
				input = Blocks.LAPIS_BLOCK.toIngredient()
				output {
					addGroup {
						addStack { "sodium".toStack(6 * 3) }
						addStack { "calcium".toStack(2 * 3) }
						addStack { "aluminum".toStack(6 * 3) }
						addStack { "silicon".toStack(6 * 3) }
						addStack { "oxygen".toStack(24 * 3) }
						addStack { "sulfur".toStack(2 * 3) }
					}
				}
			})

		recipes.add(
			dissolverRecipe {
				input = Items.STRING.toIngredient()
				output {
					relativeProbability = false
					addGroup {
						probability = 50.0
						addStack { "protein".toStack() }
					}
				}
			})

		recipes.add(
			dissolverRecipe {
				input = ModItems.condensedMilk.toIngredient()
				output {
					relativeProbability = false
					addGroup { addStack { "calcium".toStack(4) }; probability = 40.0 }
					addGroup { addStack { "protein".toStack() }; probability = 20.0 }
					addGroup { addStack { "sucrose".toStack() }; probability = 20.0 }
				}
			})

		recipes.add(
			dissolverRecipe {
				input = Items.WHEAT.toIngredient()
				output {
					relativeProbability = false
					addGroup { addStack { "starch".toStack() }; probability = 5.0 }
					addGroup { addStack { "cellulose".toStack() }; probability = 25.0 }
				}
			})

		recipes.add(
			dissolverRecipe {
				input = Blocks.GRAVEL.toIngredient()
				output {
					addStack { "silicon_dioxide".toStack() }
				}
			})

		recipes.add(
			dissolverRecipe {
				input = Blocks.HAY_BLOCK.toIngredient()
				output {
					rolls = 9
					relativeProbability = false
					addGroup { addStack { "starch".toStack() }; probability = 5.0 }
					addGroup { addStack { "cellulose".toStack() }; probability = 25.0 }
				}
			})

		recipes.add(
			dissolverRecipe {
				input = Items.POTATO.toIngredient()
				output {
					relativeProbability = false
					addGroup { addStack { "starch".toStack() }; probability = 10.0 }
					addGroup { addStack { "potassium".toStack(5) }; probability = 25.0 }
				}
			})

		recipes.add(
			dissolverRecipe {
				input = Items.BAKED_POTATO.toIngredient()
				output {
					relativeProbability = false
					addGroup { addStack { "starch".toStack() }; probability = 10.0 }
					addGroup { addStack { "potassium".toStack(5) }; probability = 25.0 }
				}
			})

		recipes.add(
			dissolverRecipe {
				input = Items.REDSTONE.toIngredient()
				output {
					reversible = true
					addGroup {
						addStack { "iron_oxide".toStack() }
						addStack { "strontium_carbonate".toStack() }
					}
				}
			})

		recipes.add(
			dissolverRecipe {
				input = Blocks.REDSTONE_ORE.toIngredient()
				output {
					addGroup {
						addStack { "iron_oxide".toStack(4) }
						addStack { "strontium_carbonate".toStack(4) }
					}
				}
			})

		listOf(
			Items.BEEF,
			Items.COOKED_BEEF,
			Items.PORKCHOP,
			Items.COOKED_PORKCHOP,
			Items.MUTTON,
			Items.COOKED_MUTTON,
			Items.CHICKEN,
			Items.COOKED_CHICKEN,
			Items.RABBIT,
			Items.COOKED_RABBIT
		).forEach {
			recipes.add(dissolverRecipe {
				input = it.toIngredient()
				output {
					addStack { "protein".toStack(4) }
				}
			})
		}

		recipes.add(
			dissolverRecipe {
				input = Blocks.SPONGE.toIngredient()
				output {
					addGroup {
						addStack { "kaolinite".toStack(8) }
						addStack { "calcium_carbonate".toStack(8) }
					}
				}
			})

		(0..3).forEach {
			recipes.add(
				dissolverRecipe {
					input = Items.FISH.toIngredient(meta = it)
					output {
						addGroup {
							addStack { "protein".toStack(4) }
							addStack {
								(if(it != 3) "selenium" else "potassium_cyanide").toStack(if(it == 0) 2 else 4)
							}
						}
					}
				})
		}

		listOf(Items.LEATHER, Items.ROTTEN_FLESH).forEach {
			recipes.add(dissolverRecipe {
				input = it.toIngredient()
				output {
					addStack { "protein".toStack(3) }
				}
			})
		}

		recipes.add(
			dissolverRecipe {
				input = Items.CARROT.toIngredient()
				output {
					relativeProbability = false
					addGroup { addStack { "beta_carotene".toStack() }; probability = 20.0 }
				}
			})

		recipes.add(
			dissolverRecipe {
				input = "dyeRed".toOre()
				output {
					addStack { "mercury_sulfide".toStack(4) }
				}
			})

		recipes.add(
			dissolverRecipe {
				input = "dyePink".toOre()
				output {
					addStack { "arsenic_sulfide".toStack(4) }
				}
			})

		recipes.add(
			dissolverRecipe {
				input = "dyeGreen".toOre()
				output {
					addStack { "nickel_chloride".toStack(4) }
				}
			})

		recipes.add(
			dissolverRecipe {
				input = "dyeLime".toOre()
				output {
					addGroup {
						addStack { "cadmium_sulfide".toStack(2) }
						addStack { "chromium_oxide".toStack(2) }
					}
				}
			})

		recipes.add(
			dissolverRecipe {
				input = "dyePurple".toOre()
				output {
					addStack { "potassium_permanganate".toStack(4) }
				}
			})

		recipes.add(
			dissolverRecipe {
				input = "dyeYellow".toOre()
				output {
					addStack { "lead_iodide".toStack(4) }
				}
			})

		recipes.add(
			dissolverRecipe {
				input = "dyeOrange".toOre()
				output {
					addStack { "potassium_dichromate".toStack(4) }
				}
			})

		recipes.add(
			dissolverRecipe {
				input = "dyeBlack".toOre()
				output {
					addStack { "titanium_oxide".toStack(4) }
				}
			})

		recipes.add(
			dissolverRecipe {
				input = "dyeGray".toOre()
				output {
					addStack { "barium_sulfate".toStack(4) }
				}
			})

		recipes.add(
			dissolverRecipe {
				input = "dyeMagenta".toOre()
				output {
					addStack { "han_purple".toStack(4) }
				}
			})

		recipes.add(
			dissolverRecipe {
				input = "dyeLightBlue".toOre()
				output {
					addGroup {
						addStack { "cobalt_aluminate".toStack(2) }
						addStack { "antimony_trioxide".toStack(2) }
					}
				}
			})

		recipes.add(
			dissolverRecipe {
				input = "dyeLightGray".toOre()
				output {
					addStack { "magnesium_sulfate".toStack(4) }
				}
			})

		recipes.add(
			dissolverRecipe {
				input = "dyeCyan".toOre()
				output {
					addStack { "copper_chloride".toStack(4) }
				}
			})

		recipes.add(dissolverRecipe {
			input = Blocks.REDSTONE_BLOCK.toIngredient()
			output {
				addGroup {
					addStack { "iron_oxide".toStack(9) }
					addStack { "strontium_carbonate".toStack(9) }
				}
			}
		})

		recipes.add(
			dissolverRecipe {
				input = Items.SKULL.toIngredient(meta = 1) // wither skull
				output {
					addGroup {
						addStack { "hydroxylapatite".toStack(8) }
						addStack { "mendelevium".toStack(32) }
					}
				}
			})

		listOf(Blocks.PURPUR_BLOCK, Blocks.PURPUR_PILLAR).forEach {
			recipes.add(dissolverRecipe {
				input = it.toIngredient()
				output {
					relativeProbability = false
					addGroup {
						probability = 100.0
						addStack { "silicon_dioxide".toStack(4) }
					}
					addGroup {
						probability = 50.0
						addStack { "lutetium".toStack() }
					}
				}
			})
		}


		recipes.add(
			dissolverRecipe {
				input = "protein".toStack().toIngredient()
				output {
					addGroup {
						addStack { "carbon".toStack(3) };
						addStack { "hydrogen".toStack(7) }
						addStack { "nitrogen".toStack() }
						addStack { "oxygen".toStack(2) }
						addStack { "sulfur".toStack() }
					}
				}
			})


		recipes.add(
			dissolverRecipe {
				input = Blocks.CLAY.toIngredient()
				output {
					addStack { "kaolinite".toStack(4) }
				}
			})

		recipes.add(
			dissolverRecipe {
				input = Items.CLAY_BALL.toIngredient()
				reversible = true
				output {
					addStack { "kaolinite".toStack() }
				}
			})

		recipes.add(
			dissolverRecipe {
				input = Items.SUGAR.toIngredient()
				reversible = true
				output {
					addStack { "sucrose".toStack() }
				}
			})


		recipes.add(
			dissolverRecipe {
				input = Items.BEETROOT.toIngredient()
				output {
					relativeProbability = false
					addGroup {
						probability = 100.0
						addStack { "sucrose".toStack() }
					}
					addGroup {
						probability = 50.0
						addStack { "iron_oxide".toStack() }
					}
				}
			})

		recipes.add(
			dissolverRecipe {
				input = Items.BONE.toIngredient()
				reversible = true
				output {
					relativeProbability = false
					addGroup {
						probability = 50.0
						addStack { "hydroxylapatite".toStack(3) }
					}
				}
			})

		recipes.add(
			dissolverRecipe {
				input = Blocks.OBSIDIAN.toIngredient()
				output {
					addGroup {
						addStack { "magnesium_oxide".toStack(8) }
						addStack { "potassium_chloride".toStack(8) }
						addStack { "aluminum_oxide".toStack(8) }
						addStack { "silicon_dioxide".toStack(24) }
					}
				}
			})

		recipes.add(dissolverRecipe {
			input = Items.FEATHER.toIngredient()
			output {
				addStack { "protein".toStack(2) }
			}
		})

		recipes.add(
			dissolverRecipe {
				input = Items.DYE.toIngredient(meta = 15) //bonemeal
				output {
					relativeProbability = false
					addGroup {
						probability = 50.0
						addStack { "hydroxylapatite".toStack() }
					}
				}
			})

		recipes.add(
			dissolverRecipe {
				input = Blocks.BONE_BLOCK.toIngredient()
				output {
					rolls = 9
					relativeProbability = false
					addGroup {
						probability = 50.0
						addStack { "hydroxylapatite".toStack() }
					}
				}
			})

		recipes.add(
			dissolverRecipe {
				input = Items.EGG.toIngredient()
				reversible = true
				output {
					addGroup {
						addStack { "calcium_carbonate".toStack(8) }
						addStack { "protein".toStack(2) }
					}
				}
			})

		recipes.add(
			dissolverRecipe {
				input = ModItems.mineralSalt.toIngredient()
				output {
					addGroup { addStack { "sodium_chloride".toStack() }; probability = 60.0 }
					addGroup { addStack { "lithium".toStack() }; probability = 5.0 }
					addGroup { addStack { "potassium_chloride".toStack() }; probability = 10.0 }
					addGroup { addStack { "magnesium".toStack() }; probability = 10.0 }
					addGroup { addStack { "iron".toStack() }; probability = 5.0 }
					addGroup { addStack { "copper".toStack() }; probability = 4.0 }
					addGroup { addStack { "zinc".toStack() }; probability = 2.0 }
				}
			})

		recipes.add(
			dissolverRecipe {
				input = Items.COAL.toIngredient()
				output {
					addStack { "carbon".toStack(8) }
				}
			})

		recipes.add(
			dissolverRecipe {
				input = Items.COAL.toIngredient(meta = 1)
				output {
					addStack { "carbon".toStack(8) }
				}
			})

		recipes.add(
			dissolverRecipe {
				input = "slabWood".toOre()
				output {
					relativeProbability = false
					addGroup {
						probability = 12.0
						addStack { "cellulose".toStack() }
					}
				}
			})

		recipes.add(
			dissolverRecipe {
				input = "slimeball".toOre()
				reversible = true
				output {
					addGroup {
						addStack { "protein".toStack(2) }
						addStack { "sucrose".toStack(2) }
					}
				}
			})

		recipes.add(
			dissolverRecipe {
				input = "blockSlime".toOre()
				reversible = false
				output {
					addGroup {
						addStack { "protein".toStack(2 * 9) }
						addStack { "sucrose".toStack(2 * 9) }
					}
				}
			})

		recipes.add(
			dissolverRecipe {
				input = Items.STICK.toIngredient()
				output {
					relativeProbability = false
					addGroup {
						probability = 10.0
						addStack { "cellulose".toStack() }
					}
				}
			})

		recipes.add(
			dissolverRecipe {
				input = Blocks.TORCH.toIngredient()
				output {
					relativeProbability = false
					addGroup { addStack { "carbon".toStack(2) }; probability = 100.0 }
					addGroup { addStack { "cellulose".toStack() }; probability = 2.5 }
				}
			})

		recipes.add(
			dissolverRecipe {
				input = Blocks.LADDER.toIngredient()
				output {
					rolls = 7
					relativeProbability = false
					addGroup {
						probability = 10.0
						addStack { "cellulose".toStack() }
					}
				}
			})


		if(oreNotEmpty("itemSilicon")) {
			recipes.add(dissolverRecipe {
				input = "itemSilicon".toOre()
				output {
					addStack { "silicon".toStack(16) }
				}
			})
		}

		recipes.add(
			dissolverRecipe {
				input = Items.ENDER_PEARL.toIngredient()
				reversible = true
				output {
					addGroup {
						addStack { "silicon".toStack(16) }
						addStack { "mercury".toStack(16) }
						addStack { "neodymium".toStack(16) }
					}
				}
			})

		recipes.add(
			dissolverRecipe {
				input = Items.DIAMOND.toIngredient()
				output {
					addStack { "carbon".toStack(64 * 8) }
				}
			})


		recipes.add(
			dissolverRecipe {
				input = Blocks.DIAMOND_ORE.toIngredient()
				output {
					addStack { "carbon".toStack(64 * 8 * 2) }
				}
			})

		recipes.add(
			dissolverRecipe {
				input = Blocks.DIAMOND_BLOCK.toIngredient()
				output {
					addStack { "carbon".toStack(64 * 8 * 9) }
				}
			})

		recipes.add(
			dissolverRecipe {
				input = "plankWood".toOre()
				output {
					relativeProbability = false
					addGroup {
						probability = 25.0
						addStack { "cellulose".toStack() }
					}
				}
			})

		recipes.add(
			dissolverRecipe {
				input = "cobblestone".toOre()
				output {
					addGroup { addStack { ItemStack.EMPTY }; probability = 700.0 }
					addGroup { addStack { "aluminum".toStack() }; probability = 2.0 }
					addGroup { addStack { "iron".toStack() }; probability = 4.0 }
					addGroup { addStack { "gold".toStack() }; probability = 1.5 }
					addGroup { addStack { "silicon_dioxide".toStack() }; probability = 10.0 }
					addGroup { addStack { "dysprosium".toStack() }; probability = 1.0 }
					addGroup { addStack { "zirconium".toStack() }; probability = 1.5 }
					addGroup { addStack { "nickel".toStack() }; probability = 1.0 }
					addGroup { addStack { "gallium".toStack() }; probability = 1.0 }
					addGroup { addStack { "tungsten".toStack() }; probability = 1.0 }
				}
			})

		listOf("stoneGranite", "stoneGranitePolished").forEach {
			recipes.add(dissolverRecipe {
				input = it.toOre()
				output {
					addGroup { addStack { "aluminum_oxide".toStack() }; probability = 5.0 }
					addGroup { addStack { "iron".toStack() }; probability = 2.0 }
					addGroup { addStack { "potassium_chloride".toStack() }; probability = 2.0 }
					addGroup { addStack { "silicon_dioxide".toStack() }; probability = 10.0 }
					addGroup { addStack { "technetium".toStack() }; probability = 1.0 }
					addGroup { addStack { "manganese".toStack() }; probability = 1.5 }
					addGroup { addStack { "radium".toStack() }; probability = 1.5 }
				}
			})
		}

		listOf("stoneDiorite", "stoneDioritePolished").forEach {
			recipes.add(dissolverRecipe {
				input = it.toOre()
				output {
					addGroup { addStack { "aluminum_oxide".toStack() }; probability = 4.0 }
					addGroup { addStack { "iron".toStack() }; probability = 2.0 }
					addGroup { addStack { "potassium_chloride".toStack() }; probability = 4.0 }
					addGroup { addStack { "silicon_dioxide".toStack() }; probability = 10.0 }
					addGroup { addStack { "indium".toStack() }; probability = 1.5 }
					addGroup { addStack { "manganese".toStack() }; probability = 2.0 }
					addGroup { addStack { "osmium".toStack() }; probability = 2.0 }
					addGroup { addStack { "tin".toStack() }; probability = 3.0; }
				}
			})
		}

		recipes.add(
			dissolverRecipe {
				input = Blocks.MAGMA.toIngredient()
				output {
					rolls = 2
					addGroup { addStack { "manganese".toStack(2) }; probability = 10.0 }
					addGroup { addStack { "aluminum_oxide".toStack() }; probability = 5.0 }
					addGroup { addStack { "magnesium_oxide".toStack() }; probability = 20.0 }
					addGroup { addStack { "potassium_chloride".toStack() }; probability = 2.0 }
					addGroup { addStack { "silicon_dioxide".toStack(2) }; probability = 10.0 }
					addGroup { addStack { "sulfur".toStack(2) }; probability = 20.0 }
					addGroup { addStack { "iron_oxide".toStack() }; probability = 10.0 }
					addGroup { addStack { "lead".toStack(2) }; probability = 8.0 }
					addGroup { addStack { "fluorine".toStack() }; probability = 4.0 }
					addGroup { addStack { "bromine".toStack() }; probability = 4.0 }
				}
			})

		recipes.add(
			dissolverRecipe {
				input = "treeLeaves".toOre()
				output {
					relativeProbability = false
					addGroup {
						probability = 5.0
						addStack { "cellulose".toStack() }
					}
				}
			})

		listOf("stoneAndesite", "stoneAndesitePolished").forEach {
			recipes.add(dissolverRecipe {
				input = it.toOre()
				output {
					addGroup { addStack { "aluminum_oxide".toStack() }; probability = 4.0 }
					addGroup { addStack { "iron".toStack() }; probability = 3.0 }
					addGroup { addStack { "potassium_chloride".toStack() }; probability = 4.0 }
					addGroup { addStack { "silicon_dioxide".toStack() }; probability = 10.0 }
					addGroup { addStack { "platinum".toStack() }; probability = 2.0 }
					addGroup { addStack { "calcium".toStack() }; probability = 4.0 }
				}
			})
		}

		recipes.add(
			dissolverRecipe {
				input = "stone".toOre()
				output {
					addGroup { addStack { ItemStack.EMPTY }; probability = 20.0 }
					addGroup { addStack { "aluminum".toStack() }; probability = 2.0 }
					addGroup { addStack { "iron".toStack() }; probability = 4.0 }
					addGroup { addStack { "gold".toStack() }; probability = 1.5 }
					addGroup { addStack { "silicon_dioxide".toStack() }; probability = 20.0 }
					addGroup { addStack { "dysprosium".toStack() }; probability = 0.5 }
					addGroup { addStack { "zirconium".toStack() }; probability = 1.25 }
					addGroup { addStack { "tungsten".toStack() }; probability = 1.0 }
					addGroup { addStack { "nickel".toStack() }; probability = 1.0 }
					addGroup { addStack { "gallium".toStack() }; probability = 1.0 }
				}
			})

		recipes.add(
			dissolverRecipe {
				input = Blocks.SAND.toIngredient()
				output {
					relativeProbability = false
					addGroup { addStack { "silicon_dioxide".toStack(4) }; probability = 100.0 }
					addGroup { addStack { "gold".toStack() } }
				}
			})

		recipes.add(
			dissolverRecipe {
				input = Blocks.SAND.toIngredient(meta = 1) //red sand
				output {
					relativeProbability = false
					addGroup { addStack { "silicon_dioxide".toStack(4) }; probability = 100.0 }
					addGroup { addStack { "iron_oxide".toStack() }; probability = 10.0 }
				}
			})

		(0..1).forEach {
			recipes.add(dissolverRecipe {
				input = Blocks.RED_SANDSTONE.toIngredient(meta = it)
				output {
					rolls = 4
					relativeProbability = false
					addGroup { addStack { "silicon_dioxide".toStack(4) }; probability = 100.0 }
					addGroup { addStack { "iron_oxide".toStack() }; probability = 10.0 }
				}
			})
		}


		recipes.add(dissolverRecipe {
			input = Items.GUNPOWDER.toIngredient()
			reversible = true
			output {
				addGroup {
					addStack { "potassium_nitrate".toStack(2) }
					addStack { "sulfur".toStack(8) }
					addStack { "carbon".toStack(8) }
				}
			}
		})

		recipes.add(dissolverRecipe {
			input = "logWood".toOre()
			output {
				addStack { "cellulose".toStack() }
			}
		})

		metalOreData.forEach { data ->
			(0..<data.size).forEach { index ->
				val elementName = data.strs[index]
				val oreName = data.toDictName(index)
				val meta: Int = when(elementName) {
					"aluminium" -> ElementRegistry.getMeta("aluminum")
					"caesium" -> ElementRegistry.getMeta("caesium")
					else -> ElementRegistry.getMeta(elementName)
				}
				if(OreDictionary.doesOreNameExist(oreName) && OreDictionary.getOres(oreName).isNotEmpty()) {
					recipes.add(dissolverRecipe {
						input = oreName.toOre()
						output {
							addGroup {
								addStack {
									ModItems.elements.toStack(data.quantity, meta = meta)
								}
								if(oreName == "oreIron") {
									addStack {
										ModItems.elements.toStack(
											2, meta = ElementRegistry["tungsten"]!!.meta
										)
									}
									addStack {
										ModItems.elements.toStack(
											4, meta = ElementRegistry["sulfur"]!!.meta
										)
									}
								} else if(oreName == "oreGold") {
									addStack {
										ModItems.elements.toStack(
											2, meta = ElementRegistry["copper"]!!.meta
										)
									}
									addStack {
										ModItems.elements.toStack(
											2, meta = ElementRegistry["silver"]!!.meta
										)
									}
								}
							}
						}
					})
				}
			}
		}

		recipes.add(
			dissolverRecipe {
				input = Items.GLOWSTONE_DUST.toIngredient()
				reversible = true
				output {
					addStack { "phosphorus".toStack(4) }
				}
			})

		recipes.add(
			dissolverRecipe {
				input = Blocks.GLOWSTONE.toIngredient()
				output {
					addStack { "phosphorus".toStack(16) }
				}
			})

		recipes.add(
			dissolverRecipe {
				input = Blocks.IRON_BARS.toIngredient()
				output {
					addStack { "iron".toStack(6) }
				}
			})

		recipes.add(
			dissolverRecipe {
				input = Items.BLAZE_POWDER.toIngredient()
				reversible = true
				output {
					addGroup {
						addStack { "germanium".toStack(8) }
						addStack { "carbon".toStack(8) }
						addStack { "sulfur".toStack(8) }
					}
				}
			})

		recipes.add(
			dissolverRecipe {
				input = Items.NETHER_WART.toIngredient()
				reversible = true
				output {
					addGroup {
						addStack { "cellulose".toStack() }
						addStack { "germanium".toStack(4) }
						addStack { "selenium".toStack(4) }
					}
				}
			})

		recipes.add(
			dissolverRecipe {
				input = Blocks.NETHER_WART_BLOCK.toIngredient()
				output {
					addGroup {
						addStack { "cellulose".toStack(9) }
						addStack { "germanium".toStack(4 * 9) }
						addStack { "selenium".toStack(4 * 9) }
					}
				}
			})

		if(oreNotEmpty("dropHoney")) {
			recipes.add(dissolverRecipe {
				input = "dropHoney".toOre()
				output {
					addStack { "sucrose".toStack(4) }
				}
			})
		}

		if(oreNotEmpty("gemPrismarine")) {
			recipes.add(dissolverRecipe {
				input = "gemPrismarine".toOre()
				reversible = true
				output {
					addGroup {
						addStack { "beryl".toStack(2) }
						addStack { "cobalt_aluminate".toStack(4) }
					}
				}
			})
		}

		listOf("ingotBronze", "plateBronze", "dustBronze", "blockBronze").filter { oreNotEmpty(it) }.forEach {
			recipes.add(dissolverRecipe {
				input = it.toOre()
				output {
					addGroup {
						addStack { "copper".toStack(if(it == "blockBronze") 9 * 12 else 12) }
						addStack { "tin".toStack(if(it == "blockBronze") 9 * 4 else 4) }
					}
				}
			})
		}

		listOf("ingotElectrum", "plateElectrum", "dustElectrum", "blockElectrum").filter { oreNotEmpty(it) }.forEach {
			recipes.add(dissolverRecipe {
				input = it.toOre()
				output {
					addGroup {
						addStack { "gold".toStack(if(it == "blockElectrum") 9 * 8 else 8) }
						addStack { "silver".toStack(if(it == "blockElectrum") 9 * 8 else 8) }
					}
				}
			})
		}

		listOf("gemRuby", "dustRuby", "plateRuby").filter { oreNotEmpty(it) }.forEach { ore ->
			recipes.add(dissolverRecipe {
				input = ore.toOre()
				output {
					addGroup {
						addStack { "aluminum_oxide".toStack(16) }
						addStack { "chromium".toStack(8) }
					}
				}
			})
		}

		listOf("gemSapphire", "dustSapphire", "plateSapphire").filter { oreNotEmpty(it) }.forEach { ore ->
			recipes.add(dissolverRecipe {
				input = ore.toOre()
				output {
					addGroup {
						addStack { "aluminum_oxide".toStack(16) }
						addStack { "iron".toStack(4) }
						addStack { "titanium".toStack(4) }
					}
				}
			})
		}

		recipes.add(dissolverRecipe {
			input = Blocks.MELON_BLOCK.toIngredient()
			output {
				relativeProbability = false
				addGroup {
					probability = 50.0
					addStack { "cucurbitacin".toStack(); }
				}
				addGroup {
					probability = 1.0
					addStack { "water".toStack(4) }
					addStack { "sucrose".toStack(2) }
				}
			}
		})

		recipes.add(
			dissolverRecipe {
				input = "blockCactus".toOre()
				reversible = true
				output {
					relativeProbability = false
					addGroup { addStack { "cellulose".toStack() }; probability = 100.0 }
					addGroup { addStack { "mescaline".toStack() }; probability = 50.0 }
				}
			})

		recipes.add(
			dissolverRecipe {
				input = Blocks.HARDENED_CLAY.toIngredient()
				reversible = true
				output {
					addStack { "mullite".toStack(2) }
				}
			})

		(0..15).forEach {
			recipes.add(dissolverRecipe {
				input = Blocks.STAINED_HARDENED_CLAY.toIngredient(meta = it)
				reversible = false
				output {
					addStack { "mullite".toStack(2) }
				}
			})
		}

		listOf(
			Blocks.BLACK_GLAZED_TERRACOTTA,
			Blocks.BLUE_GLAZED_TERRACOTTA,
			Blocks.BROWN_GLAZED_TERRACOTTA,
			Blocks.CYAN_GLAZED_TERRACOTTA,
			Blocks.GRAY_GLAZED_TERRACOTTA,
			Blocks.GREEN_GLAZED_TERRACOTTA,
			Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA,
			Blocks.LIME_GLAZED_TERRACOTTA,
			Blocks.MAGENTA_GLAZED_TERRACOTTA,
			Blocks.ORANGE_GLAZED_TERRACOTTA,
			Blocks.PINK_GLAZED_TERRACOTTA,
			Blocks.PURPLE_GLAZED_TERRACOTTA,
			Blocks.RED_GLAZED_TERRACOTTA,
			Blocks.SILVER_GLAZED_TERRACOTTA,
			Blocks.WHITE_GLAZED_TERRACOTTA,
			Blocks.YELLOW_GLAZED_TERRACOTTA
		).forEach {
			recipes.add(dissolverRecipe {
				input = it.toIngredient()
				reversible = false
				output {
					addStack { "mullite".toStack(2) }
				}
			})
		}

		if(oreNotEmpty("cropRice")) {
			recipes.add(dissolverRecipe {
				input = "cropRice".toOre()
				output {
					addGroup {
						relativeProbability = false
						probability = 10.0
						addStack { "starch".toStack() }
					}
				}
			})
		}

		recipes.removeIf { recipe -> recipe.input == null || recipe.input!!.matchingStacks.isEmpty() }
	}

	data class DissolverOreData(val prefix: String, val quantity: Int, val strs: List<String>) {
		fun toDictName(index: Int) = "$prefix${strs[index].replaceFirstChar(Char::uppercaseChar)}"
		val size = strs.size
	}
}
