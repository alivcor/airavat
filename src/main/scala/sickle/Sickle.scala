package com.iresium.airavat.sickle

import org.apache.spark.sql.execution._
import org.apache.spark.sql.execution.aggregate.HashAggregateExec
import org.apache.spark.sql.execution.columnar.InMemoryTableScanExec
import org.apache.spark.sql.execution.exchange.ShuffleExchangeExec
import org.apache.spark.sql.execution.joins.{BroadcastHashJoinExec, ShuffledHashJoinExec, SortMergeJoinExec}
import org.apache.spark.sql.execution.{LeafExecNode, SparkPlan}


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

object Sickle {

    def cherryPick(executedPlan: SparkPlan): CherryNode = {
        def recurse(treeNode: SparkPlan, cherryNode: CherryNode): CherryNode = {
            val newCherryNode: CherryNode = treeNode match {
                    case _: WholeStageCodegenExec => new WholeStageCodegenExecNode(treeNode)
                    case _: HashAggregateExec => new HashAggregateExecNode(treeNode)
                    case _: BroadcastHashJoinExec => new BroadcastHashJoinExecNode(treeNode)
                    case _: ShuffledHashJoinExec => new ShuffledHashJoinExecNode(treeNode)
                    case _: SortMergeJoinExec => new SortMergeJoinExecNode(treeNode)
                    case _: RDDScanExec => new RDDScanExecNode(treeNode)
                    case _: DataSourceScanExec => new DataSourceScanExecNode(treeNode)
                    case _: InMemoryTableScanExec => new InMemoryTableScanExecNode(treeNode)
                    case _: ShuffleExchangeExec => new ShuffleExchangeExecNode(treeNode)
                    case _: ProjectExec => new ProjectExecNode(treeNode)
                    case _: FilterExec => new FilterExecNode(treeNode)
                    case _: FileSourceScanExec => new FileSourceScanExecNode(treeNode)
                    case _ => new UnknownNode(treeNode)
                }
            if(!treeNode.children.isEmpty){
                treeNode.children.foreach(recurse(_, newCherryNode))
            }
            cherryNode.children = cherryNode.children :+ newCherryNode
            cherryNode
        }
        recurse(executedPlan, new StemNode())
    }
//treeNode.prettyJson

    type Metrics = Map[String, Long]

    def getAggregatedQueryMetrics(executedPlan: SparkPlan): (Metrics, Metrics) = {

        def getNodeMetrics(node: SparkPlan): Metrics = node.metrics.mapValues(_.value)

        val aggregatedReadMetrics: Metrics = {
            @scala.annotation.tailrec
            def recursePlanTree(acc: Metrics, nodes: Seq[SparkPlan]): Metrics = {
                nodes match {
                    case Nil => acc
                    case (leaf: LeafExecNode) +: queue =>
                        recursePlanTree(acc ++ getNodeMetrics(leaf), queue)
                    case (node: SparkPlan) +: queue =>
                        recursePlanTree(acc, node.children ++ queue)

                }
            }

            recursePlanTree(Map.empty, Seq(executedPlan))
        }

        (aggregatedReadMetrics, getNodeMetrics(executedPlan))
    }
}
