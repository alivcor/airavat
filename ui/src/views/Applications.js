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
  },
  'config'
]



class Applications extends Component {

  // ["Abhinandans-MacBook-Pro.local", "127.0.0.1", "local-1614559631695", 1614559637, 0]

  constructor(props) {
    super(props);
    this.state = {
      appModal: false,
      appId: '',
      appModalText: '',
    };
    this.toggleAppModal = this.toggleAppModal.bind(this);
  }

  state = {
    apps: []
  }

  toggleAppModal() {
    this.setState({
      appModal: !this.state.appModal,
    });
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

            <CModal show={this.state.appModal} onClose={this.toggleAppModal} size="lg">
              <CModalHeader closeButton>Configuration for App {this.state.appId}</CModalHeader>
              <CModalBody>
                <pre>
              {this.state.appModalText}
              </pre>
              </CModalBody>
              <CModalFooter>
                <CButton color="secondary" onClick={this.toggleAppModal}>Close</CButton>
              </CModalFooter>
            </CModal>

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
                          },
                        'config':
                            (item, index)=>{
                              return (
                                <td>
                                  {/* {item.sparkMaster}
                                  {item.driverMemory}
                                  {item.driverCores}
                                  {item.executorMemory}
                                  {item.executorCores}
                                  {item.numExecutors} */}
                                  
                                  <CBadge color="primary" onClick={() => {
                                    this.setState({
                                      appModal: !this.state.appModal,
                                      appId: item.appId,
                                      appModalText: `Spark Master : ${item.sparkMaster} \nDriver Memory : ${item.driverMemory}G \nDriver Cores : ${item.driverCores} \nExecutor Memory : ${item.executorMemory}G \nExecutor Cores : ${item.executorCores} \nExecutor Instances : ${item.numExecutors}`
                                    })
                                    this.toggleAppModal()
                                    }} className="mr-1" color="info"  href="#">View Config</CBadge>
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
