package io.enderdev.alchemistry.network

import io.enderdev.alchemistry.tiles.AbstractMachine
import io.enderdev.alchemistry.tiles.TileChemicalCombiner
import io.enderdev.alchemistry.tiles.TileFusionController
import io.netty.buffer.ByteBuf
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

class ButtonPacket() : IMessage {
    private var blockPos: BlockPos? = null
    private var pause = false
    private var lock = false
    private var single = false

    override fun fromBytes(buf: ByteBuf) {
        this.blockPos = BlockPos(buf.readInt(), buf.readInt(), buf.readInt())
        this.pause = buf.readBoolean()
        this.lock = buf.readBoolean()
        this.single = buf.readBoolean()
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(blockPos!!.x)
        buf.writeInt(blockPos!!.y)
        buf.writeInt(blockPos!!.z)
        buf.writeBoolean(this.pause)
        buf.writeBoolean(this.lock)
        buf.writeBoolean(this.single)
    }

    constructor(pos: BlockPos, pause: Boolean = false, lock: Boolean = false, single: Boolean = false) : this() {
        this.blockPos = pos
        this.pause = pause
        this.lock = lock
        this.single = single
    }

    class Handler : IMessageHandler<ButtonPacket, IMessage> {
        override fun onMessage(message: ButtonPacket, ctx: MessageContext): IMessage? {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask { handle(message, ctx) }
            return null
        }

        private fun handle(message: ButtonPacket?, ctx: MessageContext?) {
            val playerEntity = ctx!!.serverHandler.player
            val tile = playerEntity.world.getTileEntity(message!!.blockPos!!)

            if (tile is AbstractMachine<*> && message.pause) {
                tile.isPaused = !(tile.isPaused)
            }
            if (tile is TileChemicalCombiner && message.lock) {
                tile.recipeIsLocked = !(tile.recipeIsLocked)
                if (!tile.recipeIsLocked) tile.currentRecipe = null
            }
            if (tile is TileFusionController && message.single) {
                tile.singleMode = !(tile.singleMode)
            }
        }
    }
}