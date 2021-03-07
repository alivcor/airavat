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


// def hostname = column[String]("hostname")
// def ipAddress = column[String]("ipAddress")
// def appId = column[String]("appId")
// def executionId = column[Long]("executionId")
// def description = column[String]("description")
// def startTimestamp = column[Long]("startTimestamp")
// def endTimestamp = column[Long]("endTimestamp")
// def sparkPlan = column[String]("sparkPlan")
// def logicalPlan = column[String]("logicalPlan")
// def optimizedPlan = column[String]("optimizedPlan")
// def executedPlan = column[String]("executedPlan")
// def queryStats = column[String]("queryStats")
// def duration = column[Long]("duration")
// def metrics = column[String]("metrics")
// def serializedPlan = column[String]("serializedPlan")
// def exceptionStackTrace = column[String]("exceptionStackTrace")

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
    key: 'queryStats', 
    label: 'Stats',
    _style: { width: '5%'} ,
    sorter: true,
    filter: false
  },
  { 
    key: 'duration', 
    label: 'Duration',
    _style: { width: '5%'} ,
    sorter: true,
    filter: false
  }
  // { 
  //   key: 'metrics', 
  //   label: 'Metrics',
  //   _style: { width: '5%'} ,
  //   sorter: true,
  //   filter: false
  // },
  // { 
  //   key: 'serializedPlan', 
  //   label: 'Plan Visual',
  //   _style: { width: '5%'} ,
  //   sorter: false,
  //   filter: false
  // }
]




class Queries extends Component {


  constructor(props) {
    super(props);
    this.state = {
      planModal: false,
      metricModal: false,
      execId: '',
      execPlan: '',
      execMetrics: '',
    };

    this.togglePlan = this.togglePlan.bind(this);
    this.toggleMetrics = this.toggleMetrics.bind(this);
  }

  togglePlan() {
    this.setState({
      planModal: !this.state.planModal,
    });
  }
  toggleMetrics() {
    this.setState({
      metricModal: !this.state.metricModal,
    });
  }

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
          Queries
          <DocsLink href="https://alivcor.github.io/airavat/"/>
        </CCardHeader>
        <CCardBody>
          <CRow>
            <CCol>
            <CModal show={this.state.planModal} onClose={this.togglePlan} size="lg">
              <CModalHeader closeButton>Query Plan for Query {this.state.execId}</CModalHeader>
              <CModalBody>
                <pre>
              {this.state.execPlan}
              </pre>
              </CModalBody>
              <CModalFooter>
                <CButton color="secondary" onClick={() => {navigator.clipboard.writeText(this.state.execPlan)}}><CIcon name={'cilClone'} color="primary"/></CButton>
                <CButton color="secondary" onClick={this.togglePlan}>Close</CButton>
              </CModalFooter>
            </CModal>
            <CModal show={this.state.metricModal} onClose={this.toggleMetrics} size="lg">
              <CModalHeader closeButton>Query Metrics for Query {this.state.execId}</CModalHeader>
              <CModalBody>
              <pre>{this.state.execMetrics}</pre>
              </CModalBody>
              <CModalFooter>
                <CButton color="secondary" onClick={() => {navigator.clipboard.writeText(this.state.execMetrics)}}><CIcon name={'cilClone'} color="primary"/></CButton>
                <CButton color="secondary" onClick={this.toggleMetrics}>Close</CButton>
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
                                  {this.timeConverter(parseInt(item.startTimestamp))}
                                </td>
                            )
                          },
                        'endTimestamp':
                            (item, index)=>{
                              return (
                                <td>
                                  {this.timeConverter(parseInt(item.endTimestamp))}
                                </td>
                            )
                          },
                        'queryStats':
                            (item, index)=>{
                              // console.log(item.queryStats)
                              return (
                                <td>
                                  <CBadge color="primary" onClick={() => {
                                    this.setState({
                                      planModal: !this.state.planModal,
                                      execId: item.executionId,
                                      // execPlan: item.queryStats
                                      execPlan: "logicalPlan : \n" + item.logicalPlan + "\n\n executedPlan : \n" + item.executedPlan + "\n\n queryStats : \n" + item.queryStats
                            //           sparkPlan: String,
                            // logicalPlan: String = "",
                            // optimizedPlan: String =  "",
                            // executedPlan: String = "",
                            // queryStats: String = "",
                                    })
                                    this.togglePlan()
                                    }} className="mr-1" color="info"  href="#">View Plan</CBadge>
                                    <CBadge color="success" onClick={() => {
                                    this.setState({
                                      metricModal: !this.state.metricModal,
                                      execId: item.executionId,
                                      execMetrics: JSON.stringify(JSON.parse(item.metrics), undefined, 2)
                                    })
                                    this.toggleMetrics()
                                    }} className="mr-1" color="success"  href="#">View Metrics</CBadge>
                                </td>
                            )
                          },
                        'duration':
                            (item, index)=>{
                              return (
                                <td>
                                  {item.duration} ms
                                </td>
                            )
                          },
                          // 'metrics':
                          //     (item, index)=>{
                          //       return (
                          //         <td>
                          //         <pre>{JSON.stringify(JSON.parse(item.metrics), undefined, 2)}</pre>
                          //         </td>
                          //     )
                          //   },
                          // 'serializedPlan':
                          //     (item, index)=>{
                          //       return (
                          //          <td>
                          //          <pre>{JSON.stringify(JSON.parse(item.serializedPlan), undefined, 2)}</pre>
                          //          </td>
                          //     )
                            // }
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
