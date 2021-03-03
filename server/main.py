from typing import Optional

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from controllers.spark_handler import SparkController
from model.Applications import Applications
from model.Jobs import Jobs
from model.Queries import Queries
from model.Executions import Executions
from controllers.session import AiravatSession
import logging
import logging.config


app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=['*']
)

session = AiravatSession()
sparkController = SparkController(session)
apps = Applications(session)
jobs = Jobs(session)
queries = Queries(session)
executions = Executions(session)
logging.basicConfig(level=logging.INFO, format='%(name)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)


@app.get("/")
def read_root():
    return {"Hello": "World"}

@app.get("/apps")
def getApplications():
    return apps.fetchAll()

@app.get("/jobs")
def getJobs():
    return jobs.fetchAll()

@app.get("/queries")
def getQueries():
    return queries.fetchAll()

@app.get("/executions")
def getExecutions():
    return executions.fetchAll()

@app.get("/cost")
def getCost(sql):
    return sparkController.getCost(sql)

