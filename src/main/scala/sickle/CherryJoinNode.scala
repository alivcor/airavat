package com.iresium.airavat.sickle

import org.apache.spark.sql.execution.BinaryExecNode
import org.apache.spark.sql.execution.joins._

/*
 * Created by @alivcor (Abhinandan Dubey) on 2/27/21 
 * Licensed under the Mozilla Public License Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

abstract class CherryHashJoinNode(t: HashJoin) extends CherryNode {
    val leftKeys = t.leftKeys.toString()
    val rightKeys = t.rightKeys.toString()
    val joinType = t.joinType.toString()
    val condition = t.condition.toString()

}

abstract class CherrySortMergeJoinNode(t: SortMergeJoinExec) extends CherryNode {
    val leftKeys = t.leftKeys.toString()
    val rightKeys = t.rightKeys.toString()
    val joinType = t.joinType.toString()
    val condition = t.condition.toString()
    val metrics = t.metrics
}

abstract class CherryVanillaBinaryJoinNode(t: BinaryExecNode) extends CherryNode {
    val metrics = t.metrics
}

class BroadcastHashJoinExecNode(t: BroadcastHashJoinExec) extends CherryHashJoinNode(t) {

}

class ShuffledHashJoinExecNode(t: ShuffledHashJoinExec) extends CherryHashJoinNode(t) {

}

class SortMergeJoinExecNode(t: SortMergeJoinExec) extends CherrySortMergeJoinNode(t) {

}

class BroadcastNestedLoopJoinExecNode(t: BroadcastNestedLoopJoinExec) extends CherryVanillaBinaryJoinNode(t) {
    val joinType = t.joinType.toString()
    val condition = t.condition.toString()
}

