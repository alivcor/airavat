import React, { Component } from 'react';
import { HashRouter, Route, Switch } from 'react-router-dom';
import './scss/style.scss';

const loading = (
  <div className="pt-3 text-center">
    <div className="sk-spinner sk-spinner-pulse"></div>
  </div>
)

// Containers
const TheLayout = React.lazy(() => import('./containers/TheLayout'));

// Pages
// const Login = React.lazy(() => import('./views/pages/login/Login'));
// const Register = React.lazy(() => import('./views/pages/register/Register'));
// const Page404 = React.lazy(() => import('./views/pages/page404/Page404'));
// const Page500 = React.lazy(() => import('./views/pages/page500/Page500'));


if(window.sessionStorage.getItem("appCountHistory") === null){
  console.log("appCountHistory is null");
  window.sessionStorage.setItem("appCountHistory", JSON.stringify([0]));
} 

if(window.sessionStorage.getItem("completedJobCountHistory") === null) {
  window.sessionStorage.setItem("completedJobCountHistory", JSON.stringify([0])) 
}

if(window.sessionStorage.getItem("failedJobCountHistory") === null) {
  window.sessionStorage.setItem("failedJobCountHistory", JSON.stringify([0])) 
}

if(window.sessionStorage.getItem("completedExecutionCountHistory") === null) {
  window.sessionStorage.setItem("completedExecutionCountHistory", JSON.stringify([0])) 
}


class App extends Component {

  state = {
    apps: [],
    jobs: [],
    queries: [],
    appCountHistory: [],
    completedJobCountHistory: [],
    failedJobCountHistory: [],
    completedExecutionCountHistory: []
  }

  updateAppHistory(data){
    var appCountHistory = JSON.parse(window.sessionStorage.getItem("appCountHistory"))
    if(appCountHistory[appCountHistory.length-1] !== data.length){
      appCountHistory.push(data.length)
    }
    window.sessionStorage.setItem("appCountHistory", JSON.stringify(appCountHistory))
    return appCountHistory;
  }

  updateCompletedJobHistory(data){
    var completedJobCountHistory = JSON.parse(window.sessionStorage.getItem("completedJobCountHistory"))
    if(completedJobCountHistory[completedJobCountHistory.length-1] !== data.length){
      completedJobCountHistory.push(data.length)
    }
    window.sessionStorage.setItem("completedJobCountHistory", JSON.stringify(completedJobCountHistory))
    return completedJobCountHistory;
  }

  updateFailedJobHistory(data){
    var failedJobCountHistory = JSON.parse(window.sessionStorage.getItem("failedJobCountHistory"))
    if(failedJobCountHistory[failedJobCountHistory.length-1] !== data.length){
      failedJobCountHistory.push(data.length)
    }
    window.sessionStorage.setItem("failedJobCountHistory", JSON.stringify(failedJobCountHistory))
    return failedJobCountHistory;
  }

  updateCompletedExecutionHistory(data){
    var completedExecutionCountHistory = JSON.parse(window.sessionStorage.getItem("completedExecutionCountHistory"))
    if(completedExecutionCountHistory[completedExecutionCountHistory.length-1] !== data.length){
      completedExecutionCountHistory.push(data.length)
    }
    window.sessionStorage.setItem("completedExecutionCountHistory", JSON.stringify(completedExecutionCountHistory))
    return completedExecutionCountHistory;
  }

  getJobsKilled(jobs){
    var killedJobs = jobs.filter(job => job.killedCause && job.killedCause !== "");
    console.log(killedJobs);
    console.log(killedJobs.length*1.0 / jobs.length*1.0);
    return killedJobs.length*1.0 / jobs.length*1.0;
  }
  
  componentDidMount() {
    fetch('http://localhost:8000/apps')
    .then(res => res.json())
    .then((data) => {
      this.setState({ apps: data })
      this.setState({ appCountHistory: this.updateAppHistory(data) })
    })
    .catch(console.log)

    fetch('http://localhost:8000/jobs')
    .then(res => res.json())
    .then((data) => {
      this.setState({ jobs: data })
      this.setState({ completedJobCountHistory: this.updateCompletedJobHistory(data) })
      this.setState({ failedJobCountHistory: this.updateFailedJobHistory(data) })
      this.setState({ killedJobsFraction: this.getJobsKilled(data) })
      this.setState({ killedJobs: data.filter(job => job.killedCause && job.killedCause !== "") })
      
    })
    .catch(console.log)

    fetch('http://localhost:8000/executions')
    .then(res => res.json())
    .then((data) => {
      this.setState({ executions: data })
      this.setState({ completedExecutionCountHistory: this.updateCompletedExecutionHistory(data) })
    })
    .catch(console.log)

    // fetch('http://localhost:8000/killed')
    // .then(res => res.json())
    // .then((data) => {
    //   this.setState({ killed: data })
    // })
    // .catch(console.log)


  }


  render() {
    return (
      <HashRouter>
          <React.Suspense fallback={loading}>
            <Switch>
              {/* <Route exact path="/login" name="Login Page" render={props => <Login {...props}/>} />
              <Route exact path="/register" name="Register Page" render={props => <Register {...props}/>} />
              <Route exact path="/404" name="Page 404" render={props => <Page404 {...props}/>} />
              <Route exact path="/500" name="Page 500" render={props => <Page500 {...props}/>} /> */}
              <Route path="/" name="Home" render={props => <TheLayout {...props}  setAppState={this.state}/>} />
            </Switch>
          </React.Suspense>
      </HashRouter>
    );
  }
}

export default App;
