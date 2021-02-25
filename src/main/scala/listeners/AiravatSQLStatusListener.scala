/*
 * Created by @alivcor (Abhinandan Dubey) on 2/24/21 
 * Licensed under the Mozilla Public License Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

//package listeners
//
//import com.iresium.airavat.QueryMetricSerializer
//import org.apache.spark.SparkConf
//import org.apache.spark.scheduler.SparkListenerEvent
//import org.apache.spark.sql.SparkSession
//import org.apache.spark.sql.execution.ui.{SQLAppStatusListener, SparkListenerSQLExecutionEnd, SparkListenerSQLExecutionStart}
//import scala.util.Try
//import org.apache.spark.status.{ElementTrackingStore, KVUtils, LiveEntity}
//
//class AiravatSQLStatusListener(conf: SparkConf,
//                               kvstore: ElementTrackingStore,
//                               live: Boolean) extends SQLAppStatusListener {
//
//
//    override def onOtherEvent(event: SparkListenerEvent): Unit = {
//        event match {
//            case executionEnd: SparkListenerSQLExecutionEnd =>
//                super.liveExecutionMetrics(executionEnd.executionId)
//
//            case _ =>
//        }
//    }
//}
