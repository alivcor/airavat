from sqlalchemy import create_engine, MetaData
from sqlalchemy.orm import scoped_session, sessionmaker
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import Column, Integer, Sequence, ForeignKey, String, and_, or_


class DBEngine:
    def __init__(self):
        self.engine = None
        self.Base = declarative_base()
        self.db_session = None
        self.metadata = None
        Session = sessionmaker(bind=self.engine)
        self.session = Session()

    def initDB(self, databaseURL):
        if "postgresql" in databaseURL:
            self.engine = create_engine(databaseURL)
        elif "sqlite" in databaseURL:
            self.engine = create_engine(databaseURL, convert_unicode=True, connect_args={'check_same_thread': False})
        else:
            self.engine = create_engine(databaseURL)

        self.db_session = scoped_session(sessionmaker(autocommit=False, autoflush=False, bind=self.engine))
        self.metadata = MetaData()


    def createDB(self, databaseURL):
        self.initDB(databaseURL)
        self.Base.metadata.bind = self.engine
        self.Base.metadata.create_all(bind=self.engine)

    def getDBSession(self):
        return self.db_session