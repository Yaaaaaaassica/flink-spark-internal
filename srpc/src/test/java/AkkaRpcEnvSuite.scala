/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.spark.rpc.akka

import org.apache.spark.SparkConf
import org.apache.spark.rpc.{RpcEndpoint, RpcEnv, RpcEnvConfig}
import org.junit.{Before, Test}

class AkkaRpcEnvSuite {

  def createRpcEnv(conf: SparkConf,
                   name: String,
                   port: Int,
                   clientMode: Boolean = false): RpcEnv = {
    new AkkaRpcEnvFactory()
      .create(RpcEnvConfig(conf, name, "localhost", port, null, clientMode))
  }

  var env: RpcEnv = _

  @Before
  def init = {
    val conf = new SparkConf()
    env = createRpcEnv(conf, "local", 0)
  }

  @Test
  def akkaTest = {

    val ref = env.setupEndpoint("test_endpoint", new RpcEndpoint {
      override val rpcEnv = env

      override def receive = {
        case _ =>
      }
    })
    val conf = new SparkConf()
    val newRpcEnv = new AkkaRpcEnvFactory()
      .create(RpcEnvConfig(conf, "test", "localhost", 0, null, false))
    try {
      val newRef =
        newRpcEnv.setupEndpointRef("local", ref.address, "test_endpoint")
      assert(
        s"akka.tcp://local@${env.address}/user/test_endpoint" equals newRef
          .asInstanceOf[AkkaRpcEndpointRef]
          .actorRef
          .path
          .toString
      )
    } finally {
      newRpcEnv.shutdown()
    }
  }

}
