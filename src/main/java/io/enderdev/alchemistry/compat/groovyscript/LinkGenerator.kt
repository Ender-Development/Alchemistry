package io.enderdev.alchemistry.compat.groovyscript

import com.cleanroommc.groovyscript.documentation.linkgenerator.BasicLinkGenerator
import io.enderdev.alchemistry.Tags

class LinkGenerator : BasicLinkGenerator() {
	override fun id(): String? {
		return Tags.MOD_ID
	}

	override fun domain(): String? {
		return "https://github.com/Ender-Development/Alchemistry/"
	}
}
