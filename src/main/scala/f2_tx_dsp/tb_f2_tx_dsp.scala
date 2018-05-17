// See LICENSE for license details.
// Use handlebars for template generation
//
//Start with a static tb and try to genererate a gnerator for it
// This uses clkdiv_n_2_4_8 verilog. You need to compile it separately

package f2_tx_dsp

import chisel3._
import java.io.{File, FileWriter, BufferedWriter}
import com.gilt.handlebars.scala.binding.dynamic._
import com.gilt.handlebars.scala.Handlebars

//Testbench.
object tb_f2_tx_dsp {
    // This is insane
    // This must be done by a method processing direction-name-width tuples
    def main(args: Array[String]): Unit = {
         val name= this.getClass.getSimpleName.split("\\$").last
         val tb = new BufferedWriter(new FileWriter("./verilog/"+name+".v"))
         object tbvars {
             val oname=name
             val dutmod = "f2_tx_dsp" 
             val n = 16
             val inputn = 9
             val gainbits= 10
             val inbits=16
             val uindexbits=2
             val rxindexbits=2
             val scalebits=10
             val interpmodebits=3
             val spreadmodebits=3
             val summodebits=3
             val dacdatamodebits=3
             val outbits=9
             val txuserdelaybits=6
             val txweightbits=10
             val txfinedelaybits=5
             val thermo=5
             val bin=4

             val paramseq=Seq(
                           ("g_infile","\"./A.txt\""), 
                           ("g_outfile","\"./Z.txt\""),
                           ("g_Rs_high","16*8*20.0e6"),
                           ("g_Rs_low","20.0e6"),
                           ("g_scale0","1"),
                           ("g_scale1","1"),
                           ("g_scale2","1"),
                           ("g_scale3","1"),
                           ("g_user_index","0"),
                           ("g_antenna_index","0"),
                           ("g_user_spread_mode","0"),
                           ("g_user_sum_mode","0"),
                           ("g_user_select_index","0"),
                           ("g_input_mode","0"),
                           ("g_interpolator_mode","4"),
                           ("g_dac_data_mode","2")
                           )

            //(type,name,upperlimit,lowerlimit, assign,init)    
            //("None","None","None","None","None","None")
            val ioseq=Seq( ("wire","clkpn","None","None","None","None"),
                          ("wire","clkp2n","None","None","None","None"),
                          ("wire","clkp4n","None","None","None","None"),
                          ("wire","clkp8n","None","None","None","None"),
                          ("reg","Ndiv",7,0,"None","c_ratio0"),
                          ("reg","reset_clk","None","None","None",1),
                          ("clock","clock","None","None","None","None"),
                          ("reset","reset","None","None","None",1),
                          ("in","clock","None","None","None","None"),
                          ("in","reset","None","None","None","None"),
                          ("out","io_iptr_A_ready","None","None","None","None"),
                          ("in","io_iptr_A_valid","None","None","None","None"),
                          ("in","io_iptr_A_bits_data_0_udata_real",inbits-1,0,"None","None"),
                          ("in","io_iptr_A_bits_data_0_udata_imag",inbits-1,0,"None","None"),
                          ("in","io_iptr_A_bits_data_0_uindex",uindexbits-1,0,"None","None"),
                          ("in","io_iptr_A_bits_data_1_udata_real",inbits-1,0,"None","None"),
                          ("in","io_iptr_A_bits_data_1_udata_imag",inbits-1,0,"None","None"),
                          ("in","io_iptr_A_bits_data_1_uindex",uindexbits-1,0,"None","None"),
                          ("in","io_iptr_A_bits_data_2_udata_real",inbits-1,0,"None","None"),
                          ("in","io_iptr_A_bits_data_2_udata_imag",inbits-1,0,"None","None"),
                          ("in","io_iptr_A_bits_data_2_uindex",uindexbits-1,0,"None","None"),
                          ("in","io_iptr_A_bits_data_3_udata_real",inbits-1,0,"None","None"),
                          ("in","io_iptr_A_bits_data_3_udata_imag",inbits-1,0,"None","None"),
                          ("in","io_iptr_A_bits_data_3_uindex",uindexbits-1,0,"None","None"),
                          ("in","io_iptr_A_bits_rxindex",rxindexbits-1,0,"None","None"),
                          ("in","io_interpolator_clocks_cic3clockfast","None","None","None","None"),
                          ("in","io_interpolator_clocks_hb1clock_high","None","None","None","None"),
                          ("in","io_interpolator_clocks_hb2clock_high","None","None","None","None"),
                          ("in","io_interpolator_clocks_hb3clock_high","None","None","None","None"),
                          ("in","io_interpolator_controls_0_cic3derivscale",scalebits-1,0,"None","g_scale3"),
                          ("in","io_interpolator_controls_0_hb1scale",scalebits-1,0,"None","g_scale0"),
                          ("in","io_interpolator_controls_0_hb2scale",scalebits-1,0,"None","g_scale1"),
                          ("in","io_interpolator_controls_0_hb3scale",scalebits-1,0,"None","g_scale2"),
                          ("in","io_interpolator_controls_0_mode",interpmodebits-1,0,"None","g_iterpolator_mode"),
                          ("in","io_interpolator_controls_1_cic3derivscale",scalebits-1,0,"None","g_scale3"),
                          ("in","io_interpolator_controls_1_hb1scale",scalebits-1,0,"None","g_scale0"),
                          ("in","io_interpolator_controls_1_hb2scale",scalebits-1,0,"None","g_scale1"),
                          ("in","io_interpolator_controls_1_hb3scale",scalebits-1,0,"None","g_scale2"),
                          ("in","io_interpolator_controls_1_mode",interpmodebits-1,0,"None","g_iterpolator_mode"),
                          ("in","io_interpolator_controls_2_cic3derivscale",scalebits-1,0,"None","g_scale3"),
                          ("in","io_interpolator_controls_2_hb1scale",scalebits-1,0,"None","g_scale0"),
                          ("in","io_interpolator_controls_2_hb2scale",scalebits-1,0,"None","g_scale1"),
                          ("in","io_interpolator_controls_2_hb3scale",scalebits-1,0,"None","g_scale2"),
                          ("in","io_interpolator_controls_2_mode",interpmodebits-1,0,"None","g_iterpolator_mode"),
                          ("in","io_interpolator_controls_3_cic3derivscale",scalebits-1,0,"None","g_scale3"),
                          ("in","io_interpolator_controls_3_hb1scale",scalebits-1,0,"None","g_scale0"),
                          ("in","io_interpolator_controls_3_hb2scale",scalebits-1,0,"None","g_scale1"),
                          ("in","io_interpolator_controls_3_hb3scale",scalebits-1,0,"None","g_scale2"),
                          ("in","io_interpolator_controls_3_mode",interpmodebits-1,0,"None","g_iterpolator_mode"),
                          ("in","io_dac_clocks_0","None","None","None","None"),
                          ("in","io_dac_clocks_1","None","None","None","None"),
                          ("in","io_dac_clocks_2","None","None","None","None"),
                          ("in","io_dac_clocks_3","None","None","None","None"),
                          ("in","io_clock_symrate","None","None","None","None"),
                          ("in","io_clock_outfifo_deq","None","None","None","None"),
                          ("in","io_reset_dacfifo","None","None","None","None"),
                          ("in","io_user_spread_mode",spreadmodebits-1,0,"None","g_user_spread_mode"),
                          ("in","io_user_sum_mode_0",summodebits-1,0,"None","g_user_sum_mode"),
                          ("in","io_user_sum_mode_1",summodebits-1,0,"None","g_user_sum_mode"),
                          ("in","io_user_sum_mode_2",summodebits-1,0,"None","g_user_sum_mode"),
                          ("in","io_user_sum_mode_3",summodebits-1,0,"None","g_user_sum_mode"),
                          ("in","io_user_select_index_0",uindexbits-1,0,"None","g_user_select_index"),
                          ("in","io_user_select_index_1",uindexbits-1,0,"None","g_user_select_index"),
                          ("in","io_user_select_index_2",uindexbits-1,0,"None","g_user_select_index"),
                          ("in","io_user_select_index_3",uindexbits-1,0,"None","g_user_select_index"),
                          ("in","io_dac_data_mode_0",uindexbits-1,0,"None","g_dac_data_mode"),
                          ("in","io_dac_data_mode_1",uindexbits-1,0,"None","g_dac_data_mode"),
                          ("in","io_dac_data_mode_2",uindexbits-1,0,"None","g_dac_data_mode"),
                          ("in","io_dac_data_mode_3",uindexbits-1,0,"None","g_dac_data_mode"),
                          ("in","io_dac_lut_write_addr_0",outbits-1,0,"w_dac_lut_write_addr","None"),
                          ("in","io_dac_lut_write_addr_1",outbits-1,0,"w_dac_lut_write_addr","None"),
                          ("in","io_dac_lut_write_addr_2",outbits-1,0,"w_dac_lut_write_addr","None"),
                          ("in","io_dac_lut_write_addr_3",outbits-1,0,"w_dac_lut_write_addr","None"),
                          ("in","io_dac_lut_write_vals_0_real",outbits-1,0,"w_dac_lut_write_val","None"),
                          ("in","io_dac_lut_write_vals_0_imag",outbits-1,0,"w_dac_lut_write_val","None"),
                          ("in","io_dac_lut_write_vals_1_real",outbits-1,0,"w_dac_lut_write_val","None"),
                          ("in","io_dac_lut_write_vals_1_imag",outbits-1,0,"w_dac_lut_write_val","None"),
                          ("in","io_dac_lut_write_vals_2_real",outbits-1,0,"w_dac_lut_write_val","None"),
                          ("in","io_dac_lut_write_vals_2_imag",outbits-1,0,"w_dac_lut_write_val","None"),
                          ("in","io_dac_lut_write_vals_3_real",outbits-1,0,"w_dac_lut_write_val","None"),
                          ("in","io_dac_lut_write_vals_3_imag",outbits-1,0,"w_dac_lut_write_val","None"),
                          ("in","io_dac_lut_write_en_0","None","None","None","None"),
                          ("in","io_dac_lut_write_en_1","None","None","None","None"),
                          ("in","io_dac_lut_write_en_2","None","None","None","None"),
                          ("in","io_dac_lut_write_en_3","None","None","None","None"),
                          ("in","io_optr_neighbours_0_ready","None","None","None","None"),
                          ("out","io_optr_neighbours_0_valid","None","None","None","None"),
                          ("out","io_optr_neighbours_0_bits_data_0_udata_real",inbits-1,0,"None","None"),
                          ("out","io_optr_neighbours_0_bits_data_0_udata_imag",inbits-1,0,"None","None"),
                          ("out","io_optr_neighbours_0_bits_data_0_uindex",uindexbits-1,0,"None","None"),
                          ("out","io_optr_neighbours_0_bits_data_1_udata_real",inbits-1,0,"None","None"),
                          ("out","io_optr_neighbours_0_bits_data_1_udata_imag",inbits-1,0,"None","None"),
                          ("out","io_optr_neighbours_0_bits_data_1_uindex",uindexbits-1,0,"None","None"),
                          ("out","io_optr_neighbours_0_bits_data_2_udata_real",inbits-1,0,"None","None"),
                          ("out","io_optr_neighbours_0_bits_data_2_udata_imag",inbits-1,0,"None","None"),
                          ("out","io_optr_neighbours_0_bits_data_2_uindex",uindexbits-1,0,"None","None"),
                          ("out","io_optr_neighbours_0_bits_data_3_udata_real",inbits-1,0,"None","None"),
                          ("out","io_optr_neighbours_0_bits_data_3_udata_imag",inbits-1,0,"None","None"),
                          ("out","io_optr_neighbours_0_bits_data_3_uindex",uindexbits-1,0,"None","None"),
                          ("out","io_optr_neighbours_0_bits_rxindex",rxindexbits-1,0,"None","None"),
                          ("in","io_optr_neighbours_1_ready","None","None","None","None"),
                          ("out","io_optr_neighbours_1_valid","None","None","None","None"),
                          ("out","io_optr_neighbours_1_bits_data_0_udata_real",inbits-1,0,"None","None"),
                          ("out","io_optr_neighbours_1_bits_data_0_udata_imag",inbits-1,0,"None","None"),
                          ("out","io_optr_neighbours_1_bits_data_0_uindex",uindexbits-1,0,"None","None"),
                          ("out","io_optr_neighbours_1_bits_data_1_udata_real",inbits-1,0,"None","None"),
                          ("out","io_optr_neighbours_1_bits_data_1_udata_imag",inbits-1,0,"None","None"),
                          ("out","io_optr_neighbours_1_bits_data_1_uindex",uindexbits-1,0,"None","None"),
                          ("out","io_optr_neighbours_1_bits_data_2_udata_real",inbits-1,0,"None","None"),
                          ("out","io_optr_neighbours_1_bits_data_2_udata_imag",inbits-1,0,"None","None"),
                          ("out","io_optr_neighbours_1_bits_data_2_uindex",uindexbits-1,0,"None","None"),
                          ("out","io_optr_neighbours_1_bits_data_3_udata_real",inbits-1,0,"None","None"),
                          ("out","io_optr_neighbours_1_bits_data_3_udata_imag",inbits-1,0,"None","None"),
                          ("out","io_optr_neighbours_1_bits_data_3_uindex",uindexbits-1,0,"None","None"),
                          ("out","io_optr_neighbours_1_bits_rxindex",rxindexbits-1,0,"None","None"),
                          ("in","io_optr_neighbours_2_ready","None","None","None","None"),
                          ("out","io_optr_neighbours_2_valid","None","None","None","None"),
                          ("out","io_optr_neighbours_2_bits_data_0_udata_real",inbits-1,0,"None","None"),
                          ("out","io_optr_neighbours_2_bits_data_0_udata_imag",inbits-1,0,"None","None"),
                          ("out","io_optr_neighbours_2_bits_data_0_uindex",uindexbits-1,0,"None","None"),
                          ("out","io_optr_neighbours_2_bits_data_1_udata_real",inbits-1,0,"None","None"),
                          ("out","io_optr_neighbours_2_bits_data_1_udata_imag",inbits-1,0,"None","None"),
                          ("out","io_optr_neighbours_2_bits_data_1_uindex",uindexbits-1,0,"None","None"),
                          ("out","io_optr_neighbours_2_bits_data_2_udata_real",inbits-1,0,"None","None"),
                          ("out","io_optr_neighbours_2_bits_data_2_udata_imag",inbits-1,0,"None","None"),
                          ("out","io_optr_neighbours_2_bits_data_2_uindex",uindexbits-1,0,"None","None"),
                          ("out","io_optr_neighbours_2_bits_data_3_udata_real",inbits-1,0,"None","None"),
                          ("out","io_optr_neighbours_2_bits_data_3_udata_imag",inbits-1,0,"None","None"),
                          ("out","io_optr_neighbours_2_bits_data_3_uindex",uindexbits-1,0,"None","None"),
                          ("out","io_optr_neighbours_2_bits_rxindex",rxindexbits-1,0,"None","None"),
                          ("in","io_optr_neighbours_3_ready","None","None","None","None"),
                          ("out","io_optr_neighbours_3_valid","None","None","None","None"),
                          ("out","io_optr_neighbours_3_bits_data_0_udata_real",inbits-1,0,"None","None"),
                          ("out","io_optr_neighbours_3_bits_data_0_udata_imag",inbits-1,0,"None","None"),
                          ("out","io_optr_neighbours_3_bits_data_0_uindex",uindexbits-1,0,"None","None"),
                          ("out","io_optr_neighbours_3_bits_data_1_udata_real",inbits-1,0,"None","None"),
                          ("out","io_optr_neighbours_3_bits_data_1_udata_imag",inbits-1,0,"None","None"),
                          ("out","io_optr_neighbours_3_bits_data_1_uindex",uindexbits-1,0,"None","None"),
                          ("out","io_optr_neighbours_3_bits_data_2_udata_real",inbits-1,0,"None","None"),
                          ("out","io_optr_neighbours_3_bits_data_2_udata_imag",inbits-1,0,"None","None"),
                          ("out","io_optr_neighbours_3_bits_data_2_uindex",uindexbits-1,0,"None","None"),
                          ("out","io_optr_neighbours_3_bits_data_3_udata_real",inbits-1,0,"None","None"),
                          ("out","io_optr_neighbours_3_bits_data_3_udata_imag",inbits-1,0,"None","None"),
                          ("out","io_optr_neighbours_3_bits_data_3_uindex",uindexbits-1,0,"None","None"),
                          ("out","io_optr_neighbours_3_bits_rxindex",rxindexbits-1,0,"None","None"),
                          ("in","io_tx_user_delays_0_0",txuserdelaybits-1,0,"None",0),
                          ("in","io_tx_user_delays_0_1",txuserdelaybits-1,0,"None",0),
                          ("in","io_tx_user_delays_0_2",txuserdelaybits-1,0,"None",0),
                          ("in","io_tx_user_delays_0_3",txuserdelaybits-1,0,"None",0),
                          ("in","io_tx_user_delays_1_0",txuserdelaybits-1,0,"None",0),
                          ("in","io_tx_user_delays_1_1",txuserdelaybits-1,0,"None",0),
                          ("in","io_tx_user_delays_1_2",txuserdelaybits-1,0,"None",0),
                          ("in","io_tx_user_delays_1_3",txuserdelaybits-1,0,"None",0),
                          ("in","io_tx_user_delays_2_0",txuserdelaybits-1,0,"None",0),
                          ("in","io_tx_user_delays_2_1",txuserdelaybits-1,0,"None",0),
                          ("in","io_tx_user_delays_2_2",txuserdelaybits-1,0,"None",0),
                          ("in","io_tx_user_delays_2_3",txuserdelaybits-1,0,"None",0),
                          ("in","io_tx_user_delays_3_0",txuserdelaybits-1,0,"None",0),
                          ("in","io_tx_user_delays_3_1",txuserdelaybits-1,0,"None",0),
                          ("in","io_tx_user_delays_3_2",txuserdelaybits-1,0,"None",0),
                          ("in","io_tx_user_delays_3_3",txuserdelaybits-1,0,"None",0),
                          ("in","io_tx_fine_delays_0",txfinedelaybits-1,0,"None",0),
                          ("in","io_tx_fine_delays_1",txfinedelaybits-1,0,"None",0),
                          ("in","io_tx_fine_delays_2",txfinedelaybits-1,0,"None",0),
                          ("in","io_tx_fine_delays_3",txfinedelaybits-1,0,"None",0),
                          ("in","io_tx_user_weights_0_0_real",txweightbits-1,0,"None",1),
                          ("in","io_tx_user_weights_0_0_imag",txweightbits-1,0,"None",0),
                          ("in","io_tx_user_weights_0_1_real",txweightbits-1,0,"None",1),
                          ("in","io_tx_user_weights_0_1_imag",txweightbits-1,0,"None",0),
                          ("in","io_tx_user_weights_0_2_real",txweightbits-1,0,"None",1),
                          ("in","io_tx_user_weights_0_2_imag",txweightbits-1,0,"None",0),
                          ("in","io_tx_user_weights_0_3_real",txweightbits-1,0,"None",1),
                          ("in","io_tx_user_weights_0_3_imag",txweightbits-1,0,"None",0),
                          ("in","io_tx_user_weights_1_0_real",txweightbits-1,0,"None",1),
                          ("in","io_tx_user_weights_1_0_imag",txweightbits-1,0,"None",0),
                          ("in","io_tx_user_weights_1_1_real",txweightbits-1,0,"None",1),
                          ("in","io_tx_user_weights_1_1_imag",txweightbits-1,0,"None",0),
                          ("in","io_tx_user_weights_1_2_real",txweightbits-1,0,"None",1),
                          ("in","io_tx_user_weights_1_2_imag",txweightbits-1,0,"None",0),
                          ("in","io_tx_user_weights_1_3_real",txweightbits-1,0,"None",1),
                          ("in","io_tx_user_weights_1_3_imag",txweightbits-1,0,"None",0),
                          ("in","io_tx_user_weights_2_0_real",txweightbits-1,0,"None",1),
                          ("in","io_tx_user_weights_2_0_imag",txweightbits-1,0,"None",0),
                          ("in","io_tx_user_weights_2_1_real",txweightbits-1,0,"None",1),
                          ("in","io_tx_user_weights_2_1_imag",txweightbits-1,0,"None",0),
                          ("in","io_tx_user_weights_2_2_real",txweightbits-1,0,"None",1),
                          ("in","io_tx_user_weights_2_2_imag",txweightbits-1,0,"None",0),
                          ("in","io_tx_user_weights_2_3_real",txweightbits-1,0,"None",1),
                          ("in","io_tx_user_weights_2_3_imag",txweightbits-1,0,"None",0),
                          ("in","io_tx_user_weights_3_0_real",txweightbits-1,0,"None",1),
                          ("in","io_tx_user_weights_3_0_imag",txweightbits-1,0,"None",0),
                          ("in","io_tx_user_weights_3_1_real",txweightbits-1,0,"None",1),
                          ("in","io_tx_user_weights_3_1_imag",txweightbits-1,0,"None",0),
                          ("in","io_tx_user_weights_3_2_real",txweightbits-1,0,"None",1),
                          ("in","io_tx_user_weights_3_2_imag",txweightbits-1,0,"None",0),
                          ("in","io_tx_user_weights_3_3_real",txweightbits-1,0,"None",1),
                          ("in","io_tx_user_weights_3_3_imag",txweightbits-1,0,"None",0),
                          ("out","io_Z_0_real_b",bin-1,0,"None","None"),
                          ("out","io_Z_0_real_t",scala.math.pow(2,thermo).toInt-1,0,"None","None"),
                          ("out","io_Z_0_imag_b",bin-1,0,"None","None"),
                          ("out","io_Z_0_imag_t",scala.math.pow(2,thermo).toInt-1,0,"None","None"),
                          ("out","io_Z_1_real_b",bin-1,0,"None","None"),
                          ("out","io_Z_1_real_t",scala.math.pow(2,thermo).toInt-1,0,"None","None"),
                          ("out","io_Z_1_imag_b",bin-1,0,"None","None"),
                          ("out","io_Z_1_imag_t",scala.math.pow(2,thermo).toInt-1,0,"None","None"),
                          ("out","io_Z_2_real_b",bin-1,0,"None","None"),
                          ("out","io_Z_2_real_t",scala.math.pow(2,thermo).toInt-1,0,"None","None"),
                          ("out","io_Z_2_imag_b",bin-1,0,"None","None"),
                          ("out","io_Z_2_imag_t",scala.math.pow(2,thermo).toInt-1,0,"None","None"),
                          ("out","io_Z_3_real_b",bin-1,0,"None","None"),
                          ("out","io_Z_3_real_t",scala.math.pow(2,thermo).toInt-1,0,"None","None"),
                          ("out","io_Z_3_imag_b",bin-1,0,"None","None"),
                          ("out","io_Z_3_imag_t",scala.math.pow(2,thermo).toInt-1,0,"None","None"),
                          ("dclk","interpolator_clocks_cic3clocksfast","None","None","clkpn","None"),
                          ("dclk","interpolator_clocks_hb1clock_high","None","None","clkp2n","None"),
                          ("dclk","interpolator_clocks_hb2clock_high","None","None","clkp4n","None"),
                          ("dclk","interpolator_clocks_hb3clock_high","None","None","clkp8n","None")
                          )
        }
        val header="//This is a tesbench generated with scala generator\n"
        var extpars="""//Things you want to control from the simulator cmdline must be parameters %nmodule %s #(""".format(tbvars.oname)+
                       tbvars.paramseq.map{ 
                           case (par,value) => "parameter %s = %s,\n            ".format(par,value)
                       }.mkString
        extpars=extpars.patch(extpars.lastIndexOf(','),"",1)+");"
        var dutdef="""//DUT definition%n    %s DUT (""".format(tbvars.dutmod)+
                     tbvars.ioseq.map{ 
                         case ("reg",name,ul,dl,assingn,init)  => ""
                         case ("wire",name,ul,dl,assingn,init)  => ""
                         case ("reset"|"clock",name,ul,dl,assign,init)  => ".%s(%s),\n    ".format(name,name)
                         case (dir,name,ul,dl,assign,init) => ".%s(%s),\n    ".format(name,name)
                         case _ => ""
                     }.mkString
        dutdef=dutdef.patch(dutdef.lastIndexOf(','),"",1)+");"

        val regdef="""//Registers for inputs %n""".format() +
                     tbvars.ioseq.map{ 
                         case ("clock",name,ul,dl,assign,init)  => "reg %s;\n".format(name)
                         case ("reset",name,ul,dl,assign,init) => "reg %s;\n".format(name)
                         case ("in"|"reg",name,"None","None",assign,init) => "reg %s;\n".format(name)
                         case ("in"|"reg",name,ul,dl,assign,init) => "reg [%s:%s] %s;\n".format(ul,dl,name)
                         case ("ins"|"regs",name,ul,dl,assign,init) => "reg signed [%s:%s] io_%s;\n".format(ul,dl,name)
                         case _ => ""
                     }.mkString

        val wiredef="""//Wires for outputs %n""".format() +
                     tbvars.ioseq.map{ 
                         case ("dclk"|"out"|"wire",name,"None","None",assign,init) => "wire %s;\n".format(name)
                         case ("out"|"wire",name,ul,dl,assign,init) => "wire [%s:%s] %s;\n".format(ul,dl,name)
                         case ("outs"|"wires",name,ul,dl,assign,init) => "wire signed [%s:%s] %s;\n".format(ul,dl,name)
                         case _ => ""
                     }.mkString

        val assdef="""//Assignments %n""".format()+
                     tbvars.ioseq.map{ 
                         case ("dclk"|"out"|"in",name,ul,dl,"None",init) => ""
                         case ("dclk"|"out"|"in",name,ul,dl,"clock",init) => "assign %s=clock;\n".format(name)
                         case ("dclk"|"out"|"in",name,ul,dl,"reset",init) => "assign %s=reset;\n".format(name)
                         case ("dclk"|"out"|"in",name,ul,dl,assign,init) => "assign %s=io_%s;\n".format(name,assign)
                         case _ => ""
                     }.mkString

        val initialdef="""%n%n//Initial values %ninitial #0 begin%n""".format()+
                     tbvars.ioseq.map{ 
                         case ( dir,name,ul,dl,assign,"None") => ""
                         case ("reset",name,ul,dl,assign,init) => "    %s=%s;\n".format(name,init)
                         case ("reset" | "in" | "wire" | "reg" |"wires" | "regs" ,name,ul,dl,assign,init) => "    %s=%s;\n".format(name,init)
                         case _ => ""
                     }.mkString
                     


        val textTemplate=header+ extpars+"""
                        |//timescale 1ps this should probably be a global model parameter 
                        |parameter integer c_Ts=1/(g_Rs_high*1e-12);
                        |parameter integer c_ratio0=g_Rs_high/(8*g_Rs_low);
                        |parameter integer c_ratio1=g_Rs_high/(4*g_Rs_low);
                        |parameter integer c_ratio2=g_Rs_high/(2*g_Rs_low);
                        |parameter integer c_ratio3=g_Rs_high/(g_Rs_low);
                        |parameter RESET_TIME = 16*c_Ts;
                        |
                        |""".stripMargin('|')+regdef+wiredef+assdef+
                        """|
                        |//File IO parameters
                        |integer StatusI, StatusO, infile, outfile;
                        |integer din1,din2,din3,din4,din5,din6,din7,din8;
                        |integer memaddrcount;
                        |integer initdone;
                        |
                        |//Initializations
                        |initial clock = 1'b0;
                        |initial reset = 1'b0;
                        |initial outfile = $fopen(g_outfile,"w"); // For writing
                        |
                        |//Clock definitions
                        |always #(c_Ts)clock = !clock ;
                        | 
                        |//Read this with Ouput fifo enquque clk
                        |always @(posedge io_clock_symrate && (initdone==1)) begin 
                        |    //Print only valid values 
                        |    if (~$isunknown(io_ofifo_bits_data_0_real) ) begin
                        |        $fwrite(outfile, "%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\n", 
                        |                         io_Z_0_real_t, io_Z_0_real_b, 
                        |                         io_Z_0_imag_t, io_Z_0_imag_b, 
                        |                         io_Z_1_real_t, io_Z_1_real_b, 
                        |                         io_Z_1_imag_t, io_Z_1_imag_b, 
                        |                         io_Z_2_real_t, io_Z_2_real_b, 
                        |                         io_Z_2_imag_t, io_Z_2_imag_b, 
                        |                         io_Z_3_real_t, io_Z_3_real_b, 
                        |                         io_Z_3_imag_t, io_Z_3_imag_b, 
                        |    end
                        |    else begin
                        |         $display( $time, "Dropping invalid output values at ");
                        |    end 
                        |end
                        |
                        |//Clock divider model
                        |clkdiv_n_2_4_8 clockdiv( // @[:@3.2]
                        |  .clock(clock), // @[:@4.4]
                        |  .reset(reset), // @[:@5.4]
                        |  .io_Ndiv(io_Ndiv), // @[:@6.4]
                        |  .io_reset_clk(io_reset_clk), // @[:@6.4]
                        |  .io_clkpn (io_clkpn), // @[:@6.4]
                        |  .io_clkp2n(io_clkp2n), // @[:@6.4]
                        |  .io_clkp4n(io_clkp4n), // @[:@6.4]
                        |  .io_clkp8n(io_clkp8n)// @[:@6.4]
                        |);
                        |
                        |""".stripMargin('|')+dutdef+initialdef+
                        """
                        |    #RESET_TIME
                        |    reset=0;
                        |    io_reset_clk=0;
                        |    #(16*RESET_TIME)
                        |    memaddrcount=0;
                        |    io_reset_adcfifo=0;
                        |    io_reset_index_count=0;
                        |    io_reset_outfifo=0;
                        |    io_reset_adcfifo=0;
                        |    io_reset_infifo=0;
                        |//Tnit the LUT
                        |    
                        |    while (memaddrcount<2**9) begin
                        |       @(posedge clock) 
                        |       io_dac_lut_write_en<=1;
                        |       io_dac_lut_write_addr<=memaddrcount;
                        |       io_dac_lut_write_vals_0_real<=memaddrcount; 
                        |       io_dac_lut_write_vals_1_real<=memaddrcount;
                        |       io_dac_lut_write_vals_2_real<=memaddrcount;
                        |       io_dac_lut_write_vals_3_real<=memaddrcount;
                        |       io_dac_lut_write_vals_0_imag<=memaddrcount; 
                        |       io_dac_lut_write_vals_1_imag<=memaddrcount;
                        |       io_dac_lut_write_vals_2_imag<=memaddrcount;
                        |       io_dac_lut_write_vals_3_imag<=memaddrcount;
                        |       @(posedge clock) 
                        |       memaddrcount=memaddrcount+1;
                        |       io_dac_lut_write_en<=0;
                        |    end
                        |    initdone=1;
                        |    infile = $fopen(g_infile,"r"); // For reading
                        |    while (!$feof(infile)) begin
                        |            @(posedge clock) 
                        |             StatusI=$fscanf(infile, "%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\n",
                        |                             din1, din2, din3, din4,din5, din6, din7, din8);
                        |             io_iptr_A_0_real <= din1;
                        |             io_iptr_A_0_imag <= din2;
                        |             io_iptr_A_1_real <= din3;
                        |             io_iptr_A_1_imag <= din4;
                        |             io_iptr_A_2_real <= din5;
                        |             io_iptr_A_2_imag <= din6;
                        |             io_iptr_A_3_real <= din7;
                        |             io_iptr_A_3_imag <= din8;
                        |    end
                        |    $fclose(infile);
                        |    $fclose(outfile);
                        |    $finish;
                        |end
                        |endmodule""".stripMargin('|')
        //val testbench=Handlebars(textTemplate)
        //tb write testbench(tbvars)
        tb write textTemplate
        tb.close()
  }
}

