package al132.alchemistry

import com.cleanroommc.configanytime.ConfigAnytime
import net.minecraftforge.common.config.Config
import net.minecraftforge.common.config.ConfigManager
import net.minecraftforge.fml.client.event.ConfigChangedEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/**
 * Created by al132 on 4/28/2017.
 */

@Config(modid = Reference.MODID, name = Reference.MODID + "/" + Reference.MODID)
object ConfigHandler {

    @JvmField
    @Config.Name("General")
    @Config.LangKey("config.alchemistry.general")
    val GENERAL = General()

    class General {
        @JvmField
        @Config.RequiresMcRestart
        @Config.Name("Family Friendly Mode")
        @Config.Comment("Illegal drug compounds will have their names replaced with more family-friendly versions")
        var familyFriendlyMode = false

        @JvmField
        @Config.RequiresMcRestart
        @Config.Name("Enable Automation")
        @Config.Comment("Enables item automation with hoppers, pipes, etc")
        var enableAutomation = true
    }

    @JvmField
    @Config.Name("Fission Reactor")
    @Config.LangKey("config.alchemistry.fission")
    val FISSION = Fission()

    class Fission {
        @JvmField
        @Config.RequiresMcRestart
        @Config.Name("Energy Capacity")
        @Config.Comment("Max energy capacity of the Fission Multiblock")
        @Config.RangeInt(min = 1, max = Integer.MAX_VALUE)
        var energyCapacity = 50000

        @JvmField
        @Config.RequiresMcRestart
        @Config.Name("Energy per Tick")
        @Config.Comment("Max energy capacity of the Fission Multiblock")
        @Config.RangeInt(min = 1, max = Integer.MAX_VALUE)
        var energyPerTick = 300

        @JvmField
        @Config.RequiresMcRestart
        @Config.Name("Processing Ticks")
        @Config.Comment("Max energy capacity of the Fission Multiblock")
        @Config.RangeInt(min = 1, max = Integer.MAX_VALUE)
        var processingTicks = 40

        @JvmField
        @Config.Name("Compact Fission Reactor")
        @Config.Comment(
            "If true, Fission Reactors can share casing blocks with adjacent reactors",
            "This allows up to 4 Fission Reactors to share a single set of casing blocks",
            "or for rows of reactors to share a wall of casing blocks."
        )
        var compactFissionReactor = false

        @JvmField
        @Config.Name("Fission Reactor Modifiers")
        @Config.Comment("List of fluid modifiers for the Fission Reactor. Syntax: fluidName;productivity;speed;energy")
        var fissionReactorModifiers = arrayOf(
            "water;0.0;0.02;-0.05",
            "lava;0.2;-0.05;0.1"
        )
    }

    @JvmField
    @Config.Name("Fusion Reactor")
    @Config.LangKey("config.alchemistry.fusion")
    val FUSION = Fusion()

    class Fusion {
        @JvmField
        @Config.RequiresMcRestart
        @Config.Name("Energy Capacity")
        @Config.Comment("Max energy capacity of the Fusion Multiblock")
        @Config.RangeInt(min = 1, max = Integer.MAX_VALUE)
        var energyCapacity = 50000

        @JvmField
        @Config.RequiresMcRestart
        @Config.Name("Energy per Tick")
        @Config.Comment("Max energy capacity of the Fusion Multiblock")
        @Config.RangeInt(min = 1, max = Integer.MAX_VALUE)
        var energyPerTick = 300

        @JvmField
        @Config.RequiresMcRestart
        @Config.Name("Processing Ticks")
        @Config.Comment("Max energy capacity of the Fusion Multiblock")
        @Config.RangeInt(min = 1, max = Integer.MAX_VALUE)
        var processingTicks = 40

        @JvmField
        @Config.Name("Compact Fusion Reactor")
        @Config.Comment(
            "If true, Fusion Reactors can share casing blocks with adjacent reactors",
            "This allows up to 4 Fusion Reactors to share a single set of casing blocks",
            "or for rows of reactors to share a wall of casing blocks."
        )
        var compactFusionReactor = false

        @JvmField
        @Config.Name("Fusion Reactor Modifiers")
        @Config.Comment("List of fluid modifiers for the Fusion Reactor. Syntax: fluidName;productivity;speed;energy")
        var fusionReactorModifiers = arrayOf(
            "water;0.0;0.02;-0.03",
            "lava;0.2;-0.05,0.1"
        )
    }

    @JvmField
    @Config.Name("Combiner")
    @Config.LangKey("config.alchemistry.combiner")
    val COMBINER = Combiner()

    class Combiner {
        @JvmField
        @Config.RequiresMcRestart
        @Config.Name("Energy Capacity")
        @Config.Comment("Max energy capacity of the Combiner")
        @Config.RangeInt(min = 1, max = Integer.MAX_VALUE)
        var energyCapacity = 10000

        @JvmField
        @Config.RequiresMcRestart
        @Config.Name("Energy per Tick")
        @Config.Comment("Energy consumption rate per tick for the Combiner")
        @Config.RangeInt(min = 0, max = Integer.MAX_VALUE)
        var energyPerTick = 200

        @JvmField
        @Config.RequiresMcRestart
        @Config.Name("Processing Ticks")
        @Config.Comment("Number of ticks per operation for the Combiner")
        @Config.RangeInt(min = 1, max = Integer.MAX_VALUE)
        var processingTicks = 5
    }

    @JvmField
    @Config.Name("Dissolver")
    @Config.LangKey("config.alchemistry.dissolver")
    val DISSOLVER = Dissolver()

    class Dissolver {
        @JvmField
        @Config.RequiresMcRestart
        @Config.Name("Energy Capacity")
        @Config.Comment("Max energy capacity of the Dissolver")
        @Config.RangeInt(min = 1, max = Integer.MAX_VALUE)
        var energyCapacity = 10000

        @JvmField
        @Config.RequiresMcRestart
        @Config.Name("Energy per Tick")
        @Config.Comment("Energy consumption rate per tick for the Dissolver")
        @Config.RangeInt(min = 0, max = Integer.MAX_VALUE)
        var energyPerTick = 100

        @JvmField
        @Config.RequiresMcRestart
        @Config.Name("Speed")
        @Config.Comment("The max amount of items that the Dissolver will output each tick. Please note: only one element will be outputted per tick, and only the elements from one input are eligible at a time. For example: Cellulose (C6 H10 O5) with speed 4 would be outputted like so, with each comma-seperated value representing 1 tick [4xC,2xC,4xH,4xH,2xH,4xO,1xO]")
        @Config.RangeInt(min = 1, max = 64)
        var speed = 8
    }

    @JvmField
    @Config.Name("Electrolyzer")
    @Config.LangKey("config.alchemistry.electrolyzer")
    val ELECTROLYZER = Electrolyzer()

    class Electrolyzer {
        @JvmField
        @Config.RequiresMcRestart
        @Config.Name("Energy Capacity")
        @Config.Comment("Max energy capacity of the Electrolyzer")
        @Config.RangeInt(min = 1, max = Integer.MAX_VALUE)
        var energyCapacity = 10000

        @JvmField
        @Config.RequiresMcRestart
        @Config.Name("Energy per Tick")
        @Config.Comment("Energy consumption rate per tick for the Electrolyzer")
        @Config.RangeInt(min = 0, max = Integer.MAX_VALUE)
        var energyPerTick = 100

        @JvmField
        @Config.RequiresMcRestart
        @Config.Name("Processing Ticks")
        @Config.Comment("Number of ticks per Electrolyzer operation")
        @Config.RangeInt(min = 1, max = Integer.MAX_VALUE)
        var processingTicks = 10
    }

    @JvmField
    @Config.Name("Evaporator")
    @Config.LangKey("config.alchemistry.evaporator")
    val EVAPORATOR = Evaporator()

    class Evaporator {
        @JvmField
        @Config.RequiresMcRestart
        @Config.Name("Processing Ticks")
        @Config.Comment("The best possible processing time for the Evaporator. In practice it will be increased by biome, time of day, etc")
        @Config.RangeInt(min = 1, max = Integer.MAX_VALUE)
        var processingTicks = 160
    }

    @JvmField
    @Config.Name("Atomizer")
    @Config.LangKey("config.alchemistry.atomizer")
    val ATOMIZER = Atomizer()

    class Atomizer {
        @JvmField
        @Config.RequiresMcRestart
        @Config.Name("Energy Capacity")
        @Config.Comment("Max energy capacity of the Atomizer")
        @Config.RangeInt(min = 1, max = Integer.MAX_VALUE)
        var energyCapacity = 10000

        @JvmField
        @Config.RequiresMcRestart
        @Config.Name("Energy per Tick")
        @Config.Comment("Energy consumption rate per tick for the Atomizer")
        @Config.RangeInt(min = 0, max = Integer.MAX_VALUE)
        var energyPerTick = 50

        @JvmField
        @Config.RequiresMcRestart
        @Config.Name("Processing Ticks")
        @Config.Comment("Number of ticks per Atomizer operation")
        @Config.RangeInt(min = 1, max = Integer.MAX_VALUE)
        var processingTicks = 100
    }

    @JvmField
    @Config.Name("Liquifier")
    @Config.LangKey("config.alchemistry.liquifier")
    val LIQUIFIER = Liquifier()

    class Liquifier {
        @JvmField
        @Config.RequiresMcRestart
        @Config.Name("Energy Capacity")
        @Config.Comment("Max energy capacity of the Liquifier")
        @Config.RangeInt(min = 1, max = Integer.MAX_VALUE)
        var energyCapacity = 10000

        @JvmField
        @Config.RequiresMcRestart
        @Config.Name("Energy per Tick")
        @Config.Comment("Energy consumption rate per tick for the Liquifier")
        @Config.RangeInt(min = 0, max = Integer.MAX_VALUE)
        var energyPerTick = 50

        @JvmField
        @Config.RequiresMcRestart
        @Config.Name("Processing Ticks")
        @Config.Comment("Number of ticks per Liquifier operation")
        @Config.RangeInt(min = 1, max = Integer.MAX_VALUE)
        var processingTicks = 100
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    object ConfigEventHandler {
        @SubscribeEvent
        @JvmStatic
        fun onConfigChangedEvent(event: ConfigChangedEvent.OnConfigChangedEvent) {
            if (event.modID == Reference.MODID) {
                ConfigManager.sync(Reference.MODID, Config.Type.INSTANCE)
            }
        }
    }

    init {
        ConfigAnytime.register(ConfigHandler::class.java)
    }
}