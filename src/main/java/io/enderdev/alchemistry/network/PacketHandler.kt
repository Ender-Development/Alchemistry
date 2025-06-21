package io.enderdev.alchemistry.network

import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper
import net.minecraftforge.fml.relauncher.Side

object PacketHandler {
	private var packetId = 0
	var INSTANCE: SimpleNetworkWrapper? = null

	fun nextID(): Int = packetId++

	fun registerMessages(channelName: String) {
		INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(channelName)
		registerMessages()
	}

	fun registerMessages() {
		INSTANCE!!.registerMessage(
			ChemicalCombinerTransferPacket.Handler::class.java, ChemicalCombinerTransferPacket::class.java, nextID(), Side.SERVER
		)
	}
}
