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
    key: 'jobId', 
    label: 'Job ID',
    _style: { width: '5%'} ,
    sorter: true,
    filter: true
  },
  { 
    key: 'numStages', 
    label: 'Stages',
    _style: { width: '5%'} ,
    sorter: true,
    filter: true
  },
  { 
    key: 'numTasks', 
    label: 'Tasks',
    _style: { width: '5%'} ,
    sorter: true,
    filter: false
  },
  { 
    key: 'totalDuration', 
    label: 'Duration',
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



class Jobs extends Component {

  state = {
    apps: []
  }

  componentDidMount() {
    fetch('http://localhost:8000/jobs')
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
          Jobs
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
                      'jobId':
                          (item, index)=>{
                            return (
                              <td>
                                
                                <a href={"http://" + item.ipAddress + ":4040/jobs/job/?id=" + item.jobId} target="_blank">{item.jobId}</a>
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
                        'totalDuration':
                            (item, index)=>{
                              return (
                                <td>
                                  {item.totalDuration}
                                </td>
                            )
                          },
                        'totalDiskSpill':
                            (item, index)=>{
                              return (
                                <td>
                                  {(parseFloat(item.totalDiskSpill) / 1000000).toFixed(2)}
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


export default Jobs
