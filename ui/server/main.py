from typing import Optional

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from model.Applications import Applications
from controllers.session import AiravatSession
import logging
import logging.config

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=['*']
)

session = AiravatSession()
apps = Applications(session)
logging.basicConfig(level=logging.DEBUG, format='%(name)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)


@app.get("/")
def read_root():
    return {"Hello": "World"}

@app.get("/apps")
def getApplications():
    return apps.fetchAll()

