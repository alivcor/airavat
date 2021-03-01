import configparser
import sqlalchemy as db
from database.dbops import DBEngine
import logging

class AiravatSession():
    def __init__(self):
        self.conf = configparser.ConfigParser()
        self.conf.read('config/server.conf')
        self.dbEngine = DBEngine()
        self.dbEngine.initDB(self.conf.get("database", "url"))





