import React, { lazy } from 'react'


const DashboardWidgets = lazy(() => import('../widgets/DashboardWidgets.js'))
const HazardousJobs = lazy(() => import('../widgets/HazardousJobs.js'))




const Dashboard = ({setAppState}) => {
  

  return (
    <>
      <DashboardWidgets setAppState={setAppState}/>
      <HazardousJobs setAppState={setAppState}/>
      
    </>
  )
}

export default Dashboard
