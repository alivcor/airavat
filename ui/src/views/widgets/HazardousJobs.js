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
  CCollapse,
  CModal,
  CModalBody,
  CModalHeader,
  CModalFooter,
  CModalTitle
} from '@coreui/react'
import { rgbToHex } from '@coreui/utils'
import { DocsLink } from 'src/reusable'
import CIcon from '@coreui/icons-react'



const fields = [
  'hostname',
  { 
    key: 'appId', 
    label: 'Name',
    _style: { width: '25%'} ,
    sorter: true,
    filter: true
  },
  { 
    key: 'jobId', 
    label: 'Job Id',
    _style: { width: '5%'} ,
    sorter: true,
    filter: true
  },
  { 
    key: 'startTimestamp', 
    label: 'Start Time',
    _style: { width: '5%'} ,
    sorter: true,
    filter: false
  },
  'killedCause', 
  { 
    key: 'duration', 
    label: 'Duration',
    _style: { width: '5%'} ,
    sorter: true,
    filter: false
  }
]




class HazardousJobs extends Component {


  constructor(props) {
    super(props);
    console.log(props);
    this.state = {
      setAppState: props.setAppState
    };
    console.log(this.state.setAppState);
  }
  
  timeConverter(UNIX_timestamp){
    var a = new Date(UNIX_timestamp*1000);
    var months = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
    var year = a.getFullYear();
    var month = months[a.getMonth()];
    var date = a.getDate();
    var hour = a.getHours();
    var min = a.getMinutes();
    var sec = a.getSeconds();
    var time = month + ' ' + date + ', ' + year + ' ' + hour + ':' + min + ':' + sec ;
    // console.log(time);
    return time;
  }

  render() {

    
    return (
      <>
      <CCard>
        <CCardHeader>
          Hazardous Jobs
          <DocsLink href="https://alivcor.github.io/airavat/"/>
        </CCardHeader>
        <CCardBody>
          <CRow>
            <CCol>
           
          <CDataTable
                    items={this.state.setAppState.killedJobs}
                    fields={fields}
                    columnFilter
                    tableFilter
                    itemsPerPageSelect
                    itemsPerPage={5}
                    hover
                    sorter
                    pagination
                    scopedSlots = {{
                      'hostname':
                        (item)=>(
                          <td>
                            {item.hostname}
                          </td>
                        ),
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
                        'startTimestamp':
                            (item, index)=>{
                              return (
                                <td>
                                  {item.timestamp}
                                </td>
                            )
                          },
                        'killedCause':
                            (item, index)=>{
                              return (
                                <td>
                                  {item.killedCause}
                                </td>
                            )
                          },
                        'duration':
                            (item, index)=>{
                              return (
                                <td>
                                  {item.totalDuration} ms
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





export default HazardousJobs
