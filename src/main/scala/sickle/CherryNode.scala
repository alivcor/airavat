/*
 * Created by @alivcor (Abhinandan Dubey) on 2/25/21 
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction,
 *  including without limitation the rights to use, copy, modify,
 *  merge, publish, distribute, sublicense, and/or sell copies
 *  of the Software, and to permit persons to whom the Software
 *  is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice, author's credentials and this
 * permission notice shall be included in all copies
 * or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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