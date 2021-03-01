import React, { Component, useEffect, useState, createRef } from 'react'
import classNames from 'classnames'
import {
  CRow,
  CCol,
  CCard,
  CCardHeader,
  CDataTable,
  CCardBody,
  CBadge,
  CButton,
  CCollapse
} from '@coreui/react'
import { rgbToHex } from '@coreui/utils'
import { DocsLink } from 'src/reusable'


// def hostname = column[String]("hostname")
//     def ipAddress = column[String]("ipAddress")
//     def appId = column[String]("appId")
//     def executionId = column[Long]("executionId")
//     def jobIds = column[String]("jobIds")
//     def description = column[String]("description")
//     def startTimestamp = column[Long]("startTimestamp")
//     def sparkPlan = column[String]("sparkPlan")
//     def endTimestamp = column[Long]("endTimestamp")
//     def numTasks = column[Int]("numTasks")
//     def totalDiskSpill = column[Long]("totalDiskSpill")
//     def totalBytesRead = column[Long]("totalBytesRead")
//     def totalBytesWritten = column[Long]("totalBytesWritten")
//     def totalResultSize = column[Long]("totalResultSize")
//     def totalShuffleReadBytes = column[Long]("totalShuffleReadBytes")
//     def totalShuffleWriteBytes = column[Long]("totalShuffleWriteBytes")

const fields = [
  { 
    key: 'hostname', 
    label: 'Host',
    _style: { width: '25%'},
    sorter: true,
    filter: true
  },
  { 
    key: 'ipAddress', 
    label: 'IP',
    _style: { width: '10%'} ,
    sorter: true,
    filter: true
  },
  { 
    key: 'appId', 
    label: 'Name',
    _style: { width: '25%'} ,
    sorter: true,
    filter: true
  },
  { 
    key: 'executionId', 
    label: 'SQL Id',
    _style: { width: '5%'} ,
    sorter: true,
    filter: true
  },
  { 
    key: 'jobIds', 
    label: 'Job IDs',
    _style: { width: '5%'} ,
    sorter: true,
    filter: true
  },
  { 
    key: 'description', 
    label: 'Description',
    _style: { width: '5%'} ,
    sorter: true,
    filter: false
  },
  { 
    key: 'startTimestamp', 
    label: 'Start Time',
    _style: { width: '5%'} ,
    sorter: true,
    filter: false
  },
  { 
    key: 'endTimestamp', 
    label: 'End Time',
    _style: { width: '5%'} ,
    sorter: true,
    filter: false
  },
  { 
    key: 'sparkPlan', 
    label: 'Spark Plan',
    _style: { width: '5%'} ,
    sorter: true,
    filter: false
  },
  { 
    key: 'numTasks', 
    label: 'Tasks',
    _style: { width: '5%'} ,
    sorter: true,
    filter: false
  },
  { 
    key: 'totalDiskSpill', 
    label: 'Disk Spill',
    _style: { width: '5%'} ,
    sorter: true,
    filter: false
  },
  { 
    key: 'totalBytesRead', 
    label: 'Total Read',
    _style: { width: '5%'} ,
    sorter: true,
    filter: false
  },
  { 
    key: 'totalBytesWritten', 
    label: 'Total Write',
    _style: { width: '5%'} ,
    sorter: true,
    filter: false
  },
  { 
    key: 'totalResultSize', 
    label: 'Result Size',
    _style: { width: '5%'} ,
    sorter: true,
    filter: false
  },
  { 
    key: 'totalShuffleReadBytes', 
    label: 'Shuffle Read',
    _style: { width: '5%'} ,
    sorter: true,
    filter: false
  },
  { 
    key: 'totalShuffleWriteBytes', 
    label: 'Shuffle Write',
    _style: { width: '5%'} ,
    sorter: true,
    filter: false
  }
]




class Queries extends Component {

  state = {
    apps: []
  }

  componentDidMount() {
    fetch('http://localhost:8000/queries')
    .then(res => res.json())
    .then((data) => {
      console.log(data);
      this.setState({ apps: data })
      // console.log(this.state.apps)
    })
    .catch(console.log)
  }

  //{details.includes(index) ? 'Hide' : 'Show'}
  timeConverter(UNIX_timestamp){
    var a = new Date(UNIX_timestamp * 1000);
    var months = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
    var year = a.getFullYear();
    var month = months[a.getMonth()];
    var date = a.getDate();
    var hour = a.getHours();
    var min = a.getMinutes();
    var sec = a.getSeconds();
    var time = month + ' ' + date + ', ' + year + ' ' + hour + ':' + min + ':' + sec ;
    console.log(time);
    return time;
  }

  render() {
    return (
      <>
      <CCard>
        <CCardHeader>
          Queries
          <DocsLink href="https://alivcor.github.io/airavat/"/>
        </CCardHeader>
        <CCardBody>
          <CRow>
            <CCol>
          <CDataTable
                    items={this.state.apps}
                    fields={fields}
                    columnFilter
                    tableFilter
                    itemsPerPageSelect
                    itemsPerPage={5}
                    hover
                    sorter
                    pagination
                    scopedSlots = {{
                      'host':
                        (item)=>(
                          <td>
                            {item.hostname}
                          </td>
                        ),
                      'ipAddress':
                        (item, index)=>{
                          return (
                            <td>
                              {item.ipAddress}
                            </td>
                            )
                        },
                      'appId':
                          (item, index)=>{
                            return (
                              <td>
                                {item.appId}
                              </td>
                          )
                        },
                      'executionId':
                          (item, index)=>{
                            return (
                              <td>
                                {item.executionId}
                              </td>
                          )
                        },
                      'jobIds':
                          (item, index)=>{
                            return (
                              <td>
                                {item.jobIds}
                              </td>
                          )
                        },
                        'description':
                            (item, index)=>{
                              return (
                                <td>
                                  {item.description}
                                </td>
                            )
                          },
                        'startTimestamp':
                            (item, index)=>{
                              return (
                                <td>
                                  {item.startTimestamp}
                                </td>
                            )
                          },
                        'endTimestamp':
                            (item, index)=>{
                              return (
                                <td>
                                  {item.endTimestamp}
                                </td>
                            )
                          },
                        'sparkPlan':
                            (item, index)=>{
                              return (
                                <td>
                                  {item.sparkPlan}
                                </td>
                            )
                          },
                        'numTasks':
                            (item, index)=>{
                              return (
                                <td>
                                  {item.numTasks}
                                </td>
                            )
                          },
                          'totalDiskSpill':
                              (item, index)=>{
                                return (
                                  <td>
                                    {(item.totalDiskSpill / 1000000).toFixed(2)}
                                  </td>
                              )
                            },
                          'totalBytesRead':
                              (item, index)=>{
                                return (
                                  <td>
                                    {(item.totalBytesRead / 1000000).toFixed(2)}
                                  </td>
                              )
                            },
                          'totalBytesWritten':
                              (item, index)=>{
                                return (
                                  <td>
                                    {(item.totalBytesWritten / 1000000).toFixed(2)}
                                  </td>
                              )
                            },
                          'totalResultSize':
                              (item, index)=>{
                                return (
                                  <td>
                                    {(item.totalResultSize / 1000000).toFixed(2)}
                                  </td>
                              )
                            },
                        'totalShuffleReadBytes':
                            (item, index)=>{
                              return (
                                <td>
                                  {(item.totalShuffleReadBytes / 1000000).toFixed(2)}
                                </td>
                            )
                          },
                        'totalShuffleWriteBytes':
                            (item, index)=>{
                              return (
                                <td>
                                  {(item.totalShuffleWriteBytes / 1000000).toFixed(2)}
                                </td>
                            )
                          }
                    }}
                  />
                  </CCol>
          </CRow>
        </CCardBody>

      </CCard>
      
      
      
    </>
    );
  }

}





export default Queries
