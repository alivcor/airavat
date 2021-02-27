/*
 * Created by @alivcor (Abhinandan Dubey) on 2/25/21 
 * Licensed under the Mozilla Public License Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.iresium.airavat.sickle

import org.apache.spark.sql.execution._
import org.apache.spark.sql.execution.aggregate.HashAggregateExec
import org.apache.spark.sql.execution.exchange.{BroadcastExchangeExec, ShuffleExchangeExec}



abstract class CherryNode {
    var children = scala.collection.mutable.Seq[CherryNode]()
    var nodeName = ""
    var nodeType = this.getClass.getName
}


class StemNode extends CherryNode {

}

class WholeStageCodegenExecNode(t: WholeStageCodegenExec) extends CherryNode {
    val collectLimit: Int = 0
    nodeName = t.nodeName
}

class CollectLimitExecNode(t: CollectLimitExec) extends CherryNode {
    val limit: Int = t.limit
    nodeName = t.nodeName
}

class HashAggregateExecNode(t: HashAggregateExec) extends CherryNode {
    val groupByExp: String = t.groupingExpressions.toString()
    val aggregateExp: String = t.aggregateExpressions.toString()
    val aggregateAttrs: String = t.aggregateAttributes.toString()
}



class ShuffleExchangeExecNode(t: ShuffleExchangeExec) extends CherryNode {

}

class InputAdapterNode(t: InputAdapter) extends CherryNode {

}


class ProjectExecNode(t: SparkPlan) extends CherryNode {
    var projectList: String = ""
}

class FilterExecNode(t: FilterExec) extends CherryNode {
    val filterCondition: String = t.condition.toString()
}



class TakeOrderedAndProjectExecNode(t: TakeOrderedAndProjectExec) extends CherryNode {
    var stringExp: String = ""
}

class BroadcastExchangeExecNode(t: BroadcastExchangeExec) extends CherryNode {
    val broadcastMode = t.mode
    val metrics = t.metrics
}


class UnknownNode(t: SparkPlan) extends CherryNode {
    var stringExp: String = ""
}