package chiselExample.ringRouter

import chisel3.{Bundle, Module, Vec}

abstract class Network[T <: chisel3.Data](p: NetworkParams[T]) extends Module {
  class NetWorkBundle extends Bundle {
    val ports: Vec[PortIORouter[T]] = Vec(p.numHosts, new PortIORouter(p))
  }
  val io: NetWorkBundle = IO(new NetWorkBundle)
}

class RingNetworkV2[T <: chisel3.Data](p: NetworkParams[T]) extends Network[T](p) {
  val routers: Seq[RingRouterV1[T]] = Seq.tabulate(p.numHosts){ id => new RingRouterV1(p, id)}

  routers.foldLeft(routers.last){
    (prev, curr) => prev.io.out <> curr.io.in
    curr
  }

  routers.zip(io.ports).foreach { case (router, port) => router.io.host <> port}
}