import React from 'react'
import CIcon from '@coreui/icons-react'
import { freeSet } from '@coreui/icons'
import {
  cilGrain
} from '@coreui/icons'


const _nav =  [
  {
    _tag: 'CSidebarNavItem',
    name: 'Dashboard',
    to: '/dashboard',
    icon: <CIcon name="cil-speedometer" customClasses="c-sidebar-nav-icon"/>,
    badge: {
      color: 'info',
    }
  },
  {
    _tag: 'CSidebarNavTitle',
    _children: ['Inspect']
  },
  {
    _tag: 'CSidebarNavItem',
    name: 'Jobs',
    to: '/jobs',
    icon: 'cib-apache-spark',
  },
  {
    _tag: 'CSidebarNavItem',
    name: 'Queries',
    to: '/queries',
    icon: 'cil-grain',
  },
  {
    _tag: 'CSidebarNavItem',
    name: 'Limits',
    to: '/limits',
    icon: 'cil-warning',
  },
  {
    _tag: 'CSidebarNavTitle',
    _children: ['Manage']
  },
  {
    _tag: 'CSidebarNavItem',
    name: 'Users',
    to: '/users',
    icon: 'cil-user',
  }
]

export default _nav
