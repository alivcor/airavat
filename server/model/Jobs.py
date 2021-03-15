import sqlalchemy as db


class Jobs:
    def __init__(self, session):
        self.session = session
        self.jobs = db.Table('AIRAVAT_JOB_INFO',
                                session.dbEngine.metadata,
                                autoload=True,
                                autoload_with=session.dbEngine.engine)

    def fetchAll(self):
        jobs = db.select([self.jobs])
        resultSet = self.session.dbEngine.db_session.query(jobs)
        return [r._asdict() for r in resultSet]
        # return resultSet.__dict__

    def fetchKilled(self):
        jobs = db.select([self.jobs]).where(self.jobs.columns.killedCause != "")
        resultSet = self.session.dbEngine.db_session.query(jobs)
        return [r._asdict() for r in resultSet]
        # return resultSet.__dict__
