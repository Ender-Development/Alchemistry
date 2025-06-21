package io.enderdev.alchemistry.recipes

import com.google.common.collect.ImmutableList
import io.enderdev.catalyx.utils.extensions.areStacksEqualIgnoreQuantity
import io.enderdev.catalyx.utils.extensions.toImmutable
import net.minecraft.item.ItemStack
import java.util.*

data class ProbabilityGroup(
	private val _output: List<ItemStack>,
	val probability: Double = 1.0
) {

	val output: List<ItemStack>
		get(): List<ItemStack> = _output.toImmutable()
}

data class ProbabilitySet(
	private var _set: List<ProbabilityGroup>? = ArrayList(),
	val relativeProbability: Boolean = true,
	val rolls: Int = 1
) {

	val set: ImmutableList<ProbabilityGroup>
		get() = ImmutableList.copyOf(_set!!.sortedBy { -it.probability })

	fun toStackList(): List<ItemStack> {
		val temp = ImmutableList.Builder<ImmutableList<ItemStack>>()
		set.forEach { temp.add(ImmutableList.copyOf(it.output)) }
		return temp.build().flatten().toImmutable()
	}

	fun probabilityAtIndex(index: Int): Double {
		return if(relativeProbability) (set[index].probability / set.sumOf { it.probability })
		else set[index].probability
	}

	fun calculateOutput(): List<ItemStack> {
		val temp = ArrayList<ItemStack>()
		val rando = Random()
		(1..rolls).forEach { _ ->
			if(relativeProbability) {
				val totalProbability = set.sumOf { it.probability }
				val targetProbability = rando.nextDouble()
				var trackingProbability = 0.0

				for(component in set) {
					trackingProbability += (component.probability / totalProbability)
					if(trackingProbability >= targetProbability) {
						component.output.filterNot { it.isEmpty }.forEach { x ->
							val stack: ItemStack = x.copy()
							val index = temp.indexOfFirst { stack.areStacksEqualIgnoreQuantity(it) }
							if(index != -1) temp[index].grow(stack.count)
							else temp.add(stack)
						}
						break
					}
				}
			} else { //absolute probability
				for(component in set) {
					if(component.probability >= rando.nextInt(101)) {
						component.output.filterNot { it.isEmpty }.forEach { x ->
							val stack: ItemStack = x.copy()
							val index = temp.indexOfFirst { stack.areStacksEqualIgnoreQuantity(it) }
							if(index != -1) temp[index].grow(stack.count)
							else temp.add(stack)
						}
					}
				}
			}
		}
		return temp
	}
}
