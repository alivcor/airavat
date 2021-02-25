/*
 * Created by @alivcor (Abhinandan Dubey) on 2/11/21 
 * Licensed under the Mozilla Public License Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.iresium.airavat

import org.apache.spark.storage.StorageLevel

case class RDDInfoTuple(id: Int,
    memSize: Long,
    diskSize: Long,
    numPartitions: Int,
    storageLevel: StorageLevel)


