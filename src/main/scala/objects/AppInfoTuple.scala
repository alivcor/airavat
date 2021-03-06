/*
 * Created by @alivcor (Abhinandan Dubey) on 3/6/21 
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

case class AppInfoTuple(
                        hostname: String,
                        ipAddress: String,
                        appId: String,
                        startTimestamp: Long,
                        endTimestamp: Long,
                        sparkMaster: String,
                        driverMemory: String,
                        driverCores: String,
                        executorMemory: String,
                        executorCores: String,
                        numExecutors: String
                       )
