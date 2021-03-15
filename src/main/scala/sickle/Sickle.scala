
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

import org.apache.spark.sql.execution.aggregate.HashAggregateExec
import org.apache.spark.sql.execution.columnar.InMemoryTableScanExec
import org.apache.spark.sql.execution.exchange.ShuffleExchangeExec
import org.apache.spark.sql.execution.joins.{BroadcastHashJoinExec, BroadcastNestedLoopJoinExec, ShuffledHashJoinExec, SortMergeJoinExec}
import org.apache.spark.sql.execution.{LeafExecNode, SparkPlan, _}


object Sickle {

    def cherryPick(executedPlan: SparkPlan): CherryNode = {
        def recurse(treeNode: SparkPlan, cherryNode: CherryNode): CherryNode = {
            val newCherryNode: CherryNode = treeNode match {
                    case _: WholeStageCodegenExec => new WholeStageCodegenExecNode(treeNode.asInstanceOf[WholeStageCodegenExec])
                    case _: HashAggregateExec => new HashAggregateExecNode(treeNode.asInstanceOf[HashAggregateExec])
                    case _: BroadcastHashJoinExec => new BroadcastHashJoinExecNode(treeNode.asInstanceOf[BroadcastHashJoinExec])
                    case _: ShuffledHashJoinExec => new ShuffledHashJoinExecNode(treeNode.asInstanceOf[ShuffledHashJoinExec])
                    case _: SortMergeJoinExec => new SortMergeJoinExecNode(treeNode.asInstanceOf[SortMergeJoinExec])
                    case _: InMemoryTableScanExec => new InMemoryTableScanExecNode(treeNode.asInstanceOf[InMemoryTableScanExec])
                    case _: ShuffleExchangeExec => new ShuffleExchangeExecNode(treeNode.asInstanceOf[ShuffleExchangeExec])
                    case _: ProjectExec => new ProjectExecNode(treeNode.asInstanceOf[ProjectExec])
                    case _: FilterExec => new FilterExecNode(treeNode.asInstanceOf[FilterExec])
                    case _: FileSourceScanExec => new FileSourceScanExecNode(treeNode.asInstanceOf[FileSourceScanExec])
                    case _: CollectLimitExec => new CollectLimitExecNode(treeNode.asInstanceOf[CollectLimitExec])
                    case _: InputAdapter => new InputAdapterNode(treeNode.asInstanceOf[InputAdapter])
                    case _: TakeOrderedAndProjectExec => new TakeOrderedAndProjectExecNode(treeNode.asInstanceOf[TakeOrderedAndProjectExec])
                    case _: RowDataSourceScanExec => new RowDataSourceScanExecNode(treeNode.asInstanceOf[RowDataSourceScanExec])
                    case _: DataSourceScanExec => new DataSourceScanExecNode(treeNode.asInstanceOf[DataSourceScanExec])
                    case _: BroadcastNestedLoopJoinExec => new BroadcastNestedLoopJoinExecNode(treeNode.asInstanceOf[BroadcastNestedLoopJoinExec])
                    case _: RDDScanExec => new RDDScanExecNode(treeNode.asInstanceOf[RDDScanExec])
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
