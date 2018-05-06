// See LICENSE for license details.
//
//Start with a static tb and try to genererate a gnerator for it
package f2_tx_dsp
import chisel3._
import chisel3.util._
import chisel3.experimental._
import dsptools._
import dsptools.numbers._
import freechips.rocketchip.util._
import f2_interpolator._
import f2_rx_dsp._
import f2_tx_path._
import prog_delay._
import edge_detector._


//Defined in f2_rx_dsp
//class usersigs
//class iofifosigs

//constants
class usersigzeros(val n: Int, val users: Int=4) extends Bundle { 
    val userzero   = 0.U.asTypeOf(new usersigs(n=n,users=users))
    val udatazero  = 0.U.asTypeOf(userzero.data)
    val uindexzero = 0.U.asTypeOf(userzero.uindex)
    val iofifozero = 0.U.asTypeOf(new iofifosigs(n=n))
    val datazero   = 0.U.asTypeOf(iofifozero.data)
    val rxindexzero= 0.U.asTypeOf(iofifozero.rxindex)
}

class f2_tx_dsp_io(
        val outputn   : Int=9, 
        val n         : Int=16, 
        val antennas  : Int=4, 
        val users     : Int=4,
        val neighbours: Int=4,
        val progdelay : Int=64,
        val finedelay : Int=32,
        val weightbits: Int=10
    ) extends Bundle {
    val iptr_A             = Flipped(DecoupledIO(new iofifosigs(n=n)))
    val interpolator_clocks   =  new f2_interpolator_clocks    
    val interpolator_controls = Vec(antennas,new f2_interpolator_controls(gainbits=10))    
    val dac_clocks         = Input(Vec(antennas,Clock()))
    val clock_symrate      = Input(Clock())
    val clock_outfifo_deq  = Input(Clock())
    val reset_dacfifo      = Input(Bool())
    val user_spread_mode   = Input(UInt(3.W))
    val user_sum_mode      = Input(Vec(antennas,UInt(3.W)))
    val user_select_index  = Input(Vec(antennas,UInt(log2Ceil(users).W)))
    val dac_data_mode      = Input(Vec(antennas,UInt(3.W)))
    val dac_lut_write_addr = Input(Vec(antennas,UInt(outputn.W)))
    val dac_lut_write_vals = Input(Vec(antennas,DspComplex(SInt(outputn.W), SInt(outputn.W))))
    val dac_lut_write_en   = Vec(antennas,Input(Bool()))
    val optr_neighbours    = Vec(neighbours,DecoupledIO(new iofifosigs(n=n,users=users)))
    val tx_user_delays     = Input(Vec(antennas, Vec(users,UInt(log2Ceil(progdelay).W))))
    val tx_fine_delays     = Input(Vec(antennas,UInt(log2Ceil(finedelay).W)))
    val tx_user_weights    = Input(Vec(antennas,Vec(users,DspComplex(SInt(weightbits.W), SInt(weightbits.W)))))
    val Z                  = Output(Vec(antennas,DspComplex(SInt(outputn.W), SInt(outputn.W))))
}

class f2_tx_dsp (
        outputn     : Int=9,
        n          : Int=16, 
        antennas   : Int=4, 
        users      : Int=4, 
        fifodepth  : Int=128, 
        neighbours : Int=4,
        progdelay  : Int=64,
        finedelay  : Int=32,
        weightbits : Int=10
    ) extends Module {
    val io = IO( 
        new f2_tx_dsp_io(
            outputn=outputn,
            n=n,
            antennas=antennas,
            users=users,
            neighbours=neighbours,
            progdelay=progdelay,
            finedelay=finedelay
        )
    )
    //Zeros
    //val z = new usersigzeros(n=n, users=users)
    val userzero   = 0.U.asTypeOf(new usersigs(n=n,users=users))
    val udatazero  = 0.U.asTypeOf(userzero.data)
    val uindexzero = 0.U.asTypeOf(userzero.uindex)
    val iofifozero = 0.U.asTypeOf(new iofifosigs(n=n))
    val datazero   = 0.U.asTypeOf(iofifozero.data)
    val rxindexzero= 0.U.asTypeOf(iofifozero.rxindex)
    //-The RX:s
    // Vec is required to do runtime adressing of an array i.e. Seq is not hardware structure
    val tx_path  = VecInit(Seq.fill(antennas){ 
            Module ( 
                new  f2_tx_path (
                    outputn=outputn,
                    n=n, 
                    users=users, 
                    progdelay=progdelay,
                    finedelay=finedelay,
                    weightbits=weightbits
                )
           ).io})
    
    (tx_path,io.interpolator_controls).zipped.map(_.interpolator_controls:=_)
    tx_path.map(_.interpolator_clocks:=io.interpolator_clocks) 
    tx_path.map(_.clock_symrate:=io.clock_symrate) 
    tx_path.map{ x => (x.iptr_A,io.iptr_A.bits.data).zipped.map(_<>_.udata)}
    io.iptr_A.ready:=true.B
    (tx_path,io.dac_data_mode).zipped.map(_.dsp_ioctrl.dac_data_mode<>_)
    (tx_path,io.dac_lut_write_addr).zipped.map(_.dsp_ioctrl.dac_lut_write_addr<>_)
    (tx_path,io.dac_lut_write_en).zipped.map(_.dsp_ioctrl.dac_lut_write_en<>_)
    tx_path.map(_.dsp_ioctrl.reset_dacfifo:=io.reset_dacfifo)
    (tx_path,io.dac_clocks).zipped.map(_.dac_clock:=_)
    (tx_path,io.dac_lut_write_vals).zipped.map(_.dsp_ioctrl.dac_lut_write_val:=_)
    (tx_path,io.tx_user_delays).zipped.map(_.dsp_ioctrl.user_delays:=_)
    (tx_path,io.tx_fine_delays).zipped.map(_.dsp_ioctrl.fine_delays:=_)
    (tx_path,io.tx_user_weights).zipped.map(_.dsp_ioctrl.user_weights:=_)
    (tx_path,io.user_sum_mode).zipped.map(_.dsp_ioctrl.user_sum_mode:=_)
    (tx_path,io.user_select_index).zipped.map(_.dsp_ioctrl.user_select_index:=_)
    (tx_path,io.Z).zipped.map(_.Z<>_)

    //Outfifo to SerDes

    
    
    val zero :: userspread :: Nil = Enum(2)
    val outputmode=RegInit(zero)

    when (io.user_spread_mode === 0.U) {
        outputmode := zero
    }.elsewhen (io.user_spread_mode === 1.U ) {
        outputmode := userspread
    }.otherwise {
        outputmode:=zero
    }


    //Defaults
    io.optr_neighbours.map(_.bits:=iofifozero)
    io.optr_neighbours.map(_.valid:=true.B)
    when ( outputmode===userspread) {
        for (i<- 0 to neighbours-1) {
            when( io.optr_neighbours(i).ready===true.B) {
                io.optr_neighbours(i).valid:=withClock(io.clock_symrate){RegNext(true.B)}
                io.optr_neighbours(i)<>withClock(io.clock_symrate){RegNext(io.iptr_A)}
            }.otherwise {
                io.optr_neighbours(i).valid:=true.B
                io.optr_neighbours(i).bits:=iofifozero
            } 
        }
    }.elsewhen ( outputmode===zero ) {
        io.optr_neighbours.map(_.bits:=iofifozero)
        io.optr_neighbours.map(_.valid:=true.B)
    }.otherwise {
        io.optr_neighbours.map(_.bits:=iofifozero)
        io.optr_neighbours.map(_.valid:=true.B)
    }
    
    //This is a wire for various mode assignments 
}

//This gives you verilog
object f2_tx_dsp extends App {
  chisel3.Driver.execute(args, () => new f2_tx_dsp(outputn=9, n=16, antennas=4, users=4, fifodepth=128 ))
}

