package io.enderdev.alchemistry.tiles

import io.enderdev.alchemistry.ConfigHandler
import io.enderdev.alchemistry.tiles.tags.IEnergyTile
import io.enderdev.alchemistry.tiles.tags.IFluidTile
import io.enderdev.alchemistry.tiles.tags.IItemTile
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.energy.CapabilityEnergy
import net.minecraftforge.energy.EnergyStorage
import net.minecraftforge.energy.IEnergyStorage
import net.minecraftforge.fluids.FluidUtil
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable
import net.minecraftforge.items.wrapper.CombinedInvWrapper

abstract class TileBase : TileEntity() {
    var inputSlots = 0
    var outputSlots = 0
    open val SIZE
        get() = inventory.slots
    var dirtyTicks = 0

    lateinit var input: TileStackHandler
    protected lateinit var automationInput: IItemHandlerModifiable
    lateinit var output: TileStackHandler
    protected lateinit var automationOutput: IItemHandlerModifiable

    open val inventory: IItemHandler
        get() = CombinedInvWrapper(input, output)

    open val automationInvHandler: CombinedInvWrapper
        get() = CombinedInvWrapper(automationInput, automationOutput)

    fun canInteractWith(player: EntityPlayer) = !isInvalid && player.getDistanceSq(pos.add(.5, .5, .5)) <= 64

    override fun shouldRefresh(world: World, pos: BlockPos, oldState: IBlockState, newState: IBlockState): Boolean {
        return oldState.block != newState.block;
    }

    fun initInventoryCapability(inputSlots: Int, outputSlots: Int) {
        this.inputSlots = inputSlots
        this.outputSlots = outputSlots

        initInventoryInputCapability()
        automationInput = object : WrappedItemHandler(input) {
            override fun extractItem(slot: Int, amount: Int, simulate: Boolean) = ItemStack.EMPTY
        }

        output = object : TileStackHandler(outputSlots, this) {
            override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean) = stack
        }

        automationOutput = object : WrappedItemHandler(output) {
            override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
                return if (!getStackInSlot(slot).isEmpty) super.extractItem(slot, amount, simulate)
                else ItemStack.EMPTY
            }
        }
    }

    open fun initInventoryInputCapability() {
        input = TileStackHandler(inputSlots, this)
    }

    override fun getUpdateTag(): NBTTagCompound = writeToNBT(NBTTagCompound())

    override fun getUpdatePacket(): SPacketUpdateTileEntity = SPacketUpdateTileEntity(pos, 0, updateTag)

    override fun onDataPacket(net: NetworkManager, pkt: SPacketUpdateTileEntity) {
        readFromNBT(pkt.nbtCompound)
    }

    fun markDirtyClient() {
        markDirty()
        getWorld().let { world ->
            val state = world.getBlockState(getPos())
            world.notifyBlockUpdate(getPos(), state, state, 3)
        }
    }

    fun markDirtyClientEvery(ticks: Int) {
        dirtyTicks++
        if (dirtyTicks >= ticks) {
            markDirtyClient()
            dirtyTicks = 0
        }
    }

    fun markDirtyEvery(ticks: Int) {
        dirtyTicks++
        if (dirtyTicks >= ticks) {
            markDirty()
            dirtyTicks = 0
        }
    }

    fun markDirtyGUI() {
        markDirty()
        world?.let {
            val state = world.getBlockState(getPos())
            world.notifyBlockUpdate(pos, state, state, 6)
        }
    }

    fun markDirtyGUIEvery(ticks: Int) {
        dirtyTicks++
        if (dirtyTicks >= ticks) {
            markDirtyGUI()
            dirtyTicks = 0
        }
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        if(this is IEnergyTile) {
            val energyStored = compound.getInteger("EnergyStored")
            energyStorage = EnergyStorage(energyCapacity())
            energyStorage.receiveEnergy(energyStored, false)
        }
        if(this is IItemTile) {
            input.deserializeNBT(compound.getCompoundTag("input"))
            output.deserializeNBT(compound.getCompoundTag("output"))
        }
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(compound)
        if (this is IEnergyTile) {
            compound.setInteger("EnergyStored", energyStorage.energyStored)
        }
        if (this is IItemTile) {
            compound.setTag("input", input.serializeNBT())
            compound.setTag("output", output.serializeNBT())
        }
        return compound
    }

    open fun onBlockActivated(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer,
                              hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if (this is IFluidTile) {
            val heldItem = player.getHeldItem(hand)
            if (heldItem.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, facing)) {
                val didInteract = FluidUtil.interactWithFluidHandler(player, hand, world, pos, facing)
                markDirty()
                return didInteract
            }
        }
        return false
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return if (ITEM_CAP == capability && !ConfigHandler.GENERAL.enableAutomation)
            false
        else {
            when (capability) {
                ENERGY_CAP -> return this is IEnergyTile
                FLUID_CAP  -> return this is IFluidTile
                ITEM_CAP   -> return this is IItemTile
            }
            return super.hasCapability(capability, facing)
        }
    }

    override fun <T : Any> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (ITEM_CAP == capability && !ConfigHandler.GENERAL.enableAutomation)
            null
        else {
            when (capability) {
                ENERGY_CAP -> if (this is IEnergyTile) return ENERGY_CAP.cast<T>((this as IEnergyTile).energyStorage)
                FLUID_CAP  -> if (this is IFluidTile) return FLUID_CAP.cast<T>(fluidTanks)
                ITEM_CAP   -> if (this is IItemTile) return ITEM_CAP.cast<T>(automationInvHandler)
            }
            return super.getCapability(capability, facing)
        }
    }

    companion object {
        val ENERGY_CAP: Capability<IEnergyStorage> = CapabilityEnergy.ENERGY
        val ITEM_CAP: Capability<IItemHandler> = CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
        val FLUID_CAP: Capability<IFluidHandler> = CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY
    }
}
