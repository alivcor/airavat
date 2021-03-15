
/*
 * Created by @alivcor (Abhinandan Dubey) on 2/27/21
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

import org.apache.spark.sql.execution.{DataSourceScanExec, FileSourceScanExec, RDDScanExec, RowDataSourceScanExec}
import org.apache.spark.sql.execution.columnar.InMemoryTableScanExec
import org.apache.spark.sql.execution.metric.SQLMetric


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
