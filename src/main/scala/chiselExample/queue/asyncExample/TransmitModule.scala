package chiselExample.queue.asyncExample

import chisel3.util.{Counter, Decoupled, DecoupledIO, MuxLookup}
import chisel3._


object AsyncCrossingValue {
  def values: Seq[Int] =
    Seq(2, 3, 5, 7,11, 13,17,19,23,29,31,37,41,43,47,53,59,61,67,71,73,79,83,89,97,101,103,107,109,113,127,131,137,139,
      149,151,157,163,167,173,179,181,191,193,197,199,211,223,227,229,233,239,241,251,257,263,269,271,277,281,283,293,
      307,311,313,317,331,337,347,349,353,359,367,373,379,383,389,397,401,409,419,421,431,433,439,443,449,457,461,463,
      467,479,487,491,499,503,509,521,523,541,547,557,563,569,571,577,587,593,599,601,607,613,617,619,631,641,643,647,
      653,659,661,673,677,683,691,701,709,719,727,733,739,743,751,757,761,769,773,787,797,809)
}

class TransmitModule extends Module {

  class TransmitModule extends Bundle{
    val data_out: DecoupledIO[UInt] = Decoupled(UInt(10.W))
  }

  val io: TransmitModule = IO(new TransmitModule)

  io.data_out.valid := false.B
  io.data_out.bits := DontCare

  val counter: Counter = Counter(AsyncCrossingValue.values.length + 1)

  when (counter.value < AsyncCrossingValue.values.length.U) {

    val candidateValue = MuxLookup(counter.value, 0.U,
      AsyncCrossingValue.values.zipWithIndex.map {
        case (value: Int, index: Int) => (index.U, value.U)
      })
    io.data_out.enq(candidateValue)

    when (io.data_out.fire) {
      counter.inc()
    }.otherwise{
      counter.value := counter.value
    }
  }.otherwise{
    counter.value := 0.U
  }
}
