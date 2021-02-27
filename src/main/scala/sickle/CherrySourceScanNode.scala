package com.iresium.airavat.sickle

import org.apache.spark.sql.execution.{DataSourceScanExec, FileSourceScanExec, RDDScanExec, RowDataSourceScanExec}
import org.apache.spark.sql.execution.columnar.InMemoryTableScanExec
import org.apache.spark.sql.execution.metric.SQLMetric

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

abstract class CherrySourceScanNode(t: DataSourceScanExec) extends CherryNode {
    val metrics: Map[String, SQLMetric] = t.metrics

    if(!t.tableIdentifier.isEmpty){
        val tableName = t.tableIdentifier.get.table
        val databaseName = t.tableIdentifier.get.database.getOrElse("")
    }
}

class RowDataSourceScanExecNode(t: RowDataSourceScanExec) extends CherrySourceScanNode(t) {
    val filters = t.filters.toString()
    val metadata = t.metadata
}

class FileSourceScanExecNode(t: FileSourceScanExec) extends CherrySourceScanNode(t) {
    val partitionFilters: String = t.partitionFilters.toString()
    val dataFilters: String = t.dataFilters.toString()
    val relation: String = t.relation.toString()
    val fileFormat: String = t.relation.fileFormat.getClass.getSimpleName
    val metadata: Map[String, String] = t.metadata
}


class DataSourceScanExecNode(t: DataSourceScanExec) extends CherrySourceScanNode(t) {
    val relation: String = t.relation.toString()
}

class InMemoryTableScanExecNode(t: InMemoryTableScanExec) extends CherryNode {
    val partitionFilters: String = t.partitionFilters.toString()
    val relation: String = t.relation.toString()
    val predicates = t.predicates.toString()
}

class RDDScanExecNode(t: RDDScanExec) extends CherryNode  {
    val metrics = t.metrics
}
