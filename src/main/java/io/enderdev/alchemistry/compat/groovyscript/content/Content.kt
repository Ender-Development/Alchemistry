package io.enderdev.alchemistry.compat.groovyscript.content

import com.cleanroommc.groovyscript.api.GroovyBlacklist
import com.cleanroommc.groovyscript.api.INamed
import com.cleanroommc.groovyscript.api.documentation.annotations.Example
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription
import com.cleanroommc.groovyscript.sandbox.GroovyLogImpl
import io.enderdev.alchemistry.Tags
import io.enderdev.alchemistry.chemistry.CompoundPair
import io.enderdev.alchemistry.chemistry.CompoundRegistry
import io.enderdev.alchemistry.chemistry.ElementRegistry
import io.enderdev.alchemistry.proxy.CommonProxy
import java.awt.Color
import java.util.*
import kotlin.math.roundToInt

@RegistryDescription(linkGenerator = Tags.MOD_ID)
class Content : INamed {

	@MethodDescription(type = MethodDescription.Type.ADDITION, example = [Example("200, 'Advanced Hydrogen', 'Ah', 255, 255, 255", commented = true)])
	fun createElement(atomicNumber: Int, name: String, abbreviation: String, red: Int, green: Int, blue: Int) {
		if(CommonProxy.getStage() != CommonProxy.LoadingStage.PRE_INIT) {
			GroovyLogImpl.LOG.info("Element creation is only allowed during preInit stage.")
			return
		}
		if(ElementRegistry.getAllElements().isEmpty()) {
			ElementRegistry.init()
		}
		val parsedName = name.trim().lowercase(Locale.getDefault()).replace(" ", "_")
		if(ElementRegistry[atomicNumber] == null) {
			ElementRegistry.add(
				atomicNumber,
				parsedName,
				abbreviation,
				Color(red.coerceIn(0, 255), green.coerceIn(0, 255), blue.coerceIn(0, 255))
			)
		}
	}

	@GroovyBlacklist
	fun createCompoundInternal(meta: Int, name: String, color: Color?, components: ArrayList<ArrayList<Any?>>) {
		if(CommonProxy.getStage() != CommonProxy.LoadingStage.PRE_INIT) {
			GroovyLogImpl.LOG.info("Compound creation is only allowed during preInit stage.")
			return
		}
		if(ElementRegistry.getAllElements().isEmpty())
			ElementRegistry.init()

		if(CompoundRegistry.compounds().isEmpty())
			CompoundRegistry.init()

		val parsedName = name.trim().lowercase(Locale.getDefault()).replace(" ", "_")
		if(CompoundRegistry[parsedName] == null) {
			val parsedComponents = components.map { x: ArrayList<Any?> ->
				if(x.size != 2) {
					GroovyLogImpl.LOG.error("Invalid component format for $parsedName")
					return
				}
				if(ElementRegistry[x[0] as String] == null && CompoundRegistry[x[0] as String] == null) {
					GroovyLogImpl.LOG.error("Unknown component ${x[0]} for $parsedName")
					GroovyLogImpl.LOG.error("If you are using a custom element or compound, make sure it is created before this compound.")
					return
				}
				CompoundPair(
					(x[0] as String).lowercase(Locale.getDefault()).replace(" ", "_"), x[1] as Int
				)
			}
			var realColor = color
			if(color == null) {
				var r = 0
				var g = 0
				var b = 0
				parsedComponents.forEach {
					val color = it.compound.color
					r += color.red * it.quantity
					g += color.green * it.quantity
					b += color.blue * it.quantity
				}
				val size = parsedComponents.size.toDouble()
				realColor = Color((r / size).roundToInt(), (g / size).roundToInt(), (b / size).roundToInt())
			}
			CompoundRegistry.addExternal(
				meta,
				parsedName,
				realColor,
				parsedComponents
			)
		}
	}

	@MethodDescription(
		type = MethodDescription.Type.ADDITION,
		example = [Example("200, 'Advanced Hydrogen Molecule', 205, 205, 205, [['advanced_hydrogen', 1],['advanced_hydrogen', 1],['advanced_hydrogen', 1]]", commented = true)]
	)
	fun createCompound(meta: Int, name: String, red: Int, green: Int, blue: Int, components: ArrayList<ArrayList<Any?>>) =
		createCompoundInternal(meta, name, Color(red.coerceIn(0, 255), green.coerceIn(0, 255), blue.coerceIn(0, 255)), components)

	/**
	 * In comparision to the method above, this method does not require the color of the compound to be set.
	 * The color will be the weighted average of the colors of the components.
	 */
	@MethodDescription(
		type = MethodDescription.Type.ADDITION,
		example = [Example("200, 'Advanced Hydrogen Molecule', [['advanced_hydrogen', 1],['advanced_hydrogen', 1],['advanced_hydrogen', 1]]", commented = true)]
	)
	fun createCompound(meta: Int, name: String, components: ArrayList<ArrayList<Any?>>) =
		createCompoundInternal(meta, name, null, components)

	override fun getAliases() = listOf("content", "Content")
}
