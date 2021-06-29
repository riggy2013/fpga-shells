// See LICENSE for license details.
package sifive.fpgashells.shell.xilinx

import chisel3._
import chisel3.experimental.{attach, Analog, IO}
import freechips.rocketchip.config._
import freechips.rocketchip.diplomacy._
import freechips.rocketchip.tilelink._
import freechips.rocketchip.util.SyncResetSynchronizerShiftReg
import sifive.fpgashells.clocks._
import sifive.fpgashells.shell._
import sifive.fpgashells.ip.xilinx._
import sifive.blocks.devices.chiplink._
import sifive.fpgashells.devices.xilinx.xilinxvcu128mig._
import sifive.fpgashells.devices.xilinx.xdma._
import sifive.fpgashells.ip.xilinx.xxv_ethernet._

/*
class SPIFlashVCUV18PlacedOverlay(val shell: VCU128ShellBasicOverlays, name: String, val designInput: SPIFlashDesignInput, val shellInput: SPIFlashShellInput)
  extends SPIFlashXilinxPlacedOverlay(name, designInput, shellInput)
{

  shell { InModuleBody { 
    /*val packagePinsWithPackageIOs = Seq(("AF13", IOPin(io.qspi_sck)),
      ("AJ11", IOPin(io.qspi_cs)),
      ("AP11", IOPin(io.qspi_dq(0))),
      ("AN11", IOPin(io.qspi_dq(1))),
      ("AM11", IOPin(io.qspi_dq(2))),
      ("AL11", IOPin(io.qspi_dq(3))))

    packagePinsWithPackageIOs foreach { case (pin, io) => {
      shell.xdc.addPackagePin(io, pin)
      shell.xdc.addIOStandard(io, "LVCMOS18")
      shell.xdc.addIOB(io)
    } }
    packagePinsWithPackageIOs drop 1 foreach { case (pin, io) => {
      shell.xdc.addPullup(io)
    } }
*/
  } }
}
class SPIFlashVCU128ShellPlacer(shell: VCU128ShellBasicOverlays, val shellInput: SPIFlashShellInput)(implicit val valName: ValName)
  extends SPIFlashShellPlacer[VCU128ShellBasicOverlays] {
  def place(designInput: SPIFlashDesignInput) = new SPIFlashVCU128PlacedOverlay(shell, valName.name, designInput, shellInput)
}
*/

class UARTPeripheralVCU128PlacedOverlay(val shell: VCU128ShellBasicOverlays, name: String, val designInput: UARTDesignInput, val shellInput: UARTShellInput)
  extends UARTXilinxPlacedOverlay(name, designInput, shellInput, true)
{
    shell { InModuleBody {
    // VCU118 val uartLocations = List(List("AY25", "BB22", "AW25", "BB21"), List("AW11", "AP13", "AY10", "AR13")) //uart0 - USB, uart1 - FMC 105 debug card J20 p1-rx p2-tx p3-ctsn p4-rtsn
    val uartLocations = List(List("BP23", "BP22", "BP26", "BN26"), List("BL27", "BL26", "BK28", "BJ28")) //uart0 - USB, cts_b, rts_b, rx, tx, uart1 - USB, cts_b, rts_b, rx, tx
    val packagePinsWithPackageIOs = Seq((uartLocations(shellInput.index)(0), IOPin(io.ctsn.get)),
                                        (uartLocations(shellInput.index)(1), IOPin(io.rtsn.get)),
                                        (uartLocations(shellInput.index)(2), IOPin(io.rxd)),
                                        (uartLocations(shellInput.index)(3), IOPin(io.txd)))

    packagePinsWithPackageIOs foreach { case (pin, io) => {
      shell.xdc.addPackagePin(io, pin)
      shell.xdc.addIOStandard(io, "LVCMOS18")
      shell.xdc.addIOB(io)
    } }
  } }
}

class UARTPeripheralVCU128ShellPlacer(val shell: VCU128ShellBasicOverlays, val shellInput: UARTShellInput)(implicit val valName: ValName)
  extends UARTShellPlacer[VCU128ShellBasicOverlays]
{
  def place(designInput: UARTDesignInput) = new UARTPeripheralVCU128PlacedOverlay(shell, valName.name, designInput, shellInput)
}

class I2CPeripheralVCU128PlacedOverlay(val shell: VCU128ShellBasicOverlays, name: String, val designInput: I2CDesignInput, val shellInput: I2CShellInput)
  extends I2CXilinxPlacedOverlay(name, designInput, shellInput)
{
    shell { InModuleBody {
    val i2cLocations = List(List("E26", "B18"), List("D26", "A16")) // FMCP LA09_P, LA19_P, LA09_N, LA19_N, no idea why
    val packagePinsWithPackageIOs = Seq((i2cLocations(shellInput.index)(0), IOPin(io.scl)),
                                        (i2cLocations(shellInput.index)(1), IOPin(io.sda)))

    packagePinsWithPackageIOs foreach { case (pin, io) => {
      shell.xdc.addPackagePin(io, pin)
      shell.xdc.addIOStandard(io, "LVCMOS18")
      shell.xdc.addIOB(io)
    } }
  } }
}

class I2CPeripheralVCU128ShellPlacer(val shell: VCU128ShellBasicOverlays, val shellInput: I2CShellInput)(implicit val valName: ValName)
  extends I2CShellPlacer[VCU128ShellBasicOverlays]
{
  def place(designInput: I2CDesignInput) = new I2CPeripheralVCU128PlacedOverlay(shell, valName.name, designInput, shellInput)
}

class QSPIPeripheralVCU128PlacedOverlay(val shell: VCU128ShellBasicOverlays, name: String, val designInput: SPIFlashDesignInput, val shellInput: SPIFlashShellInput)
  extends SPIFlashXilinxPlacedOverlay(name, designInput, shellInput)
{
    shell { InModuleBody {
    val qspiLocations = List(List("E24", "B23", "E23", "A23", "F26", "B26")) //J1 pins 1-6 and 7-12 (sck, cs, dq0-3) 
    // LA00_P, LA10_P, LA00_N, LA10_N, LA01_P, LA11_P
//FIX when built in spi flash is integrated
    val packagePinsWithPackageIOs = Seq((qspiLocations(shellInput.index)(0), IOPin(io.qspi_sck)),
                                        (qspiLocations(shellInput.index)(1), IOPin(io.qspi_cs)),
                                        (qspiLocations(shellInput.index)(2), IOPin(io.qspi_dq(0))),
                                        (qspiLocations(shellInput.index)(3), IOPin(io.qspi_dq(1))),
                                        (qspiLocations(shellInput.index)(4), IOPin(io.qspi_dq(2))),
                                        (qspiLocations(shellInput.index)(5), IOPin(io.qspi_dq(3))))

    packagePinsWithPackageIOs foreach { case (pin, io) => {
      shell.xdc.addPackagePin(io, pin)
      shell.xdc.addIOStandard(io, "LVCMOS18")
      shell.xdc.addIOB(io)
    } }
    packagePinsWithPackageIOs drop 1 foreach { case (pin, io) => {
      shell.xdc.addPullup(io)
    } }
  } }
}

class QSPIPeripheralVCU128ShellPlacer(val shell: VCU128ShellBasicOverlays, val shellInput: SPIFlashShellInput)(implicit val valName: ValName)
  extends SPIFlashShellPlacer[VCU128ShellBasicOverlays]
{
  def place(designInput: SPIFlashDesignInput) = new QSPIPeripheralVCU128PlacedOverlay(shell, valName.name, designInput, shellInput)
}

class GPIOPeripheralVCU128PlacedOverlay(val shell: VCU128ShellBasicOverlays, name: String, val designInput: GPIODesignInput, val shellInput: GPIOShellInput)
  extends GPIOXilinxPlacedOverlay(name, designInput, shellInput)
{
    shell { InModuleBody {
    val gpioLocations = List("A19", "D20", "A18", "D19", "B16", "D17", "A16", "D16", "B21", "E21", "B20", "D21", "F25", "B25", "L23", "J22") //J20 pins 5-16, J1 pins 7-10
    // LA21_P, LA25_P, LA21_N, LA25_N, LA22_P, LA26_P, LA22_N, LA26_N, 
    // LA23_P, LA27_P, LA23_N, LA27_N, LA01_N, LA11_N, LA02_P, LA12_P
    val iosWithLocs = io.gpio.zip(gpioLocations)
    val packagePinsWithPackageIOs = iosWithLocs.map { case (io, pin) => (pin, IOPin(io)) }
    println(packagePinsWithPackageIOs)

    packagePinsWithPackageIOs foreach { case (pin, io) => {
      shell.xdc.addPackagePin(io, pin)
      shell.xdc.addIOStandard(io, "LVCMOS18")
      shell.xdc.addIOB(io)
    } }
  } }
}

class GPIOPeripheralVCU128ShellPlacer(val shell: VCU128ShellBasicOverlays, val shellInput: GPIOShellInput)(implicit val valName: ValName)
  extends GPIOShellPlacer[VCU128ShellBasicOverlays] {

  def place(designInput: GPIODesignInput) = new GPIOPeripheralVCU128PlacedOverlay(shell, valName.name, designInput, shellInput)
}

  /* 
   * there is no PMOD in VCU128
object PMODVCU128PinConstraints {
  val pins = Seq(Seq("AY14","AV16","AY15","AU16","AW15","AT15","AV15","AT16"),
                 Seq("N28","P29","M30","L31","N30","M31","P30","R29"))
}
class PMODVCU128PlacedOverlay(val shell: VCU128ShellBasicOverlays, name: String, val designInput: PMODDesignInput, val shellInput: PMODShellInput)
  extends PMODXilinxPlacedOverlay(name, designInput, shellInput, packagePin = PMODVCU128PinConstraints.pins(shellInput.index), ioStandard = "LVCMOS18")
class PMODVCU128ShellPlacer(shell: VCU128ShellBasicOverlays, val shellInput: PMODShellInput)(implicit val valName: ValName)
  extends PMODShellPlacer[VCU128ShellBasicOverlays] {
  def place(designInput: PMODDesignInput) = new PMODVCU128PlacedOverlay(shell, valName.name, designInput, shellInput)
}

class PMODJTAGVCU128PlacedOverlay(val shell: VCU128ShellBasicOverlays, name: String, val designInput: JTAGDebugDesignInput, val shellInput: JTAGDebugShellInput)
  extends JTAGDebugXilinxPlacedOverlay(name, designInput, shellInput)
{
  shell { InModuleBody {
    shell.sdc.addClock("JTCK", IOPin(io.jtag_TCK), 10)
    shell.sdc.addGroup(clocks = Seq("JTCK"))
    shell.xdc.clockDedicatedRouteFalse(IOPin(io.jtag_TCK))
    val packagePinsWithPackageIOs = Seq(("AW15", IOPin(io.jtag_TCK)),
                                        ("AU16", IOPin(io.jtag_TMS)),
                                        ("AV16", IOPin(io.jtag_TDI)),
                                        ("AY14", IOPin(io.jtag_TDO)),
                                        ("AY15", IOPin(io.srst_n))) 
    packagePinsWithPackageIOs foreach { case (pin, io) => {
      shell.xdc.addPackagePin(io, pin)
      shell.xdc.addIOStandard(io, "LVCMOS18")
      shell.xdc.addPullup(io)
      shell.xdc.addIOB(io)
    } }
  } }
}
class PMODJTAGVCU128ShellPlacer(shell: VCU128ShellBasicOverlays, val shellInput: JTAGDebugShellInput)(implicit val valName: ValName)
  extends JTAGDebugShellPlacer[VCU128ShellBasicOverlays] {
  def place(designInput: JTAGDebugDesignInput) = new PMODJTAGVCU128PlacedOverlay(shell, valName.name, designInput, shellInput)
}
  */

abstract class PeripheralsVCU128Shell(implicit p: Parameters) extends VCU128ShellBasicOverlays{
  //val pmod_female      = Overlay(PMODOverlayKey, new PMODVCU128ShellPlacer(this, PMODShellInput(index = 0)))
  //val pmodJTAG = Overlay(JTAGDebugOverlayKey, new PMODJTAGVCU128ShellPlacer(this, JTAGDebugShellInput()))
  val gpio           = Overlay(GPIOOverlayKey,       new GPIOPeripheralVCU128ShellPlacer(this, GPIOShellInput()))
  val uart  = Seq.tabulate(2) { i => Overlay(UARTOverlayKey, new UARTPeripheralVCU128ShellPlacer(this, UARTShellInput(index = i))(valName = ValName(s"uart$i"))) }
  val qspi      = Seq.tabulate(0) { i => Overlay(SPIFlashOverlayKey, new QSPIPeripheralVCU128ShellPlacer(this, SPIFlashShellInput(index = i))(valName = ValName(s"qspi$i"))) }
  val i2c       = Seq.tabulate(2) { i => Overlay(I2COverlayKey, new I2CPeripheralVCU128ShellPlacer(this, I2CShellInput(index = i))(valName = ValName(s"i2c$i"))) }

  val topDesign = LazyModule(p(DesignKey)(designParameters))
  p(ClockInputOverlayKey).foreach(_.place(ClockInputDesignInput()))

  override lazy val module = new LazyRawModuleImp(this) {
    val reset = IO(Input(Bool()))
    val por_clock = sys_clock.get.get.asInstanceOf[SysClockVCU128PlacedOverlay].clock
    val powerOnReset = PowerOnResetFPGAOnly(por_clock)

    xdc.addPackagePin(reset, "BM29")
    xdc.addIOStandard(reset, "LVCMOS12")

    val reset_ibuf = Module(new IBUF)
    reset_ibuf.io.I := reset

    sdc.addAsyncPath(Seq(powerOnReset))

    val ereset: Bool = chiplink.get() match {
      case Some(x: ChipLinkVCU128PlacedOverlay) => !x.ereset_n
      case _ => false.B
    }
   pllReset := reset_ibuf.io.O || powerOnReset || ereset
  }
}
