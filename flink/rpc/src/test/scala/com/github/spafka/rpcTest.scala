package com.github.spafka

import akka.actor.ActorSystem
import com.github.spafka.rpc.{AkkaRpcService, RpcEndpoint, RpcGateway, RpcService}
import com.github.spafka.util.{AkkaUtils, Logging}
import org.apache.flink.api.common.time.Time
import org.junit.{Before, Test}

class rpcTest {

  var actorSystem: ActorSystem = _
  val time = Time.seconds(1)

  @Before def initactorSysterm(): Unit = {
    actorSystem = AkkaUtils.startMasterActorSystem()
  }


  @Test def test(): Unit = {

    // 先建立actorSysterm
    val rpcService = new AkkaRpcService(actorSystem, time)


    val taskExecutor = new TaskExecutor(rpcService, "task")

    taskExecutor.start

  }
}

trait TaskGateWay extends RpcGateway {

  def regiest = {

  }

}


class TaskExecutor(rpcService: RpcService, endpointId: String) extends RpcEndpoint(rpcService, endpointId) with TaskGateWay with Logging {
  override def start: Unit = {

    log.info(s"taskexecutor start")
  }

  override def stop: Unit = ???

  override def preStart: Unit = ???

  override def preStop: Unit = ???

  override def getAddress: String = ???

  override def getHostname: String = ???
}

trait JobGateWay extends RpcGateway {

}

