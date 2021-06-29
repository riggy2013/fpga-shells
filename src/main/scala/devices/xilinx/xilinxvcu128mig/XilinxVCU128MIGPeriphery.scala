// See LICENSE for license details.
package sifive.fpgashells.devices.xilinx.xilinxvcu128mig

import Chisel._
import freechips.rocketchip.config._
import freechips.rocketchip.subsystem.BaseSubsystem
import freechips.rocketchip.diplomacy.{LazyModule, LazyModuleImp, AddressRange}

case object MemoryXilinxDDRKey extends Field[XilinxVCU128MIGParams]

trait HasMemoryXilinxVCU128MIG { this: BaseSubsystem =>
  val module: HasMemoryXilinxVCU128MIGModuleImp

  val xilinxvcu128mig = LazyModule(new XilinxVCU128MIG(p(MemoryXilinxDDRKey)))

  xilinxvcu128mig.node := mbus.toDRAMController(Some("xilinxvcu128mig"))()
}

trait HasMemoryXilinxVCU128MIGBundle {
  val xilinxvcu128mig: XilinxVCU128MIGIO
  def connectXilinxVCU128MIGToPads(pads: XilinxVCU128MIGPads) {
    pads <> xilinxvcu128mig
  }
}

trait HasMemoryXilinxVCU128MIGModuleImp extends LazyModuleImp
    with HasMemoryXilinxVCU128MIGBundle {
  val outer: HasMemoryXilinxVCU128MIG
  val ranges = AddressRange.fromSets(p(MemoryXilinxDDRKey).address)
  require (ranges.size == 1, "DDR range must be contiguous")
  val depth = ranges.head.size
  val xilinxvcu128mig = IO(new XilinxVCU128MIGIO(depth))

  xilinxvcu128mig <> outer.xilinxvcu128mig.module.io.port
}
