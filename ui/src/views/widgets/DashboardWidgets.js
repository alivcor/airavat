import React from 'react'
import {
  CWidgetDropdown,
  CRow,
  CCol,
  CDropdown,
  CDropdownMenu,
  CDropdownItem,
  CDropdownToggle,
  CWidgetProgress
} from '@coreui/react'
import CIcon from '@coreui/icons-react'
import ChartLineSimple from '../charts/ChartLineSimple'
import ChartBarSimple from '../charts/ChartBarSimple'


var appCounts = [];

const WidgetsDropdown = ({setAppState}) => {
  // render
  // console.log(setAppState);
  
  // console.log("setAppState.appCountHistory");
  // console.log(setAppState.appCountHistory);
  return (
    <CRow>
      <CCol sm="6" lg="3">
        <CWidgetDropdown
          color="gradient-primary"
          header={setAppState.apps.length == 0 ? "0" : setAppState.apps.length}
          text="Active Applications"
          footerSlot={
            <ChartLineSimple
              pointed
              className="c-chart-wrapper mt-3 mx-3"
              style={{height: '70px'}}
              dataPoints={setAppState.appCountHistory} //{[5, 9, 4, 4, 1, 7, 4, 5]}
              pointHoverBackgroundColor="primary"
              label="Apps"
              labels="months"
            />
          }
        >
          
        </CWidgetDropdown>
      </CCol>

      <CCol sm="6" lg="3">
        <CWidgetDropdown
          color="gradient-info"
          header={setAppState.jobs.length == 0 ? "0" : setAppState.jobs.length }
          text="Completed Jobs"
          footerSlot={
            <ChartLineSimple
              pointed
              className="mt-3 mx-3"
              style={{height: '70px'}}
              dataPoints={setAppState.completedJobCountHistory}
              pointHoverBackgroundColor="info"
              options={{ elements: { line: { tension: 0.00001 }}}}
              label="Jobs"
              labels="months"
            />
            // <CIcon size={'4xl'} name={'cilSettings'} style={{height: '85px'}} className="float-right"/>
          }
        >
         
        </CWidgetDropdown>
      </CCol>

      <CCol sm="6" lg="3">
        <CWidgetDropdown
          color="gradient-danger"
          header={setAppState.killedJobs.length == 0 ? "0": setAppState.killedJobs.length}
          text="Failed Jobs"
          footerSlot={
            <ChartLineSimple
              className="mt-3"
              style={{height: '70px'}}
              backgroundColor="rgba(255,255,255,.2)"
              dataPoints={setAppState.failedJobCountHistory}
              options={{ elements: { line: { borderWidth: 2.5 }}}}
              pointHoverBackgroundColor="warning"
              label=""
              labels=""
            />
          }
        >
          
        </CWidgetDropdown>
      </CCol>


      
 
      <CCol sm="6" lg="3">
        <CWidgetDropdown
          color="gradient-success"
          header={setAppState.executions.length == 0 ? "0" : setAppState.executions.length}
          text="Completed Queries"
          footerSlot={
            <ChartBarSimple
              className="mt-3 mx-3"
              style={{height: '70px'}}
              backgroundColor="rgb(196, 201, 208)"
              dataPoints={setAppState.completedExecutionCountHistory}
              label=""
              labels=""
            />
          }
        >
        </CWidgetDropdown>
      </CCol>

      {/* <CCol sm="6" lg="3">
        <CWidgetProgress
          color="danger"
          header="12"
          text="Jobs Killed"
          footer="Potentially hazardous jobs"
        />
      </CCol> */}
      <CCol sm="6" lg="12">
        <CWidgetProgress
          color="danger"
          header={setAppState.killedJobsFraction.toFixed(2) + '%'}
          text="Jobs Killed"
          footer="Potentially hazardous jobs"
          value={setAppState.killedJobsFraction.toFixed(2)}
        />
      </CCol>
     
    </CRow>
  )
}

export default WidgetsDropdown
