import React from 'react'
import {
  TheContent,
  TheSidebar,
  TheFooter,
  TheHeader
} from './index'

const TheLayout = ({ setAppState }) => {
  // console.log(setAppState);
  return (
    <div className="c-app c-default-layout">
      <TheSidebar setAppState={setAppState}/>
      <div className="c-wrapper">
        <TheHeader setAppState={setAppState}/>
        <div className="c-body">
          <TheContent setAppState={setAppState}/>
        </div>
        <TheFooter/>
      </div>
    </div>
  )
}

export default TheLayout
