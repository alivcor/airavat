package com.iresium.airavat.sickle

import org.apache.spark.sql.execution.SparkPlan
import org.apache.spark.sql.execution.aggregate.HashAggregateExec

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

abstract class CherryNode {
    var children = scala.collection.mutable.Seq[CherryNode]()
    var nodeName = ""
    var nodeType = this.getClass.getName

}

class StemNode extends CherryNode {

}

class WholeStageCodegenExecNode(t: SparkPlan) extends CherryNode {
    var collectLimit: Int = 0
    nodeName = t.nodeName
}

class HashAggregateExecNode(t: HashAggregateExec) extends CherryNode {
    var groupByExp: String = t.groupingExpressions.toString()
    var aggregateExp: String = t.aggregateExpressions.toString()
    var aggregateAttrs: String = t.aggregateAttributes.toString()
}


class BroadcastHashJoinExecNode(t: SparkPlan) extends CherryNode {

}

class ShuffledHashJoinExecNode(t: SparkPlan) extends CherryNode {

}

class SortMergeJoinExecNode(t: SparkPlan) extends CherryNode {

}

class RDDScanExecNode(t: SparkPlan) extends CherryNode {

}

class DataSourceScanExecNode(t: SparkPlan) extends CherryNode {

}

class InMemoryTableScanExecNode(t: SparkPlan) extends CherryNode {

}

class ShuffleExchangeExecNode(t: SparkPlan) extends CherryNode {

}

class InputAdapterNode(t: SparkPlan) extends CherryNode {

}


class ProjectExecNode(t: SparkPlan) extends CherryNode {
    var projectList: String = ""
}

class FilterExecNode(t: SparkPlan) extends CherryNode {
    var filterCondition: String = ""
}

class FileSourceScanExecNode(t: SparkPlan) extends CherryNode {
    var partitionFilters: Seq[String] = _
    var dataFilters: Seq[String] = _
    var pushedDownFilters: Seq[String] = _
    var relation: String = _
}

class UnknownNode(t: SparkPlan) extends CherryNode {
    var stringExp: String = ""
}