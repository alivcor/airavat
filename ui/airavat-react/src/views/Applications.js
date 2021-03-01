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
    key: 'startTimestamp', 
    label: 'Start Time',
    _style: { width: '20%'} ,
    sorter: true,
    filter: true
  },
  { 
    key: 'endTimestamp', 
    label: 'End time',
    _style: { width: '20%'} ,
    sorter: true,
    filter: true
  },
  { 
    key: 'status', 
    label: 'Status',
    _style: { width: '5%'} ,
    sorter: false,
    filter: false
  }
]



class Applications extends Component {

  // ["Abhinandans-MacBook-Pro.local", "127.0.0.1", "local-1614559631695", 1614559637, 0]

  

  state = {
    apps: []
  }

  componentDidMount() {
    fetch('http://localhost:8000/apps')
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
          Applications
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
                      'name':
                          (item, index)=>{
                            return (
                              <td>
                                {item.appId}
                              </td>
                          )
                        },
                      'startTimestamp':
                          (item, index)=>{
                            return (
                              <td>
                                {this.timeConverter(parseInt(item.startTimestamp))}
                              </td>
                          )
                        },
                      'endTimestamp':
                          (item, index)=>{
                            return (
                              <td>
                                {item.endTimestamp == 0? '' : this.timeConverter(parseInt(item.endTimestamp))}
                              </td>
                          )
                        },
                        'status':
                            (item, index)=>{
                              return (
                                <td>
                                  <CBadge color={item.endTimestamp == 0? 'primary' : 'success'}>
                                    {item.endTimestamp == 0? 'Active' : 'Finished'}
                                  </CBadge>
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

export default Applications;


// const Applications = ({appState}) => {
//   return (
//     <>
//       <CCard>
//         <CCardHeader>
//           Applications
//           <DocsLink href="https://alivcor.github.io/airavat/"/>
//         </CCardHeader>
//         <CCardBody>
//           <CRow>
//           {appState}
            
//           </CRow>
//         </CCardBody>
//       </CCard>
      
      
      
//     </>
//   )
// }

// export default Applications
