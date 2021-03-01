import React, { useEffect, useState, createRef } from 'react'
import classNames from 'classnames'
import {
  CRow,
  CCol,
  CCard,
  CCardHeader,
  CCardBody
} from '@coreui/react'
import { rgbToHex } from '@coreui/utils'
import { DocsLink } from 'src/reusable'





const Jobs = () => {
  return (
    <>
      <CCard>
        <CCardHeader>
          Jobs
          <DocsLink href="https://alivcor.github.io/airavat/"/>
        </CCardHeader>
        <CCardBody>
          <CRow>
          <table className="table w-100">
          <tbody>
          <tr>
            <td className="text-muted">1</td>
            <td className="font-weight-bold"> 2</td>
            <td className="font-weight-bold"> 3</td>
            <td className="font-weight-bold"> 4</td>
          </tr>
          <tr>
            <td className="text-muted">a</td>
            <td className="font-weight-bold"> b</td>
            <td className="font-weight-bold"> c</td>
            <td className="font-weight-bold"> d</td>
          </tr>
          </tbody>
        </table>
            
          </CRow>
        </CCardBody>
      </CCard>
      
      
      
    </>
  )
}

export default Jobs
