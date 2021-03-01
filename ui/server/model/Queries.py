import sqlalchemy as db


class Queries:
    def __init__(self, session):
        self.session = session
        self.queries = db.Table('AIRAVAT_QUERY_METRIC_INFO',
                                session.dbEngine.metadata,
                                autoload=True,
                                autoload_with=session.dbEngine.engine)

    def fetchAll(self):
        queries = db.select([self.queries])
        resultSet = self.session.dbEngine.db_session.query(queries)
        return [r._asdict() for r in resultSet]
        # return resultSet.__dict__
