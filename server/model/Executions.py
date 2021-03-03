import sqlalchemy as db


class Executions:
    def __init__(self, session):
        self.session = session
        self.executions = db.Table('AIRAVAT_QUERY_METRIC_INFO',
                                session.dbEngine.metadata,
                                autoload=True,
                                autoload_with=session.dbEngine.engine)

    def fetchAll(self):
        executions = db.select([self.executions])
        resultSet = self.session.dbEngine.db_session.query(executions)
        return [r._asdict() for r in resultSet]
        # return resultSet.__dict__
